package com.github.nekolr.slime.executor.node;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.constant.OutputType;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput;
import com.github.nekolr.slime.model.SpiderOutput.OutputItem;
import com.github.nekolr.slime.websocket.WebSocketEvent;
import com.github.nekolr.slime.serializer.FastJsonSerializer;
import com.github.nekolr.slime.util.DataSourceManager;
import com.github.nekolr.slime.util.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.io.SpiderResponse;
import com.github.nekolr.slime.listener.SpiderListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 输出执行器
 */
@Component
@Slf4j
public class OutputExecutor implements NodeExecutor, SpiderListener {

    /**
     * 输出其他变量
     */
    String OUTPUT_OTHERS = "output-others";

    /**
     * 输出项的名称
     */
    String OUTPUT_NAME = "output-name";

    /**
     * 输出项的值
     */
    String OUTPUT_VALUE = "output-value";

    /**
     * 输出到数据库中的表名
     */
    String OUTPUT_TABLE_NAME = "tableName";

    /**
     * CSV 文件的名称
     */
    String OUTPUT_CSV_NAME = "csvName";

    /**
     * CSV 文件的编码
     */
    String OUTPUT_CSV_ENCODING = "csvEncoding";


    @Resource
    private DataSourceManager dataSourceManager;

    @Resource
    private ExpressionParser expressionParser;

    @Resource
    private SpiderConfig spiderConfig;

    /**
     * 一个节点对应一个 CSVPrinter
     */
    private Map<String, CSVPrinter> cachePrinter = new HashMap<>();

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {

        // 创建输出结果对象
        SpiderOutput output = new SpiderOutput();
        output.setNodeName(node.getNodeName());
        output.setNodeId(node.getNodeId());

        // 是否需要输出其他变量（只在测试时输出）
        boolean outputOthers = Constants.YES.equals(node.getJsonProperty(OUTPUT_OTHERS));
        // 获取输出目标
        boolean isDatabase = Constants.YES.equals(node.getJsonProperty(OutputType.DATABASE.getVariableName()));
        boolean isCsv = Constants.YES.equals(node.getJsonProperty(OutputType.CSV.getVariableName()));

        // 如果需要输出其他变量
        if (outputOthers) {
            // 输出其他变量
            this.outputOtherVariables(output, variables);
        }

        // 获取所有的输出项数据
        List<OutputItem> outputItems = this.getOutputItems(variables, context, node);

        // 输出到数据库
        if (isDatabase) {
            this.outputDatabase(node, outputItems);
        }
        // 输出到 CSV 文件
        if (isCsv) {
            this.outputCSV(node, context, outputItems);
        }

        // 将所有的输出项放入
        output.getOutputItems().addAll(outputItems);

        // 添加输出结果到上下文
        context.addOutput(output);
    }

    /**
     * 获取用户定义的所有输出项的数据
     *
     * @param variables 传递的变量和值
     * @param context   执行上下文
     * @param node      节点
     * @return 所有输出项的数据
     */
    private List<OutputItem> getOutputItems(Map<String, Object> variables, SpiderContext context, SpiderNode node) {
        List<OutputItem> outputItems = new ArrayList<>();
        // 获取用户设置的所有输出项
        List<Map<String, String>> items = node.getJsonArrayProperty(OUTPUT_NAME, OUTPUT_VALUE);
        for (Map<String, String> item : items) {
            Object value = null;
            String itemName = item.get(OUTPUT_NAME);
            String itemValue = item.get(OUTPUT_VALUE);
            try {
                value = expressionParser.parse(itemValue, variables);
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, itemName, value);
                log.debug("解析输出项：{} = {}", itemName, value);
            } catch (Exception e) {
                log.error("解析数据项：{} 出错", itemName, e);
            }
            outputItems.add(new OutputItem(itemName, value));
        }
        return outputItems;
    }


    /**
     * 填充其他变量
     *
     * @param output    输出结果
     * @param variables 传递的变量和值
     */
    private void outputOtherVariables(SpiderOutput output, Map<String, Object> variables) {
        for (Map.Entry<String, Object> item : variables.entrySet()) {
            Object value = item.getValue();
            // resp 变量
            if (value instanceof SpiderResponse) {
                SpiderResponse resp = (SpiderResponse) value;
                output.addItem(item.getKey() + ".html", resp.getHtml());
                continue;
            }
            // 去除不输出的信息
            if (Constants.EXCEPTION_VARIABLE.equals(item.getKey())) {
                continue;
            }
            // 去除不能序列化的参数
            try {
                JSON.toJSONString(value, FastJsonSerializer.serializeConfig);
            } catch (Exception e) {
                continue;
            }
            // 其他情况正常添加
            output.addItem(item.getKey(), item.getValue());
        }
    }

    private void outputDatabase(SpiderNode node, List<OutputItem> outputItems) {
        // 获取数据源 ID
        String dsId = node.getJsonProperty(Constants.DATASOURCE_ID);
        // 获取表名
        String tableName = node.getJsonProperty(OUTPUT_TABLE_NAME);

        if (StringUtils.isBlank(dsId)) {
            log.warn("数据源 ID 不能为空");
        } else if (StringUtils.isBlank(tableName)) {
            log.warn("表名不能为空");
        } else {
            if (outputItems == null || outputItems.isEmpty()) {
                return;
            }

            JdbcTemplate template = new JdbcTemplate(dataSourceManager.getDataSource(Long.parseLong(dsId)));

            StringBuilder preSql = new StringBuilder("INSERT INTO ");
            preSql.append(tableName);
            preSql.append(" (");
            StringBuilder nextSql = new StringBuilder(" VALUES (");

            // 设置字段名和对应的占位符
            for (int i = 0; i < outputItems.size(); i++) {
                OutputItem item = outputItems.get(i);
                if (StringUtils.isNotBlank(item.getName())) {
                    if (i == outputItems.size() - 1) {
                        preSql.append(item.getName());
                        preSql.append(")");
                        nextSql.append("?)");
                    } else {
                        preSql.append(item.getName());
                        preSql.append(",");
                        nextSql.append("?,");
                    }
                }
            }

            List<Object> values = outputItems.stream().map(item -> item.getValue()).collect(Collectors.toList());

            if (!values.isEmpty()) {
                try {
                    // 执行 sql
                    template.update(preSql.append(nextSql).toString(), values.toArray());
                } catch (Exception e) {
                    log.error("执行 sql 出错", e);
                    ExceptionUtils.wrapAndThrow(e);
                }
            }
        }
    }

    private void outputCSV(SpiderNode node, SpiderContext context, List<OutputItem> outputItems) {
        // 获取文件名
        String csvName = node.getJsonProperty(OUTPUT_CSV_NAME);
        if (outputItems == null || outputItems.isEmpty()) {
            return;
        }
        String key = context.getId() + "-" + node.getNodeId();
        CSVPrinter printer = cachePrinter.get(key);

        // 所有的记录值
        List<String> records = new ArrayList<>(outputItems.size());
        // 生成头部列表
        List<String> headers = outputItems.stream().map(item -> item.getName()).collect(Collectors.toList());

        try {
            if (printer == null) {
                synchronized (cachePrinter) {
                    printer = cachePrinter.get(key);
                    if (printer == null) {
                        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers.toArray(new String[headers.size()]));
                        FileOutputStream os = new FileOutputStream(spiderConfig.getWorkspace() + File.separator + csvName);
                        String csvEncoding = node.getJsonProperty(OUTPUT_CSV_ENCODING);
                        if ("UTF-8BOM".equals(csvEncoding)) {
                            csvEncoding = csvEncoding.substring(0, 5);
                            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
                            os.write(bom);
                            os.flush();
                        }
                        OutputStreamWriter osw = new OutputStreamWriter(os, csvEncoding);
                        printer = new CSVPrinter(osw, format);
                        cachePrinter.put(key, printer);
                    }
                }
            }

            // 转储数据
            for (int i = 0; i < headers.size(); i++) {
                OutputItem item = outputItems.get(i);
                if (item.getValue() != null) {
                    records.add(item.getValue().toString());
                }
            }
            // 打印数据
            synchronized (cachePrinter) {
                if (!records.isEmpty()) {
                    printer.printRecord(records);
                }
            }

        } catch (IOException e) {
            log.error("文件输出出现错误", e);
            ExceptionUtils.wrapAndThrow(e);
        }
    }

    @Override
    public void beforeStart(SpiderContext context) {

    }

    @Override
    public void afterEnd(SpiderContext context) {
        // 执行完毕后释放缓存
        this.releasePrinters();
    }


    @Override
    public String supportType() {
        return "output";
    }

    private void releasePrinters() {
        for (Iterator<Map.Entry<String, CSVPrinter>> iterator = this.cachePrinter.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, CSVPrinter> entry = iterator.next();
            CSVPrinter printer = entry.getValue();
            if (printer != null) {
                try {
                    printer.flush();
                    printer.close();
                    this.cachePrinter.remove(entry.getKey());
                } catch (IOException e) {
                    log.error("文件输出出现错误", e);
                    ExceptionUtils.wrapAndThrow(e);
                }
            }
        }
    }
}

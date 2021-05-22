package com.github.nekolr.slime.executor.node.event;

import com.github.nekolr.slime.config.SpiderConfig;
import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.node.OutputExecutor;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.model.SpiderOutput.OutputItem;
import com.github.nekolr.slime.support.DataSourceManager;
import com.github.nekolr.slime.executor.node.event.OutputEventPublisher.OutputEventBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OutputEventHandler {

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
    private SpiderConfig spiderConfig;

    @Resource
    private DataSourceManager dataSourceManager;


    @EventListener(value = OutputEventBean.class, condition = "#eventBean.event.equals(T(com.github.nekolr.slime.constant.OutputType).DATABASE.variableName)")
    private void outputDatabase(OutputEventBean eventBean) {
        SpiderNode node = eventBean.getNode();
        List<OutputItem> outputItems = eventBean.getOutputItems();

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

    @EventListener(value = OutputEventBean.class, condition = "#eventBean.event.equals(T(com.github.nekolr.slime.constant.OutputType).CSV.variableName)")
    private void outputCSV(OutputEventBean eventBean) {
        SpiderContext context = eventBean.getContext();
        SpiderNode node = eventBean.getNode();
        List<OutputItem> outputItems = eventBean.getOutputItems();

        // 获取文件名
        String csvName = node.getJsonProperty(OUTPUT_CSV_NAME);
        if (outputItems == null || outputItems.isEmpty()) {
            return;
        }
        String key = context.getId() + "-" + node.getNodeId();
        Map<String, CSVPrinter> cachePrinter = OutputExecutor.getCachePrinter();
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
}

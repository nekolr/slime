package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import com.github.nekolr.slime.service.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Comment("全局变量相关方法")
public class GlobalVariableFunctionExecutor implements FunctionExecutor {

    private static VariableService variableService;

    @Override
    public String getFunctionPrefix() {
        return "gv";
    }

    @Comment("更新全局变量")
    @Example("${gv.update('variableName', '1')}")
    public static void update(String variableName, String variableValue) {
        variableService.update(variableName, variableValue);
    }

    @Autowired
    public void setVariableService(VariableService variableService) {
        GlobalVariableFunctionExecutor.variableService = variableService;
    }
}

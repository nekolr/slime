package com.github.nekolr.slime.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpiderLog {

    private String level;

    private String message;

    private List<Object> variables;

    public SpiderLog(String level, String message, List<Object> variables) {
        if (variables != null && variables.size() > 0) {
            List<Object> nVariables = new ArrayList<>(variables.size());
            for (Object object : variables) {
                if (object instanceof Throwable) {
                    nVariables.add(ExceptionUtils.getStackTrace((Throwable) object));
                } else {
                    nVariables.add(object);
                }
            }
            this.variables = nVariables;
        }
        this.level = level;
        this.message = message;
    }
}

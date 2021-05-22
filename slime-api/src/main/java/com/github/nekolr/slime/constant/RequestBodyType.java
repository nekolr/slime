package com.github.nekolr.slime.constant;

import lombok.Getter;

public enum RequestBodyType {

    NONE("none"),
    RAW_BODY_TYPE("raw"),
    FORM_DATA_BODY_TYPE("form-data");

    @Getter
    private String bodyType;

    RequestBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public static RequestBodyType geRequestBodyType(String bodyType) {
        for (RequestBodyType requestBodyType : values()) {
            if (requestBodyType.getBodyType().equals(bodyType)) {
                return requestBodyType;
            }
        }
        return null;
    }
}

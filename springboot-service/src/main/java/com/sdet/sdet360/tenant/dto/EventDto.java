package com.sdet.sdet360.tenant.dto;

public class EventDto {
    private String relativeXPath;
    private String absoluteXPath;
    private String relationalXPath;
    private String action;
    private String type;
    private String value;

    public String getRelativeXPath() {
        return relativeXPath;
    }

    public void setRelativeXPath(String relativeXPath) {
        this.relativeXPath = relativeXPath;
    }

    public String getAbsoluteXPath() {
        return absoluteXPath;
    }

    public void setAbsoluteXPath(String absoluteXPath) {
        this.absoluteXPath = absoluteXPath;
    }

    public String getRelationalXPath() {
        return relationalXPath;
    }

    public void setRelationalXPath(String relationalXPath) {
        this.relationalXPath = relationalXPath;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

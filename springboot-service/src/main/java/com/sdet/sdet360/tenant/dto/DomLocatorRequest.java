package com.sdet.sdet360.tenant.dto;

public class DomLocatorRequest {
    private String tool;
    private String html;

    public DomLocatorRequest() {}

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}

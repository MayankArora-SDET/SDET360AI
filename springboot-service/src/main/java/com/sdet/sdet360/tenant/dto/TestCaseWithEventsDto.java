package com.sdet.sdet360.tenant.dto;

import java.util.List;

public class TestCaseWithEventsDto {
    private String test_case_id;
    private String url;
    private String description;
    private String category;
    private List<EventDto> events;

    public TestCaseWithEventsDto(String test_case_id, String url, String description, String category, List<EventDto> events) {
        this.test_case_id = test_case_id;
        this.url = url;
        this.description = description;
        this.category = category;
        this.events = events;
    }

    public String getTest_case_id() {
        return test_case_id;
    }

    public void setTest_case_id(String test_case_id) {
        this.test_case_id = test_case_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<EventDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventDto> events) {
        this.events = events;
    }
}

package com.sdet.sdet360.tenant.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class InteractionLogRequestDto {

    private String token;

    private List<EventDto> events;
    private Boolean enableAssertion;

    public void setToken(String token) {
        this.token = token;
    }

    public void setEvents(List<EventDto> events) {
        this.events = events;
    }

    public boolean isEnableAssertion() {
        return enableAssertion;
    }

    public void setEnableAssertion(Boolean enableAssertion) {
        this.enableAssertion = enableAssertion;
    }
}

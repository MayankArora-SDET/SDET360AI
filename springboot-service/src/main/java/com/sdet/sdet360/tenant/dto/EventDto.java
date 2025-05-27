package com.sdet.sdet360.tenant.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventDto {
    private UUID eventId;
    private String relativeXPath;
    private String absoluteXPath;
    private String relationalXPath;
    private String action;
    private String type;
    private Boolean Assertion;
    private Boolean assertionStatus;
    private Boolean Autohealed;
    private String value;
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

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

    public UUID getEventId() {
        return eventId;
    }

    public Boolean getAssertion() {
        return Assertion;
    }

    public void setAssertion(Boolean assertion) {
        Assertion = assertion;
    }

    public Boolean getAutohealed() {
        return Autohealed;
    }

    public void setAutohealed(Boolean autohealed) {
        Autohealed = autohealed;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }
    
    public Boolean getAssertionStatus() {
        return assertionStatus;
    }

    public void setAssertionStatus(Boolean assertionStatus) {
        this.assertionStatus = assertionStatus;
    }
}

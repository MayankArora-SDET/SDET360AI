package com.sdet.sdet360.tenant.dto;

public class TestCaseResponseDto {
    private String test_case_id;
    private String description;
    private String category;

    public TestCaseResponseDto(String test_case_id, String description, String category) {
        this.test_case_id = test_case_id;
        this.description = description;
        this.category = category;
    }

    public String getTest_case_id() {
        return test_case_id;
    }

    public void setTest_case_id(String test_case_id) {
        this.test_case_id = test_case_id;
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
}

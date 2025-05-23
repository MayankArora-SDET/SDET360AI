package com.sdet.sdet360.tenant.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class QualityObjectiveStatusDto {
    private UUID id; 
    private String keyFeature;
    private String category;
    private String successCriteriaLevel1;
    private String successCriteriaLevel2;
    private String status;
    private UUID verticalId;
}
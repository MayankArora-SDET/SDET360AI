package com.sdet.sdet360.tenant.dto;
import lombok.Data;
import java.util.UUID;
@Data
public class ReleaseTestCoverageDto {
    private UUID id;
    private String epic;
    private Integer severity1;
    private Integer severity2;
    private Integer severity3;
    private Integer severity4;
    private Integer testCases;
    private UUID verticalId;
}

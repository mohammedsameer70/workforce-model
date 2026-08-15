package com.boostphysioclinic.workforceapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private Integer id;
    private String name;
    private String type;
    private String description;
    private String generatedAt;
    private Long fileSize;
    private String filePath;
    private String status;
    private String generatedBy;
}

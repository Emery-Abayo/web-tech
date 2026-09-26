package com.wellbeing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class WellbeingRecordRequest {

    @NotNull(message = "studentId is required")
    private Long studentId;

    @NotNull(message = "Check-in date is required")
    @PastOrPresent(message = "Check-in date cannot be in the future")
    private LocalDate checkDate;

    @NotBlank(message = "Concern type is required")
    private String concernType;

    private String notes;

    @NotBlank(message = "Risk level is required")
    private String riskLevel;

    // Optional: the service forces this true when riskLevel is High regardless of what's sent.
    private Boolean followUpRequired;

    // Optional: defaults to "Open" if not supplied.
    private String status;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public LocalDate getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDate checkDate) { this.checkDate = checkDate; }
    public String getConcernType() { return concernType; }
    public void setConcernType(String concernType) { this.concernType = concernType; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public Boolean getFollowUpRequired() { return followUpRequired; }
    public void setFollowUpRequired(Boolean followUpRequired) { this.followUpRequired = followUpRequired; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

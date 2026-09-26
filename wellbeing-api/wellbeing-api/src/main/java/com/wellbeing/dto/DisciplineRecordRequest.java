package com.wellbeing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class DisciplineRecordRequest {

    @NotNull(message = "studentId is required")
    private Long studentId;

    @NotNull(message = "Incident date is required")
    @PastOrPresent(message = "Incident date cannot be in the future")
    private LocalDate incidentDate;

    @NotBlank(message = "Fault is required")
    private String fault;

    private String description;

    @NotBlank(message = "Punishment is required")
    private String punishment;

    @NotNull(message = "Points is required")
    @Min(value = 0, message = "Points cannot be negative")
    private Integer points;

    // Optional: defaults to "Open" if not supplied, matching DisciplineRecordBean.prepareNew().
    private String status;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    public String getFault() { return fault; }
    public void setFault(String fault) { this.fault = fault; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPunishment() { return punishment; }
    public void setPunishment(String punishment) { this.punishment = punishment; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

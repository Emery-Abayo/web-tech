package com.wellbeing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * The Behavior/Well-being module referenced in the JSF version's README as
 * "part of the system design but not implemented." Mirrors DisciplineRecord's
 * shape (student link, date, category, notes, status) but tracks personal/
 * emotional wellbeing concerns rather than formal discipline.
 */
@Entity
@Table(name = "wellbeing_record")
public class WellbeingRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Student student;

    @NotNull(message = "Check-in date is required")
    @PastOrPresent(message = "Check-in date cannot be in the future")
    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @NotBlank(message = "Concern type is required")
    @Column(name = "concern_type", nullable = false, length = 50)
    private String concernType;

    @Column(name = "notes", length = 255)
    private String notes;

    @NotBlank(message = "Risk level is required")
    @Column(name = "risk_level", nullable = false, length = 10)
    private String riskLevel;

    @NotNull(message = "Follow-up flag is required")
    @Column(name = "follow_up_required", nullable = false)
    private Boolean followUpRequired = false;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    public WellbeingRecord() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
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

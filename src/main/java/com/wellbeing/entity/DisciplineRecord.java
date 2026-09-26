package com.wellbeing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "discipline_record")
public class DisciplineRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Student student;

    @NotNull(message = "Incident date is required")
    @PastOrPresent(message = "Incident date cannot be in the future")
    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    @NotBlank(message = "Fault is required")
    @Column(name = "fault", nullable = false, length = 100)
    private String fault;

    @Column(name = "description", length = 255)
    private String description;

    @NotBlank(message = "Punishment is required")
    @Column(name = "punishment", nullable = false, length = 150)
    private String punishment;

    @NotNull(message = "Points is required")
    @Min(value = 0, message = "Points cannot be negative")
    @Column(name = "points", nullable = false)
    private Integer points;

    @NotBlank(message = "Status is required")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    public DisciplineRecord() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
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

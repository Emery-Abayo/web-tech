package com.wellbeing.controller;

import com.wellbeing.dto.DisciplineRecordRequest;
import com.wellbeing.entity.DisciplineRecord;
import com.wellbeing.service.DisciplineRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discipline-records")
public class DisciplineRecordController {

    @Autowired
    private DisciplineRecordService disciplineRecordService;

    @GetMapping
    public List<DisciplineRecord> findAll() {
        return disciplineRecordService.findAll();
    }

    @GetMapping("/{id}")
    public DisciplineRecord findById(@PathVariable Long id) {
        return disciplineRecordService.findById(id);
    }

    @GetMapping("/student/{studentId}")
    public List<DisciplineRecord> findByStudent(@PathVariable Long studentId) {
        return disciplineRecordService.findByStudent(studentId);
    }

    /** Discipline Score for a student = SUM(points) - ported from DisciplineRecordBean.scoreFor(). */
    @GetMapping("/student/{studentId}/score")
    public Map<String, Object> getScore(@PathVariable Long studentId) {
        int total = disciplineRecordService.getTotalPointsForStudent(studentId);
        return Map.of("studentId", studentId, "totalPoints", total);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisciplineRecord create(@Valid @RequestBody DisciplineRecordRequest request) {
        return disciplineRecordService.create(request);
    }

    @PutMapping("/{id}")
    public DisciplineRecord update(@PathVariable Long id, @Valid @RequestBody DisciplineRecordRequest request) {
        return disciplineRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        disciplineRecordService.delete(id);
    }
}

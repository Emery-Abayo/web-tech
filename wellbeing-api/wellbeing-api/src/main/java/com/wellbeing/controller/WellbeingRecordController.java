package com.wellbeing.controller;

import com.wellbeing.dto.WellbeingRecordRequest;
import com.wellbeing.entity.WellbeingRecord;
import com.wellbeing.service.WellbeingRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wellbeing-records")
public class WellbeingRecordController {

    @Autowired
    private WellbeingRecordService wellbeingRecordService;

    @GetMapping
    public List<WellbeingRecord> findAll() {
        return wellbeingRecordService.findAll();
    }

    @GetMapping("/{id}")
    public WellbeingRecord findById(@PathVariable Long id) {
        return wellbeingRecordService.findById(id);
    }

    @GetMapping("/student/{studentId}")
    public List<WellbeingRecord> findByStudent(@PathVariable Long studentId) {
        return wellbeingRecordService.findByStudent(studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WellbeingRecord create(@Valid @RequestBody WellbeingRecordRequest request) {
        return wellbeingRecordService.create(request);
    }

    @PutMapping("/{id}")
    public WellbeingRecord update(@PathVariable Long id, @Valid @RequestBody WellbeingRecordRequest request) {
        return wellbeingRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        wellbeingRecordService.delete(id);
    }
}

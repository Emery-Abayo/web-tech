package com.wellbeing.service;

import com.wellbeing.dto.DisciplineRecordRequest;
import com.wellbeing.entity.DisciplineRecord;

import java.util.List;

public interface DisciplineRecordService {
    DisciplineRecord create(DisciplineRecordRequest request);
    DisciplineRecord update(Long id, DisciplineRecordRequest request);
    void delete(Long id);
    DisciplineRecord findById(Long id);
    List<DisciplineRecord> findAll();
    List<DisciplineRecord> findByStudent(Long studentId);
    int getTotalPointsForStudent(Long studentId);
}

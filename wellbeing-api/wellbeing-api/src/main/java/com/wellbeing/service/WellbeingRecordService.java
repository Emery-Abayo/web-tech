package com.wellbeing.service;

import com.wellbeing.dto.WellbeingRecordRequest;
import com.wellbeing.entity.WellbeingRecord;

import java.util.List;

public interface WellbeingRecordService {
    WellbeingRecord create(WellbeingRecordRequest request);
    WellbeingRecord update(Long id, WellbeingRecordRequest request);
    void delete(Long id);
    WellbeingRecord findById(Long id);
    List<WellbeingRecord> findAll();
    List<WellbeingRecord> findByStudent(Long studentId);
}

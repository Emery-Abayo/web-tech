package com.wellbeing.repository;

import com.wellbeing.entity.WellbeingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WellbeingRecordRepository extends JpaRepository<WellbeingRecord, Long> {
    List<WellbeingRecord> findAllByOrderByCheckDateDesc();
    List<WellbeingRecord> findByStudentIdOrderByCheckDateDesc(Long studentId);
}

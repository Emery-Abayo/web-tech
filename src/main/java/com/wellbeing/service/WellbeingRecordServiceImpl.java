package com.wellbeing.service;

import com.wellbeing.dto.WellbeingRecordRequest;
import com.wellbeing.entity.Student;
import com.wellbeing.entity.WellbeingRecord;
import com.wellbeing.exception.ResourceNotFoundException;
import com.wellbeing.repository.StudentRepository;
import com.wellbeing.repository.WellbeingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WellbeingRecordServiceImpl implements WellbeingRecordService {

    @Autowired
    private WellbeingRecordRepository wellbeingRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public WellbeingRecord create(WellbeingRecordRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + request.getStudentId()));

        WellbeingRecord record = new WellbeingRecord();
        record.setStudent(student);
        record.setCheckDate(request.getCheckDate());
        record.setConcernType(request.getConcernType());
        record.setNotes(request.getNotes());
        record.setRiskLevel(request.getRiskLevel());
        record.setFollowUpRequired(resolveFollowUp(request.getRiskLevel(), request.getFollowUpRequired()));
        record.setStatus(request.getStatus() != null && !request.getStatus().isBlank() ? request.getStatus() : "Open");
        return wellbeingRecordRepository.save(record);
    }

    @Override
    public WellbeingRecord update(Long id, WellbeingRecordRequest request) {
        WellbeingRecord found = findById(id);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + request.getStudentId()));

        found.setStudent(student);
        found.setCheckDate(request.getCheckDate());
        found.setConcernType(request.getConcernType());
        found.setNotes(request.getNotes());
        found.setRiskLevel(request.getRiskLevel());
        found.setFollowUpRequired(resolveFollowUp(request.getRiskLevel(), request.getFollowUpRequired()));
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            found.setStatus(request.getStatus());
        }
        return wellbeingRecordRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        WellbeingRecord found = findById(id);
        wellbeingRecordRepository.delete(found);
    }

    @Override
    public WellbeingRecord findById(Long id) {
        return wellbeingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wellbeing record not found with id " + id));
    }

    @Override
    public List<WellbeingRecord> findAll() {
        return wellbeingRecordRepository.findAllByOrderByCheckDateDesc();
    }

    @Override
    public List<WellbeingRecord> findByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id " + studentId);
        }
        return wellbeingRecordRepository.findByStudentIdOrderByCheckDateDesc(studentId);
    }

    /** A "High" risk level always flags for follow-up, regardless of what the caller sent -
     *  same spirit as IncidentDateValidator: a hard rule the UI can't quietly bypass. */
    private boolean resolveFollowUp(String riskLevel, Boolean requested) {
        if ("High".equalsIgnoreCase(riskLevel)) {
            return true;
        }
        return Boolean.TRUE.equals(requested);
    }
}

package com.wellbeing.service;

import com.wellbeing.dto.DisciplineRecordRequest;
import com.wellbeing.entity.DisciplineRecord;
import com.wellbeing.entity.Student;
import com.wellbeing.exception.ResourceNotFoundException;
import com.wellbeing.repository.DisciplineRecordRepository;
import com.wellbeing.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplineRecordServiceImpl implements DisciplineRecordService {

    @Autowired
    private DisciplineRecordRepository disciplineRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public DisciplineRecord create(DisciplineRecordRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + request.getStudentId()));

        DisciplineRecord record = new DisciplineRecord();
        record.setStudent(student);
        record.setIncidentDate(request.getIncidentDate());
        record.setFault(request.getFault());
        record.setDescription(request.getDescription());
        record.setPunishment(request.getPunishment());
        record.setPoints(request.getPoints());
        // Same default as DisciplineRecordBean.prepareNew()
        record.setStatus(request.getStatus() != null && !request.getStatus().isBlank() ? request.getStatus() : "Open");
        return disciplineRecordRepository.save(record);
    }

    @Override
    public DisciplineRecord update(Long id, DisciplineRecordRequest request) {
        DisciplineRecord found = findById(id);

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + request.getStudentId()));

        found.setStudent(student);
        found.setIncidentDate(request.getIncidentDate());
        found.setFault(request.getFault());
        found.setDescription(request.getDescription());
        found.setPunishment(request.getPunishment());
        found.setPoints(request.getPoints());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            found.setStatus(request.getStatus());
        }
        return disciplineRecordRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        DisciplineRecord found = findById(id);
        disciplineRecordRepository.delete(found);
    }

    @Override
    public DisciplineRecord findById(Long id) {
        return disciplineRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline record not found with id " + id));
    }

    @Override
    public List<DisciplineRecord> findAll() {
        return disciplineRecordRepository.findAllByOrderByIncidentDateDesc();
    }

    @Override
    public List<DisciplineRecord> findByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id " + studentId);
        }
        return disciplineRecordRepository.findByStudentIdOrderByIncidentDateDesc(studentId);
    }

    @Override
    public int getTotalPointsForStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id " + studentId);
        }
        return disciplineRecordRepository.sumPointsByStudentId(studentId);
    }
}

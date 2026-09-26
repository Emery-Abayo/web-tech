package com.wellbeing.service;

import com.wellbeing.entity.Student;
import com.wellbeing.exception.BusinessValidationException;
import com.wellbeing.exception.ResourceNotFoundException;
import com.wellbeing.repository.DisciplineRecordRepository;
import com.wellbeing.repository.StudentRepository;
import com.wellbeing.repository.WellbeingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DisciplineRecordRepository disciplineRecordRepository;

    @Autowired
    private WellbeingRecordRepository wellbeingRecordRepository;

    /** Same rule as StudentDAO.isStudentNumberDuplicate() / StudentBean.save(). */
    @Override
    public Student create(Student student) {
        if (studentRepository.existsByStudentNumber(student.getStudentNumber())) {
            throw new BusinessValidationException(
                    "This student number is already in use: " + student.getStudentNumber());
        }
        return studentRepository.save(student);
    }

    @Override
    public Student update(Long id, Student student) {
        Student found = findById(id);

        if (studentRepository.existsByStudentNumberAndIdNot(student.getStudentNumber(), id)) {
            throw new BusinessValidationException(
                    "This student number is already in use: " + student.getStudentNumber());
        }

        found.setStudentNumber(student.getStudentNumber());
        found.setFirstName(student.getFirstName());
        found.setLastName(student.getLastName());
        found.setGender(student.getGender());
        found.setClassName(student.getClassName());
        found.setEmail(student.getEmail());
        return studentRepository.save(found);
    }

    @Override
    public void delete(Long id) {
        Student found = findById(id);
        boolean hasDisciplineHistory = !disciplineRecordRepository.findByStudentIdOrderByIncidentDateDesc(id).isEmpty();
        boolean hasWellbeingHistory = !wellbeingRecordRepository.findByStudentIdOrderByCheckDateDesc(id).isEmpty();
        if (hasDisciplineHistory || hasWellbeingHistory) {
            throw new BusinessValidationException(
                    "Cannot delete a student with existing discipline or wellbeing records");
        }
        studentRepository.delete(found);
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAllByOrderByLastNameAsc();
    }
}

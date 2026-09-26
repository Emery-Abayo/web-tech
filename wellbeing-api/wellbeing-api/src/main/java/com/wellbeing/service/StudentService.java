package com.wellbeing.service;

import com.wellbeing.entity.Student;

import java.util.List;

public interface StudentService {
    Student create(Student student);
    Student update(Long id, Student student);
    void delete(Long id);
    Student findById(Long id);
    List<Student> findAll();
}

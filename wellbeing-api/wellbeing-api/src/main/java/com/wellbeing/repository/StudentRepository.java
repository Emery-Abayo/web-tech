package com.wellbeing.repository;

import com.wellbeing.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByOrderByLastNameAsc();
    boolean existsByStudentNumber(String studentNumber);
    boolean existsByStudentNumberAndIdNot(String studentNumber, Long id);
}

package com.wellbeing.repository;

import com.wellbeing.entity.DisciplineRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisciplineRecordRepository extends JpaRepository<DisciplineRecord, Long> {

    List<DisciplineRecord> findAllByOrderByIncidentDateDesc();

    List<DisciplineRecord> findByStudentIdOrderByIncidentDateDesc(Long studentId);

    /** Discipline Score = SUM(points) for a student, calculated on demand (not stored) -
     *  same as DisciplineRecordDAO.getTotalPointsForStudent(). */
    @Query("SELECT COALESCE(SUM(d.points), 0) FROM DisciplineRecord d WHERE d.student.id = :studentId")
    int sumPointsByStudentId(@Param("studentId") Long studentId);
}

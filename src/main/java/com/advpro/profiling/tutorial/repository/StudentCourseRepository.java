package com.advpro.profiling.tutorial.repository;


import com.advpro.profiling.tutorial.model.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author muhammad.khadafi
 */

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    @Query("""
        SELECT sc
        FROM StudentCourse sc
        JOIN FETCH sc.student
        JOIN FETCH sc.course
    """)
    List<StudentCourse> findAllWithStudentAndCourse();
}
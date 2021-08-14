package br.com.alura.school.enrollment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}

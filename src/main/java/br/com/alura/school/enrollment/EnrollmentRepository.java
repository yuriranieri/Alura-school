package br.com.alura.school.enrollment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.school.user.User;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);
    List<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}

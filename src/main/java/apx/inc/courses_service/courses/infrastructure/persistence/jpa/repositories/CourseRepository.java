package apx.inc.courses_service.courses.infrastructure.persistence.jpa.repositories;

import apx.inc.courses_service.courses.domain.model.aggregates.Course;
import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCourseJoinCode(CourseJoinCode courseJoinCode);

    List<Course> findByTeacherId(Long teacherId);
}

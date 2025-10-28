package apx.inc.courses_service.courses.domain.model.aggregates;

import apx.inc.courses_service.courses.domain.model.commands.CreateCourseCommand;
import apx.inc.courses_service.courses.domain.model.commands.UpdateCourseCommand;
import apx.inc.courses_service.courses.domain.model.valueobjects.CourseJoinCode;
import apx.inc.courses_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Getter
@Entity
public class Course extends AuditableAbstractAggregateRoot<Course> {

    private String title;
    private String imageUrl;

    @Embedded
    private CourseJoinCode courseJoinCode;

    private Long teacherId;

    private Set<Long> studentIds;

    protected Course() {
        this.studentIds = new HashSet<>();
    }

    public Course(String title, String imageUrl, Long teacherId) {
        super();
        this.title = title;
        this.imageUrl = imageUrl;
        this.teacherId = teacherId;
        this.courseJoinCode = null;
        this.studentIds = new HashSet<>();
    }

    public Course(CreateCourseCommand createCourseCommand) {
        this.title = createCourseCommand.title();
        this.imageUrl = generatePicsumImageUrl(createCourseCommand.title());
        this.teacherId = createCourseCommand.teacherId();
        this.courseJoinCode = generateCourseJoinCode();
        this.studentIds = new HashSet<>();
    }

    // ✅ NUEVOS: Métodos para gestionar estudiantes
    public void enrollStudent(Long studentId) {
        if (this.studentIds.contains(studentId)) {
            throw new IllegalArgumentException("Student already enrolled in this course");
        }
        this.studentIds.add(studentId);
    }

    public void kickStudent(Long studentId) {
        if (!this.studentIds.contains(studentId)) {
            throw new IllegalArgumentException("Student not enrolled in this course");
        }
        this.studentIds.remove(studentId);
    }

    public boolean hasStudent(Long studentId) {
        return this.studentIds.contains(studentId);
    }

    public int getStudentCount() {
        return this.studentIds.size();
    }

    // Métodos existentes (mantener)
    public Course setJoinCode(CourseJoinCode courseJoinCode) {
        this.courseJoinCode = courseJoinCode;
        return this;
    }

    public Course resetJoinCode() {
        this.courseJoinCode = null;
        return this;
    }

    public Course updateCourse(UpdateCourseCommand updateCourseCommand) {
        this.title = updateCourseCommand.title();
        this.imageUrl = updateCourseCommand.imageUrl();
        return this;
    }

    private String generatePicsumImageUrl(String courseTitle) {
        int seed = courseTitle != null ? Math.abs(courseTitle.hashCode()) : new Random().nextInt(1000);
        return "https://picsum.photos/400/300?random=" + seed;
    }

    private CourseJoinCode generateCourseJoinCode() {
        String key = java.util.UUID.randomUUID().toString()
                .substring(0, 8)
                .toUpperCase();
        Date expiration = java.sql.Timestamp.valueOf(
                java.time.LocalDateTime.now().plusMonths(6)
        );
        return new CourseJoinCode(key, expiration);
    }

//    // Agregar este getter explícitamente
//    public Long getId() {
//        return super.getId();
//    }


}

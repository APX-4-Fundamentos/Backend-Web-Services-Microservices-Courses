package apx.inc.courses_service.courses.interfaces.rest.resources;

public record CreateCourseResource(
        String title
) {
    public CreateCourseResource{
        if(title==null||title.isBlank()){
            throw new IllegalArgumentException(" title can not be null or empty ");
        }

    }
}

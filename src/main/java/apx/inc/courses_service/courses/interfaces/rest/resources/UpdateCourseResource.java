package apx.inc.courses_service.courses.interfaces.rest.resources;

public record UpdateCourseResource(

        String title,
        String imageUrl
) {
    public UpdateCourseResource{

        if (title==null||title.isBlank()){
            throw new IllegalArgumentException(" title cannot be null or blank  ");
        }
        if (imageUrl==null||imageUrl.isBlank()){
            throw new IllegalArgumentException(" imageUrl cannot be null or blank ");
        }
    }
}

package apx.inc.courses_service.courses.domain.model.commands;

public record CreateCourseCommand(
        String title,
        //String imageUrl,
        Long teacherId
) {
    public CreateCourseCommand{
        if (title == null){
            throw new IllegalArgumentException("Title cannot be null");
        }
//        if (imageUrl == null){
//            throw new IllegalArgumentException("Image URL cannot be null");
//        }
        if (teacherId == null){
            throw new IllegalArgumentException("Teacher ID cannot be null");
        }
    }
}

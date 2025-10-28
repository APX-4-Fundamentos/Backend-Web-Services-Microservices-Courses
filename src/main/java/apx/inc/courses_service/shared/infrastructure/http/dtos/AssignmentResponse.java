package apx.inc.courses_service.shared.infrastructure.http.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;

    @JsonProperty("course_id")
    private Long courseId;

    private LocalDateTime deadline;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
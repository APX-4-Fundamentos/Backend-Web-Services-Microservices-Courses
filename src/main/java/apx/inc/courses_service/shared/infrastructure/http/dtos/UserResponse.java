package apx.inc.courses_service.shared.infrastructure.http.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String userName;
    private String email;
    private List<String> roles;

    @JsonProperty("userName")
    public String getUsername() {
        return userName;
    }
}
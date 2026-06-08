package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotBlank @Pattern(regexp = "ADMIN|STUDENT|admin|student") String role,
        @Positive Integer studentId,
        @Size(max = 100) String staffProfile
) {
}

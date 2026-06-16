package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateStaffProfileRequest(
        @NotBlank @Size(max = 100) String staffProfile
) {
}

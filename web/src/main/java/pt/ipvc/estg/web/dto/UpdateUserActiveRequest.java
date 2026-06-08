package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserActiveRequest(@NotNull Boolean active) {
}

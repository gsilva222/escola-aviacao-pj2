package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record StudentRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres")
        String name,
        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email invalido")
        @Size(max = 100, message = "Email deve ter no maximo 100 caracteres")
        String email,
        @Size(max = 20, message = "Telefone deve ter no maximo 20 caracteres")
        String phone,
        @Pattern(regexp = "\\d{9}", message = "NIF deve ter exatamente 9 digitos")
        @Size(max = 20, message = "NIF deve ter no maximo 20 caracteres")
        String nif,
        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate birthdate,
        @Size(max = 255, message = "Morada deve ter no maximo 255 caracteres")
        String address,
        @Size(max = 50, message = "Nacionalidade deve ter no maximo 50 caracteres")
        String nationality,
        @NotNull(message = "CourseId e obrigatorio")
        @Positive(message = "CourseId deve ser positivo")
        Integer courseId,
        @Pattern(regexp = "active|suspended|completed", message = "Status deve ser active, suspended ou completed")
        @Size(max = 20, message = "Status deve ter no maximo 20 caracteres")
        String status,
        @PastOrPresent(message = "Data de inscricao nao pode estar no futuro")
        LocalDate enrollmentDate,
        @Min(value = 0, message = "Progresso deve estar entre 0 e 100")
        @Max(value = 100, message = "Progresso deve estar entre 0 e 100")
        Integer progress,
        @PositiveOrZero(message = "Horas de voo devem ser positivas")
        Double flightHours,
        @PositiveOrZero(message = "Horas teoricas devem ser positivas")
        Double theoreticalHours,
        @Pattern(regexp = "up_to_date|pending|overdue", message = "PaymentStatus deve ser up_to_date, pending ou overdue")
        @Size(max = 20, message = "PaymentStatus deve ter no maximo 20 caracteres")
        String paymentStatus
) {
}

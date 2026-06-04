package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PaymentRequest(
        @NotNull(message = "StudentId e obrigatorio")
        @Positive(message = "StudentId deve ser positivo")
        Integer studentId,
        @NotBlank(message = "Descricao e obrigatoria")
        @Size(max = 200, message = "Descricao deve ter no maximo 200 caracteres")
        String description,
        @NotNull(message = "Montante e obrigatorio")
        @Positive(message = "Montante deve ser positivo")
        Double amount,
        LocalDate dueDate,
        @PastOrPresent(message = "Data de pagamento nao pode estar no futuro")
        LocalDate paidDate,
        @Pattern(regexp = "paid|pending|overdue", message = "Status deve ser paid, pending ou overdue")
        @Size(max = 20, message = "Status deve ter no maximo 20 caracteres")
        String status,
        @Size(max = 50, message = "Metodo de pagamento deve ter no maximo 50 caracteres")
        String paymentMethod,
        @Size(max = 500, message = "Notas deve ter no maximo 500 caracteres")
        String notes
) {
}

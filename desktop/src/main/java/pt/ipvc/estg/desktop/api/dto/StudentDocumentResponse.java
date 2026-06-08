package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDateTime;

public record StudentDocumentResponse(
        Integer id,
        Integer studentId,
        String fileName,
        String contentType,
        String category,
        LocalDateTime uploadedAt
) {
}

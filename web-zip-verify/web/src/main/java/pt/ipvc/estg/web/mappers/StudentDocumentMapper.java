package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.web.dto.StudentDocumentResponse;

public final class StudentDocumentMapper {
    private StudentDocumentMapper() {}

    public static StudentDocumentResponse toResponse(StudentDocument document) {
        if (document == null) {
            return null;
        }
        Integer studentId = document.getStudent() != null ? document.getStudent().getId() : null;
        return new StudentDocumentResponse(
                document.getId(),
                studentId,
                document.getFileName(),
                document.getContentType(),
                document.getCategory(),
                document.getUploadedAt()
        );
    }
}

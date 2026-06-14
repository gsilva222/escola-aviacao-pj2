package pt.ipvc.estg.services;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.StudentDocumentRepository;

import java.nio.file.Path;
import java.util.List;

public class StudentDocumentService {

    private final StudentDocumentRepository documentRepository;

    public StudentDocumentService(StudentDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public StudentDocumentService() {
        this(new pt.ipvc.estg.repositories.mock.MockStudentDocumentRepository());
    }

    public List<StudentDocument> listByStudent(int studentId) {
        return documentRepository.findByStudentOrderByUploadedAtDesc(studentId);
    }

    public StudentDocument requireDocument(int studentId, int documentId) {
        return documentRepository.findByIdAndStudent(documentId, studentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento nao encontrado"));
    }

    public StudentDocument register(Student student, String fileName, String contentType, String storagePath, String category) {
        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException("Estudante e obrigatorio");
        }
        if (student.getStatus() != null && "suspended".equalsIgnoreCase(student.getStatus())) {
            throw new IllegalArgumentException("Aluno suspenso: nao e permitido enviar documentos");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Nome do ficheiro e obrigatorio");
        }
        if (storagePath == null || storagePath.isBlank()) {
            throw new IllegalArgumentException("Caminho de armazenamento e obrigatorio");
        }
        String safeName = sanitizeFileName(fileName);
        String safeContentType = contentType != null ? contentType : "application/octet-stream";
        StudentDocument document = new StudentDocument(student, safeName, safeContentType, storagePath, category);
        return documentRepository.save(document);
    }

    public void deleteMetadata(StudentDocument document) {
        if (document == null || document.getId() == null) {
            throw new EntityNotFoundException("Documento nao encontrado");
        }
        documentRepository.deleteById(document.getId());
    }

    public void validateStoragePath(String storagePath, Path allowedRoot) {
        Path path = Path.of(storagePath).normalize();
        if (!path.startsWith(allowedRoot)) {
            throw new IllegalArgumentException("Caminho de ficheiro invalido");
        }
    }

    public static String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "documento.bin";
        }
        String cleaned = Path.of(name).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleaned.isBlank() ? "documento.bin" : cleaned;
    }
}

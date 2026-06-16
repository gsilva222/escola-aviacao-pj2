package pt.ipvc.estg.web.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.services.StudentDocumentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class DocumentStorageService {

    private final StudentDocumentService documentService;
    private final Path uploadRoot;

    public DocumentStorageService(StudentDocumentService documentService,
                                  @Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.documentService = documentService;
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public StudentDocument store(Student student, MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ficheiro obrigatorio");
        }
        String originalName = StudentDocumentService.sanitizeFileName(file.getOriginalFilename());
        try {
            Path studentDir = uploadRoot.resolve(String.valueOf(student.getId()));
            Files.createDirectories(studentDir);
            String storedName = UUID.randomUUID() + "_" + originalName;
            Path target = studentDir.resolve(storedName);
            file.transferTo(target);

            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            return documentService.register(student, originalName, contentType, target.toString(), category);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao guardar documento");
        }
    }

    public Path resolvePath(StudentDocument document) {
        try {
            documentService.validateStoragePath(document.getStoragePath(), uploadRoot);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
        Path path = Path.of(document.getStoragePath()).normalize();
        if (!Files.exists(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ficheiro nao encontrado");
        }
        return path;
    }

    public void deleteDocument(StudentDocument document) {
        try {
            Path path = resolvePath(document);
            Files.deleteIfExists(path);
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw ex;
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao eliminar documento");
        }
        documentService.deleteMetadata(document);
    }

    public StudentDocument requireDocument(int studentId, int documentId) {
        try {
            return documentService.requireDocument(studentId, documentId);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}

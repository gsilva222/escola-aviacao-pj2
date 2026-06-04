package pt.ipvc.estg.web.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.web.repositories.StudentDocumentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class DocumentStorageService {

    private final StudentDocumentRepository documentRepository;
    private final Path uploadRoot;

    public DocumentStorageService(StudentDocumentRepository documentRepository,
                                  @Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.documentRepository = documentRepository;
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public StudentDocument store(Student student, MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ficheiro obrigatorio");
        }
        String originalName = sanitizeFileName(file.getOriginalFilename());
        try {
            Path studentDir = uploadRoot.resolve(String.valueOf(student.getId()));
            Files.createDirectories(studentDir);
            String storedName = UUID.randomUUID() + "_" + originalName;
            Path target = studentDir.resolve(storedName);
            file.transferTo(target);

            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            StudentDocument document = new StudentDocument(student, originalName, contentType, target.toString(), category);
            return documentRepository.save(document);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao guardar documento");
        }
    }

    public Path resolvePath(StudentDocument document) {
        Path path = Path.of(document.getStoragePath()).normalize();
        if (!path.startsWith(uploadRoot)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Caminho de ficheiro invalido");
        }
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
        documentRepository.delete(document);
    }

    private String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "documento.bin";
        }
        String cleaned = Path.of(name).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleaned.isBlank() ? "documento.bin" : cleaned;
    }
}

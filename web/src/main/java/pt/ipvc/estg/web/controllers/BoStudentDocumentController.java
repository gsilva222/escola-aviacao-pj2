package pt.ipvc.estg.web.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.services.StudentDocumentService;
import pt.ipvc.estg.web.dto.StudentDocumentResponse;
import pt.ipvc.estg.web.mappers.StudentDocumentMapper;
import pt.ipvc.estg.web.services.DocumentStorageService;
import pt.ipvc.estg.web.services.StudentScopeService;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/bo/student-documents")
public class BoStudentDocumentController {

    private final StudentScopeService studentScopeService;
    private final StudentDocumentService documentService;
    private final DocumentStorageService documentStorageService;

    public BoStudentDocumentController(StudentScopeService studentScopeService,
                                       StudentDocumentService documentService,
                                       DocumentStorageService documentStorageService) {
        this.studentScopeService = studentScopeService;
        this.documentService = documentService;
        this.documentStorageService = documentStorageService;
    }

    @GetMapping("/{studentId}")
    public List<StudentDocumentResponse> list(@PathVariable("studentId") Integer studentId) {
        studentScopeService.requireStudent(studentId);
        return documentService.listByStudent(studentId).stream()
                .map(StudentDocumentMapper::toResponse)
                .toList();
    }

    @PostMapping(path = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StudentDocumentResponse upload(@PathVariable("studentId") Integer studentId,
                                          @RequestPart("file") MultipartFile file,
                                          @RequestParam(required = false) String category) {
        Student student = studentScopeService.requireStudent(studentId);
        return StudentDocumentMapper.toResponse(documentStorageService.store(student, file, category));
    }

    @GetMapping("/{studentId}/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable("studentId") Integer studentId,
                                             @PathVariable("documentId") Integer documentId) {
        StudentDocument document = documentStorageService.requireDocument(studentId, documentId);
        return buildDownloadResponse(document);
    }

    @DeleteMapping("/{studentId}/{documentId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("studentId") Integer studentId,
                       @PathVariable("documentId") Integer documentId) {
        StudentDocument document = documentStorageService.requireDocument(studentId, documentId);
        documentStorageService.deleteDocument(document);
    }

    private ResponseEntity<Resource> buildDownloadResponse(StudentDocument document) {
        try {
            Path path = documentStorageService.resolvePath(document);
            Resource resource = new UrlResource(path.toUri());
            String contentType = document.getContentType() != null ? document.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao transferir ficheiro");
        }
    }
}

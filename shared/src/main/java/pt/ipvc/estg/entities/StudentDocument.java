package pt.ipvc.estg.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_document")
public class StudentDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    public StudentDocument() {}

    public StudentDocument(Student student, String fileName, String contentType, String storagePath, String category) {
        this.student = student;
        this.fileName = fileName;
        this.contentType = contentType;
        this.storagePath = storagePath;
        this.category = category;
        this.uploadedAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}

package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.estg.entities.StudentDocument;

import java.util.List;
import java.util.Optional;

public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Integer> {
    List<StudentDocument> findByStudent_IdOrderByUploadedAtDesc(Integer studentId);
    Optional<StudentDocument> findByIdAndStudent_Id(Integer id, Integer studentId);
}

package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.StudentDocumentDAOMock;
import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.repositories.StudentDocumentRepository;

import java.util.List;
import java.util.Optional;

public class MockStudentDocumentRepository implements StudentDocumentRepository {
    private final StudentDocumentDAOMock delegate = new StudentDocumentDAOMock();

    @Override
    public Optional<StudentDocument> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<StudentDocument> findByIdAndStudent(Integer id, Integer studentId) {
        return delegate.findByIdAndStudent(id, studentId);
    }

    @Override
    public List<StudentDocument> findByStudentOrderByUploadedAtDesc(Integer studentId) {
        return delegate.findByStudentOrderByUploadedAtDesc(studentId);
    }

    @Override
    public StudentDocument save(StudentDocument document) {
        return delegate.save(document);
    }

    @Override
    public void deleteById(Integer id) {
        delegate.delete(id);
    }
}

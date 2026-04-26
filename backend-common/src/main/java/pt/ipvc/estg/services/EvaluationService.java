package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.EvaluationDAOMock;
import pt.ipvc.estg.dal.mock.MockDataSeeder;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Evaluation - Lógica de negócio para Avaliações
 */
public class EvaluationService {
    
    private final EvaluationDAOMock evaluationDAO;
    
    public EvaluationService() {
        MockDataSeeder.seedAllData();
        this.evaluationDAO = new EvaluationDAOMock();
    }
    
    public Optional<Evaluation> getAvaliacao(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return evaluationDAO.findById(id);
    }
    
    public List<Evaluation> getAllAvaliacoes() {
        return evaluationDAO.findAll();
    }
    
    public List<Evaluation> getAvaliacoesPorEstudante(Integer studentId) {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Student ID deve ser válido");
        }
        return evaluationDAO.findByStudent(studentId);
    }
    
    public List<Evaluation> getAvaliacoesPorCurso(Integer courseId) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID deve ser válido");
        }
        return evaluationDAO.findByCourse(courseId);
    }
    
    public List<Evaluation> getAvaliacoesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser válido");
        }
        return evaluationDAO.findByStatus(status);
    }
    
    public Evaluation criarAvaliacao(Student student, Course course, String examName) {
        if (student == null) {
            throw new IllegalArgumentException("Estudante é obrigatório");
        }
        if (course == null) {
            throw new IllegalArgumentException("Curso é obrigatório");
        }
        if (examName == null || examName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do exame é obrigatório");
        }
        
        Evaluation evaluation = new Evaluation(student, course, examName);
        return evaluationDAO.insert(evaluation);
    }
    
    public Evaluation registarResultado(Integer id, Integer score, String evaluationType, String notes) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (score == null || score < 0 || score > 100) {
            throw new IllegalArgumentException("Pontuação deve estar entre 0-100");
        }
        
        Optional<Evaluation> opt = evaluationDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Avaliação não encontrada");
        }
        
        Evaluation evaluation = opt.get();
        evaluation.setScore(score);
        evaluation.setStatus(score >= 50 ? "passed" : "failed");
        if (evaluationType != null) evaluation.setEvaluationType(evaluationType);
        if (notes != null) evaluation.setNotes(notes);
        evaluation.setEvaluationDate(LocalDate.now());
        
        return evaluationDAO.update(evaluation);
    }
    
    public void eliminarAvaliacao(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (evaluationDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Avaliação não encontrada");
        }
        evaluationDAO.delete(id);
    }
    
    public long contarAvaliacoes() {
        return evaluationDAO.count();
    }
    
    public double calcularTaxaAprovacao() {
        List<Evaluation> todas = getAllAvaliacoes();
        if (todas.isEmpty()) return 0;
        
        long aprovadas = todas.stream()
                .filter(e -> "passed".equals(e.getStatus()))
                .count();
        
        return (aprovadas * 100.0) / todas.size();
    }
}

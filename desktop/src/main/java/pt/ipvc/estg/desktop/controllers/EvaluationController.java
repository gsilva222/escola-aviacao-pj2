package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.services.EvaluationService;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Avaliações na interface Desktop
 */
public class EvaluationController {
    
    private final EvaluationService evaluationService;
    
    public EvaluationController() {
        this.evaluationService = new EvaluationService();
    }
    
    public List<Evaluation> listarAvaliacoes() {
        return evaluationService.getAllAvaliacoes();
    }
    
    public Optional<Evaluation> obterAvaliacao(Integer id) {
        try {
            return evaluationService.getAvaliacao(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter avaliação: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Evaluation> obterAvaliacoesPorEstudante(Integer studentId) {
        return evaluationService.getAvaliacoesPorEstudante(studentId);
    }
    
    public List<Evaluation> obterAvaliacoesPorCurso(Integer courseId) {
        return evaluationService.getAvaliacoesPorCurso(courseId);
    }
    
    public List<Evaluation> obterAvaliacoesPorStatus(String status) {
        return evaluationService.getAvaliacoesPorStatus(status);
    }
    
    public void criarAvaliacao(Student student, Course course, String examName) {
        try {
            Evaluation evaluation = evaluationService.criarAvaliacao(student, course, examName);
            System.out.println("Avaliação criada com sucesso: " + evaluation);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar avaliação: " + e.getMessage());
        }
    }
    
    public void registarResultado(Integer id, Integer score, String evaluationType, String notes) {
        try {
            Evaluation evaluation = evaluationService.registarResultado(id, score, evaluationType, notes);
            System.out.println("Resultado registado com sucesso: " + evaluation);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao registar resultado: " + e.getMessage());
        }
    }
    
    public void eliminarAvaliacao(Integer id) {
        try {
            evaluationService.eliminarAvaliacao(id);
            System.out.println("Avaliação eliminada com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar avaliação: " + e.getMessage());
        }
    }
    
    public long obterTotalAvaliacoes() {
        return evaluationService.contarAvaliacoes();
    }
    
    public double obterTaxaAprovacao() {
        return evaluationService.calcularTaxaAprovacao();
    }
}

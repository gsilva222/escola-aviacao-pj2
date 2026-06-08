package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.FoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.desktop.services.FoStudentService;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.EvaluationService;

import java.util.List;
import java.util.Optional;

public class EvaluationController {

    private final EvaluationService evaluationService;
    private final BoApiService boApi;
    private final FoStudentService foStudentService;

    public EvaluationController() {
        this.evaluationService = new EvaluationService();
        this.boApi = new BoApiService();
        this.foStudentService = new FoStudentService();
    }

    public List<Evaluation> listarAvaliacoes() {
        if (BoDataAccess.useApi()) {
            return boApi.listEvaluations();
        }
        return evaluationService.getAllAvaliacoes();
    }

    public Optional<Evaluation> obterAvaliacao(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getEvaluation(id);
            }
            return evaluationService.getAvaliacao(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter avaliacao: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Evaluation> obterAvaliacoesPorEstudante(Integer studentId) {
        if (FoDataAccess.useApi()) {
            return foStudentService.myEvaluations();
        }
        if (BoDataAccess.useApi()) {
            return boApi.listEvaluationsByStudent(studentId);
        }
        return evaluationService.getAvaliacoesPorEstudante(studentId);
    }

    public List<Evaluation> obterAvaliacoesPorCurso(Integer courseId) {
        if (BoDataAccess.useApi()) {
            return boApi.listEvaluationsByCourse(courseId);
        }
        return evaluationService.getAvaliacoesPorCurso(courseId);
    }

    public List<Evaluation> obterAvaliacoesPorStatus(String status) {
        if (BoDataAccess.useApi()) {
            return boApi.listEvaluationsByStatus(status);
        }
        return evaluationService.getAvaliacoesPorStatus(status);
    }

    public void criarAvaliacao(Student student, Course course, String examName) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createEvaluation(student, course, examName);
                return;
            }
            evaluationService.criarAvaliacao(student, course, examName);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar avaliacao: " + e.getMessage());
        }
    }

    public void registarResultado(Integer id, Integer score, String evaluationType, String notes) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.recordEvaluationResult(id, score, evaluationType, notes);
                return;
            }
            evaluationService.registarResultado(id, score, evaluationType, notes);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao registar resultado: " + e.getMessage());
        }
    }

    public void eliminarAvaliacao(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deleteEvaluation(id);
                return;
            }
            evaluationService.eliminarAvaliacao(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar avaliacao: " + e.getMessage());
        }
    }

    public long obterTotalAvaliacoes() {
        return listarAvaliacoes().size();
    }

    public double obterTaxaAprovacao() {
        if (BoDataAccess.useApi()) {
            List<Evaluation> all = listarAvaliacoes();
            long graded = all.stream().filter(e -> "passed".equals(e.getStatus()) || "failed".equals(e.getStatus())).count();
            if (graded == 0) {
                return 0.0;
            }
            long passed = all.stream().filter(e -> "passed".equals(e.getStatus())).count();
            return (passed * 100.0) / graded;
        }
        return evaluationService.calcularTaxaAprovacao();
    }
}

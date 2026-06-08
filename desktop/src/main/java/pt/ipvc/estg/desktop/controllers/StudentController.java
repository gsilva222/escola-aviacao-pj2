package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.StudentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StudentController {

    private final StudentService studentService;
    private final BoApiService boApi;

    public StudentController() {
        this.studentService = new StudentService();
        this.boApi = new BoApiService();
    }

    public List<Student> listarEstudantes() {
        if (BoDataAccess.useApi()) {
            return boApi.listStudents();
        }
        return studentService.getAllEstudantes();
    }

    public Optional<Student> obterEstudante(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getStudent(id);
            }
            return studentService.getEstudante(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter estudante: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Student> obterEstudantesPorCurso(Integer courseId) {
        if (BoDataAccess.useApi()) {
            return boApi.listStudentsByCourse(courseId);
        }
        return studentService.getEstudantesPorCurso(courseId);
    }

    public List<Student> obterEstudantesPorStatus(String status) {
        if (BoDataAccess.useApi()) {
            return boApi.listStudentsByStatus(status);
        }
        return studentService.getEstudantesPorStatus(status);
    }

    public void criarEstudante(String name, String email, Course course) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createStudent(name, email, course);
                return;
            }
            studentService.criarEstudante(name, email, course);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar estudante: " + e.getMessage());
        }
    }

    public void atualizarEstudante(Integer id, String name, String email, String phone, String nif, LocalDate birthdate) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateStudent(id, name, email, phone, nif, birthdate);
                return;
            }
            studentService.atualizarEstudante(id, name, email, phone, nif, birthdate);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar estudante: " + e.getMessage());
        }
    }

    public void atualizarProgresso(Integer id, Integer progress) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateStudentProgress(id, progress);
                return;
            }
            studentService.atualizarProgresso(id, progress);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar progresso: " + e.getMessage());
        }
    }

    public void atualizarStatus(Integer id, String status) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateStudentStatus(id, status);
                return;
            }
            studentService.atualizarStatus(id, status);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }

    public void eliminarEstudante(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deleteStudent(id);
                return;
            }
            studentService.eliminarEstudante(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar estudante: " + e.getMessage());
        }
    }

    public long obterTotalEstudantes() {
        return listarEstudantes().size();
    }
}

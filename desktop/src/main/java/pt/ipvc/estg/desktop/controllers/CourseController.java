package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.services.CourseService;

import java.util.List;
import java.util.Optional;

public class CourseController {

    private final CourseService courseService;
    private final BoApiService boApi;

    public CourseController() {
        this.courseService = new CourseService();
        this.boApi = new BoApiService();
    }

    public List<Course> listarCursos() {
        if (BoDataAccess.useApi()) {
            return boApi.listCourses();
        }
        return courseService.getAllCursos();
    }

    public Optional<Course> obterCurso(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getCourse(id);
            }
            return courseService.getCurso(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter curso: " + e.getMessage());
            return Optional.empty();
        }
    }

    public void criarCurso(String name, String duration, Integer flightHours, Integer theoreticalHours, Double price) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createCourse(name, duration, flightHours, theoreticalHours, price);
                return;
            }
            courseService.criarCurso(name, duration, flightHours, theoreticalHours, price);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar curso: " + e.getMessage());
        }
    }

    public void atualizarCurso(Integer id, String name, String duration, Integer flightHours, Integer theoreticalHours, Double price) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateCourse(id, name, duration, flightHours, theoreticalHours, price);
                return;
            }
            courseService.atualizarCurso(id, name, duration, flightHours, theoreticalHours, price);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar curso: " + e.getMessage());
        }
    }

    public void eliminarCurso(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deleteCourse(id);
                return;
            }
            courseService.eliminarCurso(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar curso: " + e.getMessage());
        }
    }

    public long obterTotalCursos() {
        return listarCursos().size();
    }
}

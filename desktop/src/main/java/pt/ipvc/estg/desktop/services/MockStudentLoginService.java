package pt.ipvc.estg.desktop.services;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.desktop.controllers.StudentController;
import java.util.Optional;

/**
 * Serviço de autenticação para alunos (FrontOffice)
 * Valida credenciais contra estudantes reais do MockDataSeeder
 */
public class MockStudentLoginService {
    
    private final StudentController studentController;
    
    public MockStudentLoginService() {
        this.studentController = new StudentController();
    }
    
    /**
     * Autentica um aluno usando ID e senha
     * Credenciais válidas: ID do aluno em MockDataSeeder + qualquer senha (para demo)
     * IDs válidos: 1-24 (criados em MockDataSeeder)
     */
    public Optional<Student> authenticate(String studentId, String password) {
        try {
            Integer id = Integer.parseInt(studentId);
            Optional<Student> student = studentController.obterEstudante(id);
            
            // Para fins de demonstração, aceitamos qualquer senha se o aluno existe
            // Em produção, isso seria comparado com hash seguro
            if (student.isPresent()) {
                return student;
            }
        } catch (NumberFormatException e) {
            System.err.println("ID de aluno inválido: " + studentId);
        }
        
        return Optional.empty();
    }
    
    /**
     * Obtém um aluno por ID (sem autenticação)
     */
    public Optional<Student> getStudentById(Integer id) {
        return studentController.obterEstudante(id);
    }
}

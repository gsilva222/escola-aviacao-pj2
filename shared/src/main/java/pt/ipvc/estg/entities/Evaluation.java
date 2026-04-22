package pt.ipvc.estg.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidade Evaluation - Representa uma Avaliação/Exame
 */
@Entity
@Table(name = "evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluation")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id_course", nullable = false)
    private Course course;

    @Column(name = "exam_name", nullable = false, length = 200)
    private String examName;

    @Column(name = "evaluation_date")
    private LocalDate evaluationDate;

    @Column(name = "score")
    private Integer score;

    @Column(name = "max_score")
    private Integer maxScore = 100;

    @Column(name = "status", length = 20)
    private String status; // passed, failed

    @Column(name = "evaluation_type", length = 50)
    private String evaluationType; // theoretical, practical, simulator

    @Column(name = "notes", length = 500)
    private String notes;

    // Construtores
    public Evaluation() {}

    public Evaluation(Student student, Course course, String examName) {
        this.student = student;
        this.course = course;
        this.examName = examName;
        this.evaluationDate = LocalDate.now();
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEvaluationType() { return evaluationType; }
    public void setEvaluationType(String evaluationType) { this.evaluationType = evaluationType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Evaluation{" + "id=" + id + ", examName='" + examName + '\'' + ", score=" + score + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation evaluation = (Evaluation) o;
        return id != null ? id.equals(evaluation.id) : evaluation.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

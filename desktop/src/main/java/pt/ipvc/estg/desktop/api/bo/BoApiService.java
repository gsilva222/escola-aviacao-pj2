package pt.ipvc.estg.desktop.api.bo;

import pt.ipvc.estg.desktop.api.ApiClient;
import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.dto.*;
import pt.ipvc.estg.desktop.api.mappers.EntityMappers;
import pt.ipvc.estg.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class BoApiService {

    private final ApiClient client = new ApiClient();

    // --- Students ---

    public List<Student> listStudents() {
        return mapList(client.getList("/bo/students", StudentResponse.class), EntityMappers::toStudent);
    }

    public List<Student> listStudentsByCourse(Integer courseId) {
        return mapList(client.getList("/bo/students?courseId=" + courseId, StudentResponse.class), EntityMappers::toStudent);
    }

    public List<Student> listStudentsByStatus(String status) {
        return mapList(client.getList("/bo/students?status=" + status, StudentResponse.class), EntityMappers::toStudent);
    }

    public Optional<Student> getStudent(Integer id) {
        return optional(() -> EntityMappers.toStudent(client.get("/bo/students/" + id, StudentResponse.class)));
    }

    public Student createStudent(String name, String email, Course course) {
        StudentRequest req = new StudentRequest(
                name, email, null, null, null, null, "Portugal",
                course.getId(), null, "active", LocalDate.now(), 0, 0.0, 0.0, "up_to_date"
        );
        return EntityMappers.toStudent(client.post("/bo/students", req, StudentResponse.class));
    }

    public Student updateStudent(Integer id, String name, String email, String phone, String nif, LocalDate birthdate) {
        StudentResponse current = client.get("/bo/students/" + id, StudentResponse.class);
        StudentRequest req = new StudentRequest(
                name != null ? name : current.name(),
                email != null ? email : current.email(),
                phone != null ? phone : current.phone(),
                nif != null ? nif : current.nif(),
                birthdate != null ? birthdate : current.birthdate(),
                current.address(),
                current.nationality(),
                current.courseId(),
                current.instructorId(),
                current.status(),
                current.enrollmentDate(),
                current.progress(),
                current.flightHours(),
                current.theoreticalHours(),
                current.paymentStatus()
        );
        return EntityMappers.toStudent(client.put("/bo/students/" + id, req, StudentResponse.class));
    }

    public Student updateStudentProgress(Integer id, Integer progress) {
        StudentResponse current = client.get("/bo/students/" + id, StudentResponse.class);
        StudentRequest req = copyStudentRequest(current, current.status(), progress,
                current.flightHours(), current.theoreticalHours(), current.paymentStatus());
        return EntityMappers.toStudent(client.put("/bo/students/" + id, req, StudentResponse.class));
    }

    public Student updateStudentStatus(Integer id, String status) {
        StudentResponse current = client.get("/bo/students/" + id, StudentResponse.class);
        StudentRequest req = copyStudentRequest(current, status, current.progress(),
                current.flightHours(), current.theoreticalHours(), current.paymentStatus());
        return EntityMappers.toStudent(client.put("/bo/students/" + id, req, StudentResponse.class));
    }

    public void deleteStudent(Integer id) {
        client.delete("/bo/students/" + id);
    }

    // --- Courses ---

    public List<Course> listCourses() {
        return mapList(client.getList("/bo/courses", CourseResponse.class), EntityMappers::toCourse);
    }

    public Optional<Course> getCourse(Integer id) {
        return optional(() -> EntityMappers.toCourse(client.get("/bo/courses/" + id, CourseResponse.class)));
    }

    public Course createCourse(String name, String duration, Integer flightHours, Integer theoreticalHours, Double price) {
        CourseRequest req = new CourseRequest(name, duration, flightHours, theoreticalHours, price, null);
        return EntityMappers.toCourse(client.post("/bo/courses", req, CourseResponse.class));
    }

    public Course updateCourse(Integer id, String name, String duration, Integer flightHours, Integer theoreticalHours, Double price) {
        CourseResponse current = client.get("/bo/courses/" + id, CourseResponse.class);
        CourseRequest req = new CourseRequest(name, duration, flightHours, theoreticalHours, price, current.description());
        return EntityMappers.toCourse(client.put("/bo/courses/" + id, req, CourseResponse.class));
    }

    public void deleteCourse(Integer id) {
        client.delete("/bo/courses/" + id);
    }

    // --- Flights ---

    public List<Flight> listFlights() {
        return mapList(client.getList("/bo/flights", FlightResponse.class), EntityMappers::toFlight);
    }

    public List<Flight> listFlightsByStudent(Integer studentId) {
        return mapList(client.getList("/bo/flights?studentId=" + studentId, FlightResponse.class), EntityMappers::toFlight);
    }

    public List<Flight> listFlightsByInstructor(Integer instructorId) {
        return mapList(client.getList("/bo/flights?instructorId=" + instructorId, FlightResponse.class), EntityMappers::toFlight);
    }

    public List<Flight> listFlightsByStatus(String status) {
        return mapList(client.getList("/bo/flights?status=" + status, FlightResponse.class), EntityMappers::toFlight);
    }

    public Optional<Flight> getFlight(Integer id) {
        return optional(() -> EntityMappers.toFlight(client.get("/bo/flights/" + id, FlightResponse.class)));
    }

    public Flight createFlight(LocalDate date, Student student, Instructor instructor, Aircraft aircraft) {
        FlightRequest req = new FlightRequest(
                date, LocalTime.of(10, 0), 1.0,
                student.getId(), instructor.getId(), aircraft.getId(),
                "LPPT", "LPPT", "training", "scheduled", null, null, null
        );
        return EntityMappers.toFlight(client.post("/bo/flights", req, FlightResponse.class));
    }

    public Flight updateFlight(Integer id, Double duration, String origin, String destination,
                               String flightType, String objectives, String grade) {
        FlightResponse current = client.get("/bo/flights/" + id, FlightResponse.class);
        FlightRequest req = new FlightRequest(
                current.flightDate(), current.flightTime(),
                duration != null ? duration : current.duration(),
                current.studentId(), current.instructorId(), current.aircraftId(),
                origin != null ? origin : current.origin(),
                destination != null ? destination : current.destination(),
                flightType != null ? flightType : current.flightType(),
                current.status(),
                objectives != null ? objectives : current.objectives(),
                current.notes(),
                grade != null ? grade : current.grade()
        );
        return EntityMappers.toFlight(client.put("/bo/flights/" + id, req, FlightResponse.class));
    }

    public Flight completeFlight(Integer id, Double duration, String grade) {
        FlightResponse current = client.get("/bo/flights/" + id, FlightResponse.class);
        FlightRequest req = new FlightRequest(
                current.flightDate(), current.flightTime(),
                duration != null ? duration : current.duration(),
                current.studentId(), current.instructorId(), current.aircraftId(),
                current.origin(), current.destination(), current.flightType(),
                "completed", current.objectives(), current.notes(),
                grade != null ? grade : current.grade()
        );
        return EntityMappers.toFlight(client.put("/bo/flights/" + id, req, FlightResponse.class));
    }

    public void deleteFlight(Integer id) {
        client.delete("/bo/flights/" + id);
    }

    // --- Instructors ---

    public List<Instructor> listInstructors() {
        return mapList(client.getList("/bo/instructors", InstructorResponse.class), EntityMappers::toInstructor);
    }

    public Optional<Instructor> getInstructor(Integer id) {
        return optional(() -> EntityMappers.toInstructor(client.get("/bo/instructors/" + id, InstructorResponse.class)));
    }

    public List<Instructor> listInstructorsByStatus(String status) {
        return mapList(client.getList("/bo/instructors?status=" + status, InstructorResponse.class), EntityMappers::toInstructor);
    }

    public Instructor createInstructor(String name, String license, String specialization) {
        InstructorRequest req = new InstructorRequest(name, license, specialization, 0, "active", null, null);
        return EntityMappers.toInstructor(client.post("/bo/instructors", req, InstructorResponse.class));
    }

    public Instructor updateInstructor(Integer id, String name, String license, String specialization, String email, String phone) {
        InstructorResponse current = client.get("/bo/instructors/" + id, InstructorResponse.class);
        InstructorRequest req = new InstructorRequest(
                name != null ? name : current.name(),
                license != null ? license : current.license(),
                specialization != null ? specialization : current.specialization(),
                current.flightHours(),
                current.status(),
                email != null ? email : current.email(),
                phone != null ? phone : current.phone()
        );
        return EntityMappers.toInstructor(client.put("/bo/instructors/" + id, req, InstructorResponse.class));
    }

    public Instructor updateInstructorStatus(Integer id, String status) {
        InstructorResponse current = client.get("/bo/instructors/" + id, InstructorResponse.class);
        InstructorRequest req = new InstructorRequest(
                current.name(), current.license(), current.specialization(),
                current.flightHours(), status, current.email(), current.phone()
        );
        return EntityMappers.toInstructor(client.put("/bo/instructors/" + id, req, InstructorResponse.class));
    }

    public void deleteInstructor(Integer id) {
        client.delete("/bo/instructors/" + id);
    }

    // --- Aircraft ---

    public List<Aircraft> listAircraft() {
        return mapList(client.getList("/bo/aircraft", AircraftResponse.class), EntityMappers::toAircraft);
    }

    public Optional<Aircraft> getAircraft(Integer id) {
        return optional(() -> EntityMappers.toAircraft(client.get("/bo/aircraft/" + id, AircraftResponse.class)));
    }

    public List<Aircraft> listAircraftByStatus(String status) {
        return mapList(client.getList("/bo/aircraft?status=" + status, AircraftResponse.class), EntityMappers::toAircraft);
    }

    public Aircraft createAircraft(String registration, String model, String type) {
        AircraftRequest req = new AircraftRequest(registration, model, type, null, "operational",
                0.0, null, null, null, 100, null);
        return EntityMappers.toAircraft(client.post("/bo/aircraft", req, AircraftResponse.class));
    }

    public Aircraft updateAircraft(Integer id, String model, Integer year, String location, Integer fuelLevel) {
        AircraftResponse current = client.get("/bo/aircraft/" + id, AircraftResponse.class);
        AircraftRequest req = new AircraftRequest(
                current.registration(),
                model != null ? model : current.model(),
                current.type(),
                year != null ? year : current.manufYear(),
                current.status(),
                current.flightHours(),
                current.lastMaintenance(),
                current.nextMaintenance(),
                location != null ? location : current.location(),
                fuelLevel != null ? fuelLevel : current.fuelLevel(),
                current.notes()
        );
        return EntityMappers.toAircraft(client.put("/bo/aircraft/" + id, req, AircraftResponse.class));
    }

    public Aircraft updateAircraftStatus(Integer id, String status) {
        AircraftResponse current = client.get("/bo/aircraft/" + id, AircraftResponse.class);
        AircraftRequest req = copyAircraftRequest(current, status, current.nextMaintenance());
        return EntityMappers.toAircraft(client.put("/bo/aircraft/" + id, req, AircraftResponse.class));
    }

    public Aircraft updateAircraftMaintenance(Integer id, LocalDate nextMaintenance) {
        AircraftResponse current = client.get("/bo/aircraft/" + id, AircraftResponse.class);
        AircraftRequest req = copyAircraftRequest(current, current.status(), nextMaintenance);
        return EntityMappers.toAircraft(client.put("/bo/aircraft/" + id, req, AircraftResponse.class));
    }

    public void deleteAircraft(Integer id) {
        client.delete("/bo/aircraft/" + id);
    }

    // --- Maintenance ---

    public List<Maintenance> listMaintenance() {
        return mapList(client.getList("/bo/maintenance", MaintenanceResponse.class), EntityMappers::toMaintenance);
    }

    public Optional<Maintenance> getMaintenance(Integer id) {
        return optional(() -> EntityMappers.toMaintenance(client.get("/bo/maintenance/" + id, MaintenanceResponse.class)));
    }

    public List<Maintenance> listMaintenanceByAircraft(Integer aircraftId) {
        return mapList(client.getList("/bo/maintenance?aircraftId=" + aircraftId, MaintenanceResponse.class), EntityMappers::toMaintenance);
    }

    public List<Maintenance> listMaintenanceByStatus(String status) {
        return mapList(client.getList("/bo/maintenance?status=" + status, MaintenanceResponse.class), EntityMappers::toMaintenance);
    }

    public Maintenance createMaintenance(Aircraft aircraft, String type, String description) {
        return createMaintenance(aircraft, type, description, null);
    }

    public Maintenance createMaintenance(Aircraft aircraft, String type, String description, LocalDate estimatedEndDate) {
        MaintenanceRequest req = new MaintenanceRequest(
                aircraft.getId(), type, description, null,
                LocalDate.now(), estimatedEndDate, null, "scheduled", "medium", 0.0, null
        );
        return EntityMappers.toMaintenance(client.post("/bo/maintenance", req, MaintenanceResponse.class));
    }

    public Maintenance updateMaintenance(Integer id, String technician, LocalDate estimatedEnd,
                                         String priority, Double cost, String status) {
        MaintenanceResponse current = client.get("/bo/maintenance/" + id, MaintenanceResponse.class);
        MaintenanceRequest req = new MaintenanceRequest(
                current.aircraftId(), current.maintenanceType(), current.description(),
                technician != null ? technician : current.technician(),
                current.startDate(),
                estimatedEnd != null ? estimatedEnd : current.estimatedEndDate(),
                current.actualEndDate(),
                status != null ? status : current.status(),
                priority != null ? priority : current.priority(),
                cost != null ? cost : current.cost(),
                current.notes()
        );
        return EntityMappers.toMaintenance(client.put("/bo/maintenance/" + id, req, MaintenanceResponse.class));
    }

    public Maintenance completeMaintenance(Integer id, LocalDate actualEndDate) {
        MaintenanceResponse current = client.get("/bo/maintenance/" + id, MaintenanceResponse.class);
        MaintenanceRequest req = new MaintenanceRequest(
                current.aircraftId(), current.maintenanceType(), current.description(),
                current.technician(), current.startDate(), current.estimatedEndDate(),
                actualEndDate != null ? actualEndDate : LocalDate.now(),
                "completed", current.priority(), current.cost(), current.notes()
        );
        return EntityMappers.toMaintenance(client.put("/bo/maintenance/" + id, req, MaintenanceResponse.class));
    }

    public void deleteMaintenance(Integer id) {
        client.delete("/bo/maintenance/" + id);
    }

    // --- Evaluations ---

    public List<Evaluation> listEvaluations() {
        return mapList(client.getList("/bo/evaluations", EvaluationResponse.class), EntityMappers::toEvaluation);
    }

    public Optional<Evaluation> getEvaluation(Integer id) {
        return optional(() -> EntityMappers.toEvaluation(client.get("/bo/evaluations/" + id, EvaluationResponse.class)));
    }

    public List<Evaluation> listEvaluationsByStudent(Integer studentId) {
        return mapList(client.getList("/bo/evaluations?studentId=" + studentId, EvaluationResponse.class), EntityMappers::toEvaluation);
    }

    public List<Evaluation> listEvaluationsByCourse(Integer courseId) {
        return mapList(client.getList("/bo/evaluations?courseId=" + courseId, EvaluationResponse.class), EntityMappers::toEvaluation);
    }

    public List<Evaluation> listEvaluationsByStatus(String status) {
        return mapList(client.getList("/bo/evaluations?status=" + status, EvaluationResponse.class), EntityMappers::toEvaluation);
    }

    public Evaluation createEvaluation(Student student, Course course, String examName) {
        EvaluationRequest req = new EvaluationRequest(
                student.getId(), course.getId(), examName,
                LocalDate.now(), null, 100, "scheduled", "theoretical", null
        );
        return EntityMappers.toEvaluation(client.post("/bo/evaluations", req, EvaluationResponse.class));
    }

    public Evaluation recordEvaluationResult(Integer id, Integer score, String evaluationType, String notes) {
        EvaluationResponse current = client.get("/bo/evaluations/" + id, EvaluationResponse.class);
        String status = score != null && score >= 50 ? "passed" : "failed";
        EvaluationRequest req = new EvaluationRequest(
                current.studentId(), current.courseId(), current.examName(),
                current.evaluationDate() != null ? current.evaluationDate() : LocalDate.now(),
                score, current.maxScore() != null ? current.maxScore() : 100,
                status,
                evaluationType != null ? evaluationType : current.evaluationType(),
                notes != null ? notes : current.notes()
        );
        return EntityMappers.toEvaluation(client.put("/bo/evaluations/" + id, req, EvaluationResponse.class));
    }

    public void deleteEvaluation(Integer id) {
        client.delete("/bo/evaluations/" + id);
    }

    // --- Payments ---

    public List<Payment> listPayments() {
        return mapList(client.getList("/bo/payments", PaymentResponse.class), EntityMappers::toPayment);
    }

    public Optional<Payment> getPayment(Integer id) {
        return optional(() -> EntityMappers.toPayment(client.get("/bo/payments/" + id, PaymentResponse.class)));
    }

    public List<Payment> listPaymentsByStudent(Integer studentId) {
        return mapList(client.getList("/bo/payments?studentId=" + studentId, PaymentResponse.class), EntityMappers::toPayment);
    }

    public List<Payment> listPaymentsByStatus(String status) {
        return mapList(client.getList("/bo/payments?status=" + status, PaymentResponse.class), EntityMappers::toPayment);
    }

    public Payment createPayment(Student student, String description, Double amount, LocalDate dueDate) {
        PaymentRequest req = new PaymentRequest(
                student.getId(), description, amount, dueDate, null, "pending", null, null
        );
        return EntityMappers.toPayment(client.post("/bo/payments", req, PaymentResponse.class));
    }

    public Payment registerPayment(Integer id, LocalDate paidDate, String paymentMethod) {
        PaymentResponse current = client.get("/bo/payments/" + id, PaymentResponse.class);
        PaymentRequest req = new PaymentRequest(
                current.studentId(), current.description(), current.amount(),
                current.dueDate(), paidDate != null ? paidDate : LocalDate.now(),
                "paid", paymentMethod, current.notes()
        );
        return EntityMappers.toPayment(client.put("/bo/payments/" + id, req, PaymentResponse.class));
    }

    public void deletePayment(Integer id) {
        client.delete("/bo/payments/" + id);
    }

    // --- Users ---

    public List<UserAccountResponse> listUsers() {
        return client.getArray("/bo/users", UserAccountResponse.class);
    }

    public UserAccountResponse createUser(String username, String password, String role,
                                          Integer studentId, String staffProfile) {
        CreateUserRequest req = new CreateUserRequest(username, password, role, studentId, staffProfile);
        return client.post("/bo/users", req, UserAccountResponse.class);
    }

    public UserAccountResponse setUserActive(Integer id, boolean active) {
        return client.put("/bo/users/" + id + "/active", new UpdateUserActiveRequest(active), UserAccountResponse.class);
    }

    public UserAccountResponse updateUserStaffProfile(Integer id, String staffProfile) {
        return client.put("/bo/users/" + id + "/staff-profile",
                new UpdateStaffProfileRequest(staffProfile), UserAccountResponse.class);
    }

    // --- Reports ---

    public ReportsSummaryResponse getReportsSummary() {
        return client.get("/bo/reports/summary", ReportsSummaryResponse.class);
    }

    public BoDashboardResponse getDashboard() {
        return client.get("/bo/dashboard", BoDashboardResponse.class);
    }

    // --- Student documents ---

    public List<StudentDocumentResponse> listStudentDocuments(Integer studentId) {
        return client.getArray("/bo/student-documents/" + studentId, StudentDocumentResponse.class);
    }

    public StudentDocumentResponse uploadStudentDocument(Integer studentId, java.nio.file.Path filePath, String category) {
        return client.uploadMultipart("/bo/student-documents/" + studentId, filePath, category, StudentDocumentResponse.class);
    }

    public java.nio.file.Path downloadStudentDocument(Integer studentId, Integer documentId, String fileName)
            throws java.io.IOException {
        byte[] bytes = client.downloadBytes("/bo/student-documents/" + studentId + "/" + documentId + "/download");
        java.nio.file.Path temp = java.nio.file.Files.createTempFile(
                "aeroschool-bo-doc-", "-" + fileName.replaceAll("[^a-zA-Z0-9._-]", "_"));
        java.nio.file.Files.write(temp, bytes);
        return temp;
    }

    public void deleteStudentDocument(Integer studentId, Integer documentId) {
        client.delete("/bo/student-documents/" + studentId + "/" + documentId);
    }

    // --- helpers ---

    private StudentRequest copyStudentRequest(StudentResponse current, String status, Integer progress,
                                              Double flightHours, Double theoreticalHours, String paymentStatus) {
        return new StudentRequest(
                current.name(), current.email(), current.phone(), current.nif(), current.birthdate(),
                current.address(), current.nationality(), current.courseId(), current.instructorId(),
                status, current.enrollmentDate(), progress, flightHours, theoreticalHours, paymentStatus
        );
    }

    private AircraftRequest copyAircraftRequest(AircraftResponse current, String status, LocalDate nextMaintenance) {
        return new AircraftRequest(
                current.registration(), current.model(), current.type(), current.manufYear(),
                status, current.flightHours(), current.lastMaintenance(), nextMaintenance,
                current.location(), current.fuelLevel(), current.notes()
        );
    }

    private <E, T> List<T> mapList(List<E> source, Function<E, T> mapper) {
        return source.stream().map(mapper).toList();
    }

    private <T> Optional<T> optional(Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (ApiException ex) {
            if (ex.getStatusCode() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }
}

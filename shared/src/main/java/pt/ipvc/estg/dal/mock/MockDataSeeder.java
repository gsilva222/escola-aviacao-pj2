package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * MockDataSeeder - Inicializa dados de teste reais e representativos
 * Cria 20+ students, 50+ flights, 30+ evaluations, etc
 */
public class MockDataSeeder {
    
    private static final Random random = new Random(42); // Seed fixo para dados consistentes
    
    private static final String[] firstNames = {
        "João", "Maria", "Pedro", "Ana", "Carlos", "Francisca", "Tiago", "Joana",
        "Miguel", "Catarina", "Ricardo", "Marta", "André", "Sofia", "Paulo", "Inês",
        "Fernando", "Cristina", "Rui", "Filipa", "Gonçalo", "Raquel", "Jorge", "Susana"
    };
    
    private static final String[] lastNames = {
        "Silva", "Santos", "Oliveira", "Ferreira", "Golden", "Rodrigues", "Martins", "Costa",
        "Sousa", "Gonçalves", "Pereira", "Almeida", "Rocha", "Carvalho", "Araújo", "Teixeira",
        "Mendes", "Pinto", "Machado", "Gomes", "Barbosa", "Ribeiro", "Freitas", "Dias"
    };
    
    private static final String[] domains = {
        "@email.com", "@gmail.com", "@hotmail.com", "@outlook.com", "@student.edu"
    };
    
    private static final String[] nationalities = {
        "Portuguesa", "Espanhola", "Francês", "Italiano", "Alemão", "Holandês", 
        "Belga", "Austríaco", "Sueco", "Dinamarquês", "Polonês", "Checo"
    };
    
    private static final String[] addresses = {
        "Rua da República, 123, Porto",
        "Av. Paulista, 456, Braga",
        "Rua 25 de Abril, 789, Covilhã",
        "Estrada Nacional 2, km 100, Viseu",
        "Rua da Liberdade, 321, Guarda",
        "Av. Dom João II, 654, Lisboa",
        "Rua do Comércio, 987, Funchal",
        "Estrada Principal, 111, Évora",
        "Rua São João, 222, Leiria",
        "Av. da Boavista, 333, Porto"
    };
    
    private static final String[] statuses = {
        "active", "active", "active", "active", "active", "active", "active", // 70% ativo
        "suspended", "suspended", // 20% suspenso
        "completed" // 10% concluído
    };
    
    private static final String[] paymentStatuses = {
        "up_to_date", "up_to_date", "up_to_date", "up_to_date", "up_to_date", // 60% regularizado
        "pending", "pending", // 25% pendente
        "overdue" // 15% em atraso
    };
    
    private static final String[] evaluationTypes = {
        "Teórico PPL", "Prático PPL", "Teórico CPL", "Prático CPL",
        "Teórico IR", "Prático IR", "Teórico ATPL", "Prático ATPL"
    };
    
    private static final String[] evaluationStatuses = {
        "passed", "passed", "passed", "passed", "passed", "passed", // 70% aprovado
        "failed", "failed", // 20% reprovado
        "scheduled" // 10% agendado
    };
    
    /**
     * Popula StudentDAOMock com 20+ estudantes
     */
    public static void seedStudents() {
        StudentDAOMock studentDAO = new StudentDAOMock();
        CourseDAOMock courseDAO = new CourseDAOMock();
        InstructorDAOMock instructorDAO = new InstructorDAOMock();
        
        List<Course> courses = courseDAO.findAll();
        List<Instructor> instructors = instructorDAO.findAll();
        
        if (courses.isEmpty() || instructors.isEmpty()) {
            System.err.println("[WARN] Courses ou Instructors vazios. Abortando seed de Students.");
            return;
        }
        
        int nifCounter = 100000000;
        
        for (int i = 0; i < 24; i++) {
            String firstName = firstNames[i % firstNames.length];
            String lastName = lastNames[(i + i/7) % lastNames.length];
            String name = firstName + " " + lastName;
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + domains[i % domains.length];
            
            // Evitar duplicatas de email (substituir por número)
            email = email.replace("@", (i+1) + "@");
            
            String phone = "+351 " + (900 + i) + " " + (100000 + random.nextInt(900000));
            String nif = String.valueOf(nifCounter++);
            String status = statuses[random.nextInt(statuses.length)];
            String paymentStatus = paymentStatuses[random.nextInt(paymentStatuses.length)];
            String nationality = nationalities[random.nextInt(nationalities.length)];
            String address = addresses[random.nextInt(addresses.length)];
            
            Course course = courses.get(i % courses.size());
            Instructor instructor = instructors.get(random.nextInt(instructors.size()));
            
            LocalDate birthdate = LocalDate.of(
                1995 + random.nextInt(10),
                1 + random.nextInt(12),
                1 + random.nextInt(28)
            );
            
            LocalDate enrollmentDate = LocalDate.now().minusMonths(random.nextInt(12));
            
            Student student = new Student(name, email, course);
            student.setPhone(phone);
            student.setNif(nif);
            student.setBirthdate(birthdate);
            student.setNationality(nationality);
            student.setAddress(address);
            student.setInstructor(instructor);
            student.setStatus(status);
            student.setPaymentStatus(paymentStatus);
            student.setEnrollmentDate(enrollmentDate);
            student.setProgress(random.nextInt(101));
            student.setFlightHours(random.nextDouble() * 150);
            student.setTheoreticalHours(random.nextDouble() * 100);
            
            studentDAO.insert(student);
        }
        
        System.out.println("[SEEDER] ✅ 24 Students inicializados");
    }
    
    /**
     * Popula FlightDAOMock com 50+ voos relacionados aos students
     */
    public static void seedFlights() {
        FlightDAOMock flightDAO = new FlightDAOMock();
        StudentDAOMock studentDAO = new StudentDAOMock();
        InstructorDAOMock instructorDAO = new InstructorDAOMock();
        AircraftDAOMock aircraftDAO = new AircraftDAOMock();
        
        List<Student> students = studentDAO.findAll();
        List<Instructor> instructors = instructorDAO.findAll();
        List<Aircraft> aircrafts = aircraftDAO.findAll();
        
        if (students.isEmpty() || instructors.isEmpty() || aircrafts.isEmpty()) {
            System.err.println("[WARN] Students/Instructors/Aircrafts vazios. Abortando seed de Flights.");
            return;
        }
        
        String[] flightStatuses = {"completed", "completed", "completed", "scheduled", "cancelled"};
        
        int flightCount = 0;
        for (Student student : students) {
            // Cada student tem 2-3 voos
            int flightsPerStudent = 2 + random.nextInt(2);
            for (int j = 0; j < flightsPerStudent; j++) {
                LocalDate flightDate = LocalDate.now().minusDays(random.nextInt(90));
                double duration = 0.5 + random.nextDouble() * 3.5; // 0.5 a 4 horas
                String status = flightStatuses[random.nextInt(flightStatuses.length)];
                
                Flight flight = new Flight();
                flight.setStudent(student);
                flight.setInstructor(instructors.get(random.nextInt(instructors.size())));
                flight.setAircraft(aircrafts.get(random.nextInt(aircrafts.size())));
                flight.setFlightDate(flightDate);
                flight.setDuration(duration);
                flight.setStatus(status);
                flight.setFlightType("Training");
                flight.setOrigin("Porto");
                flight.setDestination("Covilhã");
                
                flightDAO.insert(flight);
                flightCount++;
            }
        }
        
        System.out.println("[SEEDER] ✅ " + flightCount + " Flights inicializados");
    }
    
    /**
     * Popula EvaluationDAOMock com 30+ avaliações
     */
    public static void seedEvaluations() {
        EvaluationDAOMock evaluationDAO = new EvaluationDAOMock();
        StudentDAOMock studentDAO = new StudentDAOMock();
        CourseDAOMock courseDAO = new CourseDAOMock();
        InstructorDAOMock instructorDAO = new InstructorDAOMock();
        
        List<Student> students = studentDAO.findAll();
        List<Course> courses = courseDAO.findAll();
        List<Instructor> instructors = instructorDAO.findAll();
        
        if (students.isEmpty() || courses.isEmpty() || instructors.isEmpty()) {
            System.err.println("[WARN] Students/Courses/Instructors vazios. Abortando seed de Evaluations.");
            return;
        }
        
        int evalCount = 0;
        for (Student student : students) {
            // Cada student tem 1-3 avaliações
            int evals = 1 + random.nextInt(3);
            for (int j = 0; j < evals; j++) {
                LocalDate evalDate = LocalDate.now().minusDays(random.nextInt(90));
                double grade = 10 + random.nextDouble() * 10; // 10-20
                String type = evaluationTypes[random.nextInt(evaluationTypes.length)];
                String status = evaluationStatuses[random.nextInt(evaluationStatuses.length)];
                
                Evaluation evaluation = new Evaluation();
                evaluation.setStudent(student);
                evaluation.setCourse(student.getCourse());
                evaluation.setInstructor(instructors.get(random.nextInt(instructors.size())));
                evaluation.setEvaluationDate(evalDate);
                evaluation.setGrade(grade);
                evaluation.setEvaluationType(type);
                evaluation.setStatus(status);
                evaluation.setComments("Avaliação de " + type);
                
                evaluationDAO.insert(evaluation);
                evalCount++;
            }
        }
        
        System.out.println("[SEEDER] ✅ " + evalCount + " Evaluations inicializados");
    }
    
    /**
     * Popula PaymentDAOMock com pagamentos
     */
    public static void seedPayments() {
        PaymentDAOMock paymentDAO = new PaymentDAOMock();
        StudentDAOMock studentDAO = new StudentDAOMock();
        
        List<Student> students = studentDAO.findAll();
        
        if (students.isEmpty()) {
            System.err.println("[WARN] Students vazio. Abortando seed de Payments.");
            return;
        }
        
        String[] paymentMethods = {"Credit Card", "Bank Transfer", "Cash", "Check"};
        String[] paymentPurposes = {"Tuition", "Flight Hours", "Exam Fee", "Installation"};
        String[] paymentStatuses = {"completed", "completed", "completed", "pending", "overdue"};
        
        int paymentCount = 0;
        for (Student student : students) {
            // Cada student tem 2-4 pagamentos
            int payments = 2 + random.nextInt(3);
            for (int j = 0; j < payments; j++) {
                double amount = 500 + random.nextDouble() * 2000;
                LocalDate paymentDate = LocalDate.now().minusDays(random.nextInt(365));
                String method = paymentMethods[random.nextInt(paymentMethods.length)];
                String purpose = paymentPurposes[random.nextInt(paymentPurposes.length)];
                String status = paymentStatuses[random.nextInt(paymentStatuses.length)];
                
                Payment payment = new Payment();
                payment.setStudent(student);
                payment.setAmount(amount);
                payment.setPaymentDate(paymentDate);
                payment.setPaymentMethod(method);
                payment.setPaymentPurpose(purpose);
                payment.setStatus(status);
                payment.setDescription(purpose + " - " + student.getName());
                
                paymentDAO.insert(payment);
                paymentCount++;
            }
        }
        
        System.out.println("[SEEDER] ✅ " + paymentCount + " Payments inicializados");
    }
    
    /**
     * Popula MaintenanceDAOMock com manutenções
     */
    public static void seedMaintenance() {
        MaintenanceDAOMock maintenanceDAO = new MaintenanceDAOMock();
        AircraftDAOMock aircraftDAO = new AircraftDAOMock();
        
        List<Aircraft> aircrafts = aircraftDAO.findAll();
        
        if (aircrafts.isEmpty()) {
            System.err.println("[WARN] Aircrafts vazio. Abortando seed de Maintenance.");
            return;
        }
        
        String[] maintenanceTypes = {
            "Regular Service", "Engine Overhaul", "Inspection", "Oil Change",
            "Replacement", "Repair", "Calibration", "Safety Check"
        };
        String[] maintenanceStatuses = {"completed", "completed", "in_progress", "scheduled", "scheduled"};
        
        int maintenanceCount = 0;
        for (Aircraft aircraft : aircrafts) {
            // Cada aircraft tem 1-2 manutenções
            int mains = 1 + random.nextInt(2);
            for (int j = 0; j < mains; j++) {
                LocalDate mainDate = LocalDate.now().minusDays(random.nextInt(180));
                double cost = 500 + random.nextDouble() * 5000;
                String type = maintenanceTypes[random.nextInt(maintenanceTypes.length)];
                String status = maintenanceStatuses[random.nextInt(maintenanceStatuses.length)];
                int hours = 2 + random.nextInt(40);
                
                Maintenance maintenance = new Maintenance();
                maintenance.setAircraft(aircraft);
                maintenance.setMaintenanceDate(mainDate);
                maintenance.setMaintenanceType(type);
                maintenance.setDescription(type + " para " + aircraft.getRegistration());
                maintenance.setCompletionDate(mainDate.plusDays(random.nextInt(30)));
                maintenance.setStatus(status);
                maintenance.setLaborHours(hours);
                maintenance.setCost(cost);
                
                maintenanceDAO.insert(maintenance);
                maintenanceCount++;
            }
        }
        
        System.out.println("[SEEDER] ✅ " + maintenanceCount + " Maintenance inicializados");
    }
    
    /**
     * Método principal para fazer seed de TODOS os dados
     */
    public static void seedAllData() {
        System.out.println("\n🌱 [SEEDER] Iniciando seeding de dados mock...");
        seedStudents();
        seedFlights();
        seedEvaluations();
        seedPayments();
        seedMaintenance();
        System.out.println("✨ [SEEDER] Seeding completo!\n");
    }
}

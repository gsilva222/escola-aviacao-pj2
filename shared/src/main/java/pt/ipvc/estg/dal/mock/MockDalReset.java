package pt.ipvc.estg.dal.mock;

/**
 * Utilitario para isolar testes que partilham DAOs em memoria estaticos.
 */
public final class MockDalReset {

    private MockDalReset() {
    }

    public static void resetAll() {
        StudentDAOMock.reset();
        CourseDAOMock.reset();
        InstructorDAOMock.reset();
        AircraftDAOMock.reset();
        FlightDAOMock.reset();
        PaymentDAOMock.reset();
        EvaluationDAOMock.reset();
        MaintenanceDAOMock.reset();
        PerfilDAOMock.reset();
        UserAccountDAOMock.reset();
        StudentDocumentDAOMock.reset();
    }
}

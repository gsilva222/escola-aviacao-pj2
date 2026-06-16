package pt.ipvc.estg.desktop.services;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.FoDataAccess;
import pt.ipvc.estg.desktop.api.dto.FoDashboardResponse;
import pt.ipvc.estg.desktop.api.dto.FoHoursSummaryResponse;
import pt.ipvc.estg.desktop.api.dto.PaymentSummaryResponse;
import pt.ipvc.estg.desktop.api.dto.StudentDocumentResponse;
import pt.ipvc.estg.desktop.api.fo.FoApiService;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class FoStudentService {

    private final FoApiService foApi = new FoApiService();

    public boolean useApi() {
        return FoDataAccess.useApi();
    }

    public Optional<Student> refreshProfile() {
        if (!useApi()) {
            return Optional.empty();
        }
        try {
            return Optional.of(foApi.getProfile());
        } catch (ApiException ex) {
            throw new RuntimeException("Erro ao carregar perfil: " + ex.getMessage(), ex);
        }
    }

    public Student updateProfile(String phone, String address, String nationality) {
        try {
            return foApi.updateProfile(phone, address, nationality);
        } catch (ApiException ex) {
            throw new RuntimeException("Erro ao guardar perfil: " + ex.getMessage(), ex);
        }
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!useApi()) {
            throw new RuntimeException("Alteracao de password indisponivel em modo offline.");
        }
        try {
            new DesktopAuthService().changePassword(currentPassword, newPassword);
        } catch (ApiException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public FoDashboardResponse getDashboard() {
        return foApi.getDashboard();
    }

    public List<Flight> myFlights() {
        return foApi.listFlights(null);
    }

    public List<Flight> mySchedule() {
        return foApi.listSchedule();
    }

    public FoHoursSummaryResponse getHoursSummary() {
        return foApi.getHoursSummary();
    }

    public List<Evaluation> myEvaluations() {
        return foApi.listEvaluations();
    }

    public List<Payment> myPayments() {
        return foApi.listPayments(null);
    }

    public PaymentSummaryResponse getPaymentSummary() {
        return foApi.getPaymentSummary();
    }

    public List<StudentDocumentResponse> myDocuments() {
        return foApi.listDocuments();
    }

    public StudentDocumentResponse uploadDocument(Path path, String category) {
        return foApi.uploadDocument(path, category);
    }

    public Path downloadDocument(Integer id, String fileName) {
        try {
            return foApi.downloadDocument(id, fileName);
        } catch (Exception ex) {
            throw new RuntimeException("Erro no download: " + ex.getMessage(), ex);
        }
    }

    public void deleteDocument(Integer id) {
        foApi.deleteDocument(id);
    }
}

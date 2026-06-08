package pt.ipvc.estg.desktop.api.fo;

import pt.ipvc.estg.desktop.api.ApiClient;
import pt.ipvc.estg.desktop.api.dto.*;
import pt.ipvc.estg.desktop.api.mappers.EntityMappers;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class FoApiService {

    private final ApiClient client = new ApiClient();

    public Student getProfile() {
        return EntityMappers.toStudent(client.get("/fo/me", StudentResponse.class));
    }

    public Student updateProfile(String phone, String address, String nationality) {
        FoProfileUpdateRequest request = new FoProfileUpdateRequest(phone, address, nationality);
        return EntityMappers.toStudent(client.put("/fo/me", request, StudentResponse.class));
    }

    public FoDashboardResponse getDashboard() {
        return client.get("/fo/dashboard", FoDashboardResponse.class);
    }

    public List<Flight> listFlights(String status) {
        String path = status == null || status.isBlank()
                ? "/fo/flights?size=500"
                : "/fo/flights?status=" + status + "&size=500";
        return mapList(client.getList(path, FlightResponse.class), EntityMappers::toFlight);
    }

    public List<Flight> listSchedule() {
        return mapList(client.getArray("/fo/schedule", FlightResponse.class), EntityMappers::toFlight);
    }

    public FoHoursSummaryResponse getHoursSummary() {
        return client.get("/fo/hours", FoHoursSummaryResponse.class);
    }

    public List<Evaluation> listEvaluations() {
        return mapList(client.getList("/fo/evaluations", EvaluationResponse.class), EntityMappers::toEvaluation);
    }

    public List<Payment> listPayments(String status) {
        String path = status == null || status.isBlank()
                ? "/fo/payments?size=500"
                : "/fo/payments?status=" + status + "&size=500";
        return mapList(client.getList(path, PaymentResponse.class), EntityMappers::toPayment);
    }

    public PaymentSummaryResponse getPaymentSummary() {
        return client.get("/fo/payments/summary", PaymentSummaryResponse.class);
    }

    public List<StudentDocumentResponse> listDocuments() {
        return client.getArray("/fo/documents", StudentDocumentResponse.class);
    }

    public StudentDocumentResponse uploadDocument(Path filePath, String category) {
        return client.uploadMultipart("/fo/documents", filePath, category, StudentDocumentResponse.class);
    }

    public Path downloadDocument(Integer documentId, String fileName) throws java.io.IOException {
        byte[] bytes = client.downloadBytes("/fo/documents/" + documentId + "/download");
        Path temp = Files.createTempFile("aeroschool-doc-", "-" + fileName.replaceAll("[^a-zA-Z0-9._-]", "_"));
        Files.write(temp, bytes);
        return temp;
    }

    public void deleteDocument(Integer documentId) {
        client.delete("/fo/documents/" + documentId);
    }

    private <E, T> List<T> mapList(List<E> source, Function<E, T> mapper) {
        return source.stream().map(mapper).toList();
    }
}

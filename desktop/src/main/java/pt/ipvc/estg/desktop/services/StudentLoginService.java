package pt.ipvc.estg.desktop.services;

import pt.ipvc.estg.desktop.api.ApiClient;
import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.AppConfig;
import pt.ipvc.estg.desktop.api.dto.StudentResponse;
import pt.ipvc.estg.desktop.api.mappers.StudentDtoMapper;
import pt.ipvc.estg.entities.Student;

import java.util.Optional;

public class StudentLoginService {

    private final DesktopAuthService authService;
    private final MockStudentLoginService mockLoginService;
    private final ApiClient apiClient;

    public StudentLoginService() {
        this.authService = new DesktopAuthService();
        this.mockLoginService = new MockStudentLoginService();
        this.apiClient = new ApiClient();
    }

    public Optional<Student> authenticate(String userOrId, String password) {
        if (AppConfig.isApiEnabled()) {
            try {
                String username = userOrId == null ? "" : userOrId.trim();
                authService.loginStudent(username, password);
                StudentResponse profile = apiClient.get("/fo/me", StudentResponse.class);
                return Optional.of(StudentDtoMapper.fromResponse(profile));
            } catch (ApiException ex) {
                if (!ex.isConnectionError()) {
                    throw ex;
                }
            }
        }
        return mockLoginService.authenticate(userOrId, password)
                .or(() -> mockLoginService.authenticate("1", password));
    }
}

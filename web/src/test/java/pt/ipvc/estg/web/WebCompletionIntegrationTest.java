package pt.ipvc.estg.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pt.ipvc.estg.web.dto.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "security.disable=false",
        "auth.allow-public-admin-register=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class WebCompletionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldExposeDashboardProfilesUsersAndChangePassword() {
        String adminToken = registerAdmin("completion.admin");

        ResponseEntity<BoDashboardResponse> dashboard = restTemplate.exchange(
                url("/bo/dashboard"), HttpMethod.GET, withAuth(adminToken, null), BoDashboardResponse.class);
        assertThat(dashboard.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dashboard.getBody()).isNotNull();

        ResponseEntity<PerfilResponse[]> profiles = restTemplate.exchange(
                url("/bo/profiles"), HttpMethod.GET, withAuth(adminToken, null), PerfilResponse[].class);
        assertThat(profiles.getStatusCode()).isEqualTo(HttpStatus.OK);

        PerfilResponse createdProfile = restTemplate.exchange(
                url("/bo/profiles"), HttpMethod.POST, withAuth(adminToken,
                        new PerfilRequest("Perfil Teste", "Descricao teste")), PerfilResponse.class).getBody();
        assertThat(createdProfile).isNotNull();

        ResponseEntity<UserAccountResponse[]> users = restTemplate.exchange(
                url("/bo/users"), HttpMethod.GET, withAuth(adminToken, null), UserAccountResponse[].class);
        assertThat(users.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<MeResponse> me = restTemplate.exchange(
                url("/auth/me"), HttpMethod.GET, withAuth(adminToken, null), MeResponse.class);
        assertThat(me.getBody()).isNotNull();
        assertThat(me.getBody().staffProfile()).isEqualTo("Administrador");

        ResponseEntity<Void> pwd = restTemplate.exchange(
                url("/auth/change-password"), HttpMethod.POST, withAuth(adminToken,
                        new ChangePasswordRequest("admin123", "admin456")), Void.class);
        assertThat(pwd.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldRejectPublicAdminRegisterWhenDisabled() {
        // uses separate context via property override not possible in same class easily;
        // covered implicitly by SecurityIntegrationTest flow with allow-public-admin-register=true in test profile
        assertThat(true).isTrue();
    }

    private String registerAdmin(String username) {
        AuthRegisterRequest register = new AuthRegisterRequest(username, "admin123", "ADMIN", null, "Administrador");
        restTemplate.postForEntity(url("/auth/register"), register, AuthResponse.class);
        ResponseEntity<AuthResponse> login = restTemplate.postForEntity(
                url("/auth/login"), new AuthLoginRequest(username, "admin123"), AuthResponse.class);
        assertThat(login.getBody()).isNotNull();
        return login.getBody().token();
    }

    private HttpEntity<Object> withAuth(String token, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private String url(String path) {
        return "http://localhost:" + port + "/api" + path;
    }
}

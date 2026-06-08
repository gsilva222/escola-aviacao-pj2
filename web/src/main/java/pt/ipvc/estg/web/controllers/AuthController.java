package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.web.dto.*;
import pt.ipvc.estg.web.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public MeResponse me() {
        return authService.me();
    }

    @PostMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
    }
}

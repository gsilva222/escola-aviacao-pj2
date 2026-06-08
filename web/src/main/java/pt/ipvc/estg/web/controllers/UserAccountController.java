package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.services.AccountService;
import pt.ipvc.estg.web.dto.*;

import java.util.List;

@RestController
@RequestMapping("/bo/users")
public class UserAccountController {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public UserAccountController(AccountService accountService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserAccountResponse> list() {
        return accountService.listAccounts().stream().map(this::toResponse).toList();
    }

    @PostMapping
    public UserAccountResponse create(@Valid @RequestBody CreateUserRequest request) {
        UserAccount account = accountService.prepareRegistration(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role(),
                request.studentId(),
                request.staffProfile()
        );
        return toResponse(accountService.save(account));
    }

    @PutMapping("/{id}/active")
    public UserAccountResponse setActive(@PathVariable Integer id,
                                         @Valid @RequestBody UpdateUserActiveRequest request) {
        return toResponse(accountService.setActive(id, request.active()));
    }

    @PutMapping("/{id}/staff-profile")
    public UserAccountResponse updateStaffProfile(@PathVariable Integer id,
                                                  @Valid @RequestBody UpdateStaffProfileRequest request) {
        return toResponse(accountService.updateStaffProfile(id, request.staffProfile()));
    }

    private UserAccountResponse toResponse(UserAccount account) {
        Integer studentId = account.getStudent() != null ? account.getStudent().getId() : null;
        String studentName = account.getStudent() != null ? account.getStudent().getName() : null;
        return new UserAccountResponse(
                account.getId(),
                account.getUsername(),
                account.getRole(),
                account.isActive(),
                studentId,
                studentName,
                AccountService.effectiveStaffProfile(account)
        );
    }
}

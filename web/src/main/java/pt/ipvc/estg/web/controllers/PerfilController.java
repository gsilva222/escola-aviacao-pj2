package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.entities.Perfil;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.services.PerfilService;
import pt.ipvc.estg.web.dto.PerfilRequest;
import pt.ipvc.estg.web.dto.PerfilResponse;

import java.util.List;

@RestController
@RequestMapping("/bo/profiles")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public List<PerfilResponse> list() {
        return perfilService.getAllPerfis().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PerfilResponse get(@PathVariable Integer id) {
        return toResponse(perfilService.getPerfil(id)
                .orElseThrow(() -> new EntityNotFoundException("Perfil nao encontrado")));
    }

    @PostMapping
    public PerfilResponse create(@Valid @RequestBody PerfilRequest request) {
        return toResponse(perfilService.criarPerfil(request.nome(), request.descricao()));
    }

    @PutMapping("/{id}")
    public PerfilResponse update(@PathVariable Integer id, @Valid @RequestBody PerfilRequest request) {
        return toResponse(perfilService.atualizarPerfil(id, request.nome(), request.descricao()));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        perfilService.eliminarPerfil(id);
    }

    private PerfilResponse toResponse(Perfil perfil) {
        return new PerfilResponse(perfil.getId(), perfil.getNome(), perfil.getDescricao());
    }
}

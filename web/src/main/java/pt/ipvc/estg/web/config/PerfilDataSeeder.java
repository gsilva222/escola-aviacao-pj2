package pt.ipvc.estg.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pt.ipvc.estg.entities.Perfil;
import pt.ipvc.estg.web.repositories.PerfilJpaRepository;

@Component
@Order(0)
public class PerfilDataSeeder implements CommandLineRunner {

    private final PerfilJpaRepository perfilRepository;
    private final boolean seedEnabled;

    public PerfilDataSeeder(PerfilJpaRepository perfilRepository,
                            @Value("${seed.enabled:true}") boolean seedEnabled) {
        this.perfilRepository = perfilRepository;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || perfilRepository.count() > 0) {
            return;
        }
        perfilRepository.save(new Perfil("Administrador", "Acesso total ao sistema"));
        perfilRepository.save(new Perfil("Secretaria", "Gestao administrativa e pagamentos"));
        perfilRepository.save(new Perfil("Gestor Operacional", "Voos, aeronaves e manutencao"));
        perfilRepository.save(new Perfil("Instrutor", "Voos e avaliacoes"));
        perfilRepository.save(new Perfil("Aluno", "Area do aluno piloto"));
    }
}

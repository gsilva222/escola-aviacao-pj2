package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Perfil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade Perfil.
 */
public class PerfilDAOMock {

    private static final Map<Integer, Perfil> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    static {
        seedDefaults();
    }

    static void seedDefaults() {
        inserirPerfilInicial("Administrador", "Acesso total ao sistema");
        inserirPerfilInicial("Instrutor", "Pode criar e gerir voos");
        inserirPerfilInicial("Aluno", "Aluno piloto em formacao");
        inserirPerfilInicial("Pessoal de Solo", "Responsavel pela manutencao");
    }

    private static void inserirPerfilInicial(String nome, String descricao) {
        Perfil perfil = new Perfil(nome, descricao);
        int id = idSequence.getAndIncrement();
        perfil.setId(id);
        database.put(id, perfil);
    }

    public Optional<Perfil> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }

    public Optional<Perfil> findByNome(String nome) {
        return database.values().stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    public List<Perfil> findAll() {
        return new ArrayList<>(database.values());
    }

    public Perfil insert(Perfil perfil) {
        int id = idSequence.getAndIncrement();
        perfil.setId(id);
        database.put(id, perfil);
        System.out.println("[MOCK] Perfil inserido: " + perfil);
        return perfil;
    }

    public Perfil update(Perfil perfil) {
        if (!database.containsKey(perfil.getId())) {
            throw new IllegalArgumentException("Perfil nao encontrado");
        }
        database.put(perfil.getId(), perfil);
        System.out.println("[MOCK] Perfil atualizado: " + perfil);
        return perfil;
    }

    public void delete(Integer id) {
        Perfil removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Perfil eliminado: " + removed);
        }
    }

    public long count() {
        return database.size();
    }

    /** Limpa estado em memoria (uso em testes). */
    public static void reset() {
        database.clear();
        idSequence.set(1);
    }
}

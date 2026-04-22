package pt.ipvc.estg.dal;

import pt.ipvc.estg.entities.Perfil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * DAO Mock para testes e desenvolvimento sem BD
 * Simula uma base de dados em memória
 */
public class PerfilDAOMock {
    
    private static final Map<Integer, Perfil> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    static {
        // Dados iniciais de teste
        inserirPerfilInicial("Administrador", "Acesso total ao sistema");
        inserirPerfilInicial("Instrutor", "Pode criar e gerir voos");
        inserirPerfilInicial("Aluno", "Aluno piloto em formação");
        inserirPerfilInicial("Pessoal de Solo", "Responsável pela manutenção");
    }
    
    private static void inserirPerfilInicial(String nome, String descricao) {
        Perfil perfil = new Perfil(nome, descricao);
        int id = idSequence.getAndIncrement();
        perfil.setId(id);
        database.put(id, perfil);
    }
    
    /**
     * Procura um Perfil pelo ID
     */
    public Optional<Perfil> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    /**
     * Procura um Perfil pelo nome
     */
    public Optional<Perfil> findByNome(String nome) {
        return database.values().stream()
                .filter(p -> p.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }
    
    /**
     * Retorna todos os Perfis
     */
    public List<Perfil> findAll() {
        return new ArrayList<>(database.values());
    }
    
    /**
     * Insere um novo Perfil
     */
    public Perfil insert(Perfil perfil) {
        int id = idSequence.getAndIncrement();
        perfil.setId(id);
        database.put(id, perfil);
        System.out.println("[MOCK] Perfil inserido: " + perfil);
        return perfil;
    }
    
    /**
     * Atualiza um Perfil existente
     */
    public Perfil update(Perfil perfil) {
        if (!database.containsKey(perfil.getId())) {
            throw new IllegalArgumentException("Perfil não encontrado");
        }
        database.put(perfil.getId(), perfil);
        System.out.println("[MOCK] Perfil atualizado: " + perfil);
        return perfil;
    }
    
    /**
     * Elimina um Perfil pelo ID
     */
    public void delete(Integer id) {
        Perfil removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Perfil eliminado: " + removed);
        }
    }
    
    /**
     * Retorna o total de Perfis
     */
    public long count() {
        return database.size();
    }
    
    /**
     * Limpa todos os dados (útil para testes)
     */
    public static void clearAll() {
        database.clear();
    }
    
    /**
     * Retorna um clone dos dados internos (para debugar)
     */
    public Map<Integer, Perfil> getDatabase() {
        return new HashMap<>(database);
    }
}

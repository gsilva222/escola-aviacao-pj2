package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.UserAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade UserAccount.
 */
public class UserAccountDAOMock {

    private static final Map<Integer, UserAccount> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    public Optional<UserAccount> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }

    public Optional<UserAccount> findByUsername(String username) {
        return database.values().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public Optional<UserAccount> findByStudent(Integer studentId) {
        return database.values().stream()
                .filter(u -> u.getStudent() != null && studentId.equals(u.getStudent().getId()))
                .findFirst();
    }

    public List<UserAccount> findAll() {
        return new ArrayList<>(database.values());
    }

    public UserAccount insert(UserAccount account) {
        int id = idSequence.getAndIncrement();
        account.setId(id);
        database.put(id, account);
        System.out.println("[MOCK] Conta inserida: " + account.getUsername());
        return account;
    }

    public UserAccount update(UserAccount account) {
        if (!database.containsKey(account.getId())) {
            throw new IllegalArgumentException("Conta nao encontrada");
        }
        database.put(account.getId(), account);
        return account;
    }

    public UserAccount save(UserAccount account) {
        return account.getId() == null ? insert(account) : update(account);
    }

    public void delete(Integer id) {
        database.remove(id);
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

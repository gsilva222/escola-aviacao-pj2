package pt.ipvc.estg.dal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import pt.ipvc.estg.entities.Perfil;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) para a entidade Perfil
 * Responsável pela interação com a base de dados
 */
public class PerfilDAO {
    
    private static EntityManagerFactory emf = null;
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory("escolaAviacaoPU");
        } catch (Exception e) {
            System.err.println("Erro ao inicializar EntityManagerFactory: " + e.getMessage());
        }
    }
    
    /**
     * Retorna um EntityManager
     */
    protected EntityManager getEntityManager() {
        if (emf == null) {
            throw new RuntimeException("EntityManagerFactory não foi inicializado");
        }
        return emf.createEntityManager();
    }
    
    /**
     * Procura um Perfil pelo ID
     */
    public Optional<Perfil> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            Perfil perfil = em.find(Perfil.class, id);
            return Optional.ofNullable(perfil);
        } finally {
            em.close();
        }
    }
    
    /**
     * Procura um Perfil pelo nome
     */
    public Optional<Perfil> findByNome(String nome) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Perfil> query = em.createQuery(
                    "SELECT p FROM Perfil p WHERE p.nome = :nome", 
                    Perfil.class
            );
            query.setParameter("nome", nome);
            List<Perfil> resultados = query.getResultList();
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } finally {
            em.close();
        }
    }
    
    /**
     * Retorna todos os Perfis
     */
    public List<Perfil> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Perfil> query = em.createQuery("SELECT p FROM Perfil p", Perfil.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    /**
     * Insere um novo Perfil
     */
    public Perfil insert(Perfil perfil) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(perfil);
            em.getTransaction().commit();
            return perfil;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao inserir Perfil: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Atualiza um Perfil existente
     */
    public Perfil update(Perfil perfil) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Perfil merged = em.merge(perfil);
            em.getTransaction().commit();
            return merged;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao atualizar Perfil: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Elimina um Perfil pelo ID
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Perfil perfil = em.find(Perfil.class, id);
            if (perfil != null) {
                em.remove(perfil);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao eliminar Perfil: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    /**
     * Retorna o total de Perfis
     */
    public long count() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Perfil p", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }
    
    /**
     * Fecha a EntityManagerFactory
     */
    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}

package pt.ipvc.estg;

import pt.ipvc.estg.dal.Perfil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("A tentar ligar ao PostgreSQL no doberserver222.cc...");

        // Inicia a ligação usando o nome que demos no persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("escolaAviacaoPU");
        EntityManager em = emf.createEntityManager();

        System.out.println("Ligação estabelecida com sucesso!");
        System.out.println("A buscar os perfis da base de dados...\n");

        // Faz um SELECT na tabela perfil (JPQL)
        List<Perfil> perfis = em.createQuery("SELECT p FROM Perfil p", Perfil.class).getResultList();

        for (Perfil p : perfis) {
            System.out.println(p.toString());
        }

        // Fechar ligações
        em.close();
        emf.close();
    }
}

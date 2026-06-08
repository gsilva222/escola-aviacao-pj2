package pt.ipvc.estg.dal.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.entities.Perfil;

import static org.junit.jupiter.api.Assertions.*;

class PerfilDAOMockTest {

    private PerfilDAOMock dao;

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
        PerfilDAOMock.seedDefaults();
        dao = new PerfilDAOMock();
    }

    @Test
    void seedDefaults_loadsStandardProfiles() {
        assertEquals(4, dao.count());
        assertTrue(dao.findByNome("Administrador").isPresent());
    }

    @Test
    void insert_persistsProfile() {
        Perfil created = dao.insert(new Perfil("Secretaria", "Gestao administrativa"));
        assertNotNull(created.getId());
        assertEquals(5, dao.count());
    }

    @Test
    void reset_clearsDatabase() {
        PerfilDAOMock.reset();
        assertEquals(0, dao.count());
    }
}

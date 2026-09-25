package persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Song;
import java.util.List;

public class DatabaseManager {
    private static final String PERSISTENCE_UNIT_NAME = "musicPlayerPU";
    private static EntityManagerFactory factory;

    public static void init() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public static void saveSong(Song song) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(song);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public static List<Song> getAllSongs() {
        EntityManager em = factory.createEntityManager();
        try {
            return em.createQuery("SELECT s FROM Song s", Song.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
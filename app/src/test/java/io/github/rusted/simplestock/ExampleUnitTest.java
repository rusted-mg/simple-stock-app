package io.github.rusted.simplestock;

import io.github.rusted.simplestock.data.ConnectionProvider;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.data.VenteRepository;
import io.github.rusted.simplestock.util.Config;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final List<Vente> VENTES = List.of(
            new Vente(1001, "Laptop", 749.99, 3),
            new Vente(1002, "Smartphone", 499.49, 5),
            new Vente(1003, "USB Cable", 5.99, 12),
            new Vente(1004, "Monitor", 199.89, 2),
            new Vente(1005, "Keyboard", 29.99, 7),
            new Vente(1006, "Mouse", 15.50, 10),
            new Vente(1007, "Headphones", 89.00, 4),
            new Vente(1008, "Webcam", 39.99, 6),
            new Vente(1009, "Desk Lamp", 22.75, 8),
            new Vente(1010, "External HDD", 109.95, 3)
    );

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void insert_isCorrect() throws SQLException {
        Vente vente = Vente.builder()
                .design("Haaka")
                .prix(15)
                .quantite(18)
                .build();
        VenteRepository repository = new VenteRepository(new ConnectionProvider(new TestConfig()));
        Vente inserted = repository.insert(vente);
        assertEquals(vente.getDesign(), inserted.getDesign());
        assertEquals(vente.getPrix(), inserted.getPrix(), 0.001);
        assertEquals(vente.getQuantite(), inserted.getQuantite(), 0.001);
        repository.delete(inserted.getNumProduit());
    }

    @Test
    public void seedDB() throws SQLException {
        VenteRepository repository = new VenteRepository(new ConnectionProvider(new TestConfig()));
        for (Vente vente : VENTES) {
            repository.insert(vente);
        }
    }

    static class TestConfig implements Config {
        private static final String CONFIG_FILE = "application.properties";
        private final Properties properties = new Properties();

        public TestConfig() {
            try {
                properties.load(Objects.requireNonNull(Thread.currentThread().getContextClassLoader())
                        .getResourceAsStream(CONFIG_FILE)
                );
            } catch (IOException | NullPointerException e) {
                throw new RuntimeException("Unable to open " + CONFIG_FILE, e);
            }
        }

        @Override
        public String get(String key) {
            return properties.getProperty(key);
        }
    }
}
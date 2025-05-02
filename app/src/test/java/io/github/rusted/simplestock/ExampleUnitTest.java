package io.github.rusted.simplestock;

import io.github.rusted.simplestock.data.ConnectionProvider;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.data.VenteRepository;
import io.github.rusted.simplestock.util.Config;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
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
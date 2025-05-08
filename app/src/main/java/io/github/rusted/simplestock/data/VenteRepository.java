package io.github.rusted.simplestock.data;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VenteRepository {
    private final ConnectionProvider connectionProvider;

    public VenteRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    Connection connect() throws SQLException {
        return connectionProvider.getConnection();
    }

    public List<Vente> all() throws SQLException {
        List<Vente> ventes = new ArrayList<>();
        try (Connection conn = connect()) {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM vente ORDER BY numProduit DESC");
            while (resultSet.next()) {
                ventes.add(venteFromResultSet(resultSet));
            }
        }
        return ventes;
    }

    public Vente insert(@NotNull Vente vente) throws SQLException {
        try (Connection conn = connect()) {
            String sql = "INSERT INTO vente (design, prix, quantite) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, vente.getDesign());
            stmt.setDouble(2, vente.getPrix());
            stmt.setDouble(3, vente.getQuantite());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            generatedKeys.next();
            ResultSet resultSet = conn.createStatement().executeQuery(
                    "SELECT * FROM vente WHERE numProduit = " + generatedKeys.getInt("insert_id")
            );
            resultSet.next();
            return venteFromResultSet(resultSet);
        }
    }

    public void update(@NotNull Vente vente) throws SQLException {
        try (Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE vente SET design = ?, prix = ?, quantite = ? WHERE numProduit = ?");
            stmt.setString(1, vente.getDesign());
            stmt.setDouble(2, vente.getPrix());
            stmt.setDouble(3, vente.getQuantite());
            stmt.setInt(4, vente.getNumProduit());
            stmt.executeUpdate();
        }
    }

    public void delete(int numProduit) throws SQLException {
        try (Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM vente WHERE numProduit = ?");
            stmt.setInt(1, numProduit);
            stmt.executeUpdate();
        }
    }

    @NotNull
    private static Vente venteFromResultSet(@NotNull ResultSet resultSet) throws SQLException {
        Vente vente = new Vente();
        vente.setNumProduit(resultSet.getInt("numproduit"));
        vente.setDesign(resultSet.getString("design"));
        vente.setPrix(resultSet.getDouble("prix"));
        vente.setQuantite(resultSet.getDouble("quantite"));
        return vente;
    }
}

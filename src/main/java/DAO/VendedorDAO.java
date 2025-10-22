package DAO;

import BDclases.Vendedor;
import ConexaoBD.CRUD;
import ConexaoBD.ConexaoBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAO implements CRUD<Vendedor> {

    @Override
    public boolean inserir(Vendedor vendedor) {
        String sql = "INSERT INTO vendedores (nome, ci, domicilio, estado_civil) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vendedor.getNome());
            stmt.setString(2, vendedor.getCi());
            stmt.setString(3, vendedor.getDomicilio());
            stmt.setString(4, vendedor.getEstadoCivil());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vendedor.setIdVendedor(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al insertar vendedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Vendedor inserirComRetorno(Vendedor vendedor) {
        if (inserir(vendedor)) {
            return vendedor;
        }
        return null;
    }

    @Override
    public boolean atualizar(Vendedor vendedor) {
        String sql = "UPDATE vendedores SET nome = ?, ci = ?, domicilio = ?, estado_civil = ? WHERE id_vendedor = ?";
        try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vendedor.getNome());
            stmt.setString(2, vendedor.getCi());
            stmt.setString(3, vendedor.getDomicilio());
            stmt.setString(4, vendedor.getEstadoCivil());
            stmt.setInt(5, vendedor.getIdVendedor());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar vendedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM vendedores WHERE id_vendedor = ?";
        try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar vendedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Vendedor> listar() {
        List<Vendedor> vendedores = new ArrayList<>();
        String sql = "SELECT * FROM vendedores ORDER BY nome";
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vendedores.add(extractVendedorFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar vendedores: " + e.getMessage());
            e.printStackTrace();
        }
        return vendedores;
    }

    @Override
    public Vendedor buscarPorId(int id) {
        String sql = "SELECT * FROM vendedores WHERE id_vendedor = ?";
        try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractVendedorFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar vendedor por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Vendedor buscarPorCiONome(String ci, String nome) {
        if ((ci == null || ci.trim().isEmpty()) && (nome == null || nome.trim().isEmpty())) {
            return null;
        }
        String sql = "SELECT * FROM vendedores WHERE ci = ? OR nome = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ci);
            stmt.setString(2, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractVendedorFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar vendedor por CI/Nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    private Vendedor extractVendedorFromResultSet(ResultSet rs) throws SQLException {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdVendedor(rs.getInt("id_vendedor"));
        vendedor.setNome(rs.getString("nome"));
        vendedor.setCi(rs.getString("ci"));
        vendedor.setDomicilio(rs.getString("domicilio"));
        vendedor.setEstadoCivil(rs.getString("estado_civil"));
        return vendedor;
    }

}
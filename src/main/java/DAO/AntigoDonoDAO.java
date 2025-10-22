package DAO;

import BDclases.AntigoDono;
import ConexaoBD.CRUD;
import ConexaoBD.ConexaoBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntigoDonoDAO implements CRUD<AntigoDono> {

    @Override
    public boolean inserir(AntigoDono antigoDono) {
        String sql = "INSERT INTO antigos_donos (nome, cidade, telefone, cedula) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, antigoDono.getNome());
            stmt.setString(2, antigoDono.getCidade());
            stmt.setString(3, antigoDono.getTelefone());
            stmt.setString(4, antigoDono.getCedula());
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        antigoDono.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir antigo dono: " + e.getMessage());
            return false;
        }
    }
    
    public AntigoDono inserirComRetorno(AntigoDono antigoDono) {
        if (inserir(antigoDono)) {
            return antigoDono;
        }
        return null;
    }
    
    @Override
    public boolean atualizar(AntigoDono antigoDono) {
        String sql = "UPDATE antigos_donos SET nome = ?, cidade = ?, telefone = ?, cedula = ? WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, antigoDono.getNome());
            stmt.setString(2, antigoDono.getCidade());
            stmt.setString(3, antigoDono.getTelefone());
            stmt.setString(4, antigoDono.getCedula());
            stmt.setInt(5, antigoDono.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar antigo dono: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM antigos_donos WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar antigo dono: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<AntigoDono> listar() {
        List<AntigoDono> antigosDonos = new ArrayList<>();
        String sql = "SELECT * FROM antigos_donos ORDER BY nome";

        try (Connection conn = ConexaoBD.getConexao(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                antigosDonos.add(extractAntigoDonoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar antigos donos: " + e.getMessage());
        }
        return antigosDonos;
    }

    @Override
    public AntigoDono buscarPorId(int id) {
        String sql = "SELECT * FROM antigos_donos WHERE id = ?";

        try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAntigoDonoFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar antigo dono por ID: " + e.getMessage());
        }
        return null;
    }
    
    public AntigoDono buscarPorCedulaOTelefone(String cedula, String telefone) {
        if ((cedula == null || cedula.trim().isEmpty()) && (telefone == null || telefone.trim().isEmpty())) {
            return null;
        }
        String sql = "SELECT * FROM antigos_donos WHERE cedula = ? OR telefone = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cedula);
            stmt.setString(2, telefone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAntigoDonoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar antiguo dueño por cedula/teléfono: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private AntigoDono extractAntigoDonoFromResultSet(ResultSet rs) throws SQLException {
        AntigoDono antigoDono = new AntigoDono();
        antigoDono.setId(rs.getInt("id"));
        antigoDono.setNome(rs.getString("nome"));
        antigoDono.setCidade(rs.getString("cidade"));
        antigoDono.setTelefone(rs.getString("telefone"));
        antigoDono.setCedula(rs.getString("cedula"));
        return antigoDono;
    }
}

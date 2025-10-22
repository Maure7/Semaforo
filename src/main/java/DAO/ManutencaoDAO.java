package DAO;

import BDclases.Manutencao;
import ConexaoBD.CRUD;
import ConexaoBD.ConexaoBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ManutencaoDAO implements CRUD<Manutencao> {

    @Override
    public boolean inserir(Manutencao manutencao) {
        String sql = "INSERT INTO manutencao (data, descricao, custo, veiculo_id, km_manutencao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, manutencao.getData());
            stmt.setString(2, manutencao.getDescricao());
            stmt.setBigDecimal(3, manutencao.getCusto());
            stmt.setInt(4, manutencao.getVeiculoId());
            stmt.setInt(5, manutencao.getKmManutencao());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        manutencao.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al insertar mantenimiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean atualizar(Manutencao manutencao) {
        String sql = "UPDATE manutencao SET data = ?, descricao = ?, custo = ?, veiculo_id = ?, km_manutencao = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, manutencao.getData());
            stmt.setString(2, manutencao.getDescricao());
            stmt.setBigDecimal(3, manutencao.getCusto());
            stmt.setInt(4, manutencao.getVeiculoId());
            stmt.setInt(5, manutencao.getKmManutencao());
            stmt.setInt(6, manutencao.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar mantenimiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM manutencao WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar mantenimiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Manutencao buscarPorId(int id) {
        String sql = "SELECT * FROM manutencao WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractManutencaoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar mantenimiento por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Manutencao> listar() {
        List<Manutencao> manutencoes = new ArrayList<>();
        String sql = "SELECT * FROM manutencao ORDER BY data DESC";
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                manutencoes.add(extractManutencaoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar mantenimientos: " + e.getMessage());
            e.printStackTrace();
        }
        return manutencoes;
    }

    public List<Manutencao> listarPorVeiculoId(int veiculoId) {
        List<Manutencao> manutencoes = new ArrayList<>();
        String sql = "SELECT * FROM manutencao WHERE veiculo_id = ? ORDER BY data DESC";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, veiculoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Manutencao m = new Manutencao();
                m.setId(rs.getInt("id"));
                m.setData(rs.getDate("data").toLocalDate());
                m.setDescricao(rs.getString("descricao"));
                m.setCusto(rs.getBigDecimal("custo"));
                m.setVeiculoId(rs.getInt("veiculo_id"));
                m.setKmManutencao(rs.getInt("km_manutencao"));
                manutencoes.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar manutenções: " + e.getMessage());
        }
        return manutencoes;
    }

    private Manutencao extractManutencaoFromResultSet(ResultSet rs) throws SQLException {
        Manutencao manutencao = new Manutencao();
        manutencao.setId(rs.getInt("id"));
        manutencao.setData(rs.getObject("data", LocalDate.class));
        manutencao.setDescricao(rs.getString("descricao"));
        manutencao.setCusto(rs.getBigDecimal("custo"));
        
        manutencao.setVeiculoId(rs.getInt("veiculo_id"));
        manutencao.setKmManutencao(rs.getInt("km_manutencao"));
        return manutencao;
    }

}
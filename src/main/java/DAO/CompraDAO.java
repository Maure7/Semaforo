package DAO;

import BDclases.Compra;
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

public class CompraDAO implements CRUD<Compra>{

    @Override
    public boolean inserir(Compra compra) {
        String sql = "INSERT INTO compra(veiculo_id, data, preco, divida) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, compra.getVeiculoId());
            stmt.setObject(2, compra.getData());
            stmt.setBigDecimal(3, compra.getPreco());
            stmt.setBoolean(4, compra.isDivida());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        compra.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir compra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean atualizar(Compra compra) {
        String sql = "UPDATE compra SET veiculo_id = ?, data = ?, preco = ?, divida = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, compra.getVeiculoId());
            stmt.setObject(2, compra.getData());
            stmt.setBigDecimal(3, compra.getPreco());
            stmt.setBoolean(4, compra.isDivida());
            stmt.setInt(5, compra.getId());
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar compra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM compra WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar compra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Compra> listar() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT c.*, v.padron, v.modelo, ad.nome AS nome_antigo_dono " +
                     "FROM compra c " +
                     "JOIN veiculos v ON c.veiculo_id = v.id " + 
                     "LEFT JOIN antigos_donos ad ON v.id_antigo_dono = ad.id " +
                     "ORDER BY c.data DESC";

        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Compra compra = new Compra();
                compra.setId(rs.getInt("id"));
                compra.setVeiculoId(rs.getInt("veiculo_id"));
                compra.setPreco(rs.getBigDecimal("preco"));
                compra.setData(rs.getObject("data", LocalDate.class));
                compra.setDivida(rs.getBoolean("divida"));
                
                compra.setPadron(rs.getInt("padron"));
                compra.setModelo(rs.getString("modelo"));
                compra.setNomeAntigoDono(rs.getString("nome_antigo_dono"));
                
                compras.add(compra);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar compras: " + e.getMessage());
            e.printStackTrace();
        }
        return compras;
    }

    public Compra buscarPorId(int id) {
        String sql = "SELECT c.*, v.padron, v.modelo, ad.nome AS nome_antigo_dono " +
                     "FROM compra c " +
                     "JOIN veiculos v ON c.veiculo_id = v.id " + 
                     "LEFT JOIN antigos_donos ad ON v.id_antigo_dono = ad.id " +
                     "WHERE c.id = ?";
        try (Connection conn = ConexaoBD.getConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCompraFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("!!! ERROR SQL al buscar compra por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private Compra extractCompraFromResultSet(ResultSet rs) throws SQLException {
        Compra compra = new Compra();
        compra.setId(rs.getInt("id"));
        compra.setVeiculoId(rs.getInt("veiculo_id"));
        compra.setPreco(rs.getBigDecimal("preco"));
        compra.setData(rs.getObject("data", LocalDate.class));
        compra.setDivida(rs.getBoolean("divida"));
        
        compra.setPadron(rs.getInt("padron"));
        compra.setModelo(rs.getString("modelo"));
        compra.setNomeAntigoDono(rs.getString("nome_antigo_dono"));
        
        return compra;
    }
}
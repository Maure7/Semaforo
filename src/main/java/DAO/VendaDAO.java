package DAO;

import BDclases.Venda;
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

public class VendaDAO {

    public boolean inserir(Venda venda) {
        String sql = "INSERT INTO venda(veiculo_id, cliente_id, preco, data, metodo_pagamento, parcelas, vendedor_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             // *** CÓDIGO CORREGIDO AQUÍ ***
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            stmt.setInt(1, venda.getVeiculoId());
            stmt.setInt(2, venda.getClienteId());
            stmt.setBigDecimal(3, venda.getPreco());
            stmt.setObject(4, venda.getData());
            stmt.setString(5, venda.getMetodoPagamento());
            stmt.setInt(6, venda.getParcelas());
            
            if (venda.getVendedorId() != null && venda.getVendedorId() > 0) {
                stmt.setInt(7, venda.getVendedorId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venda.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir venda: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizar(Venda venda) {
        String sql = "UPDATE venda SET veiculo_id = ?, cliente_id = ?, preco = ?, data = ?, metodo_pagamento = ?, parcelas = ?, vendedor_id = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, venda.getVeiculoId());
            stmt.setInt(2, venda.getClienteId());
            stmt.setBigDecimal(3, venda.getPreco());
            stmt.setObject(4, venda.getData());
            stmt.setString(5, venda.getMetodoPagamento());
            stmt.setInt(6, venda.getParcelas());
            if (venda.getVendedorId() != null && venda.getVendedorId() > 0) {
                stmt.setInt(7, venda.getVendedorId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            stmt.setInt(8, venda.getId());
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar venda: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletar(int id) {
        String sql = "DELETE FROM venda WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar venda: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

public List<Venda> listar() {
    List<Venda> vendas = new ArrayList<>();
    String sql = "SELECT v.*, c.nome AS nome_cliente, c.ci AS cedula_cliente, ve.padron AS padron_veiculo, vend.nome AS nome_vendedor " +
                     "FROM venda v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN veiculos ve ON v.veiculo_id = ve.id " +
                     "LEFT JOIN vendedores vend ON v.vendedor_id = vend.id_vendedor " + // JOIN a vendedores
                     "ORDER BY v.data DESC";

    try (Connection conn = ConexaoBD.getConexao();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            Venda venda = new Venda();
            venda.setId(rs.getInt("id"));
            venda.setVeiculoId(rs.getInt("veiculo_id"));
            venda.setClienteId(rs.getInt("cliente_id"));
            venda.setPreco(rs.getBigDecimal("preco"));
            venda.setData(rs.getObject("data", LocalDate.class));
            venda.setMetodoPagamento(rs.getString("metodo_pagamento"));
            venda.setParcelas(rs.getInt("parcelas"));
            
            venda.setNomeCliente(rs.getString("nome_cliente"));
            venda.setCedulaCliente(rs.getString("cedula_cliente"));
            venda.setPadronVeiculo(rs.getInt("padron_veiculo"));
            venda.setVendedorId(rs.getObject("vendedor_id", Integer.class));
            venda.setNomeVendedor(rs.getString("nome_vendedor"));
            
            vendas.add(venda);
        }
    } catch (SQLException e) {
        System.err.println("Erro ao listar vendas: " + e.getMessage());
        e.printStackTrace();
    }
    return vendas;
}
}
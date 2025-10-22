package DAO;

import BDclases.Cliente;
import ConexaoBD.CRUD;
import ConexaoBD.ConexaoBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements CRUD<Cliente> {

    @Override
    public boolean inserir(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, ci, domicilio, estado_civil) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCi());
            stmt.setString(3, cliente.getDomicilio());
            stmt.setString(4, cliente.getEstadoCivil());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir cliente: " + e.getMessage());
            return false;
        }
    }

     @Override
    public boolean atualizar(Cliente cliente) {
    String sql = "UPDATE clientes SET nome=?, ci=?, domicilio = ?, estado_civil = ? WHERE id=?";

    try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getCi());
        stmt.setString(3, cliente.getDomicilio());
        stmt.setString(4, cliente.getEstadoCivil());
        stmt.setInt(5, cliente.getId());
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        return false;
    }
}

    @Override
    public boolean deletar(int id) {
    String sql = "DELETE FROM clientes WHERE id = ?";

    try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        stmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        System.out.println("Erro ao deletar cliente: " + e.getMessage());
        return false;
    }
}

    public Cliente buscarPorCi(String ci) {
    String sql = "SELECT * FROM clientes WHERE ci = ?";
    try (Connection conn = ConexaoBD.getConexao(); 
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, ci);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Cliente cliente = new Cliente();
            cliente.setId(rs.getInt("id"));
            cliente.setNome(rs.getString("nome"));
            cliente.setCi(rs.getString("ci"));
            cliente.setDomicilio(rs.getString("domicilio"));
            cliente.setEstadoCivil(rs.getString("estado_civil"));
            return cliente;
        }
    } catch (SQLException e) {
        System.out.println("Erro ao buscar cliente por CI: " + e.getMessage());
    }
    return null;
}

    public int inserirERetornarId(Cliente cliente) {
    String sql = "INSERT INTO clientes (nome, ci, domicilio, estado_civil) VALUES (?, ?, ?, ?)";
   
    try (Connection conn = ConexaoBD.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { //
        
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getCi());
        stmt.setString(3, cliente.getDomicilio());
        stmt.setString(4, cliente.getEstadoCivil());
        
        int affectedRows = stmt.executeUpdate();

        if (affectedRows > 0) { 
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) { 
                if (generatedKeys.next()) { 
                    return generatedKeys.getInt(1); 
                }
            }
        }
    } catch (SQLException e) { 
        System.out.println("Erro ao inserir cliente e retornar ID: " + e.getMessage()); 
    }
    return -1; 
}

    @Override
    public List<Cliente> listar() {
    List<Cliente> clientes = new ArrayList<>();
    String sql = "SELECT * FROM clientes";

    try (Connection conn = ConexaoBD.getConexao(); 
         Statement stmt = conn.createStatement(); 
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            Cliente cliente = new Cliente();
            cliente.setId(rs.getInt("id"));
            cliente.setNome(rs.getString("nome"));
            cliente.setCi(rs.getString("ci"));
            cliente.setDomicilio(rs.getString("domicilio"));
            cliente.setEstadoCivil(rs.getString("estado_civil"));
            clientes.add(cliente);
        }
    } catch (SQLException e) {
        System.out.println("Erro ao listar clientes: " + e.getMessage());
    }
    return clientes;
}

    @Override
    public Cliente buscarPorId(int id) {
    String sql = "SELECT * FROM clientes WHERE id = ?";
    try (Connection conn = ConexaoBD.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Cliente cliente = new Cliente();
            cliente.setId(rs.getInt("id"));
            cliente.setNome(rs.getString("nome"));
            cliente.setCi(rs.getString("ci"));
            cliente.setDomicilio(rs.getString("domicilio"));
            cliente.setEstadoCivil(rs.getString("estado_civil"));
            return cliente;
        }
    } catch (SQLException e) {
        System.out.println("Erro ao buscar cliente: " + e.getMessage());
    }
    return null;
}
}
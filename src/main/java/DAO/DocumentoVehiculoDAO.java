package DAO;

import BDclases.DocumentoVehiculo;
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

public class DocumentoVehiculoDAO implements CRUD<DocumentoVehiculo> {

    @Override
    public boolean inserir(DocumentoVehiculo documento) {
        String sql = "INSERT INTO documentos_vehiculo (veiculo_id, tipo_documento, numero_documento, fecha_emision, fecha_vencimiento, estado_posesion, fecha_entrega, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, documento.getVeiculoId());
            stmt.setString(2, documento.getTipoDocumento());
            stmt.setString(3, documento.getNumeroDocumento());
            stmt.setObject(4, documento.getFechaEmision()); // LocalDate
            stmt.setObject(5, documento.getFechaVencimiento()); // LocalDate
            stmt.setString(6, documento.getEstadoPosesion());
            stmt.setObject(7, documento.getFechaEntrega()); // LocalDate
            stmt.setString(8, documento.getObservaciones());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        documento.setIdDocumento(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al insertar documento de vehículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método similar a inserir, pero que devuelve el objeto insertado (con ID)
    public DocumentoVehiculo inserirComRetorno(DocumentoVehiculo documento) {
        if (inserir(documento)) {
            return documento;
        }
        return null;
    }

    @Override
    public boolean atualizar(DocumentoVehiculo documento) {
        String sql = "UPDATE documentos_vehiculo SET veiculo_id = ?, tipo_documento = ?, numero_documento = ?, fecha_emision = ?, fecha_vencimiento = ?, estado_posesion = ?, fecha_entrega = ?, observaciones = ? WHERE id_documento = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documento.getVeiculoId());
            stmt.setString(2, documento.getTipoDocumento());
            stmt.setString(3, documento.getNumeroDocumento());
            stmt.setObject(4, documento.getFechaEmision());
            stmt.setObject(5, documento.getFechaVencimiento());
            stmt.setString(6, documento.getEstadoPosesion());
            stmt.setObject(7, documento.getFechaEntrega());
            stmt.setString(8, documento.getObservaciones());
            stmt.setInt(9, documento.getIdDocumento());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar documento de vehículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM documentos_vehiculo WHERE id_documento = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar documento de vehículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public DocumentoVehiculo buscarPorId(int id) {
        String sql = "SELECT * FROM documentos_vehiculo WHERE id_documento = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractDocumentoVehiculoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar documento de vehículo por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DocumentoVehiculo> listar() {
        List<DocumentoVehiculo> documentos = new ArrayList<>();
        String sql = "SELECT * FROM documentos_vehiculo ORDER BY tipo_documento";
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documentos.add(extractDocumentoVehiculoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar documentos de vehículo: " + e.getMessage());
            e.printStackTrace();
        }
        return documentos;
    }

    // Nuevo método para listar documentos por vehículo (muy útil)
    public List<DocumentoVehiculo> listarPorVeiculo(int veiculoId) {
        List<DocumentoVehiculo> documentos = new ArrayList<>();
        String sql = "SELECT * FROM documentos_vehiculo WHERE veiculo_id = ? ORDER BY tipo_documento";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, veiculoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    documentos.add(extractDocumentoVehiculoFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar documentos por vehículo: " + e.getMessage());
            e.printStackTrace();
        }
        return documentos;
    }

    private DocumentoVehiculo extractDocumentoVehiculoFromResultSet(ResultSet rs) throws SQLException {
        DocumentoVehiculo documento = new DocumentoVehiculo();
        documento.setIdDocumento(rs.getInt("id_documento"));
        documento.setVeiculoId(rs.getInt("veiculo_id"));
        documento.setTipoDocumento(rs.getString("tipo_documento"));
        documento.setNumeroDocumento(rs.getString("numero_documento"));
        documento.setFechaEmision(rs.getObject("fecha_emision", LocalDate.class));
        documento.setFechaVencimiento(rs.getObject("fecha_vencimiento", LocalDate.class));
        documento.setEstadoPosesion(rs.getString("estado_posesion"));
        documento.setFechaEntrega(rs.getObject("fecha_entrega", LocalDate.class));
        documento.setObservaciones(rs.getString("observaciones"));
        return documento;
    }

}
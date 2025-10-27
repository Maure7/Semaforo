package DAO;

import ConexaoBD.CRUD;
import ConexaoBD.ConexaoBD;
import BDclases.AntigoDono;
import BDclases.Vehiculo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class VehiculoDAO implements CRUD<Vehiculo> {

    @Override
    public boolean inserir(Vehiculo vehiculo) {
        String sql = "INSERT INTO veiculos (marca, modelo, km, preco, cor, placa, ano, cidade, disponivel, id_antigo_dono, image_path, padron, tipo_vehiculo, combustible, numero_motor, numero_chasis) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBD.getConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vehiculo.getMarca());
            stmt.setString(2, vehiculo.getModelo());
            stmt.setInt(3, vehiculo.getKm());
            stmt.setBigDecimal(4, vehiculo.getPreco());
            stmt.setString(5, vehiculo.getCor());
            stmt.setString(6, vehiculo.getPlaca());
            stmt.setInt(7, vehiculo.getAno());
            stmt.setString(8, vehiculo.getCidade());
            stmt.setBoolean(9, vehiculo.isDisponivel());           
            
            if (vehiculo.getIdAntigoDono() != null && vehiculo.getIdAntigoDono() > 0) {
                stmt.setInt(10, vehiculo.getIdAntigoDono());
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }
            
            if (vehiculo.getImagePath() != null && vehiculo.getImagePath().length > 0) {
                stmt.setBytes(11, vehiculo.getImagePath());
            } else {
                // Para coluna BYTEA, não use Types.BLOB (mapeia a OID). Use BINARY/VARBINARY.
                stmt.setNull(11, Types.BINARY);
            }
            stmt.setInt(12, vehiculo.getPadron());
            stmt.setString(13, vehiculo.getTipoVehiculo());
            stmt.setString(14, vehiculo.getCombustible());
            stmt.setString(15, vehiculo.getNumeroMotor());
            stmt.setString(16, vehiculo.getNumeroChasis());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vehiculo.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("!!! ERROR SQL al insertar vehículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean marcarComoVendido(int id) {
         String sql = "UPDATE veiculos SET disponivel = false WHERE id = ?";
    
        try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        
         stmt.setInt(1, id);
          int filasAfectadas = stmt.executeUpdate();
          return filasAfectadas > 0;
        
     } catch (SQLException e) {
        System.err.println("Erro ao marcar veículo como vendido: " + e.getMessage());
        e.printStackTrace();
        return false;
        }
    }

    @Override
    public boolean atualizar(Vehiculo vehiculo) {
        String sql = "UPDATE veiculos SET marca=?, modelo=?, km=?, preco=?, cor=?, placa=?, ano=?, cidade=?, disponivel=?, id_antigo_dono=?, image_path=?, padron=?, tipo_vehiculo=?, combustible=?, numero_motor=?, numero_chasis=? WHERE id=?";
        
        try (Connection conn = ConexaoBD.getConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehiculo.getMarca());
            stmt.setString(2, vehiculo.getModelo());
            stmt.setInt(3, vehiculo.getKm());
            stmt.setBigDecimal(4, vehiculo.getPreco());
            stmt.setString(5, vehiculo.getCor());
            stmt.setString(6, vehiculo.getPlaca());
            stmt.setInt(7, vehiculo.getAno());
            stmt.setString(8, vehiculo.getCidade());
            stmt.setBoolean(9, vehiculo.isDisponivel());
            
            if (vehiculo.getIdAntigoDono() != null && vehiculo.getIdAntigoDono() > 0) {
                stmt.setInt(10, vehiculo.getIdAntigoDono());
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }
            
            if (vehiculo.getImagePath() != null && vehiculo.getImagePath().length > 0) {
                stmt.setBytes(11, vehiculo.getImagePath());
            } else {
                // Para coluna BYTEA, não use Types.BLOB (mapeia a OID). Use BINARY/VARBINARY.
                stmt.setNull(11, Types.BINARY);
            }
            stmt.setInt(12, vehiculo.getPadron());
            stmt.setString(13, vehiculo.getTipoVehiculo());
            stmt.setString(14, vehiculo.getCombustible());
            stmt.setString(15, vehiculo.getNumeroMotor());
            stmt.setString(16, vehiculo.getNumeroChasis());
            stmt.setInt(17, vehiculo.getId());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("!!! ERROR SQL al actualizar vehículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM veiculos WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("!!! ERROR SQL al deletar veículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public Vehiculo buscarPorPadron(int padron) {
     String sql = "SELECT * FROM veiculos WHERE padron = ?";
        try (Connection conn = ConexaoBD.getConexao(); 
          PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setInt(1, padron);
         try (ResultSet rs = stmt.executeQuery()) {
             if (rs.next()) {
                 return extractVehiculoFromResultSet(rs); // Reutiliza o método que você já tem
             }
          }
        } catch (SQLException e) {
          System.err.println("!!! ERROR SQL al buscar veículo por padrón: " + e.getMessage());
           e.printStackTrace();
        }
     return null;
    }

    public boolean marcarComoDisponivel(int id) {
      String sql = "UPDATE veiculos SET disponivel = true WHERE id = ?";
    
     try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
         
           stmt.setInt(1, id);
          int filasAfectadas = stmt.executeUpdate();
          return filasAfectadas > 0;
        
     } catch (SQLException e) {
         System.err.println("Erro ao marcar veículo como disponível: " + e.getMessage());
            e.printStackTrace();
            return false;
     }
    }

    @Override
    public List<Vehiculo> listar() {
        List<Vehiculo> vehiculos = new ArrayList<>();
         String sql = "SELECT v.*, ad.nome AS nome_antigo_dono, ad.cidade AS cidade_antigo_dono, ad.telefone AS telefone_antigo_dono, ad.cedula AS cedula_antigo_dono FROM veiculos v LEFT JOIN antigos_donos ad ON v.id_antigo_dono = ad.id";

        try (Connection conn = ConexaoBD.getConexao(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(extractVehiculoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("!!! ERROR SQL al listar veículos: " + e.getMessage());
            e.printStackTrace();
        }
        return vehiculos;
    }

    @Override
    public Vehiculo buscarPorId(int id) {
            String sql = "SELECT v.*, ad.nome AS nome_antigo_dono, ad.cidade AS cidade_antigo_dono, ad.telefone AS telefone_antigo_dono, ad.cedula AS cedula_antigo_dono FROM veiculos v LEFT JOIN antigos_donos ad ON v.id_antigo_dono = ad.id WHERE v.id = ?";        try (Connection conn = ConexaoBD.getConexao(); 
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractVehiculoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("!!! ERROR SQL al buscar veículo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private Vehiculo extractVehiculoFromResultSet(ResultSet rs) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(rs.getInt("id"));
        vehiculo.setMarca(rs.getString("marca"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setKm(rs.getInt("km"));
        vehiculo.setPreco(rs.getBigDecimal("preco"));
        vehiculo.setCor(rs.getString("cor"));
        vehiculo.setPlaca(rs.getString("placa"));
        vehiculo.setAno(rs.getInt("ano"));
        vehiculo.setCidade(rs.getString("cidade"));
        vehiculo.setDisponivel(rs.getBoolean("disponivel"));
        vehiculo.setImagePath(rs.getBytes("image_path"));
        vehiculo.setPadron(rs.getInt("padron"));
        vehiculo.setTipoVehiculo(rs.getString("tipo_vehiculo"));
        vehiculo.setCombustible(rs.getString("combustible"));
        vehiculo.setNumeroMotor(rs.getString("numero_motor"));
        vehiculo.setNumeroChasis(rs.getString("numero_chasis"));
        
        int idDono = rs.getInt("id_antigo_dono");
        if (!rs.wasNull()) {
            vehiculo.setIdAntigoDono(idDono);
        } else {
             vehiculo.setIdAntigoDono(null);
        }
        
        try {
            vehiculo.setNomeAntigoDono(rs.getString("nome_antigo_dono"));
            vehiculo.setCidadeAntigoDono(rs.getString("cidade_antigo_dono"));
            vehiculo.setTelefoneAntigoDono(rs.getString("telefone_antigo_dono"));
            vehiculo.setCedulaAntigoDono(rs.getString("cedula_antigo_dono"));
        } catch (SQLException e) {
            vehiculo.setNomeAntigoDono(null);
            vehiculo.setCidadeAntigoDono(null);
            vehiculo.setTelefoneAntigoDono(null);
            vehiculo.setCedulaAntigoDono(null);
        }
        
        return vehiculo;
    }
    
public List<Vehiculo> listarVehiculosDisponibles() {
    List<Vehiculo> vehiculos = new ArrayList<>();
    String sql = "SELECT v.id, v.marca, v.modelo, v.cor, v.placa, v.padron, v.preco, v.id_antigo_dono, v.tipo_vehiculo, v.combustible, v.numero_motor, v.numero_chasis, ad.nome AS nome_antigo_dono, ad.cidade AS cidade_antigo_dono, ad.telefone AS telefone_antigo_dono, ad.cedula AS cedula_antigo_dono FROM veiculos v LEFT JOIN antigos_donos ad ON v.id_antigo_dono = ad.id WHERE v.disponivel = TRUE ORDER BY v.marca, v.modelo";

    try (Connection conn = ConexaoBD.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setId(rs.getInt("id"));
            vehiculo.setMarca(rs.getString("marca"));
            vehiculo.setModelo(rs.getString("modelo"));
            vehiculo.setCor(rs.getString("cor"));
            vehiculo.setPlaca(rs.getString("placa"));
            vehiculo.setPadron(rs.getInt("padron"));
            vehiculo.setPreco(rs.getBigDecimal("preco"));
            vehiculo.setTipoVehiculo(rs.getString("tipo_vehiculo"));
            vehiculo.setCombustible(rs.getString("combustible"));
            vehiculo.setNumeroMotor(rs.getString("numero_motor"));
            vehiculo.setNumeroChasis(rs.getString("numero_chasis"));
            vehiculos.add(vehiculo);
        }
    } catch (SQLException e) {
        System.err.println("!!! ERROR SQL al listar vehículos disponibles: " + e.getMessage());
        e.printStackTrace();
    }
    return vehiculos;
}
    
    public Vehiculo inserirComRetorno(Vehiculo vehiculo) {
    boolean sucesso = this.inserir(vehiculo);
    
    if (sucesso) {
        return vehiculo;
    } else {
        return null;
    }
}
    
}

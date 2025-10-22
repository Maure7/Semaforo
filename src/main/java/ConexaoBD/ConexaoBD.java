package ConexaoBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String HOST = "ep-late-cell-acjamihd-pooler.sa-east-1.aws.neon.tech"; 
    private static final String DATABASE = "neondb"; 
    private static final String USUARIO = "neondb_owner";     
    private static final String SENHA = "npg_bckmxDf6K9nJ"; 
    private static final String PORT = "5432"; 
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE + "?sslmode=require";    
    private static Connection conexao;

    public static Connection getConexao() {
        try {
            Class.forName("org.postgresql.Driver");  // driver de PostgreSQL
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            return conexao;
        } catch (ClassNotFoundException e) {
            System.err.println("!!! Driver JDBC no encontrado: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("!!! Error al conectar con BD: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
                conexao = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

package ConexaoBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    // Lee primero de variables de entorno para no hardcodear secretos.
    // Si no están definidas, usa valores por defecto (solo para desarrollo local).
    private static final String HOST = getenvOrDefault("DB_HOST", "ep-late-cell-acjamihd-pooler.sa-east-1.aws.neon.tech");
    private static final String PORT = getenvOrDefault("DB_PORT", "5432");
    private static final String DATABASE = getenvOrDefault("DB_NAME", "neondb");
    private static final String USUARIO = getenvOrDefault("DB_USER", "neondb_owner");
    private static final String SENHA = getenvOrDefault("DB_PASSWORD", "npg_bckmxDf6K9nJ");
    private static final String SSLMODE = getenvOrDefault("DB_SSLMODE", "require");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE + "?sslmode=" + SSLMODE;
    private static Connection conexao;

    public static Connection getConexao() {
        try {
            Class.forName("org.postgresql.Driver");
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            return conexao;
        } catch (ClassNotFoundException e) {
            System.err.println("!!! Driver JDBC no encontrado: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("!!! Error al conectar con BD: " + e.getMessage());
            System.err.println("URL: " + URL);
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

    private static String getenvOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? def : v;
    }
}

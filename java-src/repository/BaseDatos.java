package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Clase BaseDatos — Capa de persistencia / utilidad.
 * Simula un driver de conexión a base de datos relacional (MySQL/MariaDB).
 * En producción, aquí se usaría JDBC (java.sql.Connection, PreparedStatement, ResultSet).
 *
 * Equivalente Java al PDO de PHP.
 * Dependencias: Libro y Prestamo dependen de esta clase para sus operaciones CRUD.
 */
public class BaseDatos {

    // ─────────────────────────────────────────────
    // Atributo de conexión (simulado)
    // En producción sería: java.sql.Connection conexion;
    // ─────────────────────────────────────────────
    private String  conexion;   // Simula el objeto de conexión (en JDBC sería Connection)
    private String  host;
    private String  usuario;
    private String  contrasena;
    private String  nombreBD;
    private boolean conectado;

    // ─────────────────────────────────────────────
    // Constantes de configuración de BD
    // En producción, vendrían de un archivo .properties o variables de entorno
    // ─────────────────────────────────────────────
    private static final String HOST_DEFAULT    = "localhost";
    private static final String USUARIO_DEFAULT = "root";
    private static final String CLAVE_DEFAULT   = "";
    private static final String BD_DEFAULT      = "biblioteca_web";
    private static final String URL_JDBC        =
            "jdbc:mysql://" + HOST_DEFAULT + ":3306/" + BD_DEFAULT +
            "?useSSL=false&serverTimezone=UTC";

    // ─────────────────────────────────────────────
    // Constructor por defecto
    // ─────────────────────────────────────────────
    public BaseDatos() {
        this.host      = HOST_DEFAULT;
        this.usuario   = USUARIO_DEFAULT;
        this.contrasena = CLAVE_DEFAULT;
        this.nombreBD  = BD_DEFAULT;
        this.conectado = false;
    }

    // ─────────────────────────────────────────────
    // Constructor parametrizado (permite configurar BD externa)
    // ─────────────────────────────────────────────
    public BaseDatos(String host, String usuario, String contrasena, String nombreBD) {
        this.host      = host;
        this.usuario   = usuario;
        this.contrasena = contrasena;
        this.nombreBD  = nombreBD;
        this.conectado = false;
    }

    // ─────────────────────────────────────────────
    // Métodos de conexión
    // ─────────────────────────────────────────────

    /**
     * Establece la conexión con la base de datos MySQL.
     * En producción usaría DriverManager.getConnection(URL_JDBC, usuario, contrasena).
     *
     * @return true si la conexión fue exitosa.
     */
    public boolean conectar() {
        // En producción:
        // Class.forName("com.mysql.cj.jdbc.Driver");
        // this.conexion = DriverManager.getConnection(URL_JDBC, usuario, contrasena);
        this.conexion = "CONEXION_SIMULADA://" + host + "/" + nombreBD;
        this.conectado = true;
        System.out.println("[BaseDatos] Conectado a: " + URL_JDBC);
        return true;
    }

    /**
     * Cierra la conexión activa con la base de datos y libera recursos.
     * En producción llamaría a connection.close().
     */
    public void desconectar() {
        // En producción: if (conexion != null && !conexion.isClosed()) conexion.close();
        this.conexion  = null;
        this.conectado = false;
        System.out.println("[BaseDatos] Conexión cerrada correctamente.");
    }

    // ─────────────────────────────────────────────
    // Métodos de ejecución de consultas
    // ─────────────────────────────────────────────

    /**
     * Ejecuta una consulta SQL genérica y retorna el resultado como Object.
     * Útil para consultas SELECT que retornan un único registro.
     * En producción usaría PreparedStatement + ResultSet.
     *
     * @param sql Sentencia SQL a ejecutar.
     * @return Objeto con el resultado de la consulta (Row/Map en producción).
     */
    public Object ejecutarConsulta(String sql) {
        // En producción:
        // PreparedStatement stmt = conexion.prepareStatement(sql);
        // ResultSet rs = stmt.executeQuery();
        // return rs.next() ? rs : null;
        verificarConexion();
        System.out.println("[BaseDatos] Ejecutando consulta: " + sql);
        return new Object(); // Simulación: retorna un objeto genérico
    }

    /**
     * Ejecuta una consulta SELECT y retorna una lista de registros.
     * Equivalente a fetchAll() en PDO.
     *
     * @param sql Sentencia SELECT a ejecutar.
     * @return Lista de objetos que representan los registros encontrados.
     */
    public List<Object> obtenerRegistros(String sql) {
        // En producción:
        // PreparedStatement stmt = conexion.prepareStatement(sql);
        // ResultSet rs = stmt.executeQuery();
        // List<Map<String,Object>> lista = new ArrayList<>();
        // while (rs.next()) { /* mapear columnas a Map y agregar a lista */ }
        // return lista;
        verificarConexion();
        System.out.println("[BaseDatos] Obteniendo registros: " + sql);
        return new ArrayList<>(); // Simulación: lista vacía
    }

    /**
     * Ejecuta una sentencia INSERT con parámetros nombrados.
     * Usa un Map<String,Object> para evitar SQL Injection (PreparedStatement en producción).
     *
     * @param sql   Sentencia INSERT con placeholders.
     * @param datos Mapa con los valores a insertar (clave = nombre columna).
     * @return true si al menos una fila fue insertada.
     */
    public boolean insertar(String sql, Map<String, Object> datos) {
        // En producción:
        // PreparedStatement stmt = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        // datos.forEach((col, val) -> stmt.setObject(indice, val));
        // int filasAfectadas = stmt.executeUpdate();
        // return filasAfectadas > 0;
        verificarConexion();
        System.out.println("[BaseDatos] Insertando registro con SQL: " + sql);
        System.out.println("[BaseDatos] Datos: " + datos);
        return true; // Simulación: siempre exitoso
    }

    /**
     * Ejecuta una sentencia UPDATE o DELETE sobre la base de datos.
     *
     * @param sql Sentencia SQL de actualización.
     * @return true si al menos una fila fue afectada.
     */
    public boolean actualizar(String sql) {
        // En producción:
        // PreparedStatement stmt = conexion.prepareStatement(sql);
        // int filasAfectadas = stmt.executeUpdate();
        // return filasAfectadas > 0;
        verificarConexion();
        System.out.println("[BaseDatos] Actualizando con SQL: " + sql);
        return true; // Simulación: siempre exitoso
    }

    /**
     * Ejecuta una sentencia DELETE sobre la base de datos.
     *
     * @param sql Sentencia DELETE a ejecutar.
     * @return true si al menos una fila fue eliminada.
     */
    public boolean eliminar(String sql) {
        // En producción:
        // PreparedStatement stmt = conexion.prepareStatement(sql);
        // int filasAfectadas = stmt.executeUpdate();
        // return filasAfectadas > 0;
        verificarConexion();
        System.out.println("[BaseDatos] Eliminando con SQL: " + sql);
        return true; // Simulación: siempre exitoso
    }

    // ─────────────────────────────────────────────
    // Método auxiliar privado
    // ─────────────────────────────────────────────

    /**
     * Verifica que haya una conexión activa antes de ejecutar cualquier operación.
     * Lanza una excepción si no hay conexión establecida.
     */
    private void verificarConexion() {
        if (!conectado || conexion == null) {
            throw new IllegalStateException(
                "[BaseDatos] ERROR: No hay conexión activa. Llame a conectar() primero.");
        }
    }

    // ─────────────────────────────────────────────
    // Getters y Setters
    // ─────────────────────────────────────────────

    public String getConexion()   { return conexion;   }
    public boolean isConectado()  { return conectado;  }
    public String getHost()       { return host;       }
    public void setHost(String host) { this.host = host; }
    public String getNombreBD()   { return nombreBD;   }
    public void setNombreBD(String nombreBD) { this.nombreBD = nombreBD; }

    @Override
    public String toString() {
        return "BaseDatos{" +
                "host='" + host + '\'' +
                ", nombreBD='" + nombreBD + '\'' +
                ", conectado=" + conectado +
                '}';
    }
}

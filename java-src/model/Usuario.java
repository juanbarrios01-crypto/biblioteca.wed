package model;

/**
 * Clase base del sistema de biblioteca.
 * Representa a cualquier usuario del sistema (Administrador o Estudiante).
 * Aplica encapsulamiento completo con atributos privados y acceso mediante getters/setters.
 */
public class Usuario {

    // ─────────────────────────────────────────────
    // Atributos privados (encapsulamiento)
    // ─────────────────────────────────────────────
    private int    idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;  // Se evita tilde en nombre de variable
    private String rol;

    // ─────────────────────────────────────────────
    // Constructor por defecto
    // ─────────────────────────────────────────────
    public Usuario() {}

    // ─────────────────────────────────────────────
    // Constructor parametrizado
    // ─────────────────────────────────────────────
    public Usuario(int idUsuario, String nombre, String correo,
                   String contrasena, String rol) {
        this.idUsuario  = idUsuario;
        this.nombre     = nombre;
        this.correo     = correo;
        this.contrasena = contrasena;
        this.rol        = rol;
    }

    // ─────────────────────────────────────────────
    // Métodos de negocio
    // ─────────────────────────────────────────────

    /**
     * Valida las credenciales del usuario contra la base de datos.
     *
     * @param correo     Correo electrónico del usuario.
     * @param contrasena Contraseña ingresada.
     * @return true si las credenciales son correctas, false en caso contrario.
     */
    public boolean iniciarSesion(String correo, String contrasena) {
        // Consulta a la BD verificando correo y contraseña (hash)
        // Ejemplo: SELECT * FROM usuarios WHERE correo = ? AND contrasena = SHA2(?, 256)
        System.out.println("[Usuario] Iniciando sesión para: " + correo);
        return this.correo.equals(correo) && this.contrasena.equals(contrasena);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param nombre     Nombre completo.
     * @param correo     Correo electrónico único.
     * @param contrasena Contraseña en texto plano (se hasheará antes de persistir).
     * @param rol        Rol asignado: "Administrador" o "Estudiante".
     * @return true si el registro fue exitoso.
     */
    public boolean registrarse(String nombre, String correo,
                               String contrasena, String rol) {
        // INSERT INTO usuarios (nombre, correo, contrasena, rol)
        // VALUES (?, ?, SHA2(?, 256), ?)
        this.nombre     = nombre;
        this.correo     = correo;
        this.contrasena = contrasena;
        this.rol        = rol;
        System.out.println("[Usuario] Usuario registrado: " + nombre + " | Rol: " + rol);
        return true;
    }

    /**
     * Cierra la sesión activa del usuario (invalida el token/sesión en el servidor).
     */
    public void cerrarSesion() {
        // Invalida el token JWT o la sesión HTTP activa del usuario
        System.out.println("[Usuario] Sesión cerrada para: " + this.correo);
    }

    /**
     * Retorna el objeto Usuario con los datos del perfil actual.
     *
     * @return Instancia del usuario con sus datos cargados.
     */
    public Usuario getPerfil() {
        // SELECT * FROM usuarios WHERE idUsuario = ?
        System.out.println("[Usuario] Obteniendo perfil de: " + this.nombre);
        return this;
    }

    // ─────────────────────────────────────────────
    // Getters y Setters
    // ─────────────────────────────────────────────

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // ─────────────────────────────────────────────
    // toString para depuración
    // ─────────────────────────────────────────────
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}

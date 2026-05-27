package model;

/**
 * Clase Administrador — hereda de Usuario.
 * Gestiona el catálogo de libros, los usuarios del sistema y los reportes.
 * Relación: Un Administrador (1) gestiona muchos Prestamo (*).
 */
public class Administrador extends Usuario {

    // ─────────────────────────────────────────────
    // Atributo propio (FK hacia usuarios)
    // ─────────────────────────────────────────────
    private int idUsuario; // Clave foránea que referencia a Usuario

    // ─────────────────────────────────────────────
    // Constructor por defecto
    // ─────────────────────────────────────────────
    public Administrador() {
        super();
    }

    // ─────────────────────────────────────────────
    // Constructor parametrizado — usa super() para
    // inicializar los campos heredados de Usuario
    // ─────────────────────────────────────────────
    public Administrador(int idUsuario, String nombre, String correo,
                         String contrasena) {
        super(idUsuario, nombre, correo, contrasena, "Administrador");
        this.idUsuario = idUsuario;
    }

    // ─────────────────────────────────────────────
    // Métodos de gestión de libros
    // ─────────────────────────────────────────────

    /**
     * Agrega un nuevo libro al catálogo de la biblioteca.
     *
     * @param libro Objeto Libro con los datos a persistir.
     * @return true si el libro fue insertado correctamente.
     */
    public boolean agregarLibro(Libro libro) {
        // INSERT INTO libros (titulo, autor, categoria, editorial,
        //   anioPublicacion, isbn, disponible)
        // VALUES (?, ?, ?, ?, ?, ?, 1)
        System.out.println("[Administrador] Agregando libro: " + libro.getTitulo());
        return libro.agregar();
    }

    /**
     * Edita los datos de un libro existente en el catálogo.
     *
     * @param libro Objeto Libro con los datos actualizados (debe contener idLibro).
     * @return true si la actualización fue exitosa.
     */
    public boolean editarLibro(Libro libro) {
        // UPDATE libros SET titulo=?, autor=?, categoria=?, editorial=?,
        //   anioPublicacion=?, isbn=? WHERE idLibro=?
        System.out.println("[Administrador] Editando libro ID: " + libro.getIdLibro());
        return libro.editar();
    }

    /**
     * Elimina permanentemente un libro del catálogo por su ID.
     *
     * @param idLibro Identificador del libro a eliminar.
     * @return true si la eliminación fue exitosa.
     */
    public boolean eliminarLibro(int idLibro) {
        // DELETE FROM libros WHERE idLibro = ?
        System.out.println("[Administrador] Eliminando libro ID: " + idLibro);
        Libro libro = new Libro();
        libro.setIdLibro(idLibro);
        return libro.eliminar();
    }

    /**
     * Desactiva un libro para que no sea visible ni prestable (borrado lógico).
     *
     * @param idLibro Identificador del libro a desactivar.
     * @return true si la desactivación fue exitosa.
     */
    public boolean desactivarLibro(int idLibro) {
        // UPDATE libros SET disponible = 0 WHERE idLibro = ?
        System.out.println("[Administrador] Desactivando libro ID: " + idLibro);
        Libro libro = new Libro();
        libro.setIdLibro(idLibro);
        return libro.desactivar();
    }

    /**
     * Permite al administrador ver y gestionar todos los usuarios registrados.
     * Lista usuarios activos, cambia roles o desactiva cuentas.
     */
    public void gestionarUsuarios() {
        // SELECT * FROM usuarios ORDER BY nombre ASC
        // Puede también incluir: UPDATE, DELETE lógico sobre usuarios
        System.out.println("[Administrador] Gestionando usuarios del sistema...");
    }

    /**
     * Genera reportes estadísticos del sistema (préstamos, devoluciones, usuarios).
     * Los reportes pueden exportarse a PDF o CSV.
     */
    public void generarReportes() {
        // SELECT COUNT(*) FROM prestamos WHERE estado = 'Pendiente'
        // SELECT COUNT(*) FROM prestamos WHERE estado = 'Devuelto'
        // Agrupa datos por período de tiempo para dashboard de estadísticas
        System.out.println("[Administrador] Generando reportes del sistema...");
    }

    // ─────────────────────────────────────────────
    // Getter y Setter del idUsuario (FK)
    // ─────────────────────────────────────────────

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + getNombre() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                '}';
    }
}

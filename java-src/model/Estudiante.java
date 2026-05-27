package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Estudiante — hereda de Usuario.
 * Representa a los estudiantes que consultan y solicitan préstamos de libros.
 * Dependencia hacia Libro: usa sus métodos de búsqueda.
 */
public class Estudiante extends Usuario {

    // ─────────────────────────────────────────────
    // Atributo propio (FK hacia usuarios)
    // ─────────────────────────────────────────────
    private int idUsuario; // Clave foránea que referencia a Usuario

    // ─────────────────────────────────────────────
    // Constructor por defecto
    // ─────────────────────────────────────────────
    public Estudiante() {
        super();
    }

    // ─────────────────────────────────────────────
    // Constructor parametrizado — usa super() para
    // inicializar los campos heredados de Usuario
    // ─────────────────────────────────────────────
    public Estudiante(int idUsuario, String nombre, String correo,
                      String contrasena) {
        super(idUsuario, nombre, correo, contrasena, "Estudiante");
        this.idUsuario = idUsuario;
    }

    // ─────────────────────────────────────────────
    // Métodos de negocio — dependencia hacia Libro
    // ─────────────────────────────────────────────

    /**
     * Busca libros en el catálogo según un filtro (título, autor, categoría, ISBN).
     * Delega la búsqueda al método buscar() de la clase Libro.
     *
     * @param filtro Texto de búsqueda libre.
     * @return Lista de libros que coincidan con el filtro.
     */
    public List<Libro> buscarLibro(String filtro) {
        // SELECT * FROM libros
        // WHERE titulo LIKE ? OR autor LIKE ? OR categoria LIKE ? OR isbn = ?
        // AND disponible = 1
        System.out.println("[Estudiante] Buscando libros con filtro: " + filtro);
        Libro libro = new Libro(); // Dependencia hacia Libro (uso de sus métodos)
        return libro.buscar(filtro);
    }

    /**
     * Solicita un préstamo para un libro disponible.
     * Crea un nuevo registro en la tabla prestamos.
     *
     * @param idLibro Identificador del libro a solicitar.
     * @return Objeto Prestamo creado con los datos del préstamo.
     */
    public Prestamo solicitarPrestamo(int idLibro) {
        // INSERT INTO prestamos (idUsuario, idLibro, fechaPrestamo, estado)
        // VALUES (?, ?, NOW(), 'Pendiente')
        // Además actualiza disponible = 0 en libros WHERE idLibro = ?
        System.out.println("[Estudiante] Solicitando préstamo del libro ID: " + idLibro);
        Prestamo prestamo = new Prestamo();
        prestamo.crearPrestamo(this.idUsuario, idLibro);
        return prestamo;
    }

    /**
     * Obtiene la lista de todos los préstamos activos e históricos del estudiante.
     *
     * @return Lista de préstamos asociados al estudiante.
     */
    public List<Prestamo> verMisPrestamos() {
        // SELECT * FROM prestamos WHERE idUsuario = ? ORDER BY fechaPrestamo DESC
        System.out.println("[Estudiante] Consultando préstamos del usuario ID: " + idUsuario);
        Prestamo prestamo = new Prestamo();
        return prestamo.obtenerPrestamosUsuario(this.idUsuario);
    }

    /**
     * Registra la devolución de un libro prestado.
     *
     * @param idPrestamo Identificador del préstamo a cerrar.
     * @return true si la devolución fue registrada exitosamente.
     */
    public boolean devolverLibro(int idPrestamo) {
        // UPDATE prestamos SET fechaDevolucion = NOW(), estado = 'Devuelto'
        // WHERE idPrestamo = ? AND idUsuario = ?
        // Además actualiza disponible = 1 en libros para ese libro
        System.out.println("[Estudiante] Devolviendo libro del préstamo ID: " + idPrestamo);
        Prestamo prestamo = new Prestamo();
        return prestamo.devolverLibro(idPrestamo);
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
        return "Estudiante{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + getNombre() + '\'' +
                ", correo='" + getCorreo() + '\'' +
                '}';
    }
}

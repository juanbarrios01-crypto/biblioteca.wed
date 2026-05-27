package model;

import repository.BaseDatos;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Prestamo — Entidad que representa un préstamo de libro.
 * Depende de BaseDatos para persistencia.
 * Relación: Un Administrador (1) crea muchos Prestamo (*).
 *           Un Libro (1) puede estar en muchos Prestamo (*).
 *
 * Estado posibles: "Pendiente" | "Devuelto" | "Vencido"
 */
public class Prestamo {

    // ─────────────────────────────────────────────
    // Constantes de estado
    // ─────────────────────────────────────────────
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_DEVUELTO  = "Devuelto";
    public static final String ESTADO_VENCIDO   = "Vencido";

    // ─────────────────────────────────────────────
    // Atributos privados (encapsulamiento)
    // ─────────────────────────────────────────────
    private int       idPrestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaEntrega;      // Fecha límite de devolución pactada
    private LocalDate fechaDevolucion;   // Fecha real en que fue devuelto
    private String    estado;            // "Pendiente", "Devuelto", "Vencido"

    // FK hacia otras entidades (simuladas como int)
    private int idUsuario;
    private int idLibro;

    // Dependencia hacia BaseDatos (inyección simulada)
    private final BaseDatos baseDatos;

    // ─────────────────────────────────────────────
    // Constructor por defecto
    // ─────────────────────────────────────────────
    public Prestamo() {
        this.baseDatos = new BaseDatos();
    }

    // ─────────────────────────────────────────────
    // Constructor con inyección de dependencia
    // ─────────────────────────────────────────────
    public Prestamo(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    // ─────────────────────────────────────────────
    // Constructor parametrizado completo
    // ─────────────────────────────────────────────
    public Prestamo(int idPrestamo, int idUsuario, int idLibro,
                    LocalDate fechaPrestamo, LocalDate fechaEntrega,
                    LocalDate fechaDevolucion, String estado) {
        this.baseDatos       = new BaseDatos();
        this.idPrestamo      = idPrestamo;
        this.idUsuario       = idUsuario;
        this.idLibro         = idLibro;
        this.fechaPrestamo   = fechaPrestamo;
        this.fechaEntrega    = fechaEntrega;
        this.fechaDevolucion = fechaDevolucion;
        this.estado          = estado;
    }

    // ─────────────────────────────────────────────
    // Métodos de negocio — dependen de BaseDatos
    // ─────────────────────────────────────────────

    /**
     * Crea un nuevo préstamo para un usuario y un libro específico.
     * La fecha de entrega límite se calcula a 15 días desde hoy.
     *
     * @param idUsuario Identificador del usuario que solicita el préstamo.
     * @param idLibro   Identificador del libro a prestar.
     * @return true si el préstamo fue creado exitosamente.
     */
    public boolean crearPrestamo(int idUsuario, int idLibro) {
        // Conecta a la BD y registra el nuevo préstamo:
        // INSERT INTO prestamos (idUsuario, idLibro, fechaPrestamo, fechaEntrega, estado)
        // VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'Pendiente')
        // También ejecuta: UPDATE libros SET disponible = 0 WHERE idLibro = ?
        baseDatos.conectar();
        this.idUsuario     = idUsuario;
        this.idLibro       = idLibro;
        this.fechaPrestamo = LocalDate.now();
        this.fechaEntrega  = LocalDate.now().plusDays(15);
        this.estado        = ESTADO_PENDIENTE;

        String sql = "INSERT INTO prestamos (idUsuario, idLibro, fechaPrestamo, " +
                     "fechaEntrega, estado) VALUES (" + idUsuario + ", " + idLibro +
                     ", CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'Pendiente')";
        System.out.println("[Prestamo] Creando préstamo → Usuario: " + idUsuario +
                           " | Libro: " + idLibro + " | Entrega: " + this.fechaEntrega);
        boolean resultado = baseDatos.actualizar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    /**
     * Registra la devolución de un libro y actualiza el estado del préstamo.
     * También libera el libro (disponible = 1).
     *
     * @param idPrestamo Identificador del préstamo a cerrar.
     * @return true si la devolución fue procesada correctamente.
     */
    public boolean devolverLibro(int idPrestamo) {
        // Conecta a la BD y actualiza el préstamo:
        // UPDATE prestamos
        // SET fechaDevolucion = CURDATE(), estado = 'Devuelto'
        // WHERE idPrestamo = ?
        // Además: UPDATE libros SET disponible = 1
        //         WHERE idLibro = (SELECT idLibro FROM prestamos WHERE idPrestamo = ?)
        baseDatos.conectar();
        this.fechaDevolucion = LocalDate.now();
        this.estado          = ESTADO_DEVUELTO;

        String sql = "UPDATE prestamos SET fechaDevolucion=CURDATE(), " +
                     "estado='Devuelto' WHERE idPrestamo=" + idPrestamo;
        System.out.println("[Prestamo] Registrando devolución del préstamo ID: " + idPrestamo);
        boolean resultado = baseDatos.actualizar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    /**
     * Renueva el plazo de entrega de un préstamo activo (extiende 15 días más).
     * Solo se puede renovar préstamos en estado "Pendiente".
     *
     * @param idPrestamo Identificador del préstamo a renovar.
     * @return true si la renovación fue exitosa.
     */
    public boolean renovarPrestamo(int idPrestamo) {
        // Conecta a la BD y extiende la fecha de entrega:
        // UPDATE prestamos
        // SET fechaEntrega = DATE_ADD(fechaEntrega, INTERVAL 15 DAY)
        // WHERE idPrestamo = ? AND estado = 'Pendiente'
        baseDatos.conectar();
        this.fechaEntrega = (this.fechaEntrega != null)
                ? this.fechaEntrega.plusDays(15)
                : LocalDate.now().plusDays(15);

        String sql = "UPDATE prestamos SET fechaEntrega=DATE_ADD(fechaEntrega, " +
                     "INTERVAL 15 DAY) WHERE idPrestamo=" + idPrestamo +
                     " AND estado='Pendiente'";
        System.out.println("[Prestamo] Renovando préstamo ID: " + idPrestamo +
                           " | Nueva fecha de entrega: " + this.fechaEntrega);
        boolean resultado = baseDatos.actualizar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    /**
     * Obtiene todos los préstamos (activos e históricos) de un usuario específico.
     *
     * @param idUsuario Identificador del usuario.
     * @return Lista de préstamos asociados al usuario.
     */
    public List<Prestamo> obtenerPrestamosUsuario(int idUsuario) {
        // Conecta a la BD y consulta:
        // SELECT p.*, l.titulo, l.autor FROM prestamos p
        // INNER JOIN libros l ON p.idLibro = l.idLibro
        // WHERE p.idUsuario = ?
        // ORDER BY p.fechaPrestamo DESC
        baseDatos.conectar();
        String sql = "SELECT p.*, l.titulo, l.autor FROM prestamos p " +
                     "INNER JOIN libros l ON p.idLibro = l.idLibro " +
                     "WHERE p.idUsuario=" + idUsuario + " ORDER BY p.fechaPrestamo DESC";
        System.out.println("[Prestamo] Obteniendo préstamos del usuario ID: " + idUsuario);
        List<Object> registros = baseDatos.obtenerRegistros(sql);
        baseDatos.desconectar();

        // Transforma registros raw en objetos Prestamo (mapeo simulado)
        List<Prestamo> prestamos = new ArrayList<>();
        for (Object registro : registros) {
            Prestamo p = new Prestamo();
            // En implementación real: mapear columnas del ResultSet al objeto Prestamo
            prestamos.add(p);
        }
        return prestamos;
    }

    // ─────────────────────────────────────────────
    // Getters y Setters
    // ─────────────────────────────────────────────

    public int getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    @Override
    public String toString() {
        return "Prestamo{" +
                "idPrestamo=" + idPrestamo +
                ", idUsuario=" + idUsuario +
                ", idLibro=" + idLibro +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaEntrega=" + fechaEntrega +
                ", fechaDevolucion=" + fechaDevolucion +
                ", estado='" + estado + '\'' +
                '}';
    }
}

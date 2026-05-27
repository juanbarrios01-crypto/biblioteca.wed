package model;

import repository.BaseDatos;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Libro — Entidad del catálogo bibliográfico.
 * Depende de BaseDatos para todas las operaciones de persistencia.
 * Relación: Un Libro (1) puede estar en muchos Prestamo (*).
 */
public class Libro {

    // ─────────────────────────────────────────────
    // Atributos privados (encapsulamiento)
    // ─────────────────────────────────────────────
    private int     idLibro;
    private String  titulo;
    private String  autor;
    private String  categoria;
    private String  editorial;
    private int     anioPublicacion;
    private String  isbn;
    private boolean disponible;

    // Dependencia hacia BaseDatos (inyección simulada)
    private final BaseDatos baseDatos;

    // ─────────────────────────────────────────────
    // Constructor por defecto — instancia su propia BD
    // ─────────────────────────────────────────────
    public Libro() {
        this.baseDatos = new BaseDatos();
    }

    // ─────────────────────────────────────────────
    // Constructor con inyección de dependencia
    // Permite pasar una instancia de BaseDatos externa
    // ─────────────────────────────────────────────
    public Libro(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    // ─────────────────────────────────────────────
    // Constructor parametrizado completo
    // ─────────────────────────────────────────────
    public Libro(int idLibro, String titulo, String autor, String categoria,
                 String editorial, int anioPublicacion, String isbn, boolean disponible) {
        this.baseDatos       = new BaseDatos();
        this.idLibro         = idLibro;
        this.titulo          = titulo;
        this.autor           = autor;
        this.categoria       = categoria;
        this.editorial       = editorial;
        this.anioPublicacion = anioPublicacion;
        this.isbn            = isbn;
        this.disponible      = disponible;
    }

    // ─────────────────────────────────────────────
    // Métodos de persistencia — dependen de BaseDatos
    // ─────────────────────────────────────────────

    /**
     * Inserta este libro en la base de datos.
     *
     * @return true si la inserción fue exitosa.
     */
    public boolean agregar() {
        // Conecta a la BD y ejecuta:
        // INSERT INTO libros (titulo, autor, categoria, editorial,
        //   anioPublicacion, isbn, disponible)
        // VALUES (?, ?, ?, ?, ?, ?, 1)
        baseDatos.conectar();
        String sql = "INSERT INTO libros (titulo, autor, categoria, editorial, " +
                     "anioPublicacion, isbn, disponible) VALUES (?, ?, ?, ?, ?, ?, 1)";
        System.out.println("[Libro] Insertando libro: " + this.titulo);
        boolean resultado = baseDatos.actualizar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    /**
     * Actualiza los datos de este libro en la base de datos.
     *
     * @return true si la actualización fue exitosa.
     */
    public boolean editar() {
        // Conecta a la BD y actualiza el estado del libro:
        // UPDATE libros SET titulo=?, autor=?, categoria=?, editorial=?,
        //   anioPublicacion=?, isbn=? WHERE idLibro=?
        baseDatos.conectar();
        String sql = "UPDATE libros SET titulo=?, autor=?, categoria=?, " +
                     "editorial=?, anioPublicacion=?, isbn=? WHERE idLibro=" + this.idLibro;
        System.out.println("[Libro] Editando libro ID: " + this.idLibro);
        boolean resultado = baseDatos.actualizar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    /**
     * Elimina permanentemente este libro de la base de datos.
     *
     * @return true si la eliminación fue exitosa.
     */
    public boolean eliminar() {
        // Conecta a la BD y elimina el registro:
        // DELETE FROM libros WHERE idLibro = ?
        baseDatos.conectar();
        String sql = "DELETE FROM libros WHERE idLibro=" + this.idLibro;
        System.out.println("[Libro] Eliminando libro ID: " + this.idLibro);
        boolean resultado = baseDatos.eliminar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    /**
     * Realiza un borrado lógico marcando el libro como no disponible.
     *
     * @return true si la desactivación fue exitosa.
     */
    public boolean desactivar() {
        // Conecta a la BD y cambia la disponibilidad a false:
        // UPDATE libros SET disponible = 0 WHERE idLibro = ?
        baseDatos.conectar();
        String sql = "UPDATE libros SET disponible=0 WHERE idLibro=" + this.idLibro;
        System.out.println("[Libro] Desactivando libro ID: " + this.idLibro);
        boolean resultado = baseDatos.actualizar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    /**
     * Busca libros en el catálogo según un texto de filtro.
     * Busca por título, autor, categoría o ISBN.
     *
     * @param filtro Texto a buscar (búsqueda parcial con LIKE).
     * @return Lista de libros que coincidan con el criterio.
     */
    public List<Libro> buscar(String filtro) {
        // SELECT * FROM libros
        // WHERE (titulo LIKE '%?%' OR autor LIKE '%?%'
        //   OR categoria LIKE '%?%' OR isbn = ?)
        // AND disponible = 1
        baseDatos.conectar();
        String sql = "SELECT * FROM libros WHERE (titulo LIKE '%" + filtro +
                     "%' OR autor LIKE '%" + filtro + "%' OR categoria LIKE '%" +
                     filtro + "%' OR isbn='" + filtro + "') AND disponible=1";
        System.out.println("[Libro] Buscando libros con filtro: " + filtro);
        List<Object> registros = baseDatos.obtenerRegistros(sql);
        baseDatos.desconectar();

        // Transforma los registros raw en objetos Libro (simulado)
        List<Libro> libros = new ArrayList<>();
        for (Object registro : registros) {
            Libro libro = new Libro();
            // En implementación real: mapear columnas del ResultSet al objeto
            libros.add(libro);
        }
        return libros;
    }

    /**
     * Alterna la disponibilidad del libro (disponible ↔ no disponible).
     *
     * @return true si el cambio fue exitoso.
     */
    public boolean cambiarDisponibilidad() {
        // UPDATE libros SET disponible = NOT disponible WHERE idLibro = ?
        baseDatos.conectar();
        this.disponible = !this.disponible;
        String sql = "UPDATE libros SET disponible=" + (this.disponible ? 1 : 0) +
                     " WHERE idLibro=" + this.idLibro;
        System.out.println("[Libro] Cambiando disponibilidad del libro ID: " +
                           this.idLibro + " → disponible=" + this.disponible);
        boolean resultado = baseDatos.actualizar(sql);
        baseDatos.desconectar();
        return resultado;
    }

    // ─────────────────────────────────────────────
    // Getters y Setters
    // ─────────────────────────────────────────────

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return "Libro{" +
                "idLibro=" + idLibro +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", isbn='" + isbn + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}

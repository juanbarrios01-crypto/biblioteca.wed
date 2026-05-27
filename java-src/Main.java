import model.Administrador;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.Usuario;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         SISTEMA BIBLIOTECA WEB — Clase de Prueba Principal       ║
 * ║  Demuestra el flujo completo del sistema usando todas las clases ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Ejecutar con:
 *   javac -d out java-src/repository/BaseDatos.java java-src/model/*.java java-src/Main.java
 *   java -cp out Main
 */
public class Main {

    public static void main(String[] args) {

        separador("SISTEMA BIBLIOTECA WEB — DEMO");

        // ─────────────────────────────────────────────────────────────
        // 1. REGISTRO Y LOGIN DE ADMINISTRADOR
        // ─────────────────────────────────────────────────────────────
        separador("1. FLUJO DE ADMINISTRADOR");

        Administrador admin = new Administrador(1, "Carlos Pérez",
                "admin@biblioteca.edu", "Admin123!");

        System.out.println("→ Perfil: " + admin.getPerfil());

        boolean loginAdmin = admin.iniciarSesion("admin@biblioteca.edu", "Admin123!");
        System.out.println("→ Login Admin: " + (loginAdmin ? "EXITOSO ✔" : "FALLIDO ✘"));

        // Agregar un libro al catálogo
        Libro libro1 = new Libro(0, "Clean Code", "Robert C. Martin",
                "Programación", "Prentice Hall", 2008, "978-0132350884", true);
        admin.agregarLibro(libro1);

        // Editar un libro existente
        libro1.setTitulo("Clean Code — Edición Revisada");
        admin.editarLibro(libro1);

        // Desactivar un libro (borrado lógico)
        admin.desactivarLibro(99);

        // Gestionar usuarios y generar reportes
        admin.gestionarUsuarios();
        admin.generarReportes();

        admin.cerrarSesion();

        // ─────────────────────────────────────────────────────────────
        // 2. REGISTRO Y FLUJO DE ESTUDIANTE
        // ─────────────────────────────────────────────────────────────
        separador("2. FLUJO DE ESTUDIANTE");

        Estudiante estudiante = new Estudiante(2, "Ana García",
                "ana.garcia@uni.edu", "Pass456!");

        boolean loginEst = estudiante.iniciarSesion("ana.garcia@uni.edu", "Pass456!");
        System.out.println("→ Login Estudiante: " + (loginEst ? "EXITOSO ✔" : "FALLIDO ✘"));

        // Buscar libros por filtro
        List<Libro> resultados = estudiante.buscarLibro("Clean Code");
        System.out.println("→ Libros encontrados: " + resultados.size() +
                           " (simulado — BD vacía en demo)");

        // Solicitar préstamo de un libro
        Prestamo prestamo = estudiante.solicitarPrestamo(1);
        System.out.println("→ Estado del préstamo: " + prestamo.getEstado());
        System.out.println("→ Fecha préstamo : " + prestamo.getFechaPrestamo());
        System.out.println("→ Fecha entrega  : " + prestamo.getFechaEntrega());

        // Ver mis préstamos
        List<Prestamo> misPrestamos = estudiante.verMisPrestamos();
        System.out.println("→ Total préstamos del usuario: " + misPrestamos.size() +
                           " (simulado)");

        // Renovar préstamo
        Prestamo prestamo2 = new Prestamo();
        prestamo2.crearPrestamo(2, 5);
        boolean renovado = prestamo2.renovarPrestamo(1);
        System.out.println("→ Renovación exitosa: " + renovado);

        // Devolver libro
        boolean devuelto = estudiante.devolverLibro(1);
        System.out.println("→ Devolución registrada: " + devuelto);

        estudiante.cerrarSesion();

        // ─────────────────────────────────────────────────────────────
        // 3. OPERACIONES DIRECTAS SOBRE LIBRO
        // ─────────────────────────────────────────────────────────────
        separador("3. OPERACIONES DIRECTAS — LIBRO");

        Libro libro2 = new Libro(2, "Design Patterns", "Gang of Four",
                "Arquitectura", "Addison-Wesley", 1994, "978-0201633610", true);

        libro2.cambiarDisponibilidad(); // disponible → false
        System.out.println("→ Disponible tras cambio: " + libro2.isDisponible());

        libro2.cambiarDisponibilidad(); // false → disponible
        System.out.println("→ Disponible restaurado:  " + libro2.isDisponible());

        // ─────────────────────────────────────────────────────────────
        // 4. RESUMEN DE CLASES
        // ─────────────────────────────────────────────────────────────
        separador("4. RESUMEN DEL SISTEMA");
        System.out.println(admin);
        System.out.println(estudiante);
        System.out.println(libro1);
        System.out.println(libro2);

        separador("FIN DE DEMO");
    }

    // ─────────────────────────────────────────────
    // Utilidad para separar secciones en consola
    // ─────────────────────────────────────────────
    private static void separador(String titulo) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + titulo);
        System.out.println("═".repeat(60));
    }
}

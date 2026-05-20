package principal;

import modelos.Libro;

public class Main {

    public static void main(String[] args) {

        Libro libro1 = new Libro(
                "Cien años de soledad",
                "Gabriel Garcia Marquez"
        );

        System.out.println("===== SISTEMA BIBLIOTECA =====");

        libro1.mostrarLibro();

    }

}
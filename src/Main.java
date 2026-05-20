import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);

        int opcion;

        do {

            System.out.println("\n===== SISTEMA BIBLIOTECA =====");
            System.out.println("1. Agregar libro");
            System.out.println("2. Mostrar libros");
            System.out.println("3. Buscar libro");
            System.out.println("4. Prestar libro");
            System.out.println("5. Devolver libro");
            System.out.println("0. Salir");

            System.out.print("Seleccione una opcion: ");
            opcion = entrada.nextInt();

            switch (opcion) {

                case 1:
                    System.out.println("Agregar libro");
                    break;

                case 2:
                    System.out.println("Mostrar libros");
                    break;

                case 3:
                    System.out.println("Buscar libro");
                    break;

                case 4:
                    System.out.println("Prestar libro");
                    break;

                case 5:
                    System.out.println("Devolver libro");
                    break;

                case 0:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opcion invalida");

            }

        } while (opcion != 0);

    }

}
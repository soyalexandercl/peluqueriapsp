import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Evento {

    // Método para agregar una línea al logs
    public void add(String linea) {
        try (PrintWriter out = new PrintWriter(new FileWriter("logs.txt", true))) {

            out.println(linea); // Guardar la línea en el archivo

            System.out.println(linea); // Mostrar la línea en consola

        } catch (IOException e) {}
    }
}

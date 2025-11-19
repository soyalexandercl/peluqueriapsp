import java.util.Random;
import java.util.concurrent.Semaphore;

public class Cliente extends Thread {
    
    private String nombre; // Nombre del cliente
    private Zona[] zonas; // Zonas que el cliente debe recorrer

    private Semaphore semaphore = new Semaphore(0); // Semáforo

    private Random random = new Random();
    private Evento evento = new Evento(); // Objeto para registrar eventos

    public Cliente(Zona[] zonas) {
        this.nombre = "Cliente-" + Math.abs(random.nextInt()); // Nombre aleatorio
        this.zonas = zonas;
    }

    public String getNombre() {
        return nombre;
    }

    // Método para dormir al cliente
    public void esperar() throws InterruptedException {
        semaphore.acquire();
    }

    // Método para despertar al cliente
    public void activar() {
        semaphore.release();
    }

    @Override
    public void run() {
        try {
            // El cliente recorre todas las zonas en orden
            for (Zona zona : zonas) {

                // El cliente entra en la fila de la zona y espera atención
                zona.entraCliente(this);

                // Una vez atendido, el cliente sale de la zona
                zona.saleCliente(this);
            }

            // Registro al completar el recorrido por todas las zonas
            evento.add(nombre + " completó todas las zonas");
        } catch (Exception e) {}
    }
}
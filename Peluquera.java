import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Peluquera extends Thread {
    private String nombre; // Nombre del cliente
    private int atenciones = 0; // Cantidad total de atenciones
    private Zona[] zonas; // Zonas que la peluquera debe recorrer

    private Lock lock = new ReentrantLock(true); // Lock
    private Condition condicion = lock.newCondition(); // Condición para que la peluquera espere

    private Random random = new Random();
    private Evento evento = new Evento(); // Objeto para registrar eventos

    public Peluquera(Zona[] zonas) {
        this.nombre = "Peluquera-" + Math.abs(random.nextInt()); // Nombre aleatorio
        this.zonas = zonas;
    }

    // Obtener nombre
    public String getNombre() {
        return nombre;
    }

    // Obtener atenciones
    public int getAtenciones() {
        return atenciones;
    }

    // Método para despertar a la peluquera
    public void despertar() {
        lock.lock();
        try {
            condicion.signal(); // Señal
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        try {
            // Corre mientras no se haya interrumpido el hilo
            while (!Thread.currentThread().isInterrupted()) {

                boolean atencion = false; // Si se registra una atencion en el ciclo cambia atrue

                // Recorremos las zonas
                for (Zona zona : zonas) {

                    // Intentamos entrar en la zona
                    if (zona.entraPeluquera(this)) {

                        // Sale de la zona tras atender
                        zona.salePeluquera(this);

                        atencion = true; // Se realizo una atencion
                        atenciones++; // Incrementamos uno en las atenciones
                    }
                }

                // Si en este ciclo no atendió a nadie se bloquea el hilo
                if (!atencion) {
                    lock.lock();
                    try {
                        evento.add(nombre + " espera a que un cliente entre o una peluquera salga de la zona");
                        condicion.await(); // Se bloquea
                    } finally {
                        lock.unlock();
                    }
                }

                // Cada 10 atenciones, la peluquera descansa
                if (atenciones > 0 && atenciones % 10 == 0) {
                    evento.add(nombre + " completo 10 clientes, descansara un momento");
                    // Simulación de tiempo de descanso
                    // Thread.sleep((random.nextInt(10) + 1) * 500);
                }
            }

        } catch (Exception e) {}
    }
}


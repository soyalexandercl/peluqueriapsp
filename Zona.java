import java.util.LinkedList;
import java.util.Queue;
// import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Zona {

    private String nombre; // Nombre de la zona

    // Indica si actualmente hay una peluquera atendiendo
    private boolean existePeluquera = false;

    private Peluquera[] peluqueras; // Arreglo de peluqueras

    // Fila de clientes en la zona
    private Queue<Cliente> fila = new LinkedList<>();

    private Lock lock = new ReentrantLock(true); // Lock

    // private Random random = new Random();

    private Evento evento = new Evento(); // Objeto para registrar eventos

    // Constructor recibiendo el nombre y el arreglo de peluqueras
    public Zona(String nombre, Peluquera[] peluqueras) {
        this.nombre = nombre;
        this.peluqueras = peluqueras;
    }

    // Obtener el nombre de la zona
    public String getNombre() {
        return nombre;
    }

    // Despertar a todas las peluqueras que estén en espera
    public void despertarPeluqueras() {
        // Recorremos todas las peluqueras que existen
        for (Peluquera peluquera : peluqueras) {
            // Obtenemos el estado y comprobamos que el estado sea en espera
            if (peluquera.getState() == Thread.State.WAITING) {
                 peluquera.despertar(); // Despertamos la peluquera
            }
        }
    }

    // Método para que un cliente entre en la fila de la zona
    public void entraCliente(Cliente cliente) throws InterruptedException {
        lock.lock(); // Bloquear
        try {
            fila.add(cliente); // Agregar cliente a la fila

            evento.add(cliente.getNombre() + " entra en la fila de la zona de " + nombre);

            this.despertarPeluqueras(); // Despertar a cualquier peluquera en espera
        } finally {
            lock.unlock(); // Liberar el lock
        }

        cliente.esperar(); // El cliente espera a ser atendido
    }

    // Método para que un cliente salga de la zona
    public void saleCliente(Cliente cliente) throws InterruptedException {
        lock.lock();
        try {
            fila.poll(); // Quitar cliente de la fila

            evento.add(cliente.getNombre() + " ha terminado en la zona de " + nombre);
        } finally {
            lock.unlock();
        }
    }

    // Método para que una peluquera entre y atienda si hay clientes
    public boolean entraPeluquera(Peluquera peluquera) throws InterruptedException {
        lock.lock();
        try {
            evento.add(peluquera.getNombre() + " accede a la zona " + nombre + " y comprueba si está ocupada por otra peluquera y si existen clientes en la fila.");

            // Solo entra si no hay otra peluquera y hay clientes esperando
            if (!existePeluquera && !fila.isEmpty()) {

                existePeluquera = true; // Marcar que la zona está ocupada por una peluquera

                Cliente cliente = fila.peek(); // Tomar al primer cliente en la fila

                evento.add(peluquera.getNombre() + " está atendiendo a " + cliente.getNombre() + " en la zona " + nombre);

                // Simulación de tiempo de atención
                // Thread.sleep((random.nextInt(10) + 1) * 200);

                cliente.activar(); // Activar al cliente para que continúe

                return true;
            }

            return false; // No hay clientes o ya hay peluquera atendiendo
        } finally {
            lock.unlock();
        }
    }

    // Método para que la peluquera salga de la zona
    public void salePeluquera(Peluquera peluquera) throws InterruptedException {
        lock.lock();
        try {
            existePeluquera = false; // La zona queda libre

            evento.add(peluquera.getNombre() + " ha terminado en la zona de " + nombre);

            this.despertarPeluqueras(); // Despertar a otras peluqueras que puedan atender
        } finally {
            lock.unlock();
        }
    }
}
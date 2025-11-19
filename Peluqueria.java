public class Peluqueria {
    public static void main(String[] args) {

        Peluquera[] peluqueras = new Peluquera[3]; // Arreglo de peluqueras
        Cliente[] clientes = new Cliente[500];  // Arreglo de clientes
        
        Evento evento = new Evento(); // Objeto para registrar eventos

        // Arreglo de zonas
        Zona[] zonas = {
            new Zona("lavado", peluqueras),
            new Zona("corte", peluqueras),
            new Zona("tinte", peluqueras),
            new Zona("peinado", peluqueras)
        };

        // Creamos y arrancamos los hilos de las peluqueras
        for (int i = 0; i < peluqueras.length; i++) {
            peluqueras[i] = new Peluquera(zonas); // Creamos las peluqueras pasandole las zonas
            peluqueras[i].start(); // Iniciamos el hilo
        }

        // Creamos y arrancamos los hilos de los clientes
        for (int i = 0; i < clientes.length; i++) {
            clientes[i] = new Cliente(zonas); // Creamos los clientes pasandole las zonas
            clientes[i].start(); // Iniciamos el hilo
        }

        // Esperamos a que todos los clientes terminen su recorrido
        for (Cliente cliente : clientes) {
            try {
                cliente.join(); // Bloquea hasta que el hilo actual termine
            } catch (Exception e) {}
        }

        // Interrumpimos a las peluqueras para indicar que deben terminar la jornada
        for (Peluquera peluquera : peluqueras) {
            peluquera.interrupt(); // Interrumpimos el hilo
        }

        // Esperamos a que todos las peluqueras terminen
        for (Peluquera peluquera : peluqueras) {
            try {
                peluquera.join(); // Bloquea hasta que el hilo actual termine
            } catch (Exception e) {}
        }
        
        // Registrar fin de la jornada
        evento.add("Terminó la jornada");

        // Registrar la cantidad de atenciones por peluquera
        for (Peluquera peluquera : peluqueras) {
            evento.add(peluquera.getNombre() + " en su jornada realizó " + peluquera.getAtenciones() + " atenciones entre las " + zonas.length + " zonas");
        }
    }
}

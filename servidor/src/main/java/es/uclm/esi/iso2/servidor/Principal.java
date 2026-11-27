package es.uclm.esi.iso2.servidor;

import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;

/**
 * Punto de entrada del servidor: lee la configuración, se conecta a MySQL y espera clientes.
 */
public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) throws Exception {
        Configuracion configuracion = Configuracion.delSistema();
        FabricaConexiones conexiones = FabricaConexiones.jdbc(
                configuracion.valor(Configuracion.BD_URL),
                configuracion.valor(Configuracion.BD_USUARIO),
                configuracion.valor(Configuracion.BD_CONTRASENA));

        Servidor servidor = Servidor.crear(configuracion.puerto(), conexiones);
        servidor.arrancar();
        System.out.println("Servidor de radiodiagnóstico escuchando en el puerto " + servidor.puerto()
                + " (base de datos " + configuracion.valor(Configuracion.BD_URL) + ")");
    }
}

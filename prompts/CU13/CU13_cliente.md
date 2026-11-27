# CU13 · Implementación del cliente (issue #34)

- **Herramienta:** Claude (asistente de IA), 26/11/2026.
- **Responsable:** Álvaro Braojos Martín.

## Prompt

> dale maquina vamos con el dale cañaaaaaaaa

(Después de terminar el servidor: «vamos con el cliente de CU13».)

Contexto que se le dio al asistente:

- Implementar el cliente JavaFX de CU13 Gestionar citas médicas siguiendo el diseño
  (DEC-062 a DEC-067) y el servidor ya hecho en `feature/CU13-servidor` (PR #59).
- Seguir la misma estructura que el cliente de CU12: pantallas sin lógica, una clase de control
  que se pueda probar con JUnit, un servicio por sockets y otro en memoria (DEC-059 y DEC-060).
- Usar `ProtocoloCitas` y `CodigoError` del módulo `comun`; no modificar archivos de compañeros
  salvo lo imprescindible.
- Java 21, Maven, JavaFX, JUnit, cobertura de ramas mínima del 75 %.

## Qué se generó

En el paquete `servicio`: `ServicioCitas`, `ServicioCitasSocket`, `ServicioCitasEnMemoria`,
`FabricaServicioCitas` y `ExcepcionServicioCitas`. En `logica`: `GestorCitasCliente`,
`MensajesCitas`, `FormatosCitas`, `Catalogo`, `EntradaCita` y `ResultadoCitas`. En
`presentacion`: `PantallaBusquedaCitas`, `FormularioCita` y `TareaCitas`, con sus FXML. Se añadió
la opción `-Dradiodiagnostico.pantalla=citas` en `AplicacionCliente` y en el `pom.xml` del
cliente. Pruebas JUnit. El código se revisó, se probó con `mvn verify` y se abrieron las
pantallas para comprobar que funcionan.

# CU10 · Implementación del cliente (issue #45)

- **Herramienta:** Claude (asistente de IA), 26/11/2026.
- **Responsable:** Alberto Calvo De Mora Riballo.

## Prompt

> Necesito avanzar con la implementacion, ayudame a hacer la implementacion del CU10

Contexto que se le dio al asistente:

- Implementar el cliente de CU10 Registrar realización de prueba siguiendo el diseño aprobado
  (DEC-074 a DEC-080 en `DECISIONS.md`), con la estructura de CU12: pantallas JavaFX sin
  lógica, clase de control y servicio con implementaciones por sockets y en memoria.
- Reutilizar el protocolo, las validaciones y los códigos de error del módulo `comun`, de modo que
  el cliente valide con las mismas reglas que el servidor.
- Java 21, Maven, JavaFX, JUnit, cobertura de ramas mínima del 75 % (el paquete `presentacion`
  queda excluido).

## Qué se generó

`ServicioPruebas` con sus implementaciones `ServicioPruebasSocket` y `ServicioPruebasEnMemoria`,
`FabricaServicioPruebas`, `ExcepcionServicioPruebas`, `GestorPruebasCliente`, `ResultadoPruebas`,
`MensajesPruebas`, `FormatosPruebas` y la pantalla JavaFX `PantallaPruebas` (solicitudes
pendientes, registro de realización o cancelación, búsqueda, detalle e incidencias); pruebas JUnit
de la lógica y los servicios. El código se revisó y se probó con `mvn verify` y abriendo la
pantalla en modo sin servidor.
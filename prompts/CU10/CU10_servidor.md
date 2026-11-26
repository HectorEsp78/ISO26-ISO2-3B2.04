# CU10 · Implementación del servidor (issue #45)

- **Herramienta:** Claude (asistente de IA), 26/11/2026.
- **Responsable:** Alberto Calvo De Mora Riballo.

## Prompt

> Necesito avanzar con la implementacion, ayudame a hacer la implementacion del CU10

Contexto que se le dio al asistente:

- Implementar CU10 Registrar realización de prueba siguiendo el diseño aprobado
  (DEC-074 a DEC-080 en `DECISIONS.md`).
- Partir de la rama `feature/CU13-servidor`, y reutilizar de ella `ICitaService`
  (`obtenerCita`, `marcarRealizada`, `marcarCancelada`), el protocolo, el escape y los códigos
  de error del módulo `comun`, y el acceso a MySQL.
- CU06 (solicitudes) aún no existe: debe ir detrás de una interfaz provisional que se pueda
  simular en las pruebas, y la comprobación del paciente debe usar el servicio de CU12.
- Registrar una prueba cambia la prueba, la cita y la solicitud, y debe hacerse todo o nada
  (DEC-076): se guarda primero la prueba y, si falla el cambio de la cita o de la solicitud, se
  borra.
- Java 21, Maven, MySQL (H2 en las pruebas), JUnit, cobertura de ramas mínima del 75 %.
- Añadir los códigos de error de CU10 en un bloque propio de `CodigoError`.

## Qué se generó

Modelo de la prueba, validaciones compartidas y `ProtocoloPruebas` en `comun`; `IPruebaService`,
`PruebaService`, `PruebaDAO`, `PruebaException`, `IConsultaPruebas` (para CU09),
`SolicitudesDePrueba` (provisional, a la espera de CU06) y `OperacionesPruebas` en el servidor;
las tablas `prueba` e `incidencia` en el script SQL; pruebas JUnit. El código se revisó y se probó
con `mvn verify`. Una de las pruebas generadas tenía mal la expectativa de un caso límite
(DEC-076) y se corrigió.
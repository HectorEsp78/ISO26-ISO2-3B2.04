# CU13 · Implementación del servidor (issue #35)

- **Herramienta:** Claude (asistente de IA), 26/11/2026.
- **Responsable:** Álvaro Braojos Martín.

## Prompt

> Vale, entonces si ya tenemos el diseño pasamos con la implementación.

Contexto que se le dio al asistente:

- Implementar el servidor de CU13 Gestionar citas médicas siguiendo el diseño aprobado
  (DEC-062 a DEC-067 en `DECISIONS.md`).
- Reutilizar el módulo `comun` de CU12 (protocolo, escape, códigos de error) y el servidor de
  la rama `feature/CU12-servidor` (sockets, acceso a MySQL, script SQL y `comprobar(dni)`).
- La comprobación del paciente debe ir detrás de una interfaz para poder simularla en las pruebas.
- Java 21, Maven, MySQL (H2 en las pruebas), JUnit, cobertura de ramas mínima del 75 %.
- Añadir los códigos de error de CU13 en un bloque propio de `CodigoError`.

## Qué se generó

Modelo de la cita y protocolo en `comun`; `ICitaService`, `CitaService`, `CitaDAO`,
`CitaException`, `ComprobacionPaciente` y `OperacionesCitas` en el servidor; tablas y datos de
ejemplo en los scripts SQL; pruebas JUnit. El código se revisó y se probó con `mvn verify`.

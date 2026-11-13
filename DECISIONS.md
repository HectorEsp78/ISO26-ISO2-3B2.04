# DECISIONS.md

## Registro de decisiones del proyecto

Este documento recoge las decisiones adoptadas por el equipo durante el desarrollo del proyecto de la asignatura **Ingeniería del Software II**. Su objetivo es mantener un histórico de las elecciones realizadas, las razones que las motivaron y servir como referencia para futuras decisiones.

---

## DEC-001: Nombramiento del coordinador del proyecto

**Fecha:** 15/10/2026

### Decisión
Se designa a **Héctor Espinosa Navarro** como coordinador del proyecto.

### Justificación
El repositorio inicial del proyecto ya había sido configurado por el coordinador y el equipo consideró adecuada su designación para centralizar la organización y seguimiento del trabajo.

### Estado
✅ Aprobada

---

## DEC-002: Uso de cuentas institucionales de la UCLM

**Fecha:** 15/10/2026

### Decisión
Todo el trabajo relacionado con el proyecto se realizará utilizando las cuentas institucionales proporcionadas por la UCLM.

### Justificación
Facilita la colaboración entre los integrantes del equipo y garantiza la disponibilidad de las herramientas corporativas necesarias.

### Estado
✅ Aprobada

---

## DEC-003: Herramienta de Inteligencia Artificial

**Fecha:** 15/10/2026

### Decisión
Se utilizará **GitHub Copilot** como herramienta principal de asistencia basada en inteligencia artificial.

### Justificación
Todos los integrantes disponen de acceso a la herramienta mediante las cuentas institucionales y ofrece integración directa con el entorno de desarrollo seleccionado.

### Estado
✅ Aprobada

---

## DEC-004: Entorno de desarrollo

**Fecha:** 15/10/2026

### Decisión
Se utilizará **Visual Studio Code** como entorno de desarrollo principal.

### Justificación
Es una herramienta ampliamente utilizada, compatible con GitHub Copilot y adecuada para el desarrollo del proyecto.

### Estado
✅ Aprobada

---

## DEC-005: Lenguaje de programación

**Fecha:** 15/10/2026

### Decisión
El proyecto se desarrollará en **Java**.

### Justificación
Es el lenguaje especificado en el enunciado y documentación de la práctica.

### Estado
✅ Aprobada

---

## DEC-006: Herramienta de modelado y diagramación

**Fecha:** 15/10/2026

### Decisión
Se utilizará **Visual Paradigm** para la elaboración de diagramas y modelos del sistema.

### Justificación
Permite la creación y mantenimiento de diagramas UML y facilita la documentación del proyecto.

### Estado
✅ Aprobada

---

## DEC-007: Sistema de control de versiones

**Fecha:** 15/10/2026

### Decisión
Se empleará **Git** junto con **GitHub** para la gestión del código fuente y la colaboración del equipo.

### Justificación
Es la herramienta establecida en la asignatura y facilita el control de cambios y el trabajo colaborativo.

### Estado
✅ Aprobada

---

## DEC-008: Canal oficial de comunicación

**Fecha:** 15/10/2026

### Decisión
El equipo utilizará **Microsoft Teams** como medio principal de comunicación.

### Justificación
Centraliza las reuniones, mensajes y coordinación del equipo en una única plataforma.

### Estado
✅ Aprobada

---

## DEC-009: Gestión documental

**Fecha:** 15/10/2026

### Decisión
La wiki del repositorio será el repositorio principal de documentación del proyecto.

### Justificación
Permite mantener centralizados los documentos, actas, diagramas y demás información relevante del proyecto.

### Estado
✅ Aprobada

---

## DEC-010: Almacenamiento de actas y diagramas

**Fecha:** 15/10/2026

### Decisión
Todas las actas de reunión y diagramas elaborados durante el proyecto se almacenarán en la wiki del repositorio.

### Justificación
Facilita la consulta histórica y la trazabilidad de las decisiones y diseños realizados.

### Estado
✅ Aprobada

---

## DEC-011: Frecuencia de reuniones

**Fecha:** 15/10/2026

### Decisión
El equipo realizará una reunión de seguimiento semanal.

### Justificación
Permite realizar un control continuo del avance del proyecto y resolver incidencias de forma temprana.

### Estado
✅ Aprobada

---

## DEC-012: Horario habitual de reuniones

**Fecha:** 15/10/2026

### Decisión
Las reuniones semanales se celebrarán los **martes a las 11:30 horas**.

### Justificación
Es el horario consensuado por todos los miembros del equipo.

### Estado
✅ Aprobada

---

## DEC-013: Correspondencia entre requisitos y casos de uso

**Fecha:** 15/10/2026

### Decisión
Cada requisito funcional deberá estar asociado a un caso de uso identificado y documentado.

### Justificación
Garantiza la trazabilidad entre requisitos y funcionalidad implementada.

### Estado
✅ Aprobada

---

## DEC-014: Información mínima de los casos de uso

**Fecha:** 15/10/2026

### Decisión
Todos los casos de uso deberán documentarse incluyendo como mínimo:

- Identificador del requisito.
- Nombre del caso de uso.
- Actor asociado.
- Prioridad.
- Fase correspondiente.

### Justificación
Asegura uniformidad en la documentación y facilita el seguimiento del desarrollo.

### Estado
✅ Aprobada

---

## DEC-015: Gestión de tareas mediante Issues

**Fecha:** 15/10/2026

### Decisión
Las tareas del proyecto se gestionarán mediante **GitHub Issues**.

### Justificación
Permite registrar, asignar, priorizar y realizar seguimiento del trabajo pendiente.

### Estado
✅ Aprobada

---

## DEC-016: Reparto inicial de áreas funcionales

**Fecha:** 15/10/2026

### Decisión
Se establece el siguiente reparto inicial de trabajo:

- **Carla Dacuña Fernández:** casos de uso asociados al actor *Paciente*.
- **Álvaro Braojos:** casos de uso asociados al actor *Médico*.
- **Pablo Pardo Sánchez:** casos restantes de *Paciente* y casos pendientes de *Médico*.
- **Óscar Carrasco Ramírez:** casos de uso de *Personal Administrativo*.
- **Héctor Espinosa Navarro:** casos de uso de *Administrador del Sistema* y *Personal Administrativo*.

### Justificación
Permite distribuir el trabajo desde las primeras fases del proyecto y equilibrar la carga entre los integrantes.

### Estado
✅ Aprobada

---
## DEC-017: proyecto Maven con padre, servidor y cliente

**Fecha:** 19/10/2026

### Decisión
Se toma la decision de crear el proyecto maven con una estructura de padre, cliente y servidor.


### Justificación
Permite compartir el proyecto sin que haya errores de compatibilidad entre los diferentes programadores.

### Estado
✅ Aprobada

---

## DEC-018: Finalización de requisitos antes de documentar casos de uso

**Fecha:** 20/10/2026

### Decisión
La documentación detallada de los casos de uso comenzará únicamente cuando la especificación de requisitos esté completada y revisada.

### Justificación
Los casos de uso dependen directamente de los requisitos funcionales. Completar previamente los requisitos evitará inconsistencias y retrabajo.

### Estado
✅ Aprobada

---

## DEC-019: Gestión formal de restricciones del proyecto

**Fecha:** 20/10/2026

### Decisión
Las restricciones del proyecto se registrarán mediante una incidencia específica dentro del repositorio.

### Justificación
Permite mantener trazabilidad, revisión y seguimiento de las restricciones identificadas durante el proyecto.

### Estado
✅ Aprobada

---

## DEC-020: Responsable del diagrama general de casos de uso

**Fecha:** 20/10/2026

### Decisión
Se designa a **Héctor Espinosa Navarro** como responsable de elaborar el diagrama general de casos de uso.

### Justificación
Centralizar esta tarea favorece la coherencia entre actores, relaciones y casos de uso del sistema.

### Estado
✅ Aprobada

---

## DEC-021: Responsable de la priorización de casos de uso

**Fecha:** 20/10/2026

### Decisión
Se designa a **Carla Dacuña Fernández** como responsable de realizar la priorización de los casos de uso.

### Justificación
La existencia de una única responsable facilita la aplicación homogénea de los criterios de prioridad establecidos por el equipo.

### Estado
✅ Aprobada

---

## DEC-022: Responsable de las tablas de planificación del PUD

**Fecha:** 20/10/2026

### Decisión
Se designa a **Alberto Calvo de Mora Riballo** como responsable de elaborar las tablas de planificación requeridas para el Proceso Unificado de Desarrollo (PUD).

### Justificación
La planificación requiere una preparación específica basada en los casos de uso documentados y priorizados. Se acuerda asignar esta responsabilidad de forma explícita.

### Estado
✅ Aprobada

---

## DEC-023: Mantenimiento de la estructura documental de la Wiki

**Fecha:** 20/10/2026

### Decisión
Se mantendrá actualizada la estructura de enlaces de la documentación de la wiki para garantizar la correcta navegación entre índices, reuniones y documentos relacionados.

### Justificación
Se detectaron errores en la navegación entre actas, lo que dificultaba el acceso a la documentación histórica del proyecto.

### Estado
✅ Aprobada

---

### DEC-024: Prioridad de la documentación sobre la implementación

**Fecha:** 10/11/2026

#### Decisión

La finalización de toda la documentación correspondiente a la primera entrega tendrá prioridad sobre el inicio de las tareas de implementación del sistema.

#### Justificación

La documentación de planificación, requisitos, casos de uso, gestión de configuración, gestión de calidad y diagramas constituye el entregable principal de la primera evaluación y debe completarse antes de comenzar el desarrollo.

#### Estado

✅ Aprobada

---

### DEC-025: Contenido mínimo del documento de Gestión de Calidad

**Fecha:** 10/11/2026

#### Decisión

El documento de Gestión de Calidad deberá incluir:

- Características de calidad relevantes para el sistema.
- Requisitos de calidad identificados para el proyecto.
- Criterios de evaluación y validación.
- Aspectos relacionados con la calidad del producto.
- Aspectos relacionados con la calidad del proceso.

#### Justificación

Se acordó unificar criterios sobre el contenido del documento tomando como referencia el material visto en las sesiones de teoría.

#### Estado

✅ Aprobada

---

### DEC-026: Sincronización obligatoria entre diagramas y documentación

**Fecha:** 10/11/2026

#### Decisión

Todos los diagramas del proyecto deberán mantenerse sincronizados con la documentación de requisitos y casos de uso vigente.

#### Justificación

Evita inconsistencias entre el modelo del sistema y la documentación funcional utilizada durante el desarrollo y las entregas.

#### Estado

✅ Aprobada

---

### DEC-027: Centralización de la documentación del proyecto

**Fecha:** 10/11/2026

#### Decisión

Se centralizará la elaboración y mantenimiento de la documentación con el fin de preservar la coherencia de formato, terminología y criterios de redacción.

#### Justificación

Una documentación homogénea facilita la revisión de las entregas y mejora la mantenibilidad de la información del proyecto.

#### Estado

✅ Aprobada

---

### DEC-028: Mantenimiento obligatorio del registro de decisiones

**Fecha:** 10/11/2026

#### Decisión

El archivo `DECISIONS.md` deberá actualizarse de forma continua para registrar todas aquellas decisiones que tengan impacto en la organización, desarrollo o documentación del proyecto.

#### Justificación

Permite mantener trazabilidad de las decisiones tomadas y facilita la incorporación de nuevos miembros o la revisión histórica del proyecto.

#### Estado

✅ Aprobada

---

### DEC-029: Posposición de la asignación definitiva de tareas de implementación

**Fecha:** 10/11/2026

#### Decisión

La asignación definitiva de responsabilidades para la fase de implementación se realizará una vez completada y validada la documentación de la primera entrega.

#### Justificación

La organización del desarrollo depende de la versión final de requisitos, casos de uso y planificación, por lo que se considera prematuro fijar responsabilidades definitivas antes de finalizar dicha documentación.

#### Estado

✅ Aprobada

## Historial de modificaciones

| Fecha | Cambio |
|---------|---------|
| 15/10/2026 | Creación inicial del documento de decisiones. |
| 20/10/2026 | Incorporadas las decisiones DEC-017 a DEC-022 derivadas de la segunda reunión de seguimiento. |
| 20/10/2026 | Actualizadas las decisiones DEC-009 y DEC-015 con información adicional sobre gestión documental y gestión de restricciones. |
| 10/11/2026 | Corregida la numeración de decisiones a partir de la duplicidad detectada en DEC-017. |
| 10/11/2026 | Incorporadas las decisiones DEC-024 a DEC-029 derivadas de la cuarta reunión de seguimiento. |


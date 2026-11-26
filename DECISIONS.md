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

---

## DEC-030: Tecnologías base de la arquitectura del sistema

**Fecha:** 17/11/2026

### Decisión

Se establece la siguiente pila tecnológica para el desarrollo del sistema:

- JavaFX para la interfaz de usuario.
- MySQL como sistema gestor de bases de datos.
- Maven para la gestión del proyecto y sus dependencias.
- Arquitectura cliente-servidor.

### Justificación

Estas tecnologías forman parte de los requisitos y directrices establecidos para el desarrollo del proyecto y permiten una integración adecuada con la arquitectura definida.

### Estado

✅ Aprobada

---

## DEC-031: Inclusión del script de inicialización de base de datos

**Fecha:** 17/11/2026

### Decisión

El repositorio deberá incluir un script SQL destinado a la creación de la base de datos y a la carga de los datos iniciales necesarios para la ejecución del sistema.

### Justificación

Facilita la configuración del entorno de desarrollo y asegura que todos los integrantes trabajen sobre una estructura de datos común y reproducible.

### Estado

✅ Aprobada

---

## DEC-032: Estrategia de inicio de la implementación

**Fecha:** 17/11/2026

### Decisión

La implementación comenzará por **CU12 Gestionar pacientes** (RF12), en la iteración 1 de la fase de Elaboración, siguiendo el orden de iteraciones del Plan de Desarrollo (PUD). El trabajo se divide en dos sub-issues, una para el servidor y otra para el cliente, desarrolladas en las ramas `feature/CU12-servidor` y `feature/CU12-cliente`.

Los siguientes casos de uso se abordarán en el orden fijado por el PUD, una iteración por caso de uso.

### Justificación

CU12 es la base de los demás casos de uso: sin pacientes registrados no se pueden gestionar citas, solicitudes de pruebas ni informes. Empezar por él permite validar pronto la arquitectura cliente-servidor, la comunicación por sockets y la conexión con MySQL.

### Estado

✅ Aprobada

---

## DEC-033: Flujo de trabajo de desarrollo basado en incidencias

**Fecha:** 17/11/2026

### Decisión

Todo desarrollo deberá seguir el siguiente flujo de trabajo:

1. Crear una issue principal para el caso de uso con la plantilla «Caso de uso», con sus criterios de aceptación y horas estimadas, y asignarla al milestone de la iteración.
2. Crear dos sub-issues, una para el servidor y otra para el cliente, cada una con su responsable.
3. Crear la rama de cada sub-issue a partir de `develop`, con el nombre `feature/CUxx-servidor` o `feature/CUxx-cliente`.
4. Implementar la funcionalidad en esa rama, con commits según Conventional Commits que referencien la issue (`#n`).
5. Diseñar y ejecutar las pruebas unitarias y comprobar que `mvn verify` pasa en local con una cobertura de ramas de al menos el 75 %.
6. Abrir una pull request hacia `develop` con la plantilla del repositorio, enlazando la issue con `Closes #n`.
7. Integrar en `develop` cuando la PR tenga al menos una aprobación y el check `build` esté en verde, mediante *merge commit* y sin borrar la rama.

Solo las ramas `release/*` y `hotfix/*` se integran en `main`.

### Justificación

Garantiza la trazabilidad del trabajo realizado, facilita la revisión de cambios y reduce los riesgos de integración.

### Estado

✅ Aprobada

---

## DEC-034: Sincronización del repositorio local antes de comenzar nuevas tareas

**Fecha:** 17/11/2026

### Decisión

Antes de iniciar cualquier nueva tarea, cada integrante deberá actualizar su rama `develop` local y crear la nueva rama a partir de ella:

```bash
git switch develop
git pull origin develop
git switch -c feature/CUxx-modulo
```

Si `develop` avanza mientras se trabaja en una rama, se incorporarán sus cambios con `git pull origin develop` desde la propia rama antes de abrir la pull request.

### Justificación

Minimiza conflictos de integración y asegura que todo el equipo trabaje sobre la versión más reciente del código y la documentación.

### Estado

✅ Aprobada

---

## DEC-035: Creación de una estructura de pruebas asociada a los casos de uso

**Fecha:** 17/11/2026

### Decisión

Se definirá una estructura específica para almacenar los casos de prueba asociados a cada caso de uso del sistema.

### Justificación

Permite organizar la validación funcional del proyecto y mantener la trazabilidad entre requisitos, casos de uso e implementación.

### Estado

✅ Aprobada

---

## DEC-036: Conservación de prompts utilizados en desarrollo asistido por IA

**Fecha:** 17/11/2026

### Decisión

Los prompts utilizados durante el desarrollo asistido por herramientas de inteligencia artificial deberán conservarse y vincularse a los casos de uso correspondientes cuando resulte necesario para la documentación del proyecto.

### Justificación

Facilita la trazabilidad del desarrollo asistido por IA y permite justificar la generación de determinados artefactos o soluciones.

### Estado

✅ Aprobada

---

## DEC-037: Uso de documentación editable durante la fase de trabajo

**Fecha:** 17/11/2026

### Decisión

Durante la elaboración de documentación extensa, el equipo utilizará documentos editables compartidos para el trabajo colaborativo, incorporando posteriormente la versión consolidada al repositorio oficial del proyecto.

### Justificación

Facilita la edición simultánea por varios miembros del equipo y simplifica la consolidación de cambios antes de su publicación definitiva.

### Estado

✅ Aprobada

---

## DEC-038: Coordinación de la primera versión funcional

**Fecha:** 17/11/2026

### Decisión

La planificación de desarrollo del equipo se orientará a disponer de una primera versión funcional del sistema antes de ampliar funcionalidades secundarias.

### Justificación

Permite validar tempranamente la arquitectura seleccionada, detectar problemas de integración y servir como base para iteraciones posteriores.

### Estado

✅ Aprobada

---

## DEC-039: Comunicación cliente-servidor mediante sockets

**Fecha:** 17/11/2026

### Decisión

El cliente y el servidor se comunicarán mediante **sockets TCP** (`java.net.Socket` y `java.net.ServerSocket`), con un protocolo de texto propio:

- Cada mensaje ocupa una línea codificada en UTF-8.
- Una petición tiene la forma `OPERACION;argumento1;argumento2;...`.
- Una respuesta empieza por `OK` o `ERROR`, seguida de sus datos separados por `;`.
- El servidor atiende a cada cliente en un hilo independiente.

La operación `PING` (respuesta `OK;PONG`) se usa para comprobar la conexión. Cada caso de uso añadirá sus propias operaciones.

### Alternativas consideradas

Una API REST sencilla. Se descarta porque añadiría un servidor HTTP y una librería JSON sin aportar nada a los objetivos de la asignatura.

### Justificación

Los sockets no necesitan dependencias externas, dejan clara la separación entre cliente y servidor que pide la profesora y el protocolo de texto es fácil de probar con JUnit.

### Estado

✅ Aprobada

---

## DEC-042: Estados de la cita

**Fecha:** 23/11/2026

### Decisión

En CU13 la cita tendrá dos estados: `PROGRAMADA` y `CANCELADA`. El estado `REALIZADA` lo incorporará CU10 (Registrar realización de prueba).

### Justificación

Limita el modelo a lo que cubre RF13 y evita sobreingeniería. Los estados posteriores se añadirán cuando su caso de uso los necesite.

### Estado

⏳ Pendiente de aprobación

---

## DEC-043: Criterio de solape de citas

**Fecha:** 23/11/2026

### Decisión

No se admiten dos citas `PROGRAMADA` solapadas en el mismo recurso (sala o equipo) ni para el mismo paciente. Dos citas se solapan si el inicio de cada una es anterior al fin de la otra; si una termina justo cuando empieza la otra, no se solapan.

La comprobación se realiza en el servidor y debe ser atómica, de forma que dos peticiones simultáneas no puedan crear citas solapadas.

### Justificación

Evita las dobles reservas, que es el principal riesgo de la iteración 2 según el PUD.

### Estado

⏳ Pendiente de aprobación

---

## DEC-044: Horario de citación provisional

**Fecha:** 23/11/2026

### Decisión

Hasta que existan las agendas de los médicos (CU14), las citas deberán empezar y terminar de lunes a viernes entre las 08:00 y las 20:00.

### Alternativas consideradas

No comprobar el horario. Se descarta porque permitiría citas a cualquier hora del día.

### Justificación

CU13 se desarrolla antes que CU14 y necesita una regla temporal. Se sustituirá por las agendas en la iteración 10.

### Estado

⏳ Pendiente de aprobación

---

## DEC-045: Reprogramar modifica la misma cita

**Fecha:** 23/11/2026

### Decisión

La reprogramación actualiza la fecha, la hora o el recurso de la cita existente y conserva su identificador. No crea una cita nueva ni guarda un historial de reprogramaciones.

### Justificación

Simplicidad y trazabilidad de la cita. RF13 no exige historial de cambios y se podrá añadir más adelante si hace falta.

### Estado

⏳ Pendiente de aprobación

---

## DEC-046: Cancelación lógica con motivo obligatorio

**Fecha:** 23/11/2026

### Decisión

Cancelar una cita la deja en estado `CANCELADA` con un motivo obligatorio (máximo 255 caracteres). La cita no se borra y su hueco queda libre.

### Justificación

Conserva el histórico para la auditoría (CU19), los informes de cancelaciones y las estadísticas posteriores. Es coherente con la baja lógica de pacientes de CU12.

### Estado

⏳ Pendiente de aprobación

---

## DEC-047: Catálogo de pruebas y recursos de ejemplo

**Fecha:** 23/11/2026

### Decisión

Hasta que el administrador pueda configurar los tipos de prueba y los recursos, CU13 usará una lista de ejemplo (tipo de prueba con su duración estimada, y recursos) cargada en el script SQL inicial (DEC-031).

### Justificación

La configuración del sistema llega en una iteración posterior y CU13 necesita esos datos para asignar citas.

### Estado

⏳ Pendiente de aprobación

---

## DEC-048: Citas pendientes de un paciente dado de baja

**Fecha:** 23/11/2026

### Decisión

Al dar de baja a un paciente, sus citas `PROGRAMADA` pasarán a `CANCELADA` con el motivo «Baja del paciente», en lugar de eliminarse. Esta decisión sustituye a RN-04 y FA-07 de la especificación de CU12, que deberán actualizarse.

### Alternativas consideradas

Eliminar las citas pendientes, como indica RN-04 de CU12. Se descarta porque borra datos que la auditoría y los informes necesitan y contradice la cancelación lógica de DEC-046.

### Justificación

Mantiene una única forma de tratar las citas y conserva el histórico que necesitan la auditoría (CU19) y los informes. Es coherente con DEC-046.

### Estado

⏳ Pendiente de aprobación (de acuerdo con el responsable de CU12)

---

## DEC-049: Asignación individual y completa de casos de uso por integrante

**Fecha:** 24/11/2026

### Decisión

Se modifica la organización del trabajo por fases (sustituyendo el reparto anterior de DEC-016) para que cada integrante desarrolle de forma individual e íntegra **4 casos de uso** (uno de cada uno de los cuatro niveles de prioridad, sumando los 24 casos de uso del sistema). Asimismo, cada miembro será responsable de abrir sus propias incidencias (*issues*) y *Pull Requests* asociadas a sus casos de uso.

### Justificación

Evita dependencias y tiempos de espera entre los miembros del equipo al avanzar entre fases, permitiendo trabajar en paralelo desde el inicio y reflejando de forma clara e individual la contribución de cada integrante en el repositorio.

### Estado

✅ Aprobada

---

## DEC-050: Actualización del Plan de Desarrollo (PUD) mediante anexo o nueva versión

**Fecha:** 24/11/2026

### Decisión

Se conservará la planificación original del PUD en la wiki sin eliminarla y se incorporará una nueva versión o anexo justificando la reorganización del trabajo e indicando en las tablas el responsable individual asignado a cada caso de uso.

### Justificación

Permite mantener la trazabilidad respecto a la planificación inicial entregada y documentar formalmente los ajustes realizados por motivos de calendario y coordinación del equipo.

### Estado

✅ Aprobada

---

## DEC-051: Omisión de diagramas individuales en la fase de análisis

**Fecha:** 24/11/2026

### Decisión

En la fase de análisis no se elaborarán diagramas UML individuales para cada caso de uso, utilizándose únicamente los diagramas generales del sistema ya definidos.

### Justificación

Siguiendo las indicaciones de la profesora, los diagramas generales son suficientes para el alcance del proyecto y evitar diagramas redundantes por caso de uso agiliza el avance hacia la fase de implementación.

### Estado

✅ Aprobada

---

## DEC-052: Validación mediante pruebas cruzadas entre integrantes

**Fecha:** 24/11/2026

### Decisión

El diseño y la ejecución de las pruebas de cada caso de uso se realizarán de forma cruzada por un integrante distinto al responsable de su implementación.

### Justificación

Aumenta la objetividad en la verificación funcional, facilita la detección temprana de errores que el propio desarrollador podría pasar por alto y asegura una revisión mutua del código.

### Estado

✅ Aprobada

---

## DEC-053: Estandarización mediante uso de un prompt común

**Fecha:** 24/11/2026

### Decisión

El equipo acordará y utilizará un *prompt* base común durante el desarrollo asistido por inteligencia artificial para la generación de artefactos e implementación.

### Justificación

Garantiza homogeneidad en la estructura, estilo de código y documentación generada por todos los miembros del equipo.

### Estado

✅ Aprobada

---

## DEC-054: Organización en parejas rotativas para el trabajo teórico

**Fecha:** 24/11/2026

### Decisión

Para el segundo trabajo de la parte teórica (3 problemas), el equipo de 6 integrantes se dividirá en **3 parejas** que trabajarán de forma rotativa en cada paso de los problemas, aplicando el mismo flujo de trabajo con repositorio e incidencias utilizado en las prácticas.

### Justificación

Permite distribuir equitativamente la carga de trabajo entre todos los miembros del grupo y asegura que todas las parejas participen en la resolución de los distintos problemas antes de la entrega del 15 de diciembre.

### Estado

✅ Aprobada

---
## DEC-055: Datos del paciente y sus validaciones

**Fecha:** 25/11/2026

### Decisión

Se concretan los formatos del apartado 8 de la especificación de CU12. Antes de validar se eliminan los espacios del principio y del final de cada campo, y el DNI/NIE y la tarjeta sanitaria se pasan a mayúsculas.

| Campo | Obligatorio | Único | Validación |
|---|---|---|---|
| DNI / NIE | Sí | Sí | DNI: 8 dígitos y letra de control. NIE: X, Y o Z, 7 dígitos y letra de control (X=0, Y=1, Z=2). La letra es la de la posición *número mod 23* en `TRWAGMYFPDXBNJZSQVHLCKE`. |
| Tarjeta sanitaria | Sí | Sí | Código CIP del SNS: exactamente 16 caracteres, letras mayúsculas o dígitos. |
| Nombre | Sí | No | Entre 1 y 50 caracteres. Letras (incluidas tildes y ñ), espacios, guion y apóstrofo. |
| Apellidos | Sí | No | Entre 1 y 100 caracteres, con los mismos caracteres que el nombre. |
| Fecha de nacimiento | No | No | Fecha válida, no posterior a hoy y no anterior al 01/01/1900. |
| Sexo | No | No | `HOMBRE` o `MUJER`. |
| Teléfono | Sí | No | 9 dígitos y el primero es 6, 7, 8 o 9. |
| Correo electrónico | No | No | Como máximo 100 caracteres, con el formato `usuario@dominio.ext`. |
| Dirección | No | No | Como máximo 200 caracteres. |
| Estado | Sí | No | `ACTIVO` o `BAJA`. Lo asigna el sistema: el alta crea el paciente `ACTIVO` y la baja lo pasa a `BAJA`. |

Las validaciones las aplican tanto el cliente como el servidor con la misma clase (`ValidadorPaciente`, del módulo `comun`, DEC-058). El servidor vuelve a validar todo lo que recibe, aunque el cliente ya lo haya hecho.

### Justificación

La especificación dejaba estos formatos sin concretar y sin ellos no se pueden programar las validaciones ni diseñar los casos de prueba (clases de equivalencia y valores límite).

### Estado

⏳ Pendiente de aprobación

---

## DEC-056: Búsqueda de pacientes solo por DNI en la iteración 1

**Fecha:** 25/11/2026

### Decisión

En la iteración 1 la búsqueda de pacientes (flujo 6.2 de CU12) se hace únicamente por DNI/NIE y devuelve como mucho un paciente. La búsqueda por nombre o por tarjeta sanitaria se aplaza a una iteración posterior.

### Justificación

Los criterios de aceptación de CU12 solo exigen la búsqueda por DNI (CA-01 y CA-05). Limitar el alcance permite terminar la iteración en el plazo previsto.

### Estado

⏳ Pendiente de aprobación

---

## DEC-057: Operaciones del protocolo de sockets para CU12

**Fecha:** 25/11/2026

### Decisión

Se amplía el protocolo de la DEC-039 con estas operaciones:

| Petición | Respuesta correcta | Errores posibles |
|---|---|---|
| `ALTA_PACIENTE;<datos>` | `OK;<paciente>` | `CAMPO_OBLIGATORIO`, `CAMPO_NO_VALIDO`, `DNI_DUPLICADO`, `TARJETA_DUPLICADA` |
| `BUSCAR_PACIENTE;<dni>` | `OK;<paciente>` | `CAMPO_OBLIGATORIO`, `CAMPO_NO_VALIDO`, `PACIENTE_NO_ENCONTRADO` |
| `MODIFICAR_PACIENTE;<datos>` | `OK;<paciente>` | los del alta (salvo `DNI_DUPLICADO`), `PACIENTE_NO_ENCONTRADO`, `PACIENTE_DE_BAJA` |
| `BAJA_PACIENTE;<dni>` | `OK;<paciente>` | `CAMPO_OBLIGATORIO`, `CAMPO_NO_VALIDO`, `PACIENTE_NO_ENCONTRADO`, `PACIENTE_DE_BAJA` |

- `<datos>` son 9 campos en este orden: `dni;tarjetaSanitaria;nombre;apellidos;fechaNacimiento;sexo;telefono;correo;direccion`.
- `<paciente>` son esos 9 campos seguidos de `estado`.
- Un campo opcional vacío se envía como cadena vacía. La fecha va en formato ISO (`aaaa-mm-dd`).
- En `MODIFICAR_PACIENTE` el DNI identifica al paciente y no se puede cambiar (RN-03).
- Una respuesta de error tiene la forma `ERROR;<código>` o `ERROR;<código>;<campo>`. Los errores `CAMPO_OBLIGATORIO` y `CAMPO_NO_VALIDO` indican en `<campo>` el nombre del campo tal y como aparece arriba (por ejemplo `ERROR;CAMPO_OBLIGATORIO;telefono`), de modo que el cliente pueda señalar qué campo falla (CA-02).
- Códigos generales: `PETICION_NO_VALIDA` (operación desconocida o número de campos incorrecto) y `ERROR_INTERNO` (fallo del servidor, por ejemplo de la base de datos).
- Carácter de escape: dentro de un campo, `\` se envía como `\\`, `;` como `\;`, el salto de línea como `\n` y el retorno de carro como `\r`.
- El servidor escucha por defecto en el puerto **5050**. El cliente abre una conexión por petición y espera la respuesta como máximo 5 segundos. El host y el puerto se pueden configurar.

### Justificación

El cliente y el servidor se desarrollan por separado y necesitan un contrato cerrado. Los códigos de error permiten al cliente mostrar mensajes claros sin interpretar textos libres, y el escape evita que un `;` en la dirección rompa el mensaje.

### Estado

⏳ Pendiente de aprobación

---

## DEC-058: Módulo Maven `comun` compartido por cliente y servidor

**Fecha:** 25/11/2026

### Decisión

Se añade al proyecto padre de la DEC-017 un tercer módulo, `comun` (`es.uclm.esi.iso2:comun`), del que dependen el cliente y el servidor. Contiene únicamente código sin dependencias externas que deben compartir los dos:

- Las entidades de dominio que viajan por el protocolo (`Paciente`, `EstadoPaciente`).
- Las validaciones de los datos (`ValidadorPaciente`).
- La codificación del protocolo: nombres de las operaciones, códigos de error y escape de campos (`Protocolo`, `CodigoError`).

### Alternativas consideradas

Duplicar estas clases en el cliente y en el servidor. Se descarta porque obliga a mantener dos copias que acabarían siendo distintas, y precisamente el protocolo y las validaciones tienen que coincidir en los dos extremos.

### Justificación

Garantiza que el cliente y el servidor validan igual y hablan el mismo protocolo, y permite probar ese código una sola vez.

### Estado

⏳ Pendiente de aprobación

---

## DEC-059: Estructura del cliente JavaFX

**Fecha:** 25/11/2026

### Decisión

- El cliente usa **JavaFX 21** (21.0.13, versión LTS compatible con el Java 21 del proyecto y de la CI) con vistas FXML, y se ejecuta con `javafx-maven-plugin`.
- La pantalla y los formularios (paquete `es.uclm.esi.iso2.cliente.presentacion`) solo muestran datos y recogen lo que introduce el usuario. La lógica va en clases sin JavaFX: `GestorPacientesCliente` (validación previa y mensajes) y el servicio de pacientes.
- El acceso al servidor se hace a través de la interfaz `ServicioPacientes`, con dos implementaciones:
  - `ServicioPacientesSocket`: la real, por sockets (DEC-039 y DEC-057).
  - `ServicioPacientesEnMemoria`: aplica las mismas reglas que el servidor sin red ni base de datos. Se usa en las pruebas y para probar la interfaz sin servidor (`-Dradiodiagnostico.modo=memoria`).
- Mientras no exista CU23 (Autenticarse), el cliente arranca directamente en la pantalla de pacientes.

### Justificación

Permite desarrollar y probar el cliente antes de que el servidor esté terminado, y deja la lógica en clases que se pueden probar con JUnit sin interfaz gráfica.

### Estado

⏳ Pendiente de aprobación

---

## DEC-060: Cobertura de ramas obligatoria y exclusión de la capa de presentación

**Fecha:** 25/11/2026

### Decisión

- La regla de JaCoCo del proyecto padre mide la cobertura de **ramas** (`BRANCH`) con un mínimo del 75 %, como exige la DEC-033, en lugar de la cobertura de líneas.
- La comprobación pasa a ser bloqueante (`haltOnFailure` a `true`): `mvn verify`, y por tanto el check `build`, falla si un módulo no llega al mínimo.
- En el cliente se excluye del cálculo el paquete `presentacion` (controladores y vistas JavaFX), que no se puede probar con JUnit sin interfaz gráfica. Por eso esas clases no deben contener lógica (DEC-059).

### Justificación

Alinea la configuración con la Definition of Done y evita que se integre código sin el nivel de pruebas acordado, sin penalizar el código de interfaz, que se verifica manualmente.

### Estado

⏳ Pendiente de aprobación

---

## DEC-061: Operación para comprobar si un paciente existe y está activo

**Fecha:** 25/11/2026

### Decisión

Se añade al protocolo de la DEC-057 la operación `COMPROBAR_PACIENTE`, a petición del responsable de CU13, que necesita saber si un paciente existe y está activo antes de darle una cita:

| Petición | Respuesta correcta | Errores posibles |
|---|---|---|
| `COMPROBAR_PACIENTE;<dni>` | `OK;ACTIVO` u `OK;BAJA` | `CAMPO_OBLIGATORIO`, `CAMPO_NO_VALIDO`, `PACIENTE_NO_ENCONTRADO` |

- La respuesta solo incluye el estado, no los datos personales del paciente.
- En el cliente, `ServicioPacientes` ofrece `comprobar(dni)`, que devuelve el estado, y `estaActivo(dni)`, que devuelve `false` si el paciente no existe o está de baja. Los demás errores (DNI no válido, sin conexión…) se lanzan como excepción y no se confunden con «no activo».
- La pantalla de pacientes incluye un botón «Comprobar» que solo informa del resultado, sin mostrar la ficha.
- El servidor de CU12 deberá atender esta operación.

### Alternativas consideradas

Que CU13 use `BUSCAR_PACIENTE`. Se descarta porque devuelve todos los datos del paciente cuando solo se necesita su estado, lo que va contra la confidencialidad (apartado 10 de la especificación de CU12).

### Justificación

Da a CU13, y a cualquier otro caso de uso que trabaje con pacientes, una forma única y sencilla de comprobar el paciente, sin duplicar la lógica ni exponer datos innecesarios.

### Estado

⏳ Pendiente de aprobación

---

## DEC-062: Modelo de la cita y sus estados

**Fecha:** 25/11/2026

### Decisión

Se concretan los datos del apartado 8 de la especificación de CU13. La cita y lo que viaja con ella por el protocolo (`Cita`, `EstadoCita`, `TipoPrueba`, `Recurso`) van en el módulo `comun` (DEC-058), paquete `es.uclm.esi.iso2.comun.dominio`.

| Campo | Tipo | Obligatorio | Validación |
|---|---|---|---|
| Identificador | `long` | Automático | Lo asigna la base de datos. |
| DNI del paciente | `String` | Sí | Formato de la DEC-055. El paciente debe existir y estar `ACTIVO` (RN-01, DEC-064). |
| Tipo de prueba | `TipoPrueba` | Sí | Del catálogo de ejemplo (DEC-047). |
| Recurso | `Recurso` | Sí | De la lista de ejemplo (DEC-047). |
| Inicio | `LocalDateTime` | Sí | Posterior al momento actual (RN-02) y, con el fin, dentro del horario de la DEC-044 (RN-03). |
| Duración | `int` (minutos) | Sí | Mayor que 0. Por defecto, la del tipo de prueba. |
| Estado | `EstadoCita` | Sí | `PROGRAMADA`, `CANCELADA` o `REALIZADA`. Lo asigna el sistema: la cita se crea `PROGRAMADA`. |
| Motivo de cancelación | `String` | Solo al cancelar | Entre 1 y 255 caracteres (DEC-046). |

- El fin de la cita no se guarda como dato propio del modelo: se calcula como inicio más duración.
- Dos citas se solapan si el inicio de cada una es anterior al fin de la otra (RN-06, DEC-043).
- `TipoPrueba` tiene identificador, nombre y duración en minutos. `Recurso` tiene identificador y nombre.
- El estado `REALIZADA` se incluye ya en CU13, aunque solo lo usa CU10 (DEC-063). Esto modifica la DEC-042, que decía que lo añadiría CU10.

### Alternativas consideradas

Que CU10 añada el estado `REALIZADA` cuando lo necesite, como decía la DEC-042. Se descarta porque obligaría a dos personas a modificar la misma clase en ramas distintas y provocaría conflictos al integrar.

### Justificación

La especificación dejaba sin concretar los tipos y el lugar de las clases, y sin ellos no se puede programar ni diseñar las pruebas. Poner las entidades en `comun` sigue el mismo criterio que CU12.

### Estado

⏳ Pendiente de aprobación

---

## DEC-063: Interfaz del servicio de citas (`ICitaService`)

**Fecha:** 25/11/2026

### Decisión

Las reglas de negocio de las citas están en `CitaService` (módulo `servidor`, paquete `es.uclm.esi.iso2.servidor.citas`), y el resto del sistema solo depende de la interfaz `ICitaService`:

| Operación | Quién la usa | Qué hace | Si falla |
|---|---|---|---|
| `asignarCita(datos)` | CU13 | Crea una cita `PROGRAMADA`. Comprueba RN-01 a RN-06. | No guarda nada. |
| `buscarCitas(dni, desde, hasta, estado)` | CU13 | Devuelve las citas que cumplen los filtros (cualquiera puede ir vacío), ordenadas por inicio. | — |
| `reprogramarCita(id, datos)` | CU13 | Cambia fecha, recurso o duración de la misma cita (DEC-045). Comprueba los solapes sin contar la propia cita (RN-07). | La cita no cambia. |
| `cancelarCita(id, motivo)` | CU13 | Pasa la cita a `CANCELADA` con su motivo (DEC-046). Solo si está `PROGRAMADA` y no ha empezado (RN-08). | La cita no cambia. |
| `listarTiposPrueba()`, `listarRecursos()` | CU13 | Catálogos de ejemplo para el formulario (DEC-047). | — |
| `obtenerCita(id)` | CU10 | Devuelve la cita con ese identificador. | `CITA_NO_ENCONTRADA`. |
| `marcarRealizada(id)` | CU10 | Pasa una cita `PROGRAMADA` a `REALIZADA`. **No** aplica RN-08. | La cita no cambia. |
| `marcarCancelada(id, motivo)` | CU10 | Pasa una cita `PROGRAMADA` a `CANCELADA` con su motivo. **No** aplica RN-08. | La cita no cambia. |
| `cancelarCitasPendientes(dni, motivo)` | CU12 | Al dar de baja a un paciente, pasa todas sus citas `PROGRAMADA` a `CANCELADA` con el motivo «Baja del paciente» (DEC-048). Devuelve cuántas ha cancelado. | No cambia ninguna. |

- `datos` es un objeto `DatosCita` con lo que introduce el usuario: DNI, tipo de prueba, recurso, inicio y duración. Se llama así para no confundirlo con la solicitud de CU06.
- Los errores se lanzan como `CitaException` con un código de la DEC-065.
- CU10 y CU12 llaman a sus operaciones desde el propio servidor; no necesitan mensajes de socket.

### Alternativas consideradas

Que CU10 reutilice `cancelarCita`. Se descarta porque RN-08 solo permite cancelar citas que no han empezado, y cuando se registra una prueba la cita ya ha empezado.

### Justificación

Una sola interfaz para todas las operaciones sobre citas evita que otros casos de uso modifiquen el código de CU13 y permite que CU01, CU06, CU14 y CU15 la reutilicen más adelante.

### Estado

⏳ Pendiente de aprobación

---

## DEC-064: Comprobación del paciente al asignar o reprogramar una cita

**Fecha:** 25/11/2026

### Decisión

Para cumplir RN-01, `CitaService` usa la operación `comprobar(dni)` de CU12 (DEC-061):

- Si el paciente no existe, la cita se rechaza con `PACIENTE_NO_ENCONTRADO`.
- Si está de baja (`BAJA`), se rechaza con `PACIENTE_DE_BAJA`.
- Si está `ACTIVO`, se siguen comprobando las demás reglas.

El cliente puede usar `estaActivo(dni)` de `ServicioPacientes` para avisar antes de enviar, pero el servidor lo vuelve a comprobar siempre.

Mientras el servidor de CU12 no ofrezca esta operación, `CitaService` recibe en su constructor el objeto que hace la comprobación, y en las pruebas se usa una versión simulada con DNI de ejemplo (uno activo, uno de baja y uno que no existe).

### Alternativas consideradas

Usar `BUSCAR_PACIENTE`. Se descarta por el mismo motivo que en la DEC-061: devuelve datos personales que CU13 no necesita.

### Justificación

Reutiliza la operación que CU12 ya ofrece para este fin y permite desarrollar y probar CU13 sin esperar a que el servidor de CU12 esté terminado.

### Estado

⏳ Pendiente de aprobación (falta confirmar con el responsable de CU12 el nombre de la interfaz en el servidor)

---

## DEC-065: Operaciones del protocolo de sockets para CU13

**Fecha:** 25/11/2026

### Decisión

Se amplía el protocolo de la DEC-039, con el mismo formato, escape y puerto que la DEC-057:

| Petición | Respuesta correcta |
|---|---|
| `ASIGNAR_CITA;<datos>` | `OK;<cita>` |
| `BUSCAR_CITAS;<dni>;<desde>;<hasta>;<estado>` | `OK;<n>;<cita1>;<cita2>…` |
| `REPROGRAMAR_CITA;<id>;<idRecurso>;<inicio>;<duracion>` | `OK;<cita>` |
| `CANCELAR_CITA;<id>;<motivo>` | `OK;<cita>` |
| `LISTAR_TIPOS_PRUEBA` | `OK;<n>;<id>;<nombre>;<duracion>…` |
| `LISTAR_RECURSOS` | `OK;<n>;<id>;<nombre>…` |

- `<datos>` son 5 campos en este orden: `dni;idTipoPrueba;idRecurso;inicio;duracion`.
- `<cita>` son 9 campos: `id;dni;idTipoPrueba;idRecurso;inicio;duracion;fin;estado;motivo`.
- En `BUSCAR_CITAS` un filtro vacío significa «sin filtro». Las fechas van en ISO (`aaaa-mm-dd`) y la fecha y hora en ISO (`aaaa-mm-ddThh:mm`).
- Gracias al escape de la DEC-057, el motivo de cancelación puede contener `;` o saltos de línea.
- Los nombres de las operaciones van en una clase `ProtocoloCitas` del módulo `comun`.

Códigos de error (se añaden a `CodigoError`; los marcados con * ya existen):

| Código | Cuándo |
|---|---|
| `CAMPO_OBLIGATORIO`* / `CAMPO_NO_VALIDO`* | Falta un dato o tiene mal formato (indica el campo, CA-02). Incluye duración ≤ 0 y motivo vacío o de más de 255 caracteres. |
| `PACIENTE_NO_ENCONTRADO`* / `PACIENTE_DE_BAJA`* | RN-01 (DEC-064). |
| `FECHA_PASADA` | El inicio es anterior al momento actual (RN-02). |
| `FUERA_DE_HORARIO` | La cita no empieza y termina dentro del horario de la DEC-044 (RN-03). |
| `SOLAPE_RECURSO` / `SOLAPE_PACIENTE` | Choca con otra cita `PROGRAMADA` del recurso o del paciente (RN-04, RN-05). El error indica con qué cita choca. |
| `CITA_NO_ENCONTRADA` | No existe una cita con ese identificador. |
| `CITA_NO_MODIFICABLE` | La cita no está `PROGRAMADA` o, al reprogramar o cancelar, ya ha empezado (RN-08). |

### Justificación

Mantiene un único formato de protocolo en todo el sistema y deja a CU13 un contrato cerrado entre cliente y servidor, como hizo CU12.

### Estado

⏳ Pendiente de aprobación

---

## DEC-066: Tablas de citas y control de solapes

**Fecha:** 25/11/2026

### Decisión

- Se añaden al script SQL (DEC-031) las tablas `tipo_prueba`, `recurso` y `cita`, con los datos de ejemplo de la DEC-047.
- La tabla `cita` guarda también la hora de fin (`fin`) para poder consultar los solapes directamente, con índices por recurso y por paciente.
- La comprobación de solape y el guardado de la cita se hacen en **una sola transacción**, bloqueando las citas afectadas (`SELECT … FOR UPDATE`), para que dos peticiones a la vez no creen citas solapadas (DEC-043).

### Alternativas consideradas

Un método `synchronized` en `CitaService`. Es más sencillo y válido con un único servidor, pero deja de funcionar si algún día hay más de uno.

### Justificación

Garantiza RN-04 y RN-05 aunque dos administrativos asignen citas al mismo tiempo.

### Estado

⏳ Pendiente de aprobación

---

## DEC-067: Enlace entre la cita y la solicitud

**Fecha:** 25/11/2026

### Decisión

La cita de CU13 no guarda ninguna referencia a la solicitud. Es la solicitud (CU06) la que guarda el identificador de su cita. CU10 obtiene la cita desde la solicitud con `obtenerCita(id)` (DEC-063).

### Alternativas consideradas

Que la cita guarde el identificador de la solicitud. Se descarta porque en CU13 todavía no existen las solicitudes (llegan en CU06) y obligaría a cambiar el modelo de la cita ya especificado.

### Justificación

Mantiene la cita tal y como está especificada en CU13 y deja el enlace en el caso de uso que crea la solicitud. Corrige el supuesto de la especificación de CU10 de que «la cita se obtiene de la solicitud».

### Estado

⏳ Pendiente de aprobación (de acuerdo con los responsables de CU06 y CU10)

---

## DEC-068: Modelo del informe y de la imagen diagnóstica

**Fecha:** 26/11/2026

### Decisión

Se concretan los datos del apartado 8 de la especificación de CU09. `Informe`, `EstadoInforme`, `ImagenDiagnostica` y `FormatoImagen` van en el módulo `comun` (DEC-058), paquete `es.uclm.esi.iso2.comun.dominio`, y sus validaciones en `ValidadorInforme`, que usan igual el cliente y el servidor (DEC-055).

Antes de validar se eliminan los espacios del principio y del final de los textos.

**Informe**

| Campo | Tipo | Obligatorio | Validación |
|---|---|---|---|
| Identificador | `long` | Automático | Lo asigna la base de datos. |
| Prueba | `long` | Sí | Prueba `REALIZADA` de CU10 de un paciente del médico (RN-01, RN-02). Única: un informe por prueba (RN-03). |
| DNI del paciente | `String` | Automático | Se copia de la prueba al crear el informe; permite buscar por DNI sin consultar CU10 (el DNI no cambia, RN-03 de CU12). |
| Médico autor | `String` | Sí | Identificador provisional del médico (DEC-072). |
| Hallazgos | `String` | Sí | Entre 1 y 4000 caracteres. |
| Conclusión | `String` | Sí | Entre 1 y 1000 caracteres. |
| Estado | `EstadoInforme` | Sí | `BORRADOR` o `FINALIZADO`. Lo asigna el sistema: el informe se crea `BORRADOR`. |
| Fecha de creación | `LocalDateTime` | Automático | La asigna el servidor. |
| Fecha de finalización | `LocalDateTime` | Solo al finalizar | La asigna el servidor; vacía mientras es `BORRADOR`. |

**Imagen diagnóstica**

| Campo | Tipo | Obligatorio | Validación |
|---|---|---|---|
| Identificador | `long` | Automático | Lo asigna la base de datos. |
| Prueba | `long` | Sí | Prueba de CU10 de un paciente del médico. |
| Nombre de fichero | `String` | Sí | Entre 1 y 100 caracteres, sin separadores de ruta (`/` ni `\`). |
| Formato | `FormatoImagen` | Sí | `JPG` o `PNG`. DICOM solo se simula tras la interfaz de almacenamiento (DEC-071). |
| Tamaño | `int` (bytes) | Sí | Entre 1 byte y 5 MB (5 242 880 bytes). |
| Fecha de alta | `LocalDateTime` | Automático | La asigna el servidor. |

- El tamaño máximo es de **5 MB** y no de 10 MB como proponía el apartado 8 de la especificación, porque la imagen viaja codificada en una sola línea del protocolo (DEC-070). Esto concreta el valor entre corchetes de la RN-07.
- La imagen pertenece a la prueba, no al informe (RN-08). El contenido no se guarda en la base de datos (DEC-071).

### Alternativas consideradas

Mantener los 10 MB. Se descarta porque en base64 la línea llegaría a unos 14 MB, demasiado para un protocolo de texto de una sola línea por petición.

### Justificación

La especificación dejaba estos formatos y longitudes entre corchetes y sin ellos no se pueden programar las validaciones ni diseñar los casos de prueba (clases de equivalencia y valores límite).

### Estado

⏳ Pendiente de aprobación

---

## DEC-069: Interfaces del servicio de informes y de sus colaboradores

**Fecha:** 26/11/2026

### Decisión

Las reglas de negocio de los informes están en `InformeService` (módulo `servidor`, paquete `es.uclm.esi.iso2.servidor.informes`) y el resto del sistema solo depende de la interfaz `IInformeService`:

| Operación | Qué hace | Si falla |
|---|---|---|
| `crearInforme(idMedico, idPrueba, hallazgos, conclusion)` | Crea un informe `BORRADOR`. Comprueba RN-01 a RN-04. | No guarda nada. |
| `editarInforme(idMedico, idInforme, hallazgos, conclusion)` | Cambia hallazgos y conclusión del mismo informe, solo si es `BORRADOR` (RN-05). | El informe no cambia. |
| `finalizarInforme(idMedico, idInforme)` | Pasa un informe `BORRADOR` a `FINALIZADO` con su fecha y pide el aviso al paciente (RN-06). | El informe no cambia. |
| `buscarInformes(idMedico, dni, idPrueba, desde, hasta, estado)` | Devuelve los informes del médico que cumplen los filtros (cualquiera puede ir vacío), ordenados por fecha de creación de más reciente a más antigua. | — |
| `obtenerInforme(idMedico, idInforme)` | Devuelve un informe del médico. | `INFORME_NO_ENCONTRADO`. |
| `adjuntarImagen(idMedico, idPrueba, nombre, formato, contenido)` | Valida y guarda una imagen asociada a la prueba (RN-07, RN-08). | No guarda nada. |
| `listarImagenes(idMedico, idPrueba)` | Devuelve los datos de las imágenes de la prueba, sin su contenido. | — |
| `obtenerImagen(idMedico, idImagen)` | Devuelve los datos y el contenido de una imagen. | `IMAGEN_NO_DISPONIBLE` si falta el fichero. |

Los errores se lanzan como `InformeException` con un código de la DEC-070.

`InformeService` recibe en su constructor las interfaces de las que depende. Así se puede desarrollar y probar sin que CU10 y CU02 estén integrados (plan v2.0, apartado 8.1):

- `IConsultaPruebas` (la ofrece CU10): `obtenerPrueba(idPrueba)` devuelve `DatosPrueba` con `idPrueba`, `dniPaciente`, `idMedicoSolicitante` y `estado` (`REALIZADA` o `CANCELADA`), o lanza `PRUEBA_NO_ENCONTRADA`. Los nombres son provisionales: CU10 es quien define esta interfaz y habrá que acordarlos con su responsable.
- `INotificador` (la ofrece CU02): `avisarInformeDisponible(dniPaciente, idInforme)`. Hasta It7 hay una versión simulada que solo registra el aviso. Si falla, el informe queda `FINALIZADO` igualmente (FA-11) y el fallo se registra.
- `IAlmacenImagenes`: guarda y recupera el contenido de las imágenes (DEC-071).

### Alternativas consideradas

Que `InformeService` consulte directamente las tablas de pruebas de CU10. Se descarta porque ata CU09 al esquema de otro caso de uso y obliga a esperar a que exista.

### Justificación

Una sola interfaz por caso de uso evita que otros modifiquen el código de CU09 y permite que CU03 (historial) la reutilice más adelante. Las interfaces de CU10 y CU02 permiten trabajar en paralelo.

### Estado

⏳ Pendiente de aprobación (falta acordar con el responsable de CU10 la interfaz `IConsultaPruebas`)

---

## DEC-070: Operaciones del protocolo de sockets para CU09

**Fecha:** 26/11/2026

### Decisión

Se amplía el protocolo de la DEC-039 con el mismo formato, escape y puerto que la DEC-057:

| Petición | Respuesta correcta |
|---|---|
| `CREAR_INFORME;<idMedico>;<idPrueba>;<hallazgos>;<conclusion>` | `OK;<informe>` |
| `EDITAR_INFORME;<idMedico>;<idInforme>;<hallazgos>;<conclusion>` | `OK;<informe>` |
| `FINALIZAR_INFORME;<idMedico>;<idInforme>` | `OK;<informe>` |
| `BUSCAR_INFORMES;<idMedico>;<dni>;<idPrueba>;<desde>;<hasta>;<estado>` | `OK;<n>;<informe1>;<informe2>…` |
| `OBTENER_INFORME;<idMedico>;<idInforme>` | `OK;<informe>` |
| `SUBIR_IMAGEN;<idMedico>;<idPrueba>;<nombre>;<formato>;<contenido>` | `OK;<imagen>` |
| `LISTAR_IMAGENES;<idMedico>;<idPrueba>` | `OK;<n>;<imagen1>;<imagen2>…` |
| `OBTENER_IMAGEN;<idMedico>;<idImagen>` | `OK;<imagen>;<contenido>` |

- `<informe>` son 9 campos: `id;idPrueba;dni;idMedico;hallazgos;conclusion;estado;fechaCreacion;fechaFinalizacion`.
- `<imagen>` son 6 campos: `id;idPrueba;nombre;formato;tamano;fechaAlta`.
- `<contenido>` es el contenido de la imagen en base64, en una sola línea.
- Las fechas con hora van en ISO (`aaaa-mm-ddThh:mm`) y las de los filtros de búsqueda en ISO (`aaaa-mm-dd`). En `BUSCAR_INFORMES` un filtro vacío significa «sin filtro».
- Gracias al escape de la DEC-057, los hallazgos y la conclusión pueden contener `;` y saltos de línea.

Códigos de error (se añaden a `CodigoError`; los marcados con * ya existen):

| Código | Cuándo |
|---|---|
| `CAMPO_OBLIGATORIO`* / `CAMPO_NO_VALIDO`* | Falta un dato o tiene mal formato; indica el campo (CA-02). Incluye textos demasiado largos. |
| `PRUEBA_NO_ENCONTRADA` | No existe la prueba (CA-03). |
| `PRUEBA_NO_REALIZADA` | La prueba no está `REALIZADA` (RN-01, CA-03). |
| `PRUEBA_NO_PERMITIDA` | La prueba no es de un paciente del médico (RN-02, CA-04). |
| `INFORME_YA_EXISTE` | Ya hay un informe para esa prueba (RN-03, CA-05); el error indica su identificador. |
| `INFORME_NO_ENCONTRADO` | No existe el informe, o no es de un paciente del médico. |
| `INFORME_NO_EDITABLE` | El informe está `FINALIZADO` (RN-05, CA-06). |
| `IMAGEN_FORMATO_NO_VALIDO` | El formato no es `JPG` ni `PNG` (CA-08). |
| `IMAGEN_DEMASIADO_GRANDE` | El contenido supera 5 MB (CA-08). |
| `IMAGEN_NO_DISPONIBLE` | La imagen está registrada pero su fichero no existe (CA-10). |

- Hoy el tercer campo de un error es un `CampoPaciente`. Se generaliza a un texto libre («detalle») que pueda ser el nombre de un campo o un identificador. Este cambio en `Respuesta` y `CodigoError` (módulo `comun`) afecta también a CU12 y CU13 y se hará de acuerdo con sus responsables.

### Justificación

El cliente y el servidor se desarrollan por separado y necesitan un contrato cerrado. Los códigos de error permiten mostrar mensajes claros sin interpretar textos libres.

### Estado

⏳ Pendiente de aprobación

---

## DEC-071: Almacenamiento de las imágenes

**Fecha:** 26/11/2026

### Decisión

- El contenido de cada imagen se guarda como un fichero en una carpeta del servidor (`radiodiagnostico.imagenes`, por defecto `imagenes/`), con el nombre `<idImagen>.<extension>`. Nunca se usa el nombre que envía el cliente para construir la ruta.
- En MySQL solo se guardan sus datos y la referencia (DEC-072).
- El acceso al almacenamiento se hace a través de la interfaz `IAlmacenImagenes` (`guardar`, `leer`), con dos implementaciones: `AlmacenImagenesEnDisco` y `AlmacenImagenesEnMemoria` para las pruebas.
- DICOM solo se simula: un almacén DICOM real sería otra implementación de la misma interfaz y no forma parte de esta iteración.
- Si el fichero no existe al leerlo, se responde `IMAGEN_NO_DISPONIBLE` y el resto de la consulta sigue funcionando (FA-09).
- Al subir una imagen se guarda primero el fichero y después la fila en la base de datos; si esta falla, se borra el fichero.

### Alternativas consideradas

Guardar la imagen en la base de datos (`LONGBLOB`). Se descarta porque engorda la base de datos y complica el script SQL y las copias.

### Justificación

Es lo habitual con imágenes médicas y deja preparada la integración con DICOM (CU20) sin tocar el servicio.

### Estado

⏳ Pendiente de aprobación

---

## DEC-072: Tablas de informes e imágenes y control de concurrencia

**Fecha:** 26/11/2026

### Decisión

- Se añaden al script SQL (DEC-031) las tablas `informe` e `imagen`:

| Tabla | Columnas |
|---|---|
| `informe` | `id`, `id_prueba` (**UNIQUE**), `dni_paciente`, `id_medico`, `hallazgos`, `conclusion`, `estado`, `fecha_creacion`, `fecha_finalizacion`. Índices por `dni_paciente` y por `id_medico`. |
| `imagen` | `id`, `id_prueba`, `nombre_fichero`, `formato`, `tamano`, `fecha_alta`. Índice por `id_prueba`. |

- La restricción `UNIQUE` sobre `id_prueba` garantiza la RN-03 aunque dos médicos creen el informe a la vez: el segundo recibe `INFORME_YA_EXISTE`.
- Editar y finalizar son actualizaciones condicionadas al estado (`UPDATE … WHERE id = ? AND estado = 'BORRADOR'`). Si no cambia ninguna fila, se responde `INFORME_NO_EDITABLE`, de modo que no se puede finalizar dos veces ni editar un informe ya finalizado.
- El aviso al paciente se pide después de confirmar la transacción de finalizar.
- **Identificación provisional del médico:** hasta que exista CU23, todas las peticiones de CU09 llevan el identificador del médico (texto de 1 a 20 caracteres) como primer campo. El servidor no lo verifica contra ninguna sesión. Con CU23 se sustituirá por el identificador de sesión, sin cambiar el resto del mensaje (RN-09).

### Alternativas consideradas

Un método `synchronized` en `InformeService`. Es más sencillo, pero deja de proteger si algún día hay más de un servidor.

### Justificación

Garantiza una única versión de cada informe sin depender del código de la aplicación y deja preparado el paso a CU23.

### Estado

⏳ Pendiente de aprobación

---

## DEC-073: Estructura del cliente de CU09

**Fecha:** 26/11/2026

### Decisión

El cliente de CU09 sigue la estructura de la DEC-059:

- **Presentación** (`es.uclm.esi.iso2.cliente.presentacion`): `PantallaBusquedaInformes`, `FormularioInforme` y `VisorImagenes`, con FXML. Solo muestran datos y recogen lo que escribe el usuario, sin lógica.
- **Lógica** (`es.uclm.esi.iso2.cliente.logica`): `GestorInformesCliente` valida antes de enviar (campos obligatorios, longitudes, formato y tamaño de la imagen) y traduce los códigos de error de la DEC-070 en mensajes claros.
- **Servicio** (`es.uclm.esi.iso2.cliente.servicio`): la interfaz `ServicioInformes` con `ServicioInformesSocket` (real) y `ServicioInformesEnMemoria` (aplica las mismas reglas sin red, para probar la interfaz y las pruebas JUnit).
- Las imágenes se muestran con `ImageView` a partir de los bytes recibidos. Si una imagen no está disponible, se muestra un aviso en su lugar.
- Las pantallas no ofrecen editar ni finalizar un informe `FINALIZADO`; el servidor lo rechaza igualmente (CA-06).
- La cobertura de ramas del 75 % excluye el paquete `presentacion` (DEC-060).

### Justificación

Permite desarrollar y probar el cliente antes de que el servidor esté terminado y mantiene la misma estructura en todos los casos de uso.

### Estado

⏳ Pendiente de aprobación

---

## DEC-074: Modelo de la prueba y de la incidencia

**Fecha:** 26/11/2026

### Decisión

Se concretan los datos del apartado 8 de la especificación de CU10. `Prueba`, `Incidencia`, `EstadoPrueba` y `TipoIncidencia` van en el módulo `comun` (DEC-058), paquete `es.uclm.esi.iso2.comun.dominio`, y sus validaciones en `ValidadorPrueba`, que usan igual el cliente y el servidor (DEC-055). Antes de validar se eliminan los espacios del principio y del final de los textos.

**Prueba**

| Campo | Tipo | Obligatorio | Validación |
|---|---|---|---|
| Identificador | `long` | Automático | Lo asigna la base de datos. |
| Solicitud | `long` | Sí | Debe existir en CU06 y tener una cita `PROGRAMADA` (DEC-067). Solo puede haber una prueba por solicitud (RN-05). |
| DNI del paciente | `String` | Automático | Se copia de la solicitud. Formato de la DEC-055. |
| Médico | `String` | Sí | Médico que registra la prueba. De 1 a 20 caracteres, como en la DEC-072: llega en cada petición y no se verifica hasta que exista CU23. |
| Estado | `EstadoPrueba` | Automático | `REALIZADA` o `CANCELADA`. Lo asigna el sistema según la operación. |
| Fecha y hora de realización | `LocalDateTime` | Solo si `REALIZADA` | No posterior al momento actual (CA-09). Vacía si está `CANCELADA`. |
| Observaciones | `String` | No | Como máximo 1000 caracteres. |
| Motivo de cancelación | `String` | Solo si `CANCELADA` | Entre 1 y 255 caracteres (igual que DEC-046). Vacío si está `REALIZADA`. |
| Fecha de registro | `LocalDateTime` | Automático | La asigna el servidor. |

**Incidencia**

| Campo | Tipo | Obligatorio | Validación |
|---|---|---|---|
| Identificador | `long` | Automático | Lo asigna la base de datos. |
| Prueba | `long` | Automático | Prueba a la que pertenece. |
| Tipo | `TipoIncidencia` | Sí | `TECNICA`, `PACIENTE`, `MATERIAL` u `OTRA`. |
| Descripción | `String` | Sí | Entre 1 y 500 caracteres. |
| Fecha y hora | `LocalDateTime` | Automático | La asigna el servidor al guardarla. |

- Una prueba `REALIZADA` lleva fecha de realización y no lleva motivo; una `CANCELADA` lleva motivo y no lleva fecha. Cualquier otra combinación es inválida.
- Una prueba puede tener 0 o más incidencias.
- Un paciente es «del médico» si el médico es el solicitante de la solicitud (RN-02). Es el mismo criterio que usa CU09 (DEC-068). El médico solicitante no se guarda en la prueba: se obtiene de la solicitud.

### Alternativas consideradas

Guardar un identificador numérico de paciente. Se descarta porque en el sistema el paciente se identifica por su DNI (DEC-055, DEC-062) y así CU09 puede buscar por DNI sin depender de las tablas de CU12.

### Justificación

La especificación dejaba sin concretar los tipos, las longitudes y el lugar de las clases, y sin ellos no se pueden programar las validaciones ni diseñar los casos de prueba (clases de equivalencia y valores límite). Poner las entidades en `comun` sigue el criterio de CU12, CU13 y CU09.

### Estado

⏳ Pendiente de aprobación

---

## DEC-075: Interfaces del servicio de pruebas (`IPruebaService` e `IConsultaPruebas`)

**Fecha:** 26/11/2026

### Decisión

Las reglas de negocio de CU10 están en `PruebaService` (módulo `servidor`, paquete `es.uclm.esi.iso2.servidor.pruebas`). El resto del sistema solo depende de estas dos interfaces:

**`IPruebaService`** (la usa el controlador de sockets de CU10)

| Operación | Qué hace | Si falla |
|---|---|---|
| `buscarSolicitudesPendientes(idMedico, dni)` | Devuelve las solicitudes del médico con cita `PROGRAMADA` y sin prueba. Con `dni` vacío, las de todos sus pacientes. Ordenadas por inicio de la cita. | — |
| `registrarRealizacion(idMedico, idSolicitud, fechaHora, observaciones, incidencias)` | Guarda una prueba `REALIZADA` con sus incidencias, pone la cita en `REALIZADA` (`marcarRealizada`, DEC-063) y la solicitud en `REALIZADA`. | No cambia nada (DEC-076). |
| `registrarCancelacion(idMedico, idSolicitud, motivo)` | Guarda una prueba `CANCELADA` con su motivo, pone la cita en `CANCELADA` (`marcarCancelada`, DEC-063) y la solicitud en `CANCELADA`. | No cambia nada (DEC-076). |
| `anadirIncidencia(idMedico, idPrueba, tipo, descripcion)` | Añade una incidencia a una prueba ya registrada. No cambia la prueba, la cita ni la solicitud. | No cambia nada. |
| `buscarPruebas(idMedico, dni, idSolicitud, desde, hasta, estado)` | Devuelve las pruebas del médico que cumplen los filtros (cualquiera puede ir vacío), ordenadas por fecha de registro. | — |
| `consultarPrueba(idMedico, idPrueba)` | Devuelve la prueba con sus incidencias. | `PRUEBA_NO_ENCONTRADA` o `PRUEBA_NO_PERMITIDA`. |

**`IConsultaPruebas`** (solo lectura; la usa CU09 desde el propio servidor, sin mensajes de socket). Concreta la interfaz que la DEC-069 de CU09 dejaba pendiente:

| Operación | Qué hace | Si falla |
|---|---|---|
| `obtenerPrueba(idPrueba)` | Devuelve un `DatosPrueba` con `idPrueba`, `dniPaciente`, `idMedicoSolicitante` y `estado` (`REALIZADA` o `CANCELADA`). | `PRUEBA_NO_ENCONTRADA`. |

- `DatosPrueba` es un objeto de solo lectura del paquete `es.uclm.esi.iso2.servidor.pruebas`. El `idMedicoSolicitante` se obtiene de la solicitud de CU06.
- `obtenerPrueba` no comprueba qué médico pregunta: CU09 comprueba que la prueba está `REALIZADA` y que es de un paciente del médico (RN-01, RN-02 de CU09).
- Los errores se lanzan como `PruebaException` con un código de la DEC-078.
- Las operaciones de `IPruebaService` comprueban que el paciente de la solicitud o de la prueba es del médico (RN-02).
- `PruebaService` recibe en su constructor lo que necesita de otros casos de uso: `ICitaService` (DEC-063), el servicio de solicitudes de CU06 y la comprobación del paciente de CU12 (DEC-064). Mientras no existan, en las pruebas se usan versiones simuladas.

### Alternativas consideradas

Que CU09 use las operaciones de `IPruebaService`. Se descarta porque esa interfaz también modifica datos y CU09 solo necesita saber si la prueba existe, de quién es y en qué estado está.

### Justificación

Una interfaz mínima y de solo lectura deja a CU09 un contrato estable y evita que otros casos de uso modifiquen pruebas por error.

### Estado

⏳ Pendiente de aprobación (de acuerdo con la responsable de CU09; sus nombres de la DEC-069 se mantienen)

---

## DEC-076: Registro de la prueba, la cita y la solicitud en una sola operación

**Fecha:** 26/11/2026

### Decisión

Registrar una prueba (realizada o cancelada) cambia tres cosas: guarda la prueba (CU10), cambia el estado de la cita (CU13) y cambia el estado de la solicitud (CU06). Deben hacerse las tres o ninguna (RN-07). El orden es:

1. Validar los datos y comprobar la solicitud, el médico y la cita, sin cambiar nada.
2. Guardar la prueba. La restricción `UNIQUE` de `id_solicitud` impide el duplicado aunque dos peticiones lleguen a la vez (CA-05).
3. Llamar a `marcarRealizada` o `marcarCancelada` de `ICitaService`.
4. Cambiar el estado de la solicitud.

Si falla el paso 3 o el 4, se borra la prueba guardada en el paso 2 y se responde `ERROR_INTERNO`.

Si el servidor permite que los repositorios de CU06, CU10 y CU13 compartan una única transacción de MySQL, se usará esa transacción y no hará falta borrar nada.

### Alternativas consideradas

Una transacción compartida siempre. Es la solución más limpia, pero la DEC-066 hace que cada servicio gestione la suya y hay que confirmar si pueden compartirse. Se deja como mejora si es posible.

### Justificación

Garantiza que nunca queda una prueba sin su cita y su solicitud actualizadas, con el menor cambio posible en el código de los demás casos de uso. Queda un riesgo pequeño: si falla el paso 4 después de cambiar la cita, la cita queda cambiada sin prueba. Se registra en el log para corregirlo a mano.

### Estado

⏳ Pendiente de aprobación (falta confirmar con los responsables de CU12 y CU13 si se puede compartir la transacción)

---

## DEC-077: Estados de la solicitud y de la cita al registrar la prueba

**Fecha:** 26/11/2026

### Decisión

| Operación de CU10 | Prueba | Cita (CU13) | Solicitud (CU06) |
|---|---|---|---|
| `registrarRealizacion` | `REALIZADA` | `REALIZADA` | `REALIZADA` |
| `registrarCancelacion` | `CANCELADA` | `CANCELADA` con el motivo | `CANCELADA` |

- La cita se obtiene a partir de la solicitud, que guarda su identificador (DEC-067). Si la solicitud no tiene cita, o su cita no está `PROGRAMADA`, se rechaza con `SOLICITUD_SIN_CITA` (CA-03).
- CU10 no aplica la restricción RN-08 de CU13 (la cita ya ha empezado): usa `marcarRealizada` y `marcarCancelada` de la DEC-063, no `cancelarCita`.
- El estado `REALIZADA` de la cita ya lo incluye CU13 (DEC-062), por lo que CU10 no modifica el modelo de la cita. Esto sustituye lo que decían la DEC-042 y la especificación de CU10, que deberá actualizarse.
- Los nombres exactos de los estados de la solicitud los fija CU06.

### Justificación

Deja claro qué cambia cada caso de uso y que CU10 solo llama a operaciones que los demás ya ofrecen, sin tocar su código.

### Estado

⏳ Pendiente de aprobación (de acuerdo con los responsables de CU06 y CU13)

---

## DEC-078: Operaciones del protocolo de sockets para CU10

**Fecha:** 26/11/2026

### Decisión

Se amplía el protocolo de la DEC-039 con el mismo formato, escape y puerto que la DEC-057. Como en CU09 (DEC-072), todas las peticiones llevan el identificador del médico como primer campo.

| Petición | Respuesta correcta |
|---|---|
| `LISTAR_SOLICITUDES_PENDIENTES;<idMedico>;<dni>` | `OK;<n>;<pendiente1>;<pendiente2>…` |
| `REGISTRAR_PRUEBA;<idMedico>;<idSolicitud>;<fechaHora>;<observaciones>;<n>;<incidenciaNueva1>…` | `OK;<prueba>` |
| `CANCELAR_PRUEBA;<idMedico>;<idSolicitud>;<motivo>` | `OK;<prueba>` |
| `ANADIR_INCIDENCIA;<idMedico>;<idPrueba>;<tipo>;<descripcion>` | `OK;<incidencia>` |
| `BUSCAR_PRUEBAS;<idMedico>;<dni>;<idSolicitud>;<desde>;<hasta>;<estado>` | `OK;<n>;<resumen1>;<resumen2>…` |
| `OBTENER_PRUEBA;<idMedico>;<idPrueba>` | `OK;<prueba>` |

- `<pendiente>` son 7 campos: `idSolicitud;dni;nombrePaciente;tipoPrueba;prioridad;idCita;inicioCita`.
- `<incidenciaNueva>` son 2 campos: `tipo;descripcion`. `<incidencia>` son 4 campos: `id;tipo;descripcion;fechaHora`.
- `<prueba>` son 9 campos seguidos de `<n>` y de las `n` incidencias: `id;idSolicitud;dni;idMedico;estado;fechaHoraRealizacion;observaciones;motivoCancelacion;fechaRegistro;<n>;<incidencia1>…`.
- `<resumen>` son los 9 campos de la prueba, sin incidencias, más `numIncidencias`.
- Los campos opcionales vacíos se envían como cadena vacía. En `BUSCAR_PRUEBAS` un filtro vacío significa «sin filtro». Las fechas de los filtros van en ISO (`aaaa-mm-dd`) y las fechas con hora en ISO (`aaaa-mm-ddThh:mm`).
- Gracias al escape de la DEC-057, las observaciones y las descripciones pueden contener `;` o saltos de línea.
- Los nombres de las operaciones van en una clase `ProtocoloPruebas` del módulo `comun`.

Códigos de error (se añaden a `CodigoError`; los marcados con * ya existen o los define CU09 en la DEC-070):

| Código | Cuándo |
|---|---|
| `CAMPO_OBLIGATORIO`* / `CAMPO_NO_VALIDO`* | Falta un dato o tiene mal formato; indica el campo (CA-02). Incluye textos demasiado largos y tipo de incidencia desconocido. |
| `PETICION_NO_VALIDA`* / `ERROR_INTERNO`* | Operación desconocida o número de campos incorrecto; fallo del servidor. |
| `PRUEBA_NO_ENCONTRADA`* | No existe una prueba con ese identificador. |
| `PRUEBA_NO_PERMITIDA`* | La prueba es de un paciente que no es del médico (RN-02). |
| `FECHA_FUTURA` | La fecha de realización es posterior al momento actual (CA-09). |
| `SOLICITUD_NO_ENCONTRADA` | La solicitud no existe. |
| `SOLICITUD_NO_PERMITIDA` | La solicitud es de un paciente que no es del médico (RN-02, CA-04). |
| `SOLICITUD_SIN_CITA` | La solicitud no tiene una cita `PROGRAMADA` (CA-03). |
| `PRUEBA_YA_EXISTE` | La solicitud ya tiene una prueba registrada (CA-05); el error indica su identificador. |

- El tercer campo de un error es un texto libre («detalle»: nombre de un campo o un identificador), como generaliza la DEC-070.

### Justificación

Mantiene un único formato de protocolo en todo el sistema y deja a CU10 un contrato cerrado entre cliente y servidor, como hicieron CU12, CU13 y CU09. Los nombres de operaciones y errores no chocan con los de CU09.

### Estado

⏳ Pendiente de aprobación

---

## DEC-079: Tablas de pruebas e incidencias

**Fecha:** 26/11/2026

### Decisión

Se añaden al script SQL (DEC-031) las tablas `prueba` e `incidencia`:

| Tabla | Columnas |
|---|---|
| `prueba` | `id`, `id_solicitud` (**UNIQUE**), `dni_paciente`, `id_medico`, `estado`, `fecha_hora_realizacion`, `observaciones` (1000), `motivo_cancelacion` (255), `fecha_registro`. Índice por (`id_medico`, `dni_paciente`). |
| `incidencia` | `id`, `id_prueba` (clave ajena a `prueba`), `tipo`, `descripcion` (500), `fecha_hora`. |

- Una restricción `CHECK` exige que una prueba `REALIZADA` tenga fecha y no tenga motivo, y que una `CANCELADA` tenga motivo y no tenga fecha.
- Las claves ajenas de `id_solicitud` y `dni_paciente` se añadirán cuando existan las tablas de CU06 y CU12.
- CU09 guarda en `informe` e `imagen` el identificador de la prueba (DEC-072); la clave ajena hacia `prueba` se añadirá cuando exista esta tabla.

### Justificación

La restricción `UNIQUE` garantiza una sola prueba por solicitud (RN-05, CA-05) aunque dos peticiones lleguen a la vez, y los `CHECK` mantienen coherentes el estado y los datos aunque falle una validación.

### Estado

⏳ Pendiente de aprobación

---

## DEC-080: Estructura del cliente de CU10

**Fecha:** 26/11/2026

### Decisión

El cliente de CU10 sigue la estructura de la DEC-059 y de la DEC-073:

- **Presentación** (`es.uclm.esi.iso2.cliente.presentacion`), con FXML y sin lógica: `PantallaSolicitudesPendientes` (búsqueda por DNI), `FormularioPrueba` (fecha, observaciones, incidencias y botón «Cancelar prueba» con su motivo), `PantallaBusquedaPruebas` y `DetallePrueba` (con «Añadir incidencia»).
- **Lógica** (`es.uclm.esi.iso2.cliente.logica`): `GestorPruebasCliente` valida antes de enviar con `ValidadorPrueba` y traduce los códigos de error de la DEC-078 en mensajes claros.
- **Servicio** (`es.uclm.esi.iso2.cliente.servicio`): la interfaz `ServicioPruebas` con `ServicioPruebasSocket` (real) y `ServicioPruebasEnMemoria` (aplica las mismas reglas sin red, para probar la interfaz y las pruebas JUnit).
- Mientras no exista CU23, el identificador del médico se configura al arrancar el cliente (DEC-072).
- El cliente nunca accede a la base de datos (CA-13). La cobertura de ramas del 75 % excluye el paquete `presentacion` (DEC-060).

### Justificación

Permite desarrollar y probar el cliente antes de que el servidor esté terminado y mantiene la misma estructura en todos los casos de uso.

### Estado

⏳ Pendiente de aprobación

---

## Historial de modificaciones

| Fecha | Cambio |
|---------|---------|
| 15/10/2026 | Creación inicial del documento de decisiones. |
| 20/10/2026 | Incorporadas las decisiones DEC-017 a DEC-022 derivadas de la segunda reunión de seguimiento. |
| 20/10/2026 | Actualizadas las decisiones DEC-009 y DEC-015 con información adicional sobre gestión documental y gestión de restricciones. |
| 10/11/2026 | Corregida la numeración de decisiones a partir de la duplicidad detectada en DEC-017. |
| 10/11/2026 | Incorporadas las decisiones DEC-024 a DEC-029 derivadas de la cuarta reunión de seguimiento. |
| 17/11/2026 | Incorporadas las decisiones DEC-030 a DEC-038 derivadas de la planificación de la fase de implementación y revisión documental. |
| 17/11/2026 | Concretadas DEC-032, DEC-033 y DEC-034. Incorporada la decisión DEC-039 sobre comunicación por sockets. |
| 23/11/2026 | Incorporadas las decisiones DEC-042 a DEC-048 derivadas de la especificación de CU13 (pendientes de aprobación). |
| 24/11/2026 | Incorporadas las decisiones DEC-049 a DEC-054 derivadas de la sexta reunión de seguimiento (reparto individual de casos de uso, actualización del PUD, alcance de análisis, pruebas cruzadas y trabajo teórico). |
| 25/11/2026 | Incorporadas las decisiones DEC-055 a DEC-060 derivadas del diseño de CU12 (datos del paciente, búsqueda por DNI, protocolo, módulo comun, estructura del cliente y cobertura), pendientes de aprobación. |
| 25/11/2026 | Incorporada la decisión DEC-061 (operación COMPROBAR_PACIENTE para CU13), pendiente de aprobación. |
| 25/11/2026 | Incorporadas las decisiones DEC-062 a DEC-067 derivadas del diseño de CU13 (modelo de la cita, interfaz del servicio, comprobación del paciente, protocolo, tablas y enlace con la solicitud), pendientes de aprobación. |
| 26/11/2026 | Incorporadas las decisiones DEC-074 a DEC-080 derivadas del diseño de CU10 (modelo de la prueba, interfaces, registro atómico, estados, protocolo, tablas y cliente), pendientes de aprobación. |

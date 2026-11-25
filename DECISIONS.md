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

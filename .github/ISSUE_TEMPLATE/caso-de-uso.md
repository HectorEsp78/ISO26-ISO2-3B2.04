---
name: Caso de uso
about: Una iteración = un caso de uso
title: "CUxx – Nombre (módulo vX.Y.Z)"
---

<!--
Issue padre del caso de uso. Después de crearla:
1. Asígnale la etiqueta, el milestone de la iteración y un responsable.
2. Crea las sub-issues «CUxx · servidor» y «CUxx · cliente» (botón «Create sub-issue»).
3. Crea la rama de cada sub-issue desde develop: feature/CUxx-servidor y feature/CUxx-cliente.
-->

## Caso de uso
CU · RF · Fase · Iteración
<!-- Ejemplo: CU12 · RF12 · Elaboración · It1 -->

- Acta: <!-- enlace al acta de la wiki -->
- Especificación: <!-- enlace a la ficha del caso de uso (documento de Casos de uso / wiki) -->
- Diagramas: <!-- diagramas de Visual Paradigm de esta iteración -->

## Disciplinas RADIT
- [ ] Requisitos: ficha del caso de uso ampliada (flujos, datos y reglas) y criterios de aceptación
- [ ] Análisis: clases de análisis y diagramas de secuencia
- [ ] Diseño: interfaces, contratos y tabla de casos de prueba
- [ ] Implementación: servidor y cliente
- [ ] Pruebas: `mvn verify` en verde y cobertura de ramas de al menos el 75 %

## Sub-issues
- [ ] #<!-- n --> CUxx · servidor
- [ ] #<!-- n --> CUxx · cliente

## Contratos e interfaces
<!-- Interfaces de servicio y operaciones del protocolo (DEC-039), por ejemplo IPacienteService y ALTA_PACIENTE;… -->

## Criterios de aceptación
<!-- Cada criterio debe poder comprobarse con una prueba -->
- [ ] 

## Decisiones (DECISIONS.md)
<!-- DEC-xxx relacionadas con este caso de uso -->

## Horas
| Disciplina | Estimadas (PUD) | Reales |
|---|---|---|
| Requisitos |  |  |
| Análisis |  |  |
| Diseño |  |  |
| Implementación |  |  |
| Pruebas |  |  |
| **Total** |  |  |

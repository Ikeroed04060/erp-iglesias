# ADR - Frontend Architecture Decisions

## Estado
Propuestos 7  
Implementados 1  
Parcialmente implementados 2

## Contexto

El frontend de **ERP Iglesias** está construido con **Angular** y actualmente ya presenta una organización inicial por módulos funcionales. Sin embargo, todavía existen oportunidades claras de mejora en desacoplamiento, mantenibilidad, escalabilidad, manejo de errores, organización de servicios y separación de responsabilidades dentro de los componentes.

Este documento detalla las **decisiones arquitectónicas implementadas, parcialmente implementadas y propuestas** para fortalecer la arquitectura del frontend y facilitar su evolución futura.

---

## **ADR-001: Organización modular por features**

### **Estado**
Parcialmente implementado

### **Contexto**
El frontend ya comenzó a organizarse por módulos funcionales dentro de `features`, lo cual es una buena base. Sin embargo, la separación aún no está completamente consolidada en todos los niveles del proyecto.

### **Decisión**
Adoptar una organización modular basada en:
- `core` para elementos globales del sistema.
- `shared` para elementos reutilizables.
- `features` para módulos funcionales del dominio.

### **Acción**
- Mantener cada módulo del sistema dentro de `features`.
- Centralizar autenticación, guards, interceptores y configuración HTTP en `core`.
- Ubicar componentes, pipes y utilidades reutilizables en `shared`.

### **Consecuencias**
**Positivas**:
- Mejor organización del código.
- Facilita la navegación del proyecto.
- Escala mejor cuando se agregan nuevos módulos.

**Negativas**:
- Requiere disciplina para no mezclar elementos entre carpetas.
- Implica mover archivos existentes y actualizar imports.

---

## **ADR-002: Desacoplamiento del servicio API monolítico hacia servicios por feature**

### **Estado**
Propuesto

### **Contexto**
Actualmente existe un servicio centralizado que concentra llamadas HTTP de múltiples módulos. Esto genera acoplamiento innecesario y dificulta el crecimiento del frontend.

### **Decisión**
Reemplazar el enfoque de `ApiService` monolítico por **servicios de datos específicos por módulo**.

### **Acción**
- Crear servicios como:
  - `people-api.service.ts`
  - `courses-api.service.ts`
  - `enrollments-api.service.ts`
  - `offerings-api.service.ts`
  - `payments-api.service.ts`
- Mantener cada servicio dentro del `data/` de su feature.
- Eliminar gradualmente el servicio centralizado.

### **Consecuencias**
**Positivas**:
- Reduce el acoplamiento entre módulos.
- Hace más claro el propósito de cada servicio.
- Facilita mantenimiento y pruebas.

**Negativas**:
- Incrementa el número de archivos.
- Requiere una migración ordenada para no romper imports ni componentes.

---

## **ADR-003: Externalización de configuración por ambiente**

### **Estado**
Propuesto

### **Contexto**
La URL base de la API y otras configuraciones del frontend no deben quedar hardcodeadas dentro de servicios, ya que eso dificulta el despliegue en múltiples ambientes.

### **Decisión**
Externalizar la configuración dependiente del ambiente usando archivos `environment`.

### **Acción**
- Definir `apiUrl` en:
  - `environment.ts`
  - `environment.production.ts`
- Hacer que los servicios consuman esta configuración en lugar de valores fijos.
- Preparar el frontend para desarrollo, pruebas y producción.

### **Consecuencias**
**Positivas**:
- Mejora la portabilidad entre ambientes.
- Reduce cambios manuales antes del despliegue.
- Facilita automatización en pipelines.

**Negativas**:
- Requiere revisar referencias actuales al `baseUrl`.
- Puede generar errores si no se documentan bien los ambientes.

---

## **ADR-004: Centralización de autenticación, guards e interceptores en core**

### **Estado**
Implementado

### **Contexto**
La autenticación y la seguridad del frontend deben ubicarse en una zona claramente identificable y transversal al sistema.

### **Decisión**
Centralizar en `core` los elementos relacionados con autenticación y comunicación transversal HTTP.

### **Acción**
- Mantener en `core`:
  - `auth.service`
  - `auth.guard`
  - `auth.interceptor`
- Evitar duplicar lógica de sesión o token dentro de los módulos funcionales.
- Usar esta base para extender luego control por roles y manejo de expiración del token.

### **Consecuencias**
**Positivas**:
- Hace más legible la arquitectura.
- Reduce duplicación.
- Facilita evolución de seguridad del frontend.

**Negativas**:
- Si se sobrecarga `core`, puede volverse una carpeta demasiado grande.
- Requiere mantener criterios claros de qué sí pertenece a `core`.

---

## **ADR-005: Manejo global de errores HTTP**

### **Estado**
Propuesto

### **Contexto**
Actualmente los errores se manejan de forma local dentro de muchos componentes, lo que produce repetición e inconsistencias en mensajes y comportamiento.

### **Decisión**
Centralizar el manejo de errores HTTP mediante un interceptor o servicio compartido de errores.

### **Acción**
- Implementar un `error.interceptor.ts` o un `http-error-handler.service.ts`.
- Estandarizar el tratamiento de errores 400, 401, 403, 404 y 500.
- Integrar mensajes consistentes con la interfaz de usuario.

### **Consecuencias**
**Positivas**:
- Respuestas de error uniformes.
- Menos código repetido en componentes.
- Mejor experiencia de usuario.

**Negativas**:
- Requiere definir una convención de errores del backend.
- Puede requerir ajustes en componentes existentes.

---

## **ADR-006: Control de acceso visual y navegación por roles**

### **Estado**
Propuesto

### **Contexto**
Aunque el backend aplique autorización por rol, el frontend también debe adaptar la navegación y la visibilidad de opciones para no mostrar rutas o acciones no permitidas.

### **Decisión**
Incorporar control de navegación y renderizado condicional por roles en el frontend.

### **Acción**
- Crear utilidades o guards por rol.
- Condicionar elementos del menú según `ADMIN` o `CLIENT`.
- Evitar mostrar acciones que terminarán en error 403.

### **Consecuencias**
**Positivas**:
- Mejora experiencia de usuario.
- Hace la interfaz más coherente con la seguridad del backend.
- Reduce frustración por opciones visibles pero no utilizables.

**Negativas**:
- Duplica parcialmente la lógica de permisos.
- Requiere sincronía entre frontend y backend sobre roles y capacidades.

---

## **ADR-007: Separación entre componentes contenedores y presentacionales**

### **Estado**
Propuesto

### **Contexto**
Actualmente varios componentes concentran demasiadas responsabilidades: formulario, tabla, llamadas HTTP, notificaciones, manejo de errores y estado de carga.

### **Decisión**
Separar componentes en:
- **contenedores/pages**, encargados de orquestar datos y eventos;
- **presentacionales/components**, encargados de renderizar UI y emitir eventos.

### **Acción**
- Crear carpetas `pages/` y `components/` dentro de cada feature.
- Mover formularios y tablas reutilizables a componentes presentacionales.
- Dejar en las páginas la interacción con servicios y flujo del módulo.

### **Consecuencias**
**Positivas**:
- Mejora reutilización.
- Reduce complejidad por componente.
- Facilita pruebas y mantenimiento.

**Negativas**:
- Aumenta el número de archivos.
- Requiere rediseñar parte de la estructura actual.

---

## **ADR-008: Uso de modelos tipados por feature**

### **Estado**
Propuesto

### **Contexto**
Las interfaces y tipos del frontend deben vivir cerca de su contexto funcional para reducir acoplamiento y mejorar claridad.

### **Decisión**
Mantener modelos y contratos tipados dentro de cada feature, en una carpeta `models/`.

### **Acción**
- Crear archivos como:
  - `person.model.ts`
  - `course.model.ts`
  - `payment.model.ts`
- Separar payloads de creación/actualización de los modelos de lectura.
- Evitar concentrar todos los tipos del sistema en un solo archivo.

### **Consecuencias**
**Positivas**:
- Mejor cohesión por módulo.
- Tipado más claro y mantenible.
- Facilita lectura y evolución del contrato frontend-backend.

**Negativas**:
- Requiere reorganizar imports.
- Puede generar duplicación si no se define una convención clara.

---

## **ADR-009: Implementación de lazy loading para rutas de features**

### **Estado**
Propuesto

### **Contexto**
El frontend no debería cargar todos los módulos funcionales desde el inicio si puede dividirlos en cargas diferidas.

### **Decisión**
Usar lazy loading a nivel de rutas para los módulos funcionales.

### **Acción**
- Reemplazar imports directos en rutas por `loadComponent`.
- Cargar de forma diferida las páginas de cada feature.
- Mantener la ruta de login y layout según convenga al flujo principal.

### **Consecuencias**
**Positivas**:
- Mejora rendimiento inicial.
- Escala mejor a medida que crecen los módulos.
- Reduce carga innecesaria al inicio.

**Negativas**:
- Añade algo de complejidad a la configuración de rutas.
- Puede requerir revisar dependencias transversales.

---

## **ADR-010: Estandarización de formularios reactivos y estados de UI**

### **Estado**
Parcialmente implementado

### **Contexto**
El frontend ya usa formularios reactivos en varios módulos, pero todavía puede fortalecerse el tipado, el manejo de estados de carga y la consistencia de la experiencia de usuario.

### **Decisión**
Estandarizar el uso de formularios reactivos tipados y definir estados consistentes de interfaz.

### **Acción**
- Usar formularios reactivos tipados cuando sea posible.
- Estandarizar estados:
  - `loading`
  - `success`
  - `empty`
  - `error`
- Deshabilitar acciones durante envíos.
- Unificar validaciones visuales y mensajes de error.

### **Consecuencias**
**Positivas**:
- Mejora confiabilidad y experiencia de usuario.
- Reduce errores por formularios inconsistentes.
- Facilita mantenimiento de las pantallas.

**Negativas**:
- Requiere refactor en varios componentes.
- Implica revisar formularios existentes módulo por módulo.

---

## **Conclusión**

- **ADR-001**: La organización por `features` ya está iniciada y debe consolidarse.
- **ADR-002**: Se propone desacoplar el `ApiService` central hacia servicios por módulo.
- **ADR-003**: Se propone externalizar configuración por ambiente.
- **ADR-004**: La autenticación transversal ya está centralizada en `core`.
- **ADR-005**: Se propone un manejo global de errores HTTP.
- **ADR-006**: Se propone control visual y navegación por roles.
- **ADR-007**: Se propone separar páginas de componentes presentacionales.
- **ADR-008**: Se propone mantener modelos tipados por feature.
- **ADR-009**: Se propone aplicar lazy loading en rutas.
- **ADR-010**: Los formularios reactivos y estados de UI están parcialmente implementados y deben estandarizarse.
# ADR - Backend Architecture Decisions

## Estado
Propuesto

## Contexto

El sistema **ERP Iglesias** es una aplicación web construida con Spring Boot. En su backend, se detectaron varios problemas de arquitectura que afectan la organización del código, la mantenibilidad y la escalabilidad del sistema.

Este documento detalla las decisiones arquitectónicas propuestas para mejorar la estructura y facilitar el crecimiento futuro del sistema.

---

## ADR-001: Adopción de arquitectura en capas

### Estado
Propuesto

### Contexto
El backend tiene la lógica de negocio y el acceso a la base de datos mezclados con la exposición de endpoints.

### Decisión
Adoptar una **arquitectura en capas**: **Controller -> Service -> Repository**.

### Consecuencias
**Positivas**:
- Mejor separación de responsabilidades.
- Código más mantenible y escalable.
- Lógica de negocio aislada para facilitar pruebas.

**Negativas**:
- Aumento en la cantidad de clases.

---

## ADR-002: Uso de DTOs

### Estado
Propuesto

### Contexto
Las entidades del modelo de dominio se exponen directamente a través de la API, lo que genera un alto acoplamiento entre la capa de presentación y la persistencia.

### Decisión
Usar **DTOs** (Data Transfer Objects) para desacoplar el modelo de persistencia de la API.

### Consecuencias
**Positivas**:
- Se mejora la seguridad y la flexibilidad del sistema.
- Facilita la transformación de datos entre capas.

**Negativas**:
- Introducción de más clases para representar datos.

---

## ADR-003: Manejo global de excepciones

### Estado
Propuesto

### Contexto
El manejo de errores está disperso entre los controladores.

### Decisión
Centralizar el manejo de excepciones utilizando `@ControllerAdvice` para manejar todas las excepciones a nivel global.

### Consecuencias
**Positivas**:
- Mayor consistencia en la respuesta a errores.
- Facilita la gestión de excepciones globales.

**Negativas**:
- Necesidad de configurar la excepción para cada tipo de error.

---

## ADR-004: Externalización de la configuración sensible

### Estado
Propuesto

### Contexto
Las configuraciones sensibles (como contraseñas y secretos) están directamente en el archivo `application.properties`, lo que representa un riesgo de seguridad.

### Decisión
Usar **variables de entorno** para la configuración sensible y **no almacenarlas directamente en el código**.

### Consecuencias
**Positivas**:
- Mejora la seguridad al mantener los secretos fuera del código.
- Facilita la gestión de configuraciones en diferentes entornos.

**Negativas**:
- Necesidad de gestionar las variables de entorno.

---

## ADR-005: Uso de JWT para autenticación

### Estado
Propuesto

### Contexto
El sistema necesita un mecanismo de autenticación eficiente y escalable.

### Decisión
Usar **JWT (JSON Web Tokens)** para la autenticación de usuarios de manera **stateless**.

### Consecuencias
**Positivas**:
- Mejora la escalabilidad al no depender de sesiones de servidor.
- Proporciona una solución estándar y segura.

**Negativas**:
- Requiere manejo adecuado de tokens y expiración.

---

## ADR-006: Uso de Spring Data JPA

### Estado
Propuesto

### Contexto
Se están utilizando consultas manuales en la capa de persistencia.

### Decisión
Usar **Spring Data JPA** para gestionar el acceso a datos, aprovechando sus repositorios y la implementación automática de consultas.

### Consecuencias
**Positivas**:
- Simplifica la capa de persistencia.
- Mejora la mantenibilidad y reduce el código repetido.

**Negativas**:
- Puede haber casos en los que Spring Data JPA no cubra necesidades complejas de consulta.

---

## ADR-007: Separación de configuración de seguridad

### Estado
Propuesto

### Contexto
La configuración de seguridad está mezclada con otras configuraciones del sistema.

### Decisión
Crear un **paquete de configuración de seguridad** separado para manejar todas las configuraciones relacionadas con JWT, roles y filtros de seguridad.

### Consecuencias
**Positivas**:
- Mejora la organización y claridad del código.
- Facilita las futuras modificaciones de seguridad.

**Negativas**:
- Introducción de más clases de configuración.

---

## ADR-008: Uso de Docker para servicios de infraestructura

### Estado
Propuesto

### Contexto
El proyecto debe ser fácilmente reproducible en diferentes entornos (desarrollo, testing, producción).

### Decisión
Usar **Docker** para manejar servicios de infraestructura como **PostgreSQL**.

### Consecuencias
**Positivas**:
- Facilita la creación de entornos consistentes.
- Evita la necesidad de configurar manualmente PostgreSQL.

**Negativas**:
- Puede aumentar la complejidad de la infraestructura al integrar más servicios.

---

## ADR-009: Definición de una estructura modular por dominio

### Estado
Propuesto

### Contexto
El proyecto tiene una estructura plana en la que todas las clases están dentro del mismo paquete, lo que hace difícil la expansión y escalabilidad.

### Decisión
Implementar una estructura **modular por dominio**, donde cada módulo tiene sus propios controladores, servicios, repositorios y entidades.

### Consecuencias
**Positivas**:
- Mejor organización y modularización del código.
- Facilita el mantenimiento y la escalabilidad del sistema.

**Negativas**:
- Requiere más esfuerzo para refactorizar el código y mover las clases existentes.

---

## ADR-010: Implementación de validaciones en DTO con Bean Validation

### Estado
Propuesto

### Contexto
El sistema no valida adecuadamente los datos entrantes a través de la API, lo que puede llevar a errores inesperados.

### Decisión
Usar **Bean Validation** en los DTOs para asegurar que los datos recibidos en las solicitudes cumplan con los requisitos esperados.

### Consecuencias
**Positivas**:
- Mejora la confiabilidad de las entradas de datos.
- Facilita la gestión de errores de validación.

**Negativas**:
- Requiere configurar las anotaciones adecuadas en los DTOs.

---

### Conclusión

Las decisiones arquitectónicas descritas en este documento buscan mejorar la escalabilidad, mantenibilidad y seguridad del sistema **ERP Iglesias** mediante la adopción de buenas prácticas de desarrollo y la implementación de soluciones estándar.


# ADR - Backend Architecture Decisions

## Estado
Propuestos 10  
Implementados 6

## Contexto

El sistema **ERP Iglesias** es una aplicación web construida con **Spring Boot**. Durante su desarrollo, se han identificado varios problemas de arquitectura relacionados con la organización del código, la mantenibilidad, la escalabilidad y las buenas prácticas de desarrollo.

Este documento detalla las **decisiones arquitectónicas propuestas** para mejorar la estructura y facilitar el crecimiento futuro del sistema.

---

## **ADR-001: Adopción de arquitectura en capas**

### **Estado**
Implementado

### **Contexto**
El backend tenía la lógica de negocio y el acceso a la base de datos mezclados con la exposición de endpoints.

### **Decisión**
Adoptar una **arquitectura en capas**: **Controller -> Service -> Repository**.

### **Acción**
- Refactorizar los controladores para delegar la lógica de negocio a los servicios correspondientes.
- Asegurar que cada capa tenga una única responsabilidad.

### **Consecuencias**
**Positivas**:
- Mejor separación de responsabilidades.
- Código más mantenible y escalable.
- Lógica de negocio aislada para facilitar pruebas.

**Negativas**:
- Aumento en la cantidad de clases.

---

## **ADR-002: Uso de DTOs**

### **Estado**
Implementado

### **Contexto**
Las entidades del modelo de dominio se exponían directamente a través de la API, lo que genera un alto acoplamiento entre la capa de presentación y la persistencia.

### **Decisión**
Usar **DTOs** (Data Transfer Objects) para desacoplar el modelo de persistencia de la API.

### **Acción**
- Crear DTOs que representen los datos que se intercambian entre el frontend y el backend.
- No exponer las entidades JPA directamente en los controladores.

### **Consecuencias**
**Positivas**:
- Se mejora la seguridad y la flexibilidad del sistema.
- Facilita la transformación de datos entre capas.

**Negativas**:
- Introducción de más clases para representar datos.

---

## **ADR-003: Manejo global de excepciones**

### **Estado**
Propuesto

### **Contexto**
El manejo de errores está disperso entre los controladores.

### **Decisión**
Centralizar el manejo de excepciones utilizando `@ControllerAdvice` para manejar todas las excepciones a nivel global.

### **Acción**
- Implementar un controlador global de excepciones usando `@ControllerAdvice`.
- Definir excepciones personalizadas y estructurar las respuestas de error.

### **Consecuencias**
**Positivas**:
- Mayor consistencia en la respuesta a errores.
- Facilita la gestión de excepciones globales.

**Negativas**:
- Necesidad de configurar la excepción para cada tipo de error.

---

## **ADR-004: Desacoplamiento de lógica del controlador a la capa de servicio**

### **Estado**
Implementado

### **Contexto**
La lógica de negocio estaba directamente dentro de los controladores, lo que dificultaba el mantenimiento y las pruebas unitarias.

### **Decisión**
Mover la lógica de negocio de los controladores a la capa de servicio.

### **Acción**
- Crear servicios para manejar toda la lógica de negocio.
- Los controladores solo deben delegar la lógica de negocio a los servicios.
- Usar los DTOs para las interacciones entre el controlador y el servicio.

### **Consecuencias**
**Positivas**:
- Mejora la mantenibilidad y la escalabilidad.
- Facilita las pruebas unitarias.
- Desacopla las responsabilidades de negocio de la capa de presentación.

**Negativas**:
- Requiere una refactorización de los controladores.

---

## **ADR-005: Uso de JWT para autenticación**

### **Estado**
Cumplido

### **Contexto**
El sistema necesita un mecanismo de autenticación eficiente y escalable.

### **Decisión**
Usar **JWT (JSON Web Tokens)** para la autenticación de usuarios de manera **stateless**.

### **Acción**
- Implementar un sistema de **autenticación basado en JWT** en el backend.
- Configurar filtros de seguridad para verificar los tokens en cada solicitud.

### **Consecuencias**
**Positivas**:
- Mejora la escalabilidad al no depender de sesiones de servidor.
- Proporciona una solución estándar y segura.

**Negativas**:
- Requiere manejo adecuado de tokens y expiración.

---

## **ADR-006: Uso de Spring Data JPA**

### **Estado**
Cumplido

### **Contexto**
Se están utilizando consultas manuales en la capa de persistencia.

### **Decisión**
Usar **Spring Data JPA** para gestionar el acceso a datos, aprovechando sus repositorios y la implementación automática de consultas.

### **Acción**
- Usar **JpaRepository** o **CrudRepository** para manejar operaciones CRUD.
- Evitar consultas SQL manuales, aprovechando la implementación automática de Spring Data JPA.

### **Consecuencias**
**Positivas**:
- Simplifica la capa de persistencia.
- Mejora la mantenibilidad y reduce el código repetido.

**Negativas**:
- Puede haber casos en los que Spring Data JPA no cubra necesidades complejas de consulta.

---

## **ADR-007: Separación de configuración de seguridad**

### **Estado**
Cumplido

### **Contexto**
La configuración de seguridad estaba mezclada con otras configuraciones del sistema.

### **Decisión**
Crear un **paquete de configuración de seguridad** separado para manejar todas las configuraciones relacionadas con JWT, roles y filtros de seguridad.

### **Acción**
- Implementar una clase **`SecurityConfig`** que centralice todas las configuraciones de seguridad del sistema.
- Crear filtros para la validación de JWT y roles de usuario.

### **Consecuencias**
**Positivas**:
- Mejora la organización y claridad del código.
- Facilita las futuras modificaciones de seguridad.

**Negativas**:
- Introducción de más clases de configuración.

---

## **ADR-010: Implementación de validaciones en DTO con Bean Validation**

### **Estado**
Cumplido

### **Contexto**
El sistema no valida adecuadamente los datos entrantes a través de la API, lo que puede llevar a errores inesperados.

### **Decisión**
Usar **Bean Validation** en los DTOs para asegurar que los datos recibidos en las solicitudes cumplan con los requisitos esperados.

### **Acción**
- Usar anotaciones como `@NotNull`, `@Size`, `@Email`, etc., en los DTOs para validar los datos entrantes.

### **Consecuencias**
**Positivas**:
- Mejora la confiabilidad de las entradas de datos.
- Facilita la gestión de errores de validación.

**Negativas**:
- Requiere configurar las anotaciones adecuadas en los DTOs.

---

### **Conclusión**

- **ADR-001**: Implementada la **arquitectura en capas**.
- **ADR-002**: **DTOs** implementados para desacoplar la persistencia de la capa de presentación.
- **ADR-003**: **Manejo global de excepciones** propuesto, aún por implementar con `@ControllerAdvice`.
- **ADR-004**: **Desacoplamiento de la lógica de negocio** a la capa de servicio implementado.
- **ADR-005**: **Autenticación JWT** implementada.
- **ADR-006**: **Spring Data JPA** implementado para el acceso a datos.
- **ADR-007**: **Configuración de seguridad separada** implementada con `SecurityConfig`.
- **ADR-010**: **Validaciones en DTO con Bean Validation** implementadas.
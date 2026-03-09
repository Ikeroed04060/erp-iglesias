# ADR - Backend Architecture Decisions

## Estado
Propuestos 2 
Implementados 7
Parcialmente implementados 1

## Contexto

El sistema **ERP Iglesias** es una aplicación web construida con **Spring Boot**. Durante su desarrollo, se han identificado varias decisiones arquitectónicas ya adoptadas, así como otras aún pendientes, relacionadas con la organización del código, la mantenibilidad, la escalabilidad, la seguridad y la robustez operativa del backend.

Este documento detalla las **decisiones arquitectónicas implementadas, parcialmente implementadas y propuestas** para mejorar la estructura y facilitar el crecimiento futuro del sistema.

---

## **ADR-001: Adopción de arquitectura en capas**

### **Estado**
Implementado

### **Contexto**
El backend requería una mejor organización para separar la exposición de endpoints, la lógica de negocio y el acceso a la base de datos.

### **Decisión**
Adoptar una **arquitectura en capas**: **Controller -> Service -> Repository**.

### **Acción**
- Organizar el backend en paquetes diferenciados para controladores, servicios y repositorios.
- Asegurar que cada capa tenga una responsabilidad principal.
- Mantener a los controladores como punto de entrada HTTP y a los repositorios como capa de persistencia.

### **Consecuencias**
**Positivas**:
- Mejor separación de responsabilidades.
- Código más mantenible y escalable.
- Facilita la evolución del backend.

**Negativas**:
- Incrementa la cantidad de clases y archivos.
- Requiere disciplina para no mezclar responsabilidades entre capas.

---

## **ADR-002: Uso de DTOs para entrada y salida de datos**

### **Estado**
Implementado

### **Contexto**
Exponer entidades JPA directamente en la API genera alto acoplamiento entre persistencia y presentación, además de riesgos de seguridad y rigidez ante cambios del modelo.

### **Decisión**
Usar **DTOs** (Data Transfer Objects) para desacoplar el modelo de persistencia de la API REST.

### **Acción**
- Crear DTOs de entrada y salida en paquetes separados.
- Evitar exponer las entidades JPA directamente desde los controladores.
- Realizar el mapeo entre entidades y DTOs desde la capa de servicio.

### **Consecuencias**
**Positivas**:
- Menor acoplamiento entre API y base de datos.
- Más control sobre la información expuesta.
- Facilita validaciones y evolución del contrato HTTP.

**Negativas**:
- Introduce más clases y mapeos.
- Aumenta ligeramente el trabajo de mantenimiento.

---

## **ADR-003: Manejo global de excepciones**

### **Estado**
Propuesto

### **Contexto**
El backend aún no cuenta con un mecanismo centralizado para gestionar errores de validación, reglas de negocio o fallos inesperados, lo que puede producir respuestas inconsistentes.

### **Decisión**
Centralizar el manejo de excepciones utilizando `@ControllerAdvice` o `@RestControllerAdvice`.

### **Acción**
- Implementar una clase global de manejo de excepciones.
- Definir respuestas de error uniformes para validaciones, recursos no encontrados y errores de negocio.
- Crear excepciones personalizadas para escenarios relevantes del dominio.

### **Consecuencias**
**Positivas**:
- Respuestas de error consistentes.
- Menor duplicación de manejo de errores.
- Mejor experiencia para frontend y consumidores de la API.

**Negativas**:
- Requiere definir una estrategia común de errores.
- Agrega trabajo inicial de estandarización.

---

## **ADR-004: Desacoplamiento de lógica del controlador hacia la capa de servicio**

### **Estado**
Implementado

### **Contexto**
La lógica de negocio no debe quedar dentro de los controladores, ya que eso dificulta el mantenimiento, la reutilización y las pruebas.

### **Decisión**
Mover la lógica de negocio a la capa de servicios, dejando los controladores como adaptadores HTTP delgados.

### **Acción**
- Delegar desde los controladores la ejecución de casos de uso hacia servicios dedicados.
- Evitar lógica de negocio compleja en métodos anotados con `@GetMapping`, `@PostMapping`, etc.
- Mantener a los controladores centrados en recibir solicitudes y devolver respuestas.

### **Consecuencias**
**Positivas**:
- Mejora la mantenibilidad.
- Facilita pruebas unitarias sobre la lógica del negocio.
- Reduce acoplamiento entre la API y la lógica interna.

**Negativas**:
- Requiere más clases y contratos internos.
- Puede inducir duplicación si no se define bien la responsabilidad de cada servicio.

---

## **ADR-005: Uso de JWT para autenticación stateless**

### **Estado**
Implementado

### **Contexto**
El sistema requiere autenticación segura y sin dependencia de sesiones almacenadas en servidor.

### **Decisión**
Usar **JWT (JSON Web Tokens)** como mecanismo de autenticación **stateless**.

### **Acción**
- Generar tokens JWT al autenticar usuarios.
- Validar el token en cada solicitud protegida mediante filtros de seguridad.
- Poblar el contexto de seguridad con la identidad del usuario autenticado.

### **Consecuencias**
**Positivas**:
- Escalabilidad al no depender de sesiones de servidor.
- Integración natural con APIs REST.
- Mecanismo estándar y ampliamente soportado.

**Negativas**:
- Requiere manejo correcto de expiración y firma.
- Obliga a proteger cuidadosamente secretos y configuración de seguridad.

---

## **ADR-006: Uso de Spring Data JPA para persistencia**

### **Estado**
Implementado

### **Contexto**
El backend necesita una capa de acceso a datos mantenible, consistente y alineada con el modelo relacional del sistema.

### **Decisión**
Usar **Spring Data JPA** como mecanismo principal para el acceso a datos.

### **Acción**
- Definir repositorios basados en `JpaRepository`.
- Aprovechar consultas derivadas y capacidades ORM de JPA/Hibernate.
- Evitar, en lo posible, consultas SQL manuales para operaciones CRUD básicas.

### **Consecuencias**
**Positivas**:
- Reduce código repetitivo en la capa de persistencia.
- Acelera el desarrollo de operaciones CRUD.
- Mantiene coherencia con el ecosistema Spring Boot.

**Negativas**:
- Puede ocultar detalles de rendimiento si no se controla bien el acceso a datos.
- Algunas consultas complejas pueden requerir personalización adicional.

---

## **ADR-007: Separación de la configuración de seguridad**

### **Estado**
Implementado

### **Contexto**
La seguridad del backend necesita una configuración clara, aislada y fácil de mantener.

### **Decisión**
Centralizar la configuración relacionada con autenticación, autorización y filtros en un paquete específico de configuración de seguridad.

### **Acción**
- Mantener una clase `SecurityConfig` dedicada a las reglas de seguridad.
- Separar servicios y filtros JWT en clases específicas.
- Habilitar seguridad a nivel de método cuando sea necesario.

### **Consecuencias**
**Positivas**:
- Mejora la organización del backend.
- Facilita cambios futuros en la estrategia de seguridad.
- Hace más legible la configuración de autenticación y autorización.

**Negativas**:
- Añade complejidad inicial.
- Exige comprender bien el ciclo de filtros de Spring Security.

---

## **ADR-008: Implementación de validaciones en DTO con Bean Validation**

### **Estado**
Parcialmente implementado

### **Contexto**
El backend define validaciones en los DTOs mediante anotaciones de Bean Validation, pero estas validaciones no se activan completamente en todos los endpoints al no utilizarse de forma consistente `@Valid` en los controladores.

### **Decisión**
Usar **Bean Validation** en los DTOs y aplicar la validación automáticamente en los endpoints de entrada.

### **Acción**
- Mantener anotaciones como `@NotNull`, `@NotBlank`, `@Email`, `@Size`, etc., en los DTOs.
- Incorporar `@Valid` en los parámetros `@RequestBody` de los controladores.
- Integrar esta validación con un manejo global de excepciones.

### **Consecuencias**
**Positivas**:
- Mejora la confiabilidad de los datos entrantes.
- Reduce errores por entradas inválidas.
- Estandariza las validaciones de la API.

**Negativas**:
- Requiere completar la activación en todos los endpoints.
- Necesita complementarse con manejo global de errores para explotar su beneficio completo.

---

## **ADR-009: Transaccionalidad en operaciones críticas del dominio**

### **Estado**
Implementado

### **Contexto**
Existen operaciones del dominio que involucran múltiples pasos de persistencia, por ejemplo la creación de inscripciones u ofrendas junto con sus pagos, y la confirmación de pagos con actualización del estado del negocio. Actualmente no se observa una estrategia transaccional explícita.

### **Decisión**
Aplicar control transaccional a los casos de uso críticos mediante `@Transactional`.

### **Acción**
- Identificar servicios que realizan operaciones compuestas sobre varias entidades.
- Marcar esos casos de uso con `@Transactional`.
- Garantizar rollback cuando una parte del proceso falle.

### **Consecuencias**
**Positivas**:
- Mayor consistencia de datos.
- Previene estados intermedios inválidos.
- Aumenta la robustez del backend ante fallos.

**Negativas**:
- Requiere revisar cuidadosamente límites transaccionales.
- Puede introducir bloqueos o costos adicionales si se usa sin criterio.

---

## **ADR-010: Externalización de configuración sensible y por ambiente**

### **Estado**
Propuesto

### **Contexto**
Parte de la configuración del backend, especialmente la relacionada con seguridad y despliegue, debe poder variar entre desarrollo, pruebas y producción sin modificar el código fuente.

### **Decisión**
Externalizar la configuración sensible y dependiente del ambiente mediante variables de entorno y perfiles de configuración.

### **Acción**
- Mover secretos, credenciales y parámetros sensibles fuera del código y propiedades fijas.
- Usar perfiles como `dev`, `test` y `prod`.
- Centralizar la lectura de configuración en un esquema consistente para despliegue local y productivo.

### **Consecuencias**
**Positivas**:
- Mejora la seguridad.
- Facilita despliegues en múltiples ambientes.
- Reduce el riesgo de exponer secretos en repositorio.

**Negativas**:
- Aumenta la complejidad de configuración inicial.
- Requiere disciplina en la gestión de entornos y secretos.

---

### **Conclusión**

- **ADR-001**: Se adopta una **arquitectura en capas**.
- **ADR-002**: Se implementa el uso de **DTOs** para desacoplar la API de la persistencia.
- **ADR-003**: Se propone un **manejo global de excepciones**.
- **ADR-004**: Se implementa el **desacoplamiento de la lógica de negocio** hacia la capa de servicio.
- **ADR-005**: Se implementa **JWT** como mecanismo de autenticación stateless.
- **ADR-006**: Se implementa **Spring Data JPA** para la persistencia.
- **ADR-007**: Se centraliza la **configuración de seguridad**.
- **ADR-008**: Las **validaciones con Bean Validation** están **parcialmente implementadas**.
- **ADR-009**: Se propone incorporar **transaccionalidad** en operaciones críticas.
- **ADR-010**: Se propone la **externalización de configuración sensible y por ambiente**.
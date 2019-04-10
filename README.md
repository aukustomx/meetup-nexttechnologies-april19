Fast RESTful WS development with JEE8
======

## Creación proyecto
1. Generar proyecto base con Thorntail (antes Wildfly Swarm. Red Hat, Inc. https://thorntail.io/), utilizando el *project generator*
    * Capturar Group ID: com.next
    * Artifact ID: simple-rest
    * Seleccionar la dependencia JAX-RS
    * Clic en el botón *Generate Project*
2. El paso anterior produce la descarga del archivo `simple-rest.zip` 
3. Extraer el proyecto en la ruta que se prefiera.
4. Dentro del directorio del proyecto, ejecutar:
```
mvn thorntail:run
```
5. Abrir la dirección http://localhost:8080/hello en el navegador para ver el mensaje:
```
Hello from Thorntail!
```
## Despliegue
El proyecto es capaz de generar un FatJar (con un web server embebido) o un ThinWAR para desplegarse en un servidor de aplicaciones existente.

* Despliege como aplicación Java directamente como Jar
* Despliegue en servidor de aplicaciones existente

## Análisis de código
El proyecto generado contiene lo siguiente:
```
$ tree
.
├── pom.xml
└── src
    └── main
        └── java
            └── com
                └── aukustomx
                    └── simpleapp
                        └── rest
                            ├── HelloWorldEndpoint.java
                            └── RestApplication.java

7 directories, 3 files
```
Aparte de la estructura de carpetas requerida por maven y aquellas correspondientes al paquete que definimos al generar el proyecto, tenemos solo 3 archivos. El `pom.xml` (en este archivo se define el proyecto), una clase de aplicación `RestApplication.java` y una clase con el endpoint REST `HelloWorldEndpoint.java`. Revisemos el código de este proyecto.

## Usando POJOs como respuesta
1. Crear paquete `com.aukusto.simpleapp.user`
1. Crear la clase `UserEndpoint.java` dentro del paquete `com.aukusto.simpleapp.user.rest`
1. Agregar el archivo `User.java` al paquete `com.aukusto.simpleapp.user.model`
1. Dentro de la clase `UserEndpoint.java` creamos un método helper temporal para generar una lista como respuesta. Será una lista de Users.
1. Generar un objeto `javax.ws.rs.core.Response` con la lista como entidad de respuesta.
1. Ir a la dirección http://localhost:8080/users/ para obtener una salida similar a la siguiente:
```
[{"id":1,"name":"Juan","email":"Juan@mail.com"},{"id":2,"name":"Pedro","email":"Pedro@mail.com"},{"id":3,"name":"María","email":"María@mail.com"},{"id":4,"name":"Jose","email":"Jose@mail.com"},{"id":5,"name":"Ana","email":"Ana@mail.com"}]
```

## Usando servicios. Inyección de dependencia con CDI
1. Agregar las siguiente 2 dependencias al `pom.xml`: 
```xml
    <dependency>
      <groupId>io.thorntail</groupId>
      <artifactId>ejb</artifactId>
    </dependency>
    <dependency>
      <groupId>io.thorntail</groupId>
      <artifactId>cdi</artifactId>
    </dependency>
```
2. Agregar el archivo `UserService.java` al paquete `com.aukusto.simpleapp.user.service`
   * Anotaciones `@Stateless`, `@Stateful`, `@Singleton` e `@Inject`.
3. Nuevamente vamos a la dirección http://localhost:8080/users/ para ver la lista de usuarios, ahora obtenida desde el servicio (formalmente un ejb) inyectado en el Endpoint.

## Extrayendo datos del request
Para obtener la información de solo un User, implementamos un endpoint que responda a la dirección:
```
http://localhost:8080/users/1
```
por ejemplo. Esta URI solicita la información del usuario 1.
1. En la clase `UserEndpoint.java` implementamos el siguiente método:
```java
    @GET
    @Path("/{id}")
    public Response byId(@PathParam("id") int id) {
       ...
    }
```
Anotaciones `@Path` y `@PathParam`.
2. Cambiamos el servicio de un ejb @Stateless a un @Singleton para manipular la lista durante la ejecución de la aplicación al agregar, consultar, eliminar o actualizar algún User.
3. En la clase de servicio `UserService.java` se implementa un método que reciba el id del User y devuelva el detalle del mismo, si existe. Más adelante se verá cómo manejar el caso en el que no exista.
```java
    public User byId(int id) {
        ...
    }
```
4. Ejecutamos la aplicación y visitamos http://localhost:8080/users/1 para ver el detalle del User con id 1

## Registrando un nuevo User
Para efectos de demostrar el uso del método POST (a través de la anotación @POST):
1. Implementar el método `add` a la clase `UserEndpoint.java`.
2. Anotar el método mencionado anteriormente con `@POST
3. Crear la clase `UserRequest.java`, que será la encargada de "mapear" el body del request a un objeto Java
4. Implementar el método `add`, ahora en la clase de servicio `UserService.java`.
5. Desplegar y probar agregando un nuevo User y seguidamente consultando la lista para verificar que esté dado de alta.
6.- ¡Qué pasa si queremos agregar un usuario que ya existe o ver el detalle de uno que no existe?

## Manejo Global de Excepciones
Cuando un error ocurre dentro de nuestros servicios, ya sea un error de negocio o uno de runtime, no queremos que el StackTrace sea devuelto a nuestros cliente (no es apto para cardiacos). En cambio, sería mejor manejar las excepciones y contestar con un código HTTP más adecuado y con un mensaje claro que le indique lo que ha ocurrido, además de alguna sugerencia para remediar lo ocurrido.

1. Cambiar el tipo de respuesta de los métodos de la clase UserService.java a javax.ws.rs.core.Response
2. Solicitar el alta de un User con un email que ya existe.
3. Manejar la excepción y generar un objeto Response con un código HTTP 400 - Bad Request y un mensaje que indique que el usuario ya existe.
4. Cambiar la implementación del `UserEndpoint.java`, en el método `add` para que regrese lo que el servicio ha regresado tal cual.
5. Probar nuevamente el punto 2.

Este manejo de excepciones es aparentemente correcto. Pero qué pasa en el caso en el que tengamos muchos escenarios de fallas. No deseamos que nuestros servicios estén llenos de código que atrapa excepciones y construye Responses de acuerdo al problema (esa no es su tarea). Para este problema, JAX-RS ofrece una inteface de manejo de excepciones, que nos permite atrapar (a través de un interceptor) una excepción antes de regresar al cliente, manejarla en un punto central y convertirla en un Response para ser devuelta al solicitante. Estamos hablando de la interface 

1. Definir una clase de acarreo de respuesta llamada ResponseVO.java como sigue:
2. Se recomienda que las APIs tengan una serie de códigos de retorno que el usuario pueda conocer para saber qué ha ocurrido con sus peticiones, así que generamos una clase ResponseCode.java y agregamos algunos códigos de retorno
```java
...
public enum ResponseCode {
    SUCCESSFUL_OPERATION(OK, "1", "Operación exitosa"),
    FAILED_OPERATION(INTERNAL_SERVER_ERROR, "2", "Operación fallida"),
    USER_DOES_NOT_EXISTS(BAD_REQUEST, "3", "El usuario no existe"),
    USER_ALREADY_EXISTS(BAD_REQUEST, "4", "El usuario ya existe");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

3. Definir una clase que implemente a RuntimeException, llamada UserException.java, como sigue:
```java
...
@ApplicationException(rollback = true)
public class UserException extends RuntimeException {
    
    private ResponseCode responseCode;
    
    public UserException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public UserException(String message, ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public UserException(String message, Throwable cause, ResponseCode responseCode) {
        super(message, cause);
        this.responseCode = responseCode;
    }
}
```
La anotación `@ApplicationException` define a una excepción como de aplicación. Se agrega el atributo `rollback = true` para indicar que si se presenta esta excepción en una operación `@Transactional` de un EJB, la transacción debe rechazarse.

4. Implementar el ExceptionMapper que manejará las excepciones. Crearemos uno genérico:
```java
...
@Provider
public class UserExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e instanceof UserException) {
            return responseOf((UserException) e);
        }
        
        return responseOf(e);
    }
    ...
}
```

5. El siguiente es el código del VO para transportar las respuestas de los servicios a los endpoints.
```java
...
public class ResponseVO<T> {
    private final ResponseCode responseCode;
    private final T result;

    //constructors, getters y setters
```
2. Cambiar la implementación del servicio de alta para devolver un objeto de la clase `ResponseVO.java`.
3. En el caso de que el User ya exista, generar una excepción

Existe otra forma de manejo de excepciones y es generar una `WebApplicationException`. Esta queda de tarea para el lector.

## Bean Validation y manejo de excepciones de validación
1. Anotar los atributos del la clase UserRequest.java con las anotaciones de BeanValidation que apliquen, por ejemplo: @NotNull, @NotBlank, @Min y sus respectivos mensajes
2. Probar la validación enviando solicitudes incorrectas provocando que fallen las validaciones.
3. Agregar el manejo de las excepciones `javax.validation.ConstraintViolationException` en el ExceptionMapper
```java
...
private static Response responseOf(ConstraintViolationException e) {
  List<String> errorMessages = e.getConstraintViolations().stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.toList());

  return Response
          .status(INVALID_PARAMS.getStatus())
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .entity(INVALID_PARAMS.asMap(errorMessages))
          .build();
}
...
```

## Persistencia en Base de Datos
1. Para probar la persistencia en base de datos con JPA, agregamos las siguientes dependencias al `pom.xml`:
```xml
<dependency>
      <groupId>io.thorntail</groupId>
      <artifactId>jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <version>1.4.187</version>
    </dependency>
```
2. Creamos la siguiente estructura de directorios:
```bash
src/main/resources/META-INF
```
3. En el recién creado directorio META-INF creamos los archivos:
persistence.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        version="2.1"
        xmlns="http://xmlns.jcp.org/xml/ns/persistence"
        xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="UserPU" transaction-type="JTA">
        <properties>
            <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
            <property name="javax.persistence.schema-generation.create-source" value="metadata"/>
            <property name="javax.persistence.schema-generation.drop-source" value="metadata"/>
            <property name="javax.persistence.sql-load-script-source" value="META-INF/load.sql"/>
        </properties>
    </persistence-unit>
</persistence>
```
load.sql
```sql
INSERT INTO USER("ID", "NAME", "EMAIL") VALUES (1, 'Penny', 'Penny@mail.com');
INSERT INTO USER("ID", "NAME", "EMAIL") VALUES (2, 'Sheldon', 'Sheldon@mail.com');
INSERT INTO USER("ID", "NAME", "EMAIL") VALUES (3, 'Amy', 'Amy@mail.com');
INSERT INTO USER("ID", "NAME", "EMAIL") VALUES (4, 'Leonard', 'Leonard@mail.com');
INSERT INTO USER("ID", "NAME", "EMAIL") VALUES (5, 'Bernadette', 'Bernadette@mail.com');
```
4. Transformamos la clase User en una `@Entity` que mapea a la tabla `"USER"` en la base de datos en `h2`.
```java
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private String email;
    
    ...constructors, getters y setters
```
5. Generamos la clase `com.aukustomx.simpleapp.user.persistence.UserRepository.java` que servirá de DAO a la clase de servicio UserService.java. Esta clase utiliza en EntityManager que automaticamente se genera con el PersistenceUnit `UserPU` definido en el `persistence.xml`.
```java
...
@Stateless
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    public User byId(int id) {
        return em.find(User.class, id);
    }
    ...
}
```
6. Cambiamos la implementación del UserService para
   * Inyectar el UserRepository
   * Buscar el User byId desde la base de datos y no de la lista statica de la clase.
7. Ir a la dirección http://localhost:8080/users/1 y ver el resultado. 
   
## Bonus: Multipart request - Uploading a file
1. Agregar fraction al pom.xml
```xml
    <dependency>
      <groupId>io.thorntail</groupId>
      <artifactId>jaxrs-multipart</artifactId>
    </dependency>
```

2. Crear endpoint para alta de archivo (photo)
```java
    @PUT
    @Path("/{id}/profile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPhoto(@PathParam("id") int id, MultipartFormDataInput file) {
        return responseOf(userService.uploadPhoto(id, file));
    }
```

3. Implementar servicio que procesa el archivo recibido y lo guarda en el FS (esta operación es solo para fines demostrativos)
```java
    public ResponseVO uploadPhoto(int id, MultipartFormDataInput inputFile) {

        //Validate user exists
        User user = userRepository.byId(id);
        if (null == user) {
            throw new UserException(USER_DOES_NOT_EXISTS);
        }

        // Extracting and saving file
        Map<String, List<InputPart>> uploadForm = inputFile.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("attachment");
        return saveFile(inputParts);
    }
```
4. Probar subida de archivo:
```bash
curl -X PUT -F attachment=@"/home/aukustomx/photo.png" http://localhost:8080/users/1/profile
```

## Conclusiones
La implementación de servicios RESTful con JEE8, específicamente con JAX-RS es bastante rápida y sencilla, y lo mejor es que se utilizan estándares del mismo Jakarta EE.

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

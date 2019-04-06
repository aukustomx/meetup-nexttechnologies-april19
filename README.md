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


# VDEK Mock API

This is a small project to mock the Vdek API.

It's build using SpringBoot 2.x and uses an embedded mongodb to store the data. It's build using maven.

## Building and running the application

Since it's just a default SpringBoot application you can build it by using the following command (in a terminal)

> `$ mvn clean package`

The compiled executable jar can be found in `./target/vdemock.jar` And to run on the commandline:

> `$ mvn spring-boot:run` or `$ java -jar target/vdekmock.jar`

After the application is started up, it exposes a simple REST api.
The Swagger UI page can be found at http://localhost:8080/swagger-ui.html and the API definition can be found at http://localhost:8080/v2/api-docs

## Building the mock as a Docker image

 -- TODO --



# Exercise prerequisites:

Locally installed: java 8, maven 3.5, docker (one of the latest ones)

# Exercise technology stack:
1. Java 8
1. Maven 3
1. String Boot
1. String Cloud
1. Docker

# Exercise steps:
1. Update /etc/hosts file at your local machine:
    * `127.0.0.1	localhost discovery-service gateway-service custom-service`
1. Create new java8/maven project
    * the parent of main pom.xml should be ``spring-boot-starter-parent`` (2.0.x version or above)
    * main pom.xml should have ``dependencyManagement`` element pointing to the one of latest stable spring-cloud-dependencies artifact (for instance : Finchley.SR2)    
1. Create discovery-service maven module inside of main project
    * Add main application class with @EnableEurekaServer annotation
    * Add application.yaml (setting server.port to 8888)
    * Add to discovery-service/pom.xml required spring-cloud dependencies (for instance: spring-cloud-starter-netflix-eureka-server) 
1. Run discovery-service
    * Start new service using: mvn spring-boot:run command
    * Verify if it's running properly in browser (http://localhost:8888)            
1. Add gateway-service maven module
    * Add main application class 
    * Add application.yaml with following parameters:
        * `server.port=8080`
        * `spring.application.name=gateway-service`
        * `eureka.instance.hostname=discovery-service`
        * `eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:8888/eureka/`
    * Add to gateway-service/pom.xml required dependencies:
        * spring-cloud-starter-gateway
        * spring-boot-starter-actuator
        * spring-cloud-starter-netflix-eureka-client
        * spring-boot-starter-webflux
1. Run gateway-service
    * Start new service using: mvn spring-boot:run command
    * Verify in logs if it's started properly on port 8080
    * Verify if it was registerd in discovery-service (see list of registered apps at lhttp://localhost:8888 page)
            
1. Add custom-service maven module
    * Add main application class 
    * Add rest controller with single GET /hello method and inject parameter called `name`, for example:
    ```
       @RestController
       public class HelloWorldController {
           @Value("${name:Default}")
           private String name;
       
           @GetMapping("/hello")
           public String hello() {
               String message = "Hello World!, " + name + " - " + new Date();
               System.out.println(message);     
               return message;
           }
       }
    ```
    * Add bootstrap.yaml with parameters:
        * `spring.application.name=custom-service`
    * Add application.yaml with parameters:
        * `server.port=8081`
        * `spring.application.name=custom-service`
        * `eureka.instance.hostname=discovery-service`
        * `eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:8888/eureka/`
    * Add to custom-service/pom.xml required dependencies:
        * spring-boot-starter-actuator
        * spring-cloud-starter-netflix-eureka-client
        * spring-boot-starter-webflux

1. Run custom-service
    * Start new service using: mvn spring-boot:run command
    * Verify if it's started properly on port 8081 (http://localhost:8081/hello) - you should see `Hello World, Default`
    * Verify if it was registered in discovery-service (see list of registered apps at http://localhost:8888 page)

1. Update configuration of gateway-service to route requests to custom-service
    * Update application.yml with parameters:
    ```
    spring:
      cloud:
        gateway:
          discovery:
            locator:
              enabled: true
          routes:
            - id: custom-service
              uri: lb://custom-service
              predicates:
                - Path=/custom-service/**
              filters:
                - RewritePath=/custom-service/(?<path>.*), /$\{path}
    ```    
1. Rebuild and restart gateway-service
    * verify if /hello endpoint of custom-service can be accessible via gateway service: http://localhost:8080/custom-service/hello

1. Run second instance of custom-service (this time on port 8082) and verify if routing via gateway works for both instances (on port 8081 and 8082)
    
1. Add config-service maven module
    * Add main application class with @EnableConfigServer annotation
    * Add application.yaml with parameters:    
        * `server.port=9999`
        * `spring.application.name=config-server`
        * `spring.cloud.config.server.git.uri=https://github.com/bepoland-academy/config`
    * Add to config-server/pom.xml required dependencies:
        * spring-cloud-config-server
        * spring-boot-starter-actuator

1. Run config-service
    * Start new service using: mvn spring-boot:run command
    * Verify if it's started properly on port 9999                        

1. Update configuration of custom-service to integrate with config-service
    * Add spring-cloud-starter-config to pom.xml
    * Add parameter to boostrap.yaml:
        * `spring.cloud.config.url=http://config-service:9999`

1. Rebuild and restart custom-service
    * verify if value of parameter `name` was changed from `Default` to `All` by calling http://localhost:8081/hello        
    
1. Stop all services

1. Add Dockerfile to discovery-service:
    ```
    FROM java:8
    VOLUME /tmp
    ADD target/discovery-service-1.0-SNAPSHOT.jar discovery-service.jar
    RUN sh -c 'touch /discovery-service.jar'
    ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/discovery-service.jar"]
    ```

1. Add to gateway-service/Dockerfile:

    ```
    FROM java:8
    VOLUME /tmp
    ADD target/gateway-service-1.0-SNAPSHOT.jar gateway-service.jar
    RUN sh -c 'touch /gateway-service.jar'
    ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/gateway-service.jar"]
    ```

1. Add config-service/Dockerfile:

    ```
    FROM java:8
    VOLUME /tmp
    ADD target/config-service-1.0-SNAPSHOT.jar config-service.jar
    RUN sh -c 'touch /discovery-service.jar'
    ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/config-service.jar"]
    ```

1. Add custom-service/Dockerfile:

    ```
    FROM java:8
    VOLUME /tmp
    ADD target/custom-service-1.0-SNAPSHOT.jar custom-service.jar
    RUN sh -c 'touch /custom-service.jar'
    ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/custom-service.jar"]
    ```
            
1. Add docker-compose.yaml to the main project:

    ```
    version: "3.1"

    services:
      discovery-service:
        build:
          context: ./discovery-service
          dockerfile: Dockerfile
        container_name: discovery-service
        ports:
          - "8888:8888"
    
      config-service:
        build:
          context: ./config-service
          dockerfile: Dockerfile
        container_name: config-service
        ports:
          - "9999:9999"
    
      gateway-service:
        build:
          context: ./gateway-service
          dockerfile: Dockerfile
        container_name: gateway-service
        ports:
          - "8080:8080"
    
      custom-service:
        build:
          context: ./custom-service
          dockerfile: Dockerfile
        container_name: custom-service
        ports:
          - "8081:8081"
    ```       

1.  Build and run all docker containers with docker-compose:
    ```
    mvn clean install && docker-compose build && docker-compose up -d
       
    ```
    
1. Verify if all services are working in the same way as without docker    
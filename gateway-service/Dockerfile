FROM java:8
VOLUME /tmp
ADD target/gateway-service-1.0-SNAPSHOT.jar gateway-service.jar
RUN sh -c 'touch /gateway-service.jar'
ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/gateway-service.jar"]
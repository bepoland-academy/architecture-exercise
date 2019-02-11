FROM java:8
VOLUME /tmp
ADD target/custom-service-1.0-SNAPSHOT.jar custom-service.jar
RUN sh -c 'touch /custom-service.jar'
ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/custom-service.jar"]
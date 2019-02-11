FROM java:8
VOLUME /tmp
ADD target/config-service-1.0-SNAPSHOT.jar config-service.jar
RUN sh -c 'touch /discovery-service.jar'
ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/config-service.jar"]
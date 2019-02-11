FROM java:8
VOLUME /tmp
ADD target/discovery-service-1.0-SNAPSHOT.jar discovery-service.jar
RUN sh -c 'touch /discovery-service.jar'
ENTRYPOINT ["java","-Xmx64m","-Djava.security.egd=file:/dev/./urandom","-jar","/discovery-service.jar"]
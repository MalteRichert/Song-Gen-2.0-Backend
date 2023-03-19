FROM eclipse-temurin:19-jre
RUN mkdir /opt/app
WORKDIR /opt/app
RUN mkdir data && chmod g+w data -R
CMD ["java", "-jar", "song-gen-2-backend-0.0.1-SNAPSHOT.jar"]
COPY target/song-gen-2-backend-0.0.1-SNAPSHOT.jar .
COPY src/main/resources ./src/main/resources
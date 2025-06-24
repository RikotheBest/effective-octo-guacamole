 FROM openjdk:21
 WORKDIR /app/backend
 ARG DEPENDENCY=target/dependency
 COPY ${DEPENDENCY}/BOOT-INF/lib ./lib
 COPY ${DEPENDENCY}/META-INF ./META-INF
 COPY ${DEPENDENCY}/BOOT-INF/classes .
 ENTRYPOINT ["java","-cp",".:./lib/*","com.example.springboot_webapp.SpringBootWebAppApplication"]
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /ms_clients

# Copy maven scripts
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# install the application dependencies without generate jar
RUN ./mvnw dependency:go-offline

# Copy in the source code
COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
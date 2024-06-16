# syntax=docker/dockerfile:1

ARG JAVA_VERSION=17

## Stage 1: Create a stage for building the application.
FROM eclipse-temurin:${JAVA_VERSION}-jdk-jammy AS base
WORKDIR /ms_clients
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

## Stage 2: Create a stage for running tests.
FROM base AS test
WORKDIR /ms_clients
COPY ./src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw test

## Stage 3: Create a stage for downloading dependencies.
FROM base AS deps
WORKDIR /ms_clients
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -DskipTests

## Stage 4: Create a stage for building the application based on the stage with downloaded dependencies.
FROM deps AS package
WORKDIR /ms_clients
COPY ./src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

## Stage 5: Create a stage for extracting the application into separate layers.
FROM package AS extract
WORKDIR /ms_clients
RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

## Stage 6: Create a stage for development
FROM extract AS development
WORKDIR /ms_clients
RUN cp -r /ms_clients/target/extracted/dependencies/. ./
RUN cp -r /ms_clients/target/extracted/spring-boot-loader/. ./
RUN cp -r /ms_clients/target/extracted/snapshot-dependencies/. ./
RUN cp -r /ms_clients/target/extracted/application/. ./

# CMD ["./mvnw", "spring-boot:run"]
CMD [ "java", "-Dspring.profiles.active=dev", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'", "org.springframework.boot.loader.launch.JarLauncher" ]

## Stage 7: Create a stage for production
FROM eclipse-temurin:${JAVA_VERSION}-jre-jammy AS final
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser
COPY --from=extract ms_clients/target/extracted/dependencies/ ./
COPY --from=extract ms_clients/target/extracted/spring-boot-loader/ ./
COPY --from=extract ms_clients/target/extracted/snapshot-dependencies/ ./
COPY --from=extract ms_clients/target/extracted/application/ ./
EXPOSE 8080
ENTRYPOINT [ "java", "-Dspring.profiles.active=dev", "org.springframework.boot.loader.launch.JarLauncher" ]
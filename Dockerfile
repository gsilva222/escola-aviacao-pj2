# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY shared/pom.xml shared/
COPY backend-common/pom.xml backend-common/
COPY web/pom.xml web/
COPY desktop/pom.xml desktop/
RUN mvn -pl web -am dependency:go-offline -B
COPY shared shared
COPY backend-common backend-common
COPY web web
RUN mvn -pl web -am package -DskipTests -B

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/web/target/escola-aviacao-web-*-exec.jar app.jar
RUN mkdir -p /app/uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

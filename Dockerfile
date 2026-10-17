# Stage 1: Build the Java application
FROM gradle:8.14-jdk21 AS build

WORKDIR /app

COPY . .

RUN gradle --no-daemon clean installDist

# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/install/todo/ /app/

EXPOSE 8082

CMD ["/app/bin/todo"]

FROM maven:3.9-eclipse-temurin-21 AS builder
ENV APP_HOME  /usr/apps
COPY . $APP_HOME
WORKDIR	$APP_HOME
RUN mvn clean install

FROM eclipse-temurin:21-jre-alpine
COPY --from=builder /usr/apps/target/app.jar /usr/apps/
EXPOSE 8080

CMD ["java", "-jar", "/usr/apps/app.jar"]
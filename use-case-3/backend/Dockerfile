FROM 539247483490.dkr.ecr.us-east-1.amazonaws.com/easy-task-repo:mave AS builder
ENV APP_HOME  /usr/apps
COPY . $APP_HOME
WORKDIR	$APP_HOME
RUN mvn clean install

FROM 539247483490.dkr.ecr.us-east-1.amazonaws.com/easy-task-repo:eclipse-temurin-21-jre-alpine
COPY --from=builder /usr/apps/target/app.jar /usr/apps/
EXPOSE 8080

CMD ["java", "-jar", "/usr/apps/app.jar"]
FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

COPY build/libs/management-0.1.jar management.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "management.jar"]
FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

COPY build/libs/management-0.1.jar management.jar
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

# Docker secrets mounted are set as environment variables from the shell
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["java", "-jar", "/app/management.jar"]
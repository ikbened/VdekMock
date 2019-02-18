FROM openjdk:8-jre-slim
RUN mkdir /app
WORKDIR /app
COPY target/vdekmock.jar /app
COPY startup.sh /app
RUN chmod 777 startup.sh
EXPOSE 8080
ENTRYPOINT ["/app/startup.sh"]
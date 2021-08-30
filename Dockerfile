FROM openjdk:8-alpine

COPY target/uberjar/chaas.jar /chaas/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/chaas/app.jar"]

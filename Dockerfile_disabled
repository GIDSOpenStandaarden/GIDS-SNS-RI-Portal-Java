FROM maven:3.6.3-jdk-11 AS build

ADD pom.xml /pom.xml
ADD src /src

RUN mvn clean install

FROM openjdk:11-jre

COPY --from=build target/gids-sns-ri-portal-java.jar /gids-sns-ri-portal-java.jar

ENV TZ="Europe/Amsterdam"

EXPOSE 8080

RUN mkdir /data

ENV SPRING_DATASOURCE_URL="jdbc:h2:/data:gids-hti-portal"

ENTRYPOINT [ "sh", "-c", "java -jar /gids-sns-ri-portal-java.jar" ]

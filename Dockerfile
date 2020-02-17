FROM hub.c.163.com/library/openjdk:8-jdk

MAINTAINER nino
ADD target/*.jar wordutor.jar
RUN echo "Asia/Shanghai" > /etc/timezone
EXPOSE 23456

ENTRYPOINT ["java", "-jar", "/wordutor.jar"]
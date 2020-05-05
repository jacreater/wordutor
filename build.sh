#!/bin/bash
mvn clean package -Dmaven.test.skip=true -U
docker build . -t hub.c.163.com/jacreater/wordutor
docker push hub.c.163.com/jacreater/wordutor


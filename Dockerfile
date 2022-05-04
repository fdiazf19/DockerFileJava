FROM openjdk:16-alpine3.13  
COPY JarDocker.jar ./  
COPY *.txt ./
WORKDIR ./ 
ENTRYPOINT ["java", "-jar", "JarDocker.jar"]  
CMD ["java", "RotaL"]  

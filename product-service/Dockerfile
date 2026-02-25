FROM amazoncorretto:17-alpine

# No Maven, o arquivo .jar fica dentro da pasta target
# O asterisco (*) ajuda a pegar o arquivo mesmo que a versão mude (ex: 1.0-SNAPSHOT)
COPY target/*.jar app.jar

# Porta que sua aplicação Spring Boot utiliza (ajuste se for diferente de 8080)
EXPOSE 8090

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
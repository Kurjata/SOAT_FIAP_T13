# Etapa 1: Build da aplicação
FROM maven:3.9.8-eclipse-temurin-21 AS builder

# Define diretório de trabalho
WORKDIR /app

# Copia o pom.xml e baixa dependências (cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte
COPY src ./src

# Gera o JAR
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final, menor
FROM openjdk:21-jdk-slim

WORKDIR /app

# Copia o JAR gerado
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]

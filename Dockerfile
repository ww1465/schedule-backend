# 1. 构建阶段：使用 Maven 镜像编译项目
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# 2. 运行阶段：使用精简的 JDK 镜像运行应用
FROM openjdk:17-jdk-slim
WORKDIR /app
# 从构建阶段复制生成的 jar 文件
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
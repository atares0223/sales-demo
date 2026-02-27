# ============================================
# Stage 1: Build Stage
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /build

# 复制 Maven 配置文件（利用 Docker 层缓存）
COPY pom.xml .
COPY dependencies/pom.xml ./dependencies/
COPY mic-common/pom.xml ./mic-common/
COPY common/pom.xml ./common/
COPY feign-api/pom.xml ./feign-api/
COPY customer/pom.xml ./customer/

# 下载依赖（利用 Docker 层缓存，只有在 pom.xml 变化时才重新下载）
RUN mvn dependency:go-offline -B -pl customer -am || true

# 复制源代码
COPY dependencies ./dependencies
COPY mic-common ./mic-common
COPY common ./common
COPY feign-api ./feign-api
COPY customer ./customer

# 构建应用（跳过测试以加快构建速度，生产环境可以运行测试）
RUN mvn clean package -B -pl customer -am -DskipTests

# 验证 JAR 文件是否存在
RUN find /build -name "*.jar" -type f

# ============================================
# Stage 2: Runtime Stage
# ============================================
FROM eclipse-temurin:17-jre-alpine

# 安装必要的工具（可选，用于调试）
RUN apk add --no-cache \
    curl \
    tzdata \
    && rm -rf /var/cache/apk/*

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建非 root 用户
RUN addgroup -S spring && adduser -S spring -G spring

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /build/customer/target/*.jar app.jar

# 更改文件所有者
RUN chown spring:spring app.jar

# 切换到非 root 用户
USER spring:spring

# 暴露端口（根据实际配置调整）
EXPOSE 8080

# 设置 JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

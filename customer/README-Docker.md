# Customer Service Docker 构建指南

## 概述

本项目使用二阶段 Dockerfile 构建，可以显著减小最终镜像大小并提高构建效率。

## 构建方式

### 方式一：使用 Docker 命令构建

在项目根目录执行：

```bash
# 构建镜像
docker build -f customer/Dockerfile -t customer-service:latest .

# 运行容器
docker run -d \
  --name customer-service \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=your-nacos-host:8848 \
  customer-service:latest
```

### 方式二：使用 docker-compose

```bash
# 在 customer 目录下
cd customer
docker-compose up -d
```

## Dockerfile 说明

### 第一阶段（Builder）
- 使用 `maven:3.9.6-eclipse-temurin-17` 镜像
- 利用 Docker 层缓存优化依赖下载
- 构建 customer 服务及其依赖模块
- 生成可执行的 JAR 文件

### 第二阶段（Runtime）
- 使用 `eclipse-temurin:17-jre-alpine` 镜像（轻量级）
- 只包含运行时必需的 JAR 文件
- 使用非 root 用户运行（提高安全性）
- 配置健康检查
- 设置时区为 Asia/Shanghai

## 优化特性

1. **层缓存优化**：先复制 pom.xml 文件，利用 Docker 缓存机制，只有在依赖变化时才重新下载
2. **多阶段构建**：减小最终镜像大小（从 ~500MB 减少到 ~200MB）
3. **Alpine 基础镜像**：使用 Alpine Linux，镜像更小
4. **非 root 用户**：提高安全性
5. **健康检查**：自动监控应用状态

## 环境变量配置

可以通过环境变量配置应用：

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| SPRING_PROFILES_ACTIVE | Spring 配置文件 | dev |
| JAVA_OPTS | JVM 参数 | -Xms512m -Xmx1024m -XX:+UseG1GC |
| SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR | Nacos 服务发现地址 | - |
| SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR | Nacos 配置中心地址 | - |

## 端口说明

- **8080**: 应用端口（根据实际配置调整）

## 健康检查

应用启动后，可以通过以下端点检查健康状态：

```bash
curl http://localhost:8080/actuator/health
```

## 构建优化建议

### 1. 使用构建缓存

```bash
# 使用 BuildKit 加速构建
DOCKER_BUILDKIT=1 docker build -f customer/Dockerfile -t customer-service:latest .
```

### 2. 使用多阶段构建缓存

```bash
# 构建时指定构建参数
docker build --build-arg BUILDKIT_INLINE_CACHE=1 \
  -f customer/Dockerfile \
  -t customer-service:latest .
```

### 3. 在 CI/CD 中使用

```yaml
# GitHub Actions 示例
- name: Build Docker image
  run: |
    docker build -f customer/Dockerfile -t customer-service:${{ github.sha }} .
```

## 故障排查

### 1. 构建失败：找不到依赖

**问题**：Maven 无法找到本地依赖模块

**解决**：
- 确保在项目根目录执行构建
- 检查依赖模块是否已正确构建
- 可以先执行 `mvn install -DskipTests` 安装依赖到本地仓库

### 2. 运行时错误：找不到主类

**问题**：JAR 文件格式不正确

**解决**：
- 检查 Spring Boot Maven Plugin 配置
- 确保 JAR 文件是可执行的 fat JAR

### 3. 健康检查失败

**问题**：应用启动时间过长

**解决**：
- 增加 `start_period` 时间
- 检查应用日志：`docker logs customer-service`

## 镜像大小对比

- **单阶段构建**：~500MB
- **二阶段构建**：~200MB
- **节省空间**：~60%

## 安全建议

1. 定期更新基础镜像
2. 使用非 root 用户运行（已配置）
3. 扫描镜像漏洞：`docker scan customer-service:latest`
4. 不要在镜像中存储敏感信息

## 相关文件

- `Dockerfile`: 二阶段构建文件
- `.dockerignore`: Docker 构建忽略文件
- `docker-compose.yml`: Docker Compose 配置文件

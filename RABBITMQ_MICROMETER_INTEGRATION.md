# RabbitMQ 和 Micrometer 集成指南

本文档说明如何在 Spring Cloud 项目中集成 RabbitMQ 和 Micrometer 监控。

## 已完成的集成工作

### 1. 依赖管理

#### dependencies/pom.xml
- 添加了 `micrometer-registry-prometheus` 版本管理
- 版本：`1.12.0`

#### mic-common/pom.xml
- 添加了 `micrometer-registry-prometheus` 依赖
- 已包含 `spring-boot-starter-actuator` 和 `spring-boot-starter-amqp`

### 2. RabbitMQ 配置

#### customer/src/main/java/com/waaw/customer/conf/RabbitMQConfig.java
- 配置了 JSON 消息转换器
- 配置了 RabbitTemplate
- Spring Boot Actuator 会自动收集 RabbitMQ 指标

### 3. 配置文件示例

创建了 `customer/src/main/resources/application-rabbitmq-example.yml` 作为配置参考。

## 使用步骤

### 1. 在 Nacos 配置中心或 application.yml 中添加配置

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    publisher-confirm-type: correlated
    publisher-returns: true
    listener:
      simple:
        acknowledge-mode: auto
        concurrency: 5
        max-concurrency: 10
        prefetch: 10
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,rabbitmq
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
        step: 30s
    tags:
      application: ${spring.application.name}
      environment: ${spring.profiles.active:dev}
    rabbitmq:
      enabled: true
  health:
    rabbit:
      enabled: true
```

### 2. 在其他服务中复用配置

如果其他服务（如 product-order、stock 等）也需要 RabbitMQ 和 Micrometer 集成：

1. **确保依赖已添加**：这些服务已经依赖 `mic-common`，所以依赖已经包含。

2. **复制 RabbitMQConfig**：将 `customer/src/main/java/com/waaw/customer/conf/RabbitMQConfig.java` 复制到对应服务的 conf 包下。

3. **添加配置**：在对应服务的配置文件中添加上述 RabbitMQ 和 Actuator 配置。

### 3. 访问监控端点

启动应用后，可以访问以下端点：

- **健康检查**：`http://localhost:端口/actuator/health`
- **所有指标**：`http://localhost:端口/actuator/metrics`
- **Prometheus 格式指标**：`http://localhost:端口/actuator/prometheus`
- **RabbitMQ 指标**：`http://localhost:端口/actuator/metrics/rabbitmq.*`

### 4. 使用 RabbitMQ

#### 发送消息示例

```java
@Autowired
private RabbitTemplate rabbitTemplate;

public void sendMessage(String queueName, Object message) {
    rabbitTemplate.convertAndSend(queueName, message);
}
```

#### 接收消息示例

```java
@Component
public class MessageListener {
    
    @RabbitListener(queues = "your.queue.name")
    public void processMessage(String message) {
        // 处理消息
        System.out.println("Received: " + message);
    }
}
```

## 监控指标说明

Micrometer 会自动收集以下 RabbitMQ 指标：

- `rabbitmq.connections` - 连接数
- `rabbitmq.channels` - 通道数
- `rabbitmq.consumers` - 消费者数
- `rabbitmq.messages.published` - 发布的消息数
- `rabbitmq.messages.consumed` - 消费的消息数
- `rabbitmq.messages.acknowledged` - 确认的消息数
- `rabbitmq.messages.rejected` - 拒绝的消息数

## 注意事项

1. **生产环境安全**：生产环境中应该限制 Actuator 端点的访问，只暴露必要的端点。

2. **配置管理**：建议将敏感配置（如 RabbitMQ 密码）放在 Nacos 配置中心，不要硬编码。

3. **性能影响**：启用指标收集会有轻微的性能开销，但通常可以忽略不计。

4. **版本兼容性**：确保 Spring Boot 版本与 Micrometer 版本兼容。当前使用的是 Spring Boot 3.2.5。

## 故障排查

### RabbitMQ 连接失败
- 检查 RabbitMQ 服务是否运行
- 验证连接配置（host、port、username、password）
- 检查网络连接

### 指标未收集
- 确认 `management.metrics.rabbitmq.enabled=true`
- 检查 Actuator 端点是否已暴露
- 查看应用日志是否有相关错误

### Prometheus 端点无法访问
- 确认 `management.endpoints.web.exposure.include` 包含 `prometheus`
- 检查端口是否正确
- 验证安全配置是否阻止了访问

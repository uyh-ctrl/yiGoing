# 快速启动

环境要求：JDK 21、可访问 Maven Central，以及对应服务所需的基础设施。管理端至少需要 MySQL；门户端还依赖 Redis、MongoDB 和 RabbitMQ；搜索端需要 Elasticsearch 7.17。

双击 `start.cmd`，默认以 `dev` 配置启动管理端。也可以在 PowerShell 中指定服务：

```powershell
.\start.ps1 -Service mall-portal
.\start.ps1 -Service mall-search -Profile prod
```

启动后访问 OpenAPI 文档：

- 管理端：`http://localhost:8080/swagger-ui/index.html`
- 搜索端：`http://localhost:8081/swagger-ui/index.html`
- 门户端：`http://localhost:8085/swagger-ui/index.html`

首次启动会由 Maven Wrapper 自动下载 Maven 3.9.11 和依赖。

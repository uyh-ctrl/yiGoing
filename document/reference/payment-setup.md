# 支付与生产环境配置

生产环境只从环境变量或密钥管理服务读取配置。以仓库中的 [`.env.example`](../../.env.example) 为模板创建部署环境变量，**不要**将 `.env`、私钥或证书提交到 Git。

## 支付渠道

- 支付宝：网页商城使用 Page Pay；在支付宝开放平台创建应用后填写 `EASYGOING_ALIPAY_*`。
- 微信支付：网页商城使用 Native 扫码支付；在微信支付商户平台申请 API v3 证书后填写 `EASYGOING_WECHAT_PAY_*`。

上线前必须配置 HTTPS 公网回调地址，并在两个商户平台分别登记：

- `https://<domain>/payment/alipay/notify`
- `https://<domain>/payment/wechat/notify`

支付回调必须完成验签、金额和商户号校验、订单状态幂等更新以及库存确认；浏览器不能直接调用“支付成功”接口。

## 最低上线依赖

| 服务 | 用途 |
| --- | --- |
| MySQL 8 | 订单、商品、权限等交易数据 |
| Redis | 限流、订单号、缓存 |
| RabbitMQ | 延迟取消未支付订单 |
| MongoDB | 会员浏览、收藏等非交易数据 |
| Elasticsearch | 商品检索 |
| OSS | 商品和内容媒体 |

在生产环境中应关闭 Swagger 或限制为内网访问，并将 `/actuator` 限制为负载均衡器和运维网络可访问。

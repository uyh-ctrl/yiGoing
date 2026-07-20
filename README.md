# yiGoing

易Going 是一个面向学习与实践的前后端分离商城系统，覆盖后台商品、订单、营销、权限管理，以及门户购物车、下单、库存锁定、超时取消等核心业务链路。后端基于 Java 21、Spring Boot 3.5、Spring Security、MyBatis、MySQL、Redis、RabbitMQ、MongoDB 与 Elasticsearch 构建；管理端采用 Vue 3、TypeScript、Vite 与 Element Plus。

项目重点完成了后台 JWT 鉴权与接口级权限控制、Redis 限流、订单库存锁定与延迟取消、OpenAPI/Actuator 可观测性配置，并接入 Spring AI 构建基于检索增强的智能客服原型。支付模块已预留支付宝 Page Pay 与微信 Native 的统一配置及渠道抽象，后续可通过环境变量接入真实商户凭据与签名回调。

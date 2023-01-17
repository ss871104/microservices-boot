# Microservices with Spring Boot

![microservices-architecture](microservices-boot.jpg)

## **Index**
* [Quick Start](#quick-start)
* [Config Server](#config-server)
* [Service Discovery](#service-discovery)
* [API Gateway](#api-gateway)
* [Message Queue](#message-queue)
* [Spring Boot Actuator](#spring-boot-actuator-application-monitoring)
* [Spring Cloud Sleuth](#spring-cloud-sleuth-distributed-tracing)
* [Docker](#docker)
* [Kubernetes](#kubernetes)

## **Quick Start**
先將 main branch clone 到 local，有兩種啟動方式：

1. **IDE application 方式啟動：**

開啟 IDE import maven 將所有的 project 引入。

用 docker 啟動 zipkin, prometheus, grafana，prometheus.yml 要將 target 的參數改成你目前使用網路的 ip address:port號，並且 prometheus 執行docker 所需的路徑要改成 prometheus.yml 的路徑
```
docker run --name zipkin -d -p 9411:9411 openzipkin/zipkin
```
```
docker run --name prometheus -d -p 9090:9090 -v 你的路徑/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus
```
```
docker run --name grafana -d -p 3000:3000 grafana/grafana
```

接著，到 [Kafka 官網](https://kafka.apache.org/quickstart)的 QuickStart 照著指示執行 Zookeeper 和 Kafka

最後，照順序將 server 開啟，即可測試 api 功能：<br>
eureka-server -> customer -> product -> order -> notification -> api gateway

2. **Docker Container 方式啟動：**

docker images 已經都 push 到我的 docker repository了，可以直接pull 做使用，以下是執行 docker compose 指令(要先 cd 到 docker compose 所在資料夾)
```
docker compose up -d
```

執行完畢後，輸入以下指令觀察 eureka server 的 log，等 eureka server 抓到所有的 microservices 即可測試 api 功能 (order-service 要等比較久)
```
docker logs -f eureka-server
```

---

執行完畢後可開啟 Postman 測試 api，以下 api 可供測試：

* 透過 customerId 查詢 order, orderDetail, product info,customer info<br>
GET Method: http://localhost:8083/api/order/info/10001
* 透過 json 格式的 body request 發出請求，完成訂單新增<br>
POST Method: http://localhost:8083/api/order/placedOrder
```json
{
    "customerId" : "10001",
    "totalPrice" : "270",
    "productsIdAndQuantity" : {
        "1001": "2",
        "1002": "5"
    }
}
```
可在瀏覽器上打以下 url 看 h2 database<br>
username: sa<br>
password: password<br>
* http://localhost:8000/h2-console<br>
url: jdbc:h2:mem:customer
* http://localhost:8100/h2-console<br>
url: jdbc:h2:mem:product
* http://localhost:8200/h2-console<br>
url: jdbc:h2:mem:order
* http://localhost:8300/h2-console<br>
url: jdbc:h2:mem:notification

note: docker 啟動需到 .h2.server.properties(隱藏的) 加上以下設定
```properties
webAllowOthers=true
```

Customer Table
|id|name|email|
|---|---|---|
|10001|andy|andy@gmail.com|
|10002|wayne|wayne@gmail.com|
|10003|eric|eric@gmail.com|

Product Table
|id|name|price|quantity|
|---|---|---|---|
|1001|pen|20|100|
|1002|pencil|10|200|
|1003|eraser|15|150|

Order Table
|order_id|customer_id|total_price|
|---|---|---|
|1|10001|50|
|2|10001|20|
|3|10002|20|
|4|10003|30|

OrderDetail Table
|order_detail_id|order_id|product_id|quantity|
|---|---|---|---|
|101|1|1001|1|
|102|1|1003|2|
|103|2|1002|1|
|104|3|1001|1|
|105|4|1002|3|

---

## **Config Server**
Config Server 是一個統一管理組態設定的組態控制中心，可以透過 Spring Cloud Config 去實現。

### Spring Cloud Config Server
* Config Server 需加入以下 dependency
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
* 需加入 @EnableConfigServer 去告訴 Spring 這個 application 是 Config Server
```java
@EnableConfigServer
@SpringBootApplication
public class SpringCloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	}

}
```
* application.yml 設定到哪理抓 configuration
```yml
server:
  port: 8888

spring:
  application:
    name: spring-cloud-config-server
  cloud:
    config:
      server:
        git:
          uri: file:///裝有config的資料夾連結(local or github)
```

### Config Client
* Config Client 需加入以下 dependency
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```
* application.yml 設定抓 config server 和抓資料夾的哪個 config
```yml
spring:
  application:
    name: customer
  config:
    import: optional:configserver:http://localhost:8888
  profile:
    active: qa
  cloud:
    config:
      profile: qa
```

---

## **Service Discovery**
Service Discovery 是一個自動檢測和定位 microservices 的服務，可以透過 Eureka Server 去實現，以下是三個基本功能：
* Register: 服務啟動時的註冊機制
* Query: 查詢已註冊服務資訊的機制
* Healthy Check: 確認服務健康狀態的機制

### Eureka Server
* Eureka Server 需加入以下 dependency
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```
* 需加入 @EnableEurekaServer 去告訴 Spring 這個 application 是 Eureka Server
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```
* application.yml 設定
```yml
server:
  port: 8761

spring:
  application:
    name: eureka-server
# Eureka Server 不需要註冊他自己
eureka:
  client:
    # 表示是否從 Eureka Server 獲取註冊的服務信息
    fetch-registry: false
    # 表示是否將自己註冊到 Eureka Server
    register-with-eureka: false
```

### Client
* Client 需加入以下 dependency
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
* 需加入 @EnableEurekaClient 去告訴 Spring 這個 application 是 Eureka Client
```java
@SpringBootApplication
@EnableEurekaClient
public class CustomerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}
```
* application.yml 設定
```yml
spring:
  application:
    name: customer

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
    fetch-registry: true
    register-with-eureka: true
    enabled: true
```

### OpenFeign
Feign 是簡化 Java HTTP 客戶端開發的工具 (java-to-httpclient-binder)，在 microservices 中，可透過 SpringCloudFeign 對其他 microservices 的 api 去做呼叫，相較於 RestTemplate，Feign 的寫法更簡潔。需透過 Eureka Server 上註冊的 spring.application.name 去抓。

* 使用 OpenFeign 需加入以下 dependency
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

* FeignClient
```java
@FeignClient(name="customer")
public interface CustomerProxy {
    @GetMapping("/api/customer/{customerId}")
    public CustomerResponse retrieveCustomerInfo(@PathVariable("customerId") Long customerId);
}
```

---

## **API Gateway**
API Gateway 是接收所有 API 的第一個入口，可對 API 進行調用應用策略、身份驗證和一般訪問控制以保護 Server 的資料庫。讓開發人員可以輕鬆地建立、發佈、維護、監控和保護任何規模的 API。可透過 Spring Cloud API Gateway實現。

* 使用 Spring Cloud API Gateway 需加入以下 dependency
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
* java 設定方式
```java
@Configuration
public class ApiGatewayConfiguration {
    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/get")
                        .filters(f -> f
                                .addRequestHeader("MyHeader", "MyURI")
                                .addRequestParameter("Param", "MyValue"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p.path("/api/customer/**")
                        .uri("lb://customer"))
                .route(p -> p.path("/api/product/**")
                        .uri("lb://product"))
                .route(p -> p.path("/api/order/**")
                        .uri("lb://order"))
                .route(p -> p.path("/api/notification/**")
                        .uri("lb://notification"))
                .build();
    }
}
```

* application.yml 設定方式
```yml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
# Customer Service Route
      routes[0]:
        id: customer
        uri: lb://product
        predicates[0]: Path=/api/customer
# Product Service Route
      routes[1]:
        id: product
        uri: lb://product
        predicates[0]: Path=/api/product
# Order Service Route
      routes[2]:
        id: order
        uri: lb://order
        predicates[0]: Path=/api/order
# Notification Service Route
      routes[3]:
        id: notification
        uri: lb://notification
        predicates[0]: Path=/api/notification

```

* log 設定
```java
@Component
public class LoggingFilter implements GlobalFilter {

    private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Path of the request received -> {}", exchange.getRequest().getPath());
        return chain.filter(exchange);
    }

}
```

---

## **Message Queue**
Message Queue

## **Spring Boot Actuator (Application Monitoring)**


## **Spring Cloud Sleuth (Distributed Tracing)**


## **Docker**


## **Kubernetes**
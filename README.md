# seckill-backend

在线秒杀系统后端，Spring Boot 3 + Spring Data JPA + MySQL（本地默认 H2 内存库）。

## 目录结构

```
src/main/java/com/example/seckill
├── SeckillApplication.java          启动类
├── common/Result.java               统一返回结构
├── config/WebConfig.java            跨域配置
├── config/DataInitializer.java      启动时写入演示商品
├── controller/UserController.java   用户注册、登录、个人信息
├── controller/ProductController.java 商品列表、详情、创建秒杀活动
├── controller/OrderController.java  秒杀下单、订单列表、取消订单
├── entity/                          User、Product、SeckillOrder
├── repository/                      JPA 数据访问
└── service/UserService.java         用户业务逻辑
```

## 接口清单

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | /api/user/register | 注册，body：`{"username":"tom","password":"123456"}` |
| POST | /api/user/login | 登录，body 同上 |
| GET | /api/user/profile?username=tom | 获取用户个人信息（指导书 3.3.1 示例接口） |
| GET | /api/product/list | 商品列表 |
| GET | /api/product/{id} | 商品详情 |
| POST | /api/product/create | 管理员创建秒杀活动 |
| POST | /api/order/seckill | 参与秒杀下单，body：`{"username":"tom","productId":1}` |
| GET | /api/order/list?username=tom | 我的订单 |
| POST | /api/order/cancel/{id} | 取消订单 |

统一返回格式：`{"code":200,"message":"成功","data":...}`。

## 本地运行

```bash
# 需要 JDK 17 与 Maven
mvn clean package
java -jar target/seckill-backend-0.0.1-SNAPSHOT.jar
```

启动后接口地址为 http://localhost:8080/api/product/list 。

## 打包与部署

```bash
mvn clean package
scp target/seckill-backend-0.0.1-SNAPSHOT.jar root@<ECS 公网 IP>:/root
```

在服务器上连接 RDS for MySQL 启动（数据库地址换成 RDS 内网地址）：

```bash
java -jar seckill-backend-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url="jdbc:mysql://<RDS 内网地址>:3306/seckill_db?serverTimezone=UTC&characterEncoding=utf8" \
  --spring.datasource.username=seckill \
  --spring.datasource.password=<数据库密码>
```

表结构由 JPA 自动创建（`spring.jpa.hibernate.ddl-auto=update`），
也可以按指导书 5.3 手动执行 `CREATE DATABASE seckill_db;` 与建表语句。

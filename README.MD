# 一个消息队列管道

## 功能说明

跨机房消息队列,将一个MQ中的消息转发到另一个MQ,可以作为两个无法直接通信的MQ的通信渠道

## 使用场景

跨机房消息发送,目标机房不能直接开放MQ端口到公网

## 架构说明

Source MQ - 消息队列数据源
Target MQ - 消息队列目标

在Source MQ端启动一个message-pipeline作为消费者(Source MP), Target MQ端启动一个message-pipeline作为生产者(Target MP).

Source MP将消息从Source MQ取出作为二进制数据发送到Target MP, Target MP将接收到的消息发送到Target MQ, 完成消息转发.

## 通信协议

目前仅支持HTTP

## 配置方法

### 消费端配置

详见代码中mp-consumer.properties

### 消费端转发配置

详见代码中mp-sender.properties

### 生产端配置

详见代码中mp-provider.properties

### 生产端接收配置

详见代码中spring-jetty.xml
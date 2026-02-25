FROM rabbitmq:3.12-management

# 拷贝延迟队列插件到 RabbitMQ 插件目录
COPY rabbitmq_delayed_message_exchange-3.12.0.ez /plugins/

# 启用插件（离线模式，构建阶段）
RUN rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange

EXPOSE 5672 15672

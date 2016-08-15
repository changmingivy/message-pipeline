package cn.jpush.mp.rabbitmq;

/**
 * Created by elvin on 16/8/15.
 */
public class RabbitMQConfig {

    public final String server;
    public final int port;
    public final String username;
    public final String password;
    public final String exchangeName;
    public final String exchangeMode;
    public final String queueName;
    public final String routingKey;
    public final int basicQos;

    private RabbitMQConfig(String server, int port, String username, String password,
                           String exchangeName, String exchangeMode, String queueName,
                           String routingKey, int basicQos) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
        this.exchangeName = exchangeName;
        this.exchangeMode = exchangeMode;
        this.queueName = queueName;
        this.routingKey = routingKey;
        this.basicQos = basicQos;
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String server;
        private int port;
        private String username;
        private String password;
        private String exchangeName;
        private String exchangeMode;
        private String queueName;
        private String routingKey;
        private int basicQos;

        public RabbitMQConfig build() {
            return new RabbitMQConfig(server, port, username, password, exchangeName, exchangeMode, queueName, routingKey, basicQos);
        }

        public Builder setServer(String server) {
            this.server = server;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setExchangeName(String exchangeName) {
            this.exchangeName = exchangeName;
            return this;
        }

        public Builder setExchangeMode(String exchangeMode) {
            this.exchangeMode = exchangeMode;
            return this;
        }

        public Builder setQueueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public Builder setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
            return this;
        }

        public Builder setBasicQos(int basicQos) {
            this.basicQos = basicQos;
            return this;
        }
    }

}

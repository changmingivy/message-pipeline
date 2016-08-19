package cn.jpush.mp.rabbitmq;

/**
 * Created by elvin on 16/8/15.
 */
public class RabbitMQConfig {

    public final String server;
    public final String username;
    public final String password;
    public final String exchangeName;
    public final String queueName;
    public final int basicQos;

    private RabbitMQConfig(String server, String username, String password,
                           String exchangeName, String queueName, int basicQos) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.exchangeName = exchangeName;
        this.queueName = queueName;
        this.basicQos = basicQos;
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String server;
        private String username;
        private String password;
        private String exchangeName;
        private String queueName;
        private int basicQos;

        public RabbitMQConfig build() {
            return new RabbitMQConfig(server, username, password, exchangeName, queueName, basicQos);
        }

        public Builder setServer(String server) {
            this.server = server;
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

        public Builder setQueueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public Builder setBasicQos(int basicQos) {
            this.basicQos = basicQos;
            return this;
        }
    }

}

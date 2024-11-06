package com.compass.ms_usuario.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "notifyQueue";

    /**
     * Cria e retorna uma instância da fila de mensagens.
     *
     * @return Uma nova instância da fila, com o nome definido em QUEUE_NAME.
     */
    @Bean
    public Queue myQueue() {
        return new Queue(QUEUE_NAME, true);
    }
}


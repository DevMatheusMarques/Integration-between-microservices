package com.compass.ms_notify.config;

import com.compass.ms_notify.services.NotifyService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração para o RabbitMQ.
 *
 * Esta classe configura os componentes necessários para a comunicação com o RabbitMQ,
 * incluindo a definição de uma fila, um template Rabbit e um contêiner de listener para
 * processar mensagens. Ela utiliza a anotação @Configuration do Spring para marcar
 * esta classe como uma fonte de definições de beans.
 */
@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME = "notifyQueue";

    /**
     * Define a fila de notificação.
     *
     * @return uma instância de Queue configurada com o nome da fila.
     */
    @Bean
    public Queue notifyQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    /**
     * Cria um RabbitTemplate para enviar e receber mensagens.
     *
     * @param connectionFactory a fábrica de conexões do RabbitMQ
     * @return uma instância de RabbitTemplate configurada
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    /**
     * Configura um contêiner de listener para processar mensagens da fila.
     *
     * @param connectionFactory a fábrica de conexões do RabbitMQ
     * @param listenerAdapter o adaptador de listener para processar mensagens
     * @return uma instância de SimpleMessageListenerContainer configurada
     */
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                                    MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    /**
     * Cria um adaptador de listener que chama o método especificado no serviço de notificação.
     *
     * @param notifyService o serviço que processa mensagens de notificação
     * @return um adaptador de listener configurado
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(NotifyService notifyService) {
        return new MessageListenerAdapter(notifyService, "receiveMessage");
    }
}


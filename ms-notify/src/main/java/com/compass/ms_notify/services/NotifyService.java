package com.compass.ms_notify.services;

import com.compass.ms_notify.config.RabbitConfig;
import com.compass.ms_notify.models.Message;
import com.compass.ms_notify.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por receber e processar mensagens de notificação.
 *
 * Esta classe usa a anotação @Service do Spring para indicar que é um serviço,
 * gerenciando a lógica de recebimento de mensagens provenientes da fila do RabbitMQ.
 * As mensagens recebidas são salvas no repositório de mensagens.
 */
@Service
@RequiredArgsConstructor
public class NotifyService {

    private final MessageRepository messageRepository;

    /**
     * Método que escuta mensagens na fila de notificações.
     *
     * Este método é anotado com @RabbitListener e será chamado automaticamente
     * sempre que uma nova mensagem for recebida na fila configurada.
     *
     * @param message a mensagem recebida da fila
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        try {
            Message saveMessage = new Message(message);
            messageRepository.save(saveMessage);
            System.out.println();
            System.out.println("Received message: " + message);
            System.out.println();
        } catch (Exception e) {
            System.err.println("Failed to save message: " + e.getMessage());
        }
    }
}

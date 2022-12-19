package ru.zubarev.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.zubarev.services.ProducerService;

import static ru.zubarev.model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
@AllArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE_UPDATE, sendMessage);
    }
}


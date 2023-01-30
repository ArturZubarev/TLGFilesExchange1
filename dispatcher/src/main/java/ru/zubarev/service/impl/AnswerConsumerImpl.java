package ru.zubarev.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.zubarev.controller.UpdateProcessor;
import ru.zubarev.service.AnswerConsumer;

import static ru.zubarev.model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
@AllArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;
    @RabbitListener(queues=ANSWER_MESSAGE_UPDATE)
    @Override
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);

    }
}

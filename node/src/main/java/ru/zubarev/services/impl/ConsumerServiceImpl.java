package ru.zubarev.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zubarev.services.ConsumerService;
import ru.zubarev.services.MainService;
import ru.zubarev.services.ProducerService;

import static ru.zubarev.model.RabbitQueue.*;

@Service
@AllArgsConstructor
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainServiceImpl mainService;

    @RabbitListener(queues=TEXT_MESSAGE_UPDATE)
    @Override
    public void consumerTextMessageUpdates(Update update) {
        log.debug("NODE: text message is received");
        mainService.processTextMessage(update);

    }
    @RabbitListener(queues=PHOTO_MESSAGE_UPDATE)
    @Override
    public void consumerPhotoMessageUpdates(Update update) {

        log.debug("NODE: photo message is received");
        mainService.processPhotoMessage(update);
    }
    @RabbitListener(queues=DOC_MESSAGE_UPDATE)
    @Override
    public void consumerDocMessageUpdates(Update update) {
        log.debug("NODE: doc message is received");
        mainService.processDocMessage(update);

    }
}

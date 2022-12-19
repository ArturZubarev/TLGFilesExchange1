package ru.zubarev.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zubarev.Utils.MessageUtils;
import ru.zubarev.service.UpdateProducer;
import ru.zubarev.service.impl.UpdateProducerImpl;

import static ru.zubarev.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {
    private UpdateProducer updateProducer;
    private final MessageUtils messageUtils;
   private TelegramBot telegramBot;
   public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer){
       this.messageUtils=messageUtils;
       this.updateProducer=updateProducer;
   }
   public void registerBot(TelegramBot telegramBot){
       this.telegramBot=telegramBot;
   }
   public void processUpdate(Update update) {
       if (update == null) {
           log.error("Unsupported message type is received");
           return;
       }
       if (update.getMessage() != null) {
           distributeMessagesByType(update);
       } else {log.error("Received unsupported message type"+ update);}
   }
   private void distributeMessagesByType(Update update){
       var message=update.getMessage();
       if (message.hasText()){
           processTextMessage(update);
       } else if (message.getDocument()!=null) {
           processDocMessage(update);
       }    else if (message.getPhoto()!=null){
           processPhotoMessage(update);
       } else setUnsupportedMessageTypeView(update);

   }
    private void processPhotoMessage(Update update) {
       updateProducer.produce(PHOTO_MESSAGE_UPDATE,update);
       setFileIsRecieved(update);
    }

    private void setFileIsRecieved(Update update) {
        var sendMessage=messageUtils.generateSendMessageWithText(update,
                "Ведется обработка файла");
        setView(sendMessage);
    }

    private void processDocMessage(Update update) {
       updateProducer.produce(DOC_MESSAGE_UPDATE, update);
       setFileIsRecieved(update);
    }

    private void processTextMessage(Update update) {
       updateProducer.produce(TEXT_MESSAGE_UPDATE,update);
    }
    private void setUnsupportedMessageTypeView(Update update){
       var sendMessage=messageUtils.generateSendMessageWithText(update,
               "Данный тип сообщения не поддерживается");
       setView(sendMessage);
    }
    public void setView(SendMessage sendMessage){
       telegramBot.sendAnswerMessage(sendMessage);
    }


}

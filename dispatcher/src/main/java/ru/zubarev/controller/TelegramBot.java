package ru.zubarev.controller;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {
    private UpdateController updateController;
    public TelegramBot(UpdateController updateController){
        this.updateController=updateController;
    }
    @PostConstruct
    public void init(){
        updateController.registerBot(this);
    }
    @Override
    public String getBotUsername() {
        return "My1FilesExchangeBot";
    }
    @Override
    public String getBotToken() {
        return "5505239274:AAG8fDzE7hZpior1MsetdwPPBBJfOat2Xbc";
    }
    @Override
    public void onUpdateReceived(Update update) {
        var originalMessage=update.getMessage();
        updateController.processUpdate(update);
    }
    public void sendAnswerMessage(SendMessage message){
        if (message!=null) try {
            execute(message);
        }
        catch (TelegramApiException e){
            log.error(e);
        }
    }
}

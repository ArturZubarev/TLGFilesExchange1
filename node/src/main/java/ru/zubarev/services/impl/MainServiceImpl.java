package ru.zubarev.services.impl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.zubarev.dao.RawDataDao;
import ru.zubarev.entity.AppDocument;
import ru.zubarev.entity.AppPhoto;
import ru.zubarev.entity.RawData;
import ru.zubarev.exception.UploadFileException;
import ru.zubarev.services.AppUserService;
import ru.zubarev.services.FileService;
import ru.zubarev.services.MainService;
import ru.zubarev.services.ProducerService;
import ru.zubarev.dao.AppUserDAO;
import ru.zubarev.entity.AppUser;
import ru.zubarev.services.enums.LinkType;

import java.util.Optional;

import static ru.zubarev.entity.enums.UserState.BASIC_STATE;
import static ru.zubarev.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.zubarev.services.enums.ServiceCommands.*;

@Service
@Log4j
@AllArgsConstructor

public class MainServiceImpl implements MainService {
        private final AppUserDAO appUserDAO;
        private final ProducerService producerService;
        private final RawDataDao rawDataDao;
        private final FileService fileService;
        private final AppUserService appUserService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);


        var appUser=findOrSaveAppUser(update);
        var userState=appUser.getState();
        var text=update.getMessage().getText();
        var output=" ";
        if(CANCEL.equals(text)){
            output=cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output=processServiceCommand(appUser,text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            appUserService.setEmail(appUser,text);
        } else { log.error("Unknown user state"+userState);
            output="Неизвестная ошибка.Введите /cancel и попробуйте снова";
        }
        var chatId=update.getMessage().getChatId();
        sendAnswer(output,chatId);

    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser=findOrSaveAppUser(update);
        var chatId=update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId,appUser)){
            return;
        }
        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            var answer = "Ваш документ успешно загружен!" +
                    "Ссылка для скачивания: " + link;
            sendAnswer(answer,chatId);
        }catch (UploadFileException e){
            log.error(e);
            String error="Загрузка не удалась, повторите попытку позднее";
            sendAnswer(error,chatId);
        }

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.isActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента";
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов";
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser=findOrSaveAppUser(update);
        var chatId=update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId,appUser)){
            return;
        }
        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Ваше фото успешно загружено!" +
                    "Ссылка для скачивания: " + link;
            sendAnswer(answer,chatId);
        }   catch (UploadFileException e){
            log.error(e);
            String error = "К сожалению загрузка не удалась, повторите попытку позднее";
            sendAnswer(error,chatId);
        }

    }

    private void sendAnswer(String output, Long chatId) {

        var sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {

            return appUserService.registerUser(appUser);
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Здравствуйте! для просмотра перечня доступных команд введите /help";
        } else {
            return "Неизвестная команда. Для просмотра списка всех доступных команд введите /help";
        }
    }


    private String help(){
            return "Cписок доступных команд:\n"
                    +"/cancel-отменить выполнение текущей операции;\n"
                    +"/registration- регистрация нового пользователя";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Выполнение команды отменено";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser=update.getMessage().getFrom();
        var optional = appUserDAO.findByTelegramUserId(telegramUser.getId());

        if (optional.isEmpty()) {
            AppUser transientUser = AppUser.builder().
                    telegramUserId(telegramUser.getId()).
                    userName(telegramUser.getUserName()).
                    firstName(telegramUser.getFirstName()).
                    lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientUser);
        }
        return optional.get();

    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().event(update).build();
        rawDataDao.save(rawData);
    }
}

package ru.zubarev.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.zubarev.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);

}

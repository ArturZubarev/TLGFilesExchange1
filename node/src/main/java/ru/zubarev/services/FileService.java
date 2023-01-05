package ru.zubarev.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.zubarev.entity.AppDocument;
import ru.zubarev.entity.AppPhoto;
import ru.zubarev.services.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);
}

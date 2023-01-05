package ru.zubarev.service;

import ru.zubarev.dto.MailParams;

public interface MailSenderService {
    void send (MailParams mailParams);
}

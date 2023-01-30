package ru.zubarev.service;

import org.springframework.core.io.FileSystemResource;
import ru.zubarev.entity.AppDocument;
import ru.zubarev.entity.AppPhoto;
import ru.zubarev.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);

}
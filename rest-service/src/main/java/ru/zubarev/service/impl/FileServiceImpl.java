package ru.zubarev.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ru.zubarev.dao.AppDocumentDAO;
import ru.zubarev.dao.AppPhotoDAO;
import ru.zubarev.entity.AppDocument;
import ru.zubarev.entity.AppPhoto;
import ru.zubarev.entity.BinaryContent;
import ru.zubarev.service.FileService;

import java.io.File;
import java.io.IOException;

@Service
@Log4j
public class FileServiceImpl implements FileService {
    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    @Override
    public AppDocument getDocument(String docId) {
        //TODO дешифрование хеш-строки
        var id= Long.parseLong(docId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        var id=Long.parseLong(photoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    //преобразует запись в бд в ссылку, с помощью которой можно будет скачать файл
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            File temp=File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp,binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        }catch (IOException e){
            log.error(e);
        } return null;
    }
}

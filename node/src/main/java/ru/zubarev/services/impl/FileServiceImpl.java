package ru.zubarev.services.impl;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.zubarev.CryptoTool;
import ru.zubarev.dao.AppDocumentDAO;
import ru.zubarev.dao.AppPhotoDAO;
import ru.zubarev.dao.BinaryContentDAO;
import ru.zubarev.entity.AppDocument;
import ru.zubarev.entity.AppPhoto;
import ru.zubarev.entity.BinaryContent;
import ru.zubarev.exception.UploadFileException;
import ru.zubarev.services.FileService;
import ru.zubarev.services.enums.LinkType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final AppPhotoDAO appPhotoDAO;
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;
    private final CryptoTool cryptoTool;
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO,
                           AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool){
        this.appDocumentDAO=appDocumentDAO;
        this.binaryContentDAO=binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }
    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId=telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response=getFilePath(fileId);{
            if (response.getStatusCode()== HttpStatus.OK){
                JSONObject jsonObject=new JSONObject(response.getBody());
                String filePath=String.valueOf(jsonObject
                        .getJSONObject("result")
                        .getString("file_path"));
            byte[] fileInByte=downloadFile(filePath);
                BinaryContent transientBinaryContent=BinaryContent.builder()
                        .fileAsArrayOfBytes(fileInByte)
                        .build();
            BinaryContent persistentBinaryContent=binaryContentDAO.save(transientBinaryContent);
                Document telegramDoc=telegramMessage.getDocument();
                AppDocument transientAppDoc=buildTransientAppDoc(telegramDoc,persistentBinaryContent);
                return appDocumentDAO.save(transientAppDoc);
            } throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType){
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" +hash;

    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }
    private AppDocument buildTransientAppDoc(Document telegramDoc,BinaryContent persistentBinaryContent){
        return AppDocument.builder()
                .telegramField(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramField(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }


    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }
    private ResponseEntity<String> getFilePath(String field){
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders httpHeaders=new HttpHeaders();
        HttpEntity<String> request=new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,field);
    }
    private byte [] downloadFile(String filePath) {
        String fullURI = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullURI);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }
        try (InputStream inputStream=urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e){
            throw new UploadFileException(urlObj.toExternalForm(),e);
        }
    }
}

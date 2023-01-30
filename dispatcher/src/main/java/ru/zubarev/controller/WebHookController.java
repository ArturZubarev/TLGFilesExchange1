package ru.zubarev.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final UpdateProcessor processor;


    public WebHookController(UpdateProcessor processor) {
        this.processor = processor;
    }

    @RequestMapping(value = "/callback/update", method = RequestMethod.POST)
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update){
        processor.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}

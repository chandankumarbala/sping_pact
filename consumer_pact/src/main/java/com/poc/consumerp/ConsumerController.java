package com.poc.consumerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    private ConsumerService service;

    @GetMapping(value = "/consumer/books",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> produce(){
        return ResponseEntity.ok(this.service.getBooksFromProducer());
    }

    @GetMapping(value = "/consumer/book",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookBeanFromConsumer> getBook(){
        return ResponseEntity.ok(this.service.getHarrysBookFromProducer());
    }
}

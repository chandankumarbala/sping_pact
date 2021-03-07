package com.poc.consumerp;


import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookBeanFromProducer {
    private String name;
    private String author;
    private String edition;
    private int pages;
}

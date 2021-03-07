package com.poc.consumerp;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookBeanFromConsumer {
     private String name;
     private String author;
     private String edition;
     private int pages;
     private String uuid;

}

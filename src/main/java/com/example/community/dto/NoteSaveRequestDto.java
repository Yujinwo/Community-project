package com.example.community.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NoteSaveRequestDto {

    private String receiver_email;
    private String message;

}

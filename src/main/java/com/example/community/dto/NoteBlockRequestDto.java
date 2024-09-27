package com.example.community.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
// 쪽지 수신 설정 요청 Dto
@Getter
@NoArgsConstructor
public class NoteBlockRequestDto {

    private String block_type;
}

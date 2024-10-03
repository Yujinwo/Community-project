package com.example.community.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// 쪽지 수신 설정 요청 Dto
@Getter
@NoArgsConstructor
public class NoteBlockRequestDto {
    @NotEmpty(message = "데이터가 존재하지 않습니다.")
    private String block_type;
}

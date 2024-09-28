package com.example.community.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

// 쪽지 수신 설정 요청 Dto
@Getter
@NoArgsConstructor
public class NoteBlockRequestDto {
    @NotNull(message = "비정상적인 데이터 입니다.")
    private String block_type;
}

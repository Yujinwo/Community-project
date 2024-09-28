package com.example.community.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
// 쪽지 작성 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestDto {
    @Size(min=8,max=320,message = "이메일은 8~320자 이내로 작성해주세요")
    private String receiver_email;
    @Size(min=8,max=320,message = "메세지는 8~500자 이내로 작성해주세요")
    private String message;

}

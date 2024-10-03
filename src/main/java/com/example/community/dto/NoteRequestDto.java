package com.example.community.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
// 쪽지 작성 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestDto {
    @NotEmpty(message = "데이터가 존재하지 않습니다.")
    @Size(min=8,max=320,message = "이메일은 8~320자 이내로 작성해주세요")
    private String receiver_email;
    @NotEmpty(message = "데이터가 존재하지 않습니다.")
    @Size(min=8,max=320,message = "메세지는 8~500자 이내로 작성해주세요")
    private String message;

}

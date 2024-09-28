package com.example.community.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IdCheckDto {

    @Size(min = 8,max =320 ,message = "이메일은 8~320자 이내로 작성해주세요")
    private String email;
}

package com.example.community.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

@Getter
@NoArgsConstructor
public class CommentUpdateDto {

    private Long id;
    @NotEmpty(message = "데이터가 존재하지 않습니다.")
    @Size(min = 3,max = 280,message = "댓글은 3~280자 이내로 작성해주세요")
    private String content;
}

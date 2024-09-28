package com.example.community.dto;

import com.example.community.entity.Article;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// 글 작성 요청 DTO
@Getter
@NoArgsConstructor
public class ArticleRequestDto {
    private Long id;
    @Size(min=1,max=60,message = "제목은 5~60자 이내로 작성해주세요")
    private String title;
    @Size(min=1,max=1000,message = "내용은 5~1000자 이내로 작성해주세요")
    private String content;
    @Size(max = 2,message = "이미지는 최대 2개까지 가능합니다")
    private List<String> ImageUrls = new ArrayList<>();

    @Size(max = 9,message = "태그는 9개 이내로 작성해주세요")
    private List<String> tags = new ArrayList<>();
}

package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import com.example.community.entity.Reply;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CommentRequestDto {

    private Long id;
    // 댓글 내용 사이즈 1~280자
    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    // 멤버 Entity
    private Member member;
    // 글 Entity
    private Article article;
    // 글 id
    private Long boardid;
    // 대댓글
    private List<Reply> child;
    // 대댓글이 있는 상태에서 부모 댓글 삭제 여부

    private Boolean deleted;

    // dto -> Entity 생성
    public Comment toEntity() {
        return Comment.builder().content(content).member(member).article(article).child(child).deleted(false).build();
    }

}

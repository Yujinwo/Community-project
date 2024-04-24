package com.example.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper=true)
public class Reply extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 대댓글 내용
    @Column
    private String content;
    // Member Entity 다:1 설정 * 한 유저가 여러 대댓글이 가능
    @ManyToOne
    @JoinColumn(name = "userid")
    private Member member;
    // Member Comment 다:1 설정 * 한 댓글에 여러 대댓글이 가능
    @ManyToOne
    @JoinColumn(name = "commentid")
    private Comment comment;

}

package com.example.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "boardImage")
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 이미지 저장 Url
    @Column(nullable = false)
    private String url;
    // Article Entity 다:1 관계 설정 * 한 게시글안에 여러 이미지가 가능
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Article article;
}
package com.example.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Article article;
}
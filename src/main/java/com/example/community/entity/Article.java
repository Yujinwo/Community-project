package com.example.community.entity;

import com.example.community.dto.ArticleindexResponseDto;
import com.example.community.dto.ArticleResponseDto;
import com.example.community.dto.MyArticleResponseDto;
import com.example.community.dto.NoteResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@BatchSize(size = 10)
public class Article extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 글 제목
    @Column
    private String title;
    // 글 내용
    @Column(length = 1000)
    private String content;
    // Member Entity 다:1 관계 설정 * 한 유저가 여러 게시글이 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;
    // 조회수
    @Column
    private int viewcount;
    // boardImages Entity 1:다 관계 설정 *한 게시글 안에 여러 이미지가 가능
    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    @BatchSize(size = 10)
    private List<BoardImage> boardImages;
    // comments Entity 1:다 관계 설정 * 한 게시글 안에 여러 댓글이 가능
    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Comment> comments;

    @OneToMany(mappedBy = "article",orphanRemoval = true,cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarks;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "article",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Tag> tags;
    // 댓글 수
    @Column
    private int commentcount;
  
    //연관관계 메서드
    public void addBoardImages (BoardImage boardImage){
        boardImage.setArticle(this);
        this.getBoardImages().add(boardImage);
    }

    public MyArticleResponseDto changeMyArticleResponseDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createdFormatDate = createdDate.format(formatter);
        return MyArticleResponseDto.builder().id(id).title(title).createdDate(createdFormatDate).build();
    }

    public void updatecount() {
        this.viewcount = viewcount + 1;
    }

    public void changeTitleandContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void chagneCommentCount(int i) {
        this.commentcount = i;
    }
}

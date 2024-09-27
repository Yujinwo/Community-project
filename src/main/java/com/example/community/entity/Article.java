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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@BatchSize(size = 10)
public class Article extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column(length = 1000)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;
    // 조회수
    @Column
    private int viewcount;
    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    @BatchSize(size = 10)
    private List<BoardImage> boardImages;
    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Comment> comments;

    @OneToMany(mappedBy = "article",orphanRemoval = true,cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarks;

    @OneToMany(mappedBy = "article",orphanRemoval = true,cascade = CascadeType.REMOVE)
    private List<Notification> notifications;

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
    // 글 작성시간 형식 변환
    public MyArticleResponseDto changeMyArticleResponseDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createdFormatDate = createdDate.format(formatter);
        return MyArticleResponseDto.builder().id(id).title(title).createdDate(createdFormatDate).build();
    }
    // 조회 수 1 올리기
    public void updatecount() {
        this.viewcount = viewcount + 1;
    }


    public void changeTitleandContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
    // 댓글 수 1 올리기
    public void chagneCommentCount(int i) {
        this.commentcount = i;
    }
}

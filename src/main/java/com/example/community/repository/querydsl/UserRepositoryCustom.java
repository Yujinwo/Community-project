package com.example.community.repository.querydsl;

import com.example.community.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<Article> findByArticlelist(Pageable pageable, String sort);
    Page<Article> findBymyArticlelist(Member user,Pageable pageable);
    Page<Article> findByTitleOrContentContaining(String sort, String query, Pageable pageable, String search);
    Page<Tag> findByTagContaining(String sort, String query, Pageable pageable, Boolean tagsearch);
    Article findByArticleAndMemberlist(Long Id);
    Page<Comment> findByCommentlist(Long boardId, Pageable pageable);
    Page<Comment> findBymyCommentlist(Member user,Pageable pageable);
    Page<Notification> findByNoticication(Member user,Pageable pageable);
    Page<Bookmark> findBymyBookmarklist(Member user,Pageable pageable);
    Page<Note> findByNote(Member user, Pageable pageable);
}
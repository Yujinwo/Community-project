package com.example.community.repository.querydsl;

import com.example.community.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<Article> findByArticlelist(Long lastId, Pageable pageable);
    Page<Article> findBymyArticlelist(Member user,Pageable pageable);
    Page<Article> findByTitleOrContentContaining(Long lastId,String query, Pageable pageable);
    Page<Tag> findByTagContaining(Long lastId,String query, Pageable pageable,Boolean tagsearch);
    Article findByArticleAndMemberlist(Long Id);
    Page<Comment> findByCommentlist(Long boardId, Pageable pageable);
    Page<Comment> findBymyCommentlist(Member user,Pageable pageable);
    Page<Notification> findByNoticication(Member user,Pageable pageable);
    Page<Note> findByNote(Member user, Pageable pageable);
}
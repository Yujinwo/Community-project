package com.example.community.repository.querydsl;

import com.example.community.entity.*;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface UserRepositoryCustom {
    Page<Article> findByArticlelist(Long lastId, Pageable pageable);
    Page<Article> findByTitleOrContentContaining(Long lastId,String query, Pageable pageable);
    Article findByArticleAndMemberlist(Long Id);
    Page<Comment> findByCommentlist(Long boardId, Pageable pageable);
    Page<Notification> findByNoticication(Member user,Pageable pageable);
    Page<Note> findByNote(Member user, Pageable pageable);
}
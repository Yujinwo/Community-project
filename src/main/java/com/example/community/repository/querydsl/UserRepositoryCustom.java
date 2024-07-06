package com.example.community.repository.querydsl;

import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import com.example.community.entity.Notification;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface UserRepositoryCustom {
    Page<Comment> findByCommentlist(Long boardId, Pageable pageable);

    Page<Article> findByArticlelist(Long lastId, Pageable pageable);

    Page<Article> findByTitleOrContentContaining(Long lastId,String query, Pageable pageable);
    Article findByArticleAndMemberlist(Long Id);

    Page<Notification> findByNoticication(Member user,Pageable pageable);
}
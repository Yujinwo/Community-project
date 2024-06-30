package com.example.community.repository.querydsl;

import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<Comment> findByCommentlist(Long boardId, Pageable pageable);

    Page<Article> findByArticlelist(Long lastId,Pageable pageable);

    Page<Article> findByTitleOrContentContaining(Long lastId,String query, Pageable pageable);
    Article findByArticleAndMemberlist(Long Id);
}
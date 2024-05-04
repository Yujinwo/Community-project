package com.example.community.repository.querydsl;

import com.example.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
    Page<Comment> findByCommentlist(Long boardId, Pageable pageable);
}
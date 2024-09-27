package com.example.community.repository.querydsl;

import com.example.community.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    // 글 조회
    Page<Article> findByArticlelist(Pageable pageable, String sort);
    // 내 글 조회
    Page<Article> findBymyArticlelist(Member user,Pageable pageable);
    // 글 검색 조회
    Page<Article> findByTitleOrContentContaining(String sort, String query, Pageable pageable, String search);
    // 글 태그 검색 조회
    Page<Tag> findByTagContaining(String sort, String query, Pageable pageable, Boolean tagsearch);
    // 글과 글 작성자 FecthJoin 조회
    Article findByArticleAndMemberlist(Long Id);
    // 댓글 조회
    Page<Comment> findByCommentlist(Long boardId, Pageable pageable);
    // 내 댓글 조회
    Page<Comment> findBymyCommentlist(Member user,Pageable pageable);
    // 알림 조회
    Page<Notification> findByNoticication(Member user,Pageable pageable);
    // 내 즐겨찾기 조회
    Page<Bookmark> findBymyBookmarklist(Member user,Pageable pageable);
    // 쪽지 조회
    Page<Note> findByNote(Member user, Pageable pageable);
}
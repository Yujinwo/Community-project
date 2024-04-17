package com.example.community.repository;

import com.example.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query("SELECT e FROM Comment e WHERE e.article.id = :boardId")
    Page<Comment> findByboardld(@Param("boardId") Long boardId, Pageable pageable);

}

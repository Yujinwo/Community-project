package com.example.community.repository;

import com.example.community.entity.BoardImage;
import com.example.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage,Long> {
    @Query("SELECT e FROM BoardImage e WHERE e.article.id = :boardId")
    List<BoardImage> findByboardId(@Param("boardId") Long boardId);
}

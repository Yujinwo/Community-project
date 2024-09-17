package com.example.community.repository;


import com.example.community.entity.Article;
import com.example.community.entity.Bookmark;
import com.example.community.repository.querydsl.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark,Long>, UserRepositoryCustom {


    @Query("select b FROM Bookmark b WHERE b.member.id = :memberId AND b.article.id = :articleId")
    Optional<Bookmark> findByMemberAndArticle(@Param("memberId") Long memberId, @Param("articleId") Long articleId);

    Optional<Bookmark> findByarticle(Article article);

}

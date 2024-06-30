package com.example.community.repository.querydsl.impl;

import com.example.community.entity.*;
import com.example.community.repository.querydsl.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<Comment> findByCommentlist(Long boardId, Pageable pageable) {
        QComment comment = QComment.comment;
        JPAQuery<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.article.id.eq(boardId))
                .orderBy(comment.commentnumber.asc(), comment.redepth.asc() , comment.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.article.id.eq(boardId));

        List<Comment> content = comments.fetch();
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }


    @Override
    public Article findByArticleAndMemberlist(Long Id) {
        QArticle article = QArticle.article;

        Article countarticle = jpaQueryFactory.selectFrom(article)
                .where(article.id.eq(Id))
                .join(article.member, QMember.member).fetchJoin()
                .fetchOne();

        return countarticle;

    }

    @Override
    public Page<Article> findByArticlelist(Long lastId, Pageable pageable) {
        QArticle article = QArticle.article;

        JPAQuery<Article> query = jpaQueryFactory.selectFrom(article)
                .where(lastId != null ? article.id.gt(lastId) : null)
                .orderBy(article.id.asc())
                .limit(pageable.getPageSize());

        List<Article> content = query.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(article.count())
                .from(article);

        long totalCount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }

    @Override
    public Page<Article> findByTitleOrContentContaining(Long lastId,String query, Pageable pageable) {
        QArticle article = QArticle.article;

        JPAQuery<Article> queryResult = jpaQueryFactory.selectFrom(article)
                .where(article.title.containsIgnoreCase(query)
                        .or(article.content.containsIgnoreCase(query)))
                .where(lastId != null ? article.id.gt(lastId) : null)
                .orderBy(article.id.asc())
                .limit(pageable.getPageSize());


        List<Article> content = queryResult.fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(article.count())
                .from(article)
                .where(article.title.containsIgnoreCase(query)
                        .or(article.content.containsIgnoreCase(query)));

        long totalCount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);
    }


}

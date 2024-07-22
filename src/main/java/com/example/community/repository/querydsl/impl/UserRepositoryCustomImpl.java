package com.example.community.repository.querydsl.impl;

import com.example.community.entity.*;
import com.example.community.repository.querydsl.UserRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.community.entity.QArticle.article;
import static com.example.community.entity.QTag.tag;

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
        long totalCount = countQuery.fetchOne();
        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }


    @Override
    public Article findByArticleAndMemberlist(Long Id) {
        Article countarticle = jpaQueryFactory.selectFrom(article)
                .where(article.id.eq(Id))
                .join(article.member, QMember.member).fetchJoin()
                .fetchOne();

        return countarticle;

    }

    @Override
    public Page<Notification> findByNoticication(Member user,Pageable pageable) {
        QNotification qNotification = QNotification.notification;
        QMember receiver = new QMember("receiver");

        JPAQuery<Notification> notifications = jpaQueryFactory.selectFrom(qNotification)
                .join(qNotification.receiver,receiver)
                .where(qNotification.receiver.eq(user))
                .orderBy(qNotification.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(qNotification.count())
                .from(qNotification)
                .where(qNotification.receiver.eq(user));

        List<Notification>  notificationlist = notifications.fetch();
        long totalCount = countQuery.fetchOne();


        return PageableExecutionUtils.getPage(notificationlist,pageable,() -> totalCount);

    }

    @Override
    public Page<Note> findByNote(Member user, Pageable pageable) {
        QNote note = QNote.note;

        JPAQuery<Note> notelistsQuery = jpaQueryFactory.selectFrom(note)
                .where(note.receiver.eq(user).and(note.createdDate.after(LocalDateTime.now().minusDays(90))));

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(note.count())
                .from(note)
                .where(note.receiver.eq(user).and(note.createdDate.after(LocalDateTime.now().minusDays(90))));


        List<Note> notelist = notelistsQuery.fetch();
        long totalCount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(notelist,pageable,() -> totalCount);

    }

    @Override
    public Page<Article> findByArticlelist(Long lastId, Pageable pageable) {
        JPAQuery<Article> query = jpaQueryFactory.selectFrom(article)
                .where(articleIdGt(lastId))
                .orderBy(article.id.asc())
                .limit(pageable.getPageSize());


        List<Article> content = query.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(article.count())
                .from(article)
                .where(lastId != null ? article.id.gt(lastId) : null);
        long totalCount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }

    @Override
    public Page<Article> findByTitleOrContentContaining(Long lastId, String query, Pageable pageable) {

            JPAQuery<Article> queryResult = jpaQueryFactory.selectFrom(article)
                    .where(titleOrcontentCt(query),articleIdGt(lastId))
                    .orderBy(article.id.asc())
                    .limit(pageable.getPageSize());

            List<Article> content = queryResult.fetch();
            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(article.count())
                    .from(article)
                    .where(titleOrcontentCt(query));

            long totalCount = countQuery.fetchOne();

            return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }
    @Override
    public Page<Tag> findByTagContaining(Long lastId, String query, Pageable pageable,Boolean tagsearch) {
            JPAQuery<Tag> queryResult = jpaQueryFactory.selectFrom(tag)
                    .where(tagcontentCt(query,tagsearch),articleIdGt(lastId))
                    .rightJoin(tag.article,article).fetchJoin()
                    .orderBy(article.id.asc())
                    .limit(pageable.getPageSize());

            List<Tag> content = queryResult.fetch();
            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(tag.count())
                    .from(tag)
                    .where(tagcontentCt(query,tagsearch));

            long totalCount = countQuery.fetchOne();

            return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }
    private BooleanExpression titleOrcontentCt(String query){
        return query != null ? article.title.containsIgnoreCase(query).or(article.content.containsIgnoreCase(query)) : null;
    }
    private BooleanExpression tagcontentCt(String query,Boolean tagsearch){
        return tagsearch ?  tag.content.containsIgnoreCase(query) : null;
    }
    private BooleanExpression articleIdGt(Long lastId) {
        return lastId != null ? article.id.gt(lastId) : null;
    }


}

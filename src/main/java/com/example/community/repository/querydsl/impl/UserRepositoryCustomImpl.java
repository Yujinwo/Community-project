package com.example.community.repository.querydsl.impl;

import com.example.community.entity.*;
import com.example.community.repository.querydsl.UserRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
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
import static com.example.community.entity.QBookmark.bookmark;
import static com.example.community.entity.QComment.comment;
import static com.example.community.entity.QTag.tag;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<Comment> findByCommentlist(Long boardId, Pageable pageable) {
        QComment comment = QComment.comment;
        JPAQuery<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.article.id.eq(boardId))
                .orderBy(comment.createdDate.asc(),comment.commentnumber.asc(),comment.commentorder.asc(),comment.redepth.asc())
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
    public Page<Comment> findBymyCommentlist(Member user, Pageable pageable) {
        JPAQuery<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.member.id.eq(user.getId()))
                .join(comment.article,article).fetchJoin()
                .limit(pageable.getPageSize());

        JPAQuery<Long> countQuery = jpaQueryFactory.select(comment.count())
                .from(comment)
                .where(comment.member.id.eq(user.getId()));

        List<Comment> commentList = comments.fetch();
        long totalcount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(commentList,pageable,() -> totalcount);

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
    public Page<Bookmark> findBymyBookmarklist(Member user, Pageable pageable) {
        JPAQuery<Bookmark> queryResult = jpaQueryFactory.selectFrom(bookmark)
                .where(bookmark.member.id.eq(user.getId()))
                .limit(pageable.getPageSize());
        List<Bookmark> content = queryResult.fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory.select(bookmark.count())
                .from(bookmark)
                .where(bookmark.member.id.eq(user.getId()));

        long totalCount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);
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
    public Page<Article> findByArticlelist(Pageable pageable, String sort) {
        JPAQuery<Article> query = jpaQueryFactory.selectFrom(article)
                .orderBy(orderData(sort))
                .limit(pageable.getPageSize());

        List<Article> content = query.fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(article.count())
                .from(article);
        long totalCount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }



    @Override
    public Page<Article> findBymyArticlelist(Member user, Pageable pageable) {
        JPAQuery<Article> queryResult = jpaQueryFactory.selectFrom(article)
                .where(article.member.id.eq(user.getId()))
                .limit(pageable.getPageSize());
        List<Article> content = queryResult.fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory.select(article.count())
                .from(article)
                .where(article.member.id.eq(user.getId()));

        long totalCount = countQuery.fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);
    }

    @Override
    public Page<Article> findByTitleOrContentContaining(String sort, String query, Pageable pageable, String search) {

            JPAQuery<Article> queryResult = jpaQueryFactory.selectFrom(article)
                    .where(titleOrcontentCt(query,search))
                    .orderBy(orderData(sort))
                    .limit(pageable.getPageSize());

            List<Article> content = queryResult.fetch();
            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(article.count())
                    .from(article)
                    .where(titleOrcontentCt(query,search));

            long totalCount = countQuery.fetchOne();

            return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }
    @Override
    public Page<Tag> findByTagContaining(String sort, String query, Pageable pageable, Boolean tagsearch) {
            JPAQuery<Tag> queryResult = jpaQueryFactory.selectFrom(tag)
                    .where(tagcontentCt(query,tagsearch))
                    .rightJoin(tag.article,article).fetchJoin()
                    .orderBy(orderData(sort))
                    .limit(pageable.getPageSize());

            List<Tag> content = queryResult.fetch();
            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(tag.count())
                    .from(tag)
                    .where(tagcontentCt(query,tagsearch));

            long totalCount = countQuery.fetchOne();

            return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }
    private OrderSpecifier<?> orderData(String sort) {
        if(sort.equals("newest"))
        {
            return article.createdDate.desc();
        }
        else if(sort.equals("latest")){
            return article.createdDate.asc();
        }
        else if(sort.equals("mostrecent")){
            return article.viewcount.desc();

        }
        else {
            return article.createdDate.asc();
        }
    }
    private BooleanExpression titleOrcontentCt(String query,String search){
        if(search.equals("title"))
        {
            return query != null ? article.title.containsIgnoreCase(query) : null;
        } else if (search.equals("content")) {
            return query != null ? (article.content.containsIgnoreCase(query)) : null;
        } else if (search.equals("titleAndcontent")) {
            return query != null ? article.title.containsIgnoreCase(query).or(article.content.containsIgnoreCase(query)) : null;
        } else {
            return query != null ? article.title.containsIgnoreCase(query).or(article.content.containsIgnoreCase(query)) : null;
        }
    }
    private BooleanExpression tagcontentCt(String query,Boolean tagsearch){
        return tagsearch ?  tag.content.containsIgnoreCase(query) : null;
    }
    private BooleanExpression articleIdGt(Long lastId) {
        return lastId != null ? article.id.gt(lastId) : null;
    }


}

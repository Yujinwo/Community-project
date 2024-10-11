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
import java.util.Optional;

import static com.example.community.entity.QArticle.article;
import static com.example.community.entity.QBookmark.bookmark;
import static com.example.community.entity.QComment.comment;
import static com.example.community.entity.QTag.tag;
import static com.example.community.entity.QNote.note;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Page<Comment> findByCommentlist(Long boardId, Pageable pageable) {
        QComment comment = QComment.comment;
        // 조회 쿼리
        JPAQuery<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.article.id.eq(boardId))
                .orderBy(comment.cNumber.asc(),comment.cOrder.asc(),comment.redepth.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        // Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.article.id.eq(boardId));
        // 댓글 조회
        List<Comment> content = comments.fetch();
        // Count 계산
        long totalCount = countQuery.fetchOne();
        // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략
        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }

    @Override
    public Page<Comment> findBymyCommentlist(Member user, Pageable pageable) {
        // 조회 쿼리
        JPAQuery<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.member.id.eq(user.getId()).and(comment.deleted.isFalse()))
                .join(comment.article,article).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        // Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory.select(comment.count())
                .from(comment)
                .where(comment.member.id.eq(user.getId()));
        // 댓글 조회
        List<Comment> commentList = comments.fetch();
        // Count 계산
        long totalcount = countQuery.fetchOne();
        // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략
        return PageableExecutionUtils.getPage(commentList,pageable,() -> totalcount);

    }


    @Override
    public Optional<Article> findByArticleAndMemberlist(Long Id) {
        // 조회 쿼리
        Article articles = jpaQueryFactory.selectFrom(article)
                .where(article.id.eq(Id))
                .join(article.member, QMember.member).fetchJoin()
                .fetchOne();

        return Optional.ofNullable(articles);

    }

    @Override
    public Page<Notification> findByNoticication(Member user,Pageable pageable) {
        // 엔티티 별칭 설정
        QNotification qNotification = QNotification.notification;
        QMember receiver = new QMember("receiver");
        // 조회 쿼리
        JPAQuery<Notification> notifications = jpaQueryFactory.selectFrom(qNotification)
                .join(qNotification.receiver,receiver)
                .where(qNotification.receiver.eq(user))
                .orderBy(qNotification.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        // Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(qNotification.count())
                .from(qNotification)
                .where(qNotification.receiver.eq(user));
        // 알림 조회
        List<Notification>  notificationlist = notifications.fetch();
        // Count 계산
        long totalCount = countQuery.fetchOne();
        // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략 
        return PageableExecutionUtils.getPage(notificationlist,pageable,() -> totalCount);

    }

    @Override
    public Page<Bookmark> findBymyBookmarklist(Member user, Pageable pageable) {
        // 조회 쿼리
        JPAQuery<Bookmark> queryResult = jpaQueryFactory.selectFrom(bookmark)
                .where(bookmark.member.id.eq(user.getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        // Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory.select(bookmark.count())
                .from(bookmark)
                .where(bookmark.member.id.eq(user.getId()));
        // 즐겨찾기 조회
        List<Bookmark> content = queryResult.fetch();
        // Count 계산
        long totalCount = countQuery.fetchOne();
        // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략 
        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);
    }

    @Override
    public Page<Note> findByNote(Member user, Pageable pageable) {
        // 엔티티 별칭 설정
        QNote note = QNote.note;
        // 조회 쿼리
        JPAQuery<Note> notelistsQuery = jpaQueryFactory.selectFrom(note)
                .where(note.receiver.eq(user).and(note.createdDate.after(LocalDateTime.now().minusDays(90))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        // Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(note.count())
                .from(note)
                .where(note.receiver.eq(user).and(note.createdDate.after(LocalDateTime.now().minusDays(90))));
        // 즐겨찾기 조회
        List<Note> notelist = notelistsQuery.fetch();
        // Count 계산
        long totalCount = countQuery.fetchOne();
        // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략 
        return PageableExecutionUtils.getPage(notelist,pageable,() -> totalCount);

    }

    @Override
    public List<Note> findNotesByIds(Member user, List<Long> selectIds) {

        JPAQuery<Note> query = jpaQueryFactory.selectFrom(note)
                .where(note.id.in(selectIds));
        return query.fetch();
    }

    @Override
    public Page<Article> findByArticlelist(Pageable pageable, String sort) {
        // 조회 쿼리
        JPAQuery<Article> query = jpaQueryFactory.selectFrom(article)
                .orderBy(orderData(sort))
                .join(article.member,QMember.member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(article.count())
                .from(article);
        // 글 조회
        List<Article> content = query.fetch();
        // Count 계산
        long totalCount = countQuery.fetchOne();
        // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략 
        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }



    @Override
    public Page<Article> findBymyArticlelist(Member user, Pageable pageable) {
        // 조회 쿼리
        JPAQuery<Article> queryResult = jpaQueryFactory.selectFrom(article)
                .where(article.member.id.eq(user.getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        // Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory.select(article.count())
                .from(article)
                .where(article.member.id.eq(user.getId()));
        // 글 조회
        List<Article> content = queryResult.fetch();
        // Count 계산
        long totalCount = countQuery.fetchOne();
        // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략 
        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);
    }

    @Override
    public Page<Article> findByTitleOrContentContaining(String sort, String query, Pageable pageable, String search) {
            // 조회 쿼리
            JPAQuery<Article> queryResult = jpaQueryFactory.selectFrom(article)
                    .where(titleOrcontentCt(query,search))
                    .orderBy(orderData(sort))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
            // Count 쿼리
            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(article.count())
                    .from(article)
                    .where(titleOrcontentCt(query,search));
            // 글 검색 조회
            List<Article> content = queryResult.fetch();
            // Count 계산
            long totalCount = countQuery.fetchOne();
            // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략 
            return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }
    @Override
    public Page<Tag> findByTagContaining(String sort, String query, Pageable pageable, Boolean tagsearch) {
            // 조회 쿼리
            JPAQuery<Tag> queryResult = jpaQueryFactory.selectFrom(tag)
                    .where(tagcontentCt(query,tagsearch))
                    .rightJoin(tag.article,article).fetchJoin()
                    .orderBy(orderData(sort))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
            // Count 쿼리
            JPAQuery<Long> countQuery = jpaQueryFactory
                    .select(tag.count())
                    .from(tag)
                    .where(tagcontentCt(query,tagsearch));
            // 글 태그 검색 조회
            List<Tag> content = queryResult.fetch();
            // Count 계산
            long totalCount = countQuery.fetchOne();
            // 남은 데이터 수가 페이지 크기보다 적으면 totalcount 계산 생략
            return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);

    }
    // 글 정렬 조건 동적 쿼리 
    private OrderSpecifier<?> orderData(String sort) {
        // 최신 순
        if(sort.equals("newest"))
        {
            return article.createdDate.desc();
        }
        // 오래된 순
        else if(sort.equals("latest")){
            return article.createdDate.asc();
        }
        // 조회수 순
        else if(sort.equals("mostrecent")){
            return article.viewCount.desc();
        }
        else {
            return article.createdDate.asc();
        }
    }
    // 글 검색 조건 동적 쿼리
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
    // 글 태그 검색 조건 동적 쿼리
    private BooleanExpression tagcontentCt(String query,Boolean tagsearch){
        return tagsearch ?  tag.content.eq(query) : null;
    }

}

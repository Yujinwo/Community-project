package com.example.community.service;

import com.example.community.domain.member.Role;
import com.example.community.dto.*;
import com.example.community.entity.*;
import com.example.community.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    EntityManager em;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("글 조회")
    void index() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        articleRepository.save(Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build());
        em.flush();
        em.clear();
        //when
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ArticleindexResponseDto> articleDtos = articleRepository.findByArticlelist(pageRequest, "newest").map(article -> ArticleindexResponseDto.builder().article(article).build());
        //then
        assertEquals(articleDtos.getTotalElements(),1);
    }

    @Test
    @DisplayName("글 검색 키워드 조회")
    void searchArticles() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        Article savedArticle = articleRepository.save(Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build());
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        //when
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ArticleindexResponseDto> articleDtos = articleRepository.findByTagContaining("newest","태그1", pageRequest,true).map(tag -> ArticleindexResponseDto.builder().article(tag.getArticle()).build());
        assertEquals(articleDtos.getTotalElements(),1);
    }

    @Test
    @DisplayName("글 작성")
    void write() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        //when
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        //then
        assertEquals(savedArticle,article);
    }

    @Test
    @DisplayName("글 수정")
    void update() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        //when
        Optional<Article> articleOptional = articleRepository.findById(savedArticle.getId());
        Iterable<Long> iterable = new ArrayList<>();
        if(articleOptional.isPresent()){
            articleOptional.get().changeTitleandContent("제목","내용");
            for (Tag tag : articleOptional.get().getTags()){
                ((ArrayList<Long>) iterable).add(tag.getId());
            }
            tagRepository.deleteAllByIdInBatch(iterable);

        }
        em.flush();
        em.clear();
        //then
        Optional<Article> updatedArticle = articleRepository.findById(savedArticle.getId());
        assertEquals(updatedArticle.get().getTitle(),"제목");
        assertEquals(updatedArticle.get().getContent(),"내용");
        assertEquals(updatedArticle.get().getTags().size(),0);
    }

    @Test
    @DisplayName("글 삭제")
    void delete() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        //when
        Optional<Article> findArticle = articleRepository.findById(savedArticle.getId());
        articleRepository.delete(findArticle.get());
        em.flush();
        em.clear();
        Optional<Article> deletedArticle = articleRepository.findById(savedArticle.getId());

        //then
        assertEquals(deletedArticle.isPresent(),false);


    }

    @Test
    @DisplayName("댓글 작성")
    void commentwrite() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        Comment comment = Comment.builder().
                content("내용").
                member(member).
                article(article).
                parent(null).
                cNumber(0L).
                cOrder(0L).
                redepth(0).
                deleted(false)
                .build();
        //when
        savedArticle.chagneCommentCount(savedArticle.getCommentCount() + 1);
        Comment savedComment =  commentRepository.save(comment);
        em.flush();
        em.clear();

        //then
        assertEquals(savedComment,comment);
        assertEquals(savedComment.getContent(),"내용");

    }

    @Test
    @DisplayName("댓글 조회")
    void findCommentid() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        Comment comment = Comment.builder().
                content("내용").
                member(member).
                article(savedArticle).
                parent(null).
                cNumber(0L).
                cOrder(0L).
                redepth(0).
                deleted(false)
                .build();
        savedArticle.chagneCommentCount(savedArticle.getCommentCount() + 1);
        Comment savedComment =  commentRepository.save(comment);
        em.flush();
        em.clear();

        //when
        Page<CommentResponseDto> findcomments = commentRepository.findByCommentlist(savedArticle.getId(),PageRequest.of(0, 10)).map(comments -> comments.toDto());
        assertEquals(findcomments.getTotalElements(),1);
    }

    @Test
    @DisplayName("조회수 올리기")
    void viewcount() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        //when
        Optional<Article> findarticle = articleRepository.findByArticleAndMemberlist(savedArticle.getId());
        findarticle.get().updatecount();
        em.flush();
        em.clear();

        //then
        assertEquals(findarticle.get().getViewCount(),1);
    }

    @Test
    @DisplayName("댓글 삭제")
    void commentdelete() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        Comment comment = Comment.builder().
                content("내용").
                member(member).
                article(article).
                parent(null).
                cNumber(0L).
                cOrder(0L).
                redepth(0).
                deleted(false)
                .build();
        savedArticle.chagneCommentCount(savedArticle.getCommentCount() + 1);
        Comment savedComment =  commentRepository.save(comment);
        em.flush();
        em.clear();
        //when
        commentRepository.delete(savedComment);
        Optional<Comment> commentOptional = commentRepository.findById(savedComment.getId());

        //then
        assertEquals(commentOptional.isPresent(),false);
    }

    @Test
    @DisplayName("작성한 글 조회")
    void findMyArticleList() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }

        em.flush();
        em.clear();
        //when
        Page<MyArticleResponseDto> bymyArticlelist = articleRepository.findBymyArticlelist(savedMember, PageRequest.of(0, 10)).map(m-> m.changeMyArticleResponseDto());
        //then
        assertEquals(bymyArticlelist.getTotalElements(),1);
    }

    @Test
    @DisplayName("작성한 댓글 조회")
    void findMyCommentList() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        Comment comment = Comment.builder().
                content("내용").
                member(member).
                article(article).
                parent(null).
                cNumber(0L).
                cOrder(0L).
                redepth(0).
                deleted(false)
                .build();
        savedArticle.chagneCommentCount(savedArticle.getCommentCount() + 1);
        Comment savedComment =  commentRepository.save(comment);
        em.flush();
        em.clear();
        //when
        Page<MyCommentResponseDto> bymyCommentlist = articleRepository.findBymyCommentlist(savedMember, PageRequest.of(0, 10)).map(m-> m.changeMyCommentResponseDto());
        //then
        assertEquals(bymyCommentlist.getTotalElements(),1);
    }

    @Test
    @DisplayName("즐겨찾기 조회")
    void findMyBookmarkList() {
        //given
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        Bookmark bookmark = Bookmark.builder().member(savedMember).article(savedArticle).build();
        bookmarkRepository.save(bookmark);
        em.flush();
        em.clear();
        //when
        Page<MyBookmarkResponseDto> bymyBookmarklist = bookmarkRepository.findBymyBookmarklist(savedMember, PageRequest.of(0,10)).map(m-> MyBookmarkResponseDto.builder().bookmark_id(m.getId()).article_id(m.getArticle().getId()).article_title(m.getArticle().getTitle()).build());
        //then
        assertEquals(bymyBookmarklist.getTotalElements(),1);

    }

    @Test
    @DisplayName("즐겨찾기 삭제")
    void setBookmark() {
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        Bookmark bookmark = Bookmark.builder().member(savedMember).article(savedArticle).build();
        Bookmark savebookmark = bookmarkRepository.save(bookmark);
        em.flush();
        em.clear();
        //when
        bookmarkRepository.delete(savebookmark);
        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findById(savebookmark.getId());

        //then
        assertEquals(bookmarkOptional.isPresent(),false);
    }

    @Test
    @DisplayName("즐겨찾기 현황 조회")
    void checkBookmark() {
        Member member = Member.builder().email("dbwlsdn0125").userpw("$2a$10$UeWyzwythf399jEl8XNXEezsbwskpaZR8HPm2V6V70WcHoygxDun2").usernick("테스트").role(Role.USER).noteblock(false).build();
        Member savedMember = memberRepository.save(member);
        List<String> tags = new ArrayList<>();
        tags.add("태그1");
        tags.add("태그2");
        Article article = Article.builder().title("테스트 제목").content("테스트 내용").member(savedMember).build();
        Article savedArticle = articleRepository.save(article);
        for (String tagname : tags) {
            if(!tagname.isBlank())
            {
                Tag tag = Tag.builder()
                        .content(tagname)
                        .article(savedArticle)
                        .build();
                tagRepository.save(tag);
            }

        }
        em.flush();
        em.clear();
        Bookmark bookmark = Bookmark.builder().member(savedMember).article(savedArticle).build();
        Bookmark savebookmark = bookmarkRepository.save(bookmark);
        em.flush();
        em.clear();
        //when
        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByMemberAndArticle(savedMember.getId(), savedArticle.getId());

        //then
        assertEquals(bookmarkOptional.isPresent(),true);

    }
}
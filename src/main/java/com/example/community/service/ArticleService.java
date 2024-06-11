package com.example.community.service;

import com.example.community.dto.*;
import com.example.community.entity.*;
import com.example.community.repository.ArticleRepository;
import com.example.community.repository.BoardImageRepository;
import com.example.community.repository.CommentRepository;
import com.example.community.util.AuthenticationUtil;
import com.example.community.util.CookieUtill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final BoardImageRepository boardImageRepository;
    private final S3Uploader s3Uploader;
    @Autowired
    public ArticleService(ArticleRepository articleRepository, CommentRepository commentRepository, BoardImageRepository boardImageRepository, S3Uploader s3Uploader) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.boardImageRepository = boardImageRepository;
        this.s3Uploader = s3Uploader;
    }

    // 글 리스트를 페이지 형태로 불러오기
    @Transactional
    public Page<ArticleResponseDto> index(Long lastId,Pageable pageable) {
        // page 위치에 있는 값은 0부터 시작한다.
        int page = pageable.getPageNumber() - 1;
        // 한페이지에 보여줄 글 개수
        int pageLimit = 3;
        // 페이지 형태로 글 불러오기
        Page<ArticleResponseDto> articleDtos = articleRepository.findByArticlelist(lastId,PageRequest.of(page, pageLimit)).map(Article::toDto);
        return articleDtos;
    }

    @Transactional
    public Page<Article> searchArticles(Long lastId, String query, Pageable pageable) {
        return articleRepository.findByTitleOrContentContaining(lastId,query, pageable);
    }

    // 글 저장
    @Transactional
    public void write(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        // dto에 Member Entity set
        articleRequestDto.setMember(member);
        // dto -> Article Entity생성 -> 글 저장
        Article savedArticle = articleRepository.save(articleRequestDto.toEntity());
        // 저장된 article Entity가 null일시 예외 상황 발생
        if (savedArticle == null) {
            throw new RuntimeException("게시글 저장에 실패했습니다.");
        }
        // 이미지가 첨부 되었으면
        if (files != null && !files.isEmpty()) {
            // 이미지를 하나씩 꺼낸다
            for (MultipartFile file : files) {
                try {
                    String fileName = s3Uploader.upload(file);
                    // 저장된 파일 디렉터리를 저장한다.
                    BoardImage image = BoardImage.builder()
                            .url(fileName)
                            .article(savedArticle)
                            .build();
                    boardImageRepository.save(image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }
    // 글 수정
    @Transactional
    public Article update(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {

        System.out.println(articleRequestDto.getImageUrls());
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(articleRequestDto.getId());
        // 기존 저장된 내용과 수정 요청한 내용이 같을 시 return 한다
        if (article.getTitle().equals(articleRequestDto.getTitle()) && article.getContent().equals(articleRequestDto.getContent())){
            return null;
        }
        // 글에 저장된 이미지 불러오기
        List<BoardImage> boardImage = boardImageRepository.findByboardId(articleRequestDto.getId());
        // 인증된 유저와 글 작성한 유저 비교 || 요청한 글이 데이터베이스에 없을시
        if(member.getId() != article.getMember().getId() || article == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 게시글 조회에 실패했습니다.");
        }
        // 요청한 제목으로 글 Entity 정보를 수정한다
        article.setTitle(articleRequestDto.getTitle());
        // 요청한 내용으로 글 Entity 정보를 수정한다
        article.setContent(articleRequestDto.getContent());
        // 수정된 article Entity로 글을 수정한다.
        Article updatedArticle = articleRepository.save(article);
        // 수정된 article Entity가 null일시 예외 상황 발생
        if (updatedArticle == null) {
            throw new RuntimeException("게시글 수정에 실패했습니다.");
        }
        // 글 이미지가 없었는데 첨부가 되었으면
        if (articleRequestDto.getImageUrls().isEmpty() && boardImage == null && files != null){
            // 이미지가 첨부 되었으면
            if (files != null && !files.isEmpty()) {
                // 이미지를 하나씩 꺼낸다
                for (MultipartFile file : files) {

                    try {
                        String fileName = s3Uploader.upload(file);
                        // 저장된 파일 디렉터리를 저장한다.
                        BoardImage image = BoardImage.builder()
                                .url(fileName)
                                .article(updatedArticle)
                                .build();
                        boardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        // 글 이미지를 다 삭제하고 이미지 첨부 파일이 있으면
        if (articleRequestDto.getImageUrls().isEmpty() && boardImage != null && files != null){
            // 이전에 저장되어 있던 이미지 파일이 존재할 시 파일과 데이터를 삭제한다.
            for (BoardImage image : boardImage) {
                s3Uploader.deleteFile(image.getUrl());
                boardImageRepository.delete(image);
            }

            // 수정된 이미지 첨부 파일로 저장한다
            if (files != null && !files.isEmpty()) {
                // 이미지를 하나씩 꺼낸다
                for (MultipartFile file : files) {

                    try {
                        String fileName = s3Uploader.upload(file);
                        // 저장된 파일 디렉터리를 저장한다.
                        BoardImage image = BoardImage.builder()
                                .url(fileName)
                                .article(updatedArticle)
                                .build();
                        boardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        // 글 이미지를 다 삭제하고 이미지 첨부 파일이 없으면
        if (articleRequestDto.getImageUrls().isEmpty() && boardImage != null && files == null){
            // 이전에 저장되어 있던 이미지 파일이 존재할 시 파일과 데이터를 삭제한다.
            for (BoardImage image : boardImage) {
                s3Uploader.deleteFile(image.getUrl());
                boardImageRepository.delete(image);
            }

        }

        // 글 이미지를 하나만 삭제하고 이미지 첨부 파일이 있으면
        if (!articleRequestDto.getImageUrls().isEmpty() && boardImage != null && files != null){
            // 수정된 이미지 첨부 파일로 저장한다
            if (files != null && !files.isEmpty()) {
                // 이미지를 하나씩 꺼낸다
                for (MultipartFile file : files) {

                    try {
                        String fileName = s3Uploader.upload(file);
                        // 저장된 파일 디렉터리를 저장한다.
                        BoardImage image = BoardImage.builder()
                                .url(fileName)
                                .article(updatedArticle)
                                .build();
                        boardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return article;
    }
    // 글 삭제
    @Transactional
    public void delete(ArticleRequestDto articleRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(articleRequestDto.getId());
        // 인증된 유저와 글 작성한 유저 비교 || 요청한 글이 데이터베이스에 없을시
        if(member.getId() != article.getMember().getId() || article == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 게시글 조회에 실패했습니다.");
        }
        // 글 삭제한다
        articleRepository.delete(article);
    }

    // 댓글 작성
    @Transactional
    public void commentwrite(CommentRequestDto commentRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(commentRequestDto.getBoardid());
        // 글 댓글 개수를 증가시킨다
        article.setCommentcount(article.getCommentcount() + 1);
        Article commentcount = articleRepository.save(article);
        // 댓글 dto에 댓글 개수 증가된 Article Entity를 저장한다
        commentRequestDto.setArticle(commentcount);
        // 댓글 dto에 Member Entity 저장한다
        commentRequestDto.setMember(member);
        // 댓글 넘버를 저장한다.
        commentRequestDto.setCommentnumber(article.getCommentcount());
        // 댓글을 저장한다
        Comment comment = commentRepository.save(commentRequestDto.toEntity());
        // 저장한 댓글 Entity가 null 일시 예외 상황 발생
        if (comment == null) {
            throw new RuntimeException("댓글 작성에 실패했습니다.");
        }
    }
    // 댓글 리스트를 페이지 형태로 불러오기
    @Transactional
    public Page<CommentResponseDto> findCommentid(Long id, Pageable pageable) {
        // page 위치에 있는 값은 0부터 시작한다.
        int page = pageable.getPageNumber() - 1;
        // 한페이지에 보여줄 글 개수
        int pageLimit = 10;

        // 페이지 형태로 댓글 불러오기
        Page<CommentResponseDto> comment = commentRepository.findByCommentlist(id,PageRequest.of(page, pageLimit)).map(comments -> comments.toDto());
        return comment;
    }

    // 글 조회수 올리기
    @Transactional
    public Article viewcount(Long id, HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에 articleid 파라미터값 불러오기
        String articleidCookie = CookieUtill.getCookieValue(request,"articleid");
        // 글 Article Entity 불러오기
        Article article = this.findById(id);
        // Article Entity가 null 일시 예외 상황 발생
        if(article == null)
        {
            throw new RuntimeException("글 조회에 실패했습니다.");
        }
        // 쿠키카 Null일시 || 쿠키에 저장된 글 Id값과 조회한 글 Id값이 다를시 조회수를 올린다
        if(articleidCookie == null || !articleidCookie.equals(String.valueOf(id))){
            // Article Entity viewcount를 1 증가시킨다
            article.setViewcount(article.getViewcount() + 1);
            // 조회수를 올린 Article Entity로 글을 수정한다.
            Article countedArticle = articleRepository.save(article);
            // 쿠키에 조회수 올린 글 Id값을 저장한다. 유효기간은 1시간으로 설정한다.
            CookieUtill.addCookie(response,"articleid",String.valueOf(id),3600);
            return countedArticle;
        }
        return article;
    }

    // id로 Article Entity(글) 불러오기
    @Transactional
    public Article findById(Long id) {
        // 글 Article Entity 불러오기
        Article article = articleRepository.findById(id).orElse(null);
        // Article Entity가 null 일시 예외 상황 발생
        if(article == null)
        {
            throw new RuntimeException("글 조회에 실패했습니다.");
        }
        return article;
    }
    // 댓글 삭제
    @Transactional
    public void commentdelete(CommentRequestDto commentRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        // 댓글 Comment Entity 불러오기
        Comment comment = commentRepository.findById(commentRequestDto.getId()).orElse(null);
        // 인증된 유저와 댓글 작성한 유저 비교 || 요청한 댓글이 데이터베이스에 없을시
        if(member.getId() != comment.getMember().getId() || comment == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 댓글 조회에 실패했습니다.");
        }

        // 글 댓글 개수를 감소시킨다
        Article article = this.findById(comment.getArticle().getId());
        article.setCommentcount(article.getCommentcount() - 1);
        articleRepository.save(article);

        // 대댓글이 없을 시 댓글을 삭제한다.
        if(comment.getParent() == null && comment.getChild().size() == 0) {
            commentRepository.delete(comment);
        }
        else if(comment.getParent() != null && comment.getChild().size() == 0) {
            commentRepository.delete(comment);
        }
        // 대댓글이 있을 시 Comment Deleted를 true로 바꾼다
        else {
            comment.setDeleted(true);
            // Deleted true된 Entity로 수정한다
            commentRepository.save(comment);
        }

    }
    // 대댓글 작성
    @Transactional
    public void replywrite(CommentRequestDto replyRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(replyRequestDto.getBoardid());
        // 글 댓글 개수를 증가시킨다
        article.setCommentcount(article.getCommentcount() + 1);
        Article commentcount = articleRepository.save(article);
        // 댓글 dto에 댓글 개수 증가된 Article Entity를 저장한다
        replyRequestDto.setArticle(commentcount);
        // 댓글 dto에 Member Entity 저장한다
        replyRequestDto.setMember(member);
        // 부모 댓글 Comment Entity를 가져온다.
        Comment parentcomment = commentRepository.findById(replyRequestDto.getParentid()).orElse(null);
        // 부모 댓글 Comment Entity로 설정한다.
        replyRequestDto.setParent(parentcomment);
        // 댓글 넘버를 저장한다.
        replyRequestDto.setCommentnumber(parentcomment.getCommentnumber());
        // 댓글 깊이를 저장한다.
        replyRequestDto.setRedepth(parentcomment.getRedepth() + 1);
        // 댓글을 저장한다
        Comment comment = commentRepository.save(replyRequestDto.toEntity());
        // 저장한 댓글 Entity가 null 일시 예외 상황 발생
        if (comment == null) {
            throw new RuntimeException("댓글 작성에 실패했습니다.");
        }

    }

    @Transactional
    public void  replydelete(CommentRequestDto replyRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        // 대댓글 Reply Entity 불러오기
        Comment reply = commentRepository.findById(replyRequestDto.getId()).orElse(null);
        // 인증된 유저와 댓글 작성한 유저 비교 || 요청한 대댓글이 데이터베이스에 없을시
        if(member.getId() != reply.getMember().getId() || reply == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 댓글 조회에 실패했습니다.");
        }
        // 글 댓글 개수를 감소시킨다
        Article article = this.findById(reply.getArticle().getId());
        article.setCommentcount(article.getCommentcount() - 1);
        articleRepository.save(article);
        // 대댓글을 삭제한다
        commentRepository.delete(reply);
    }
}

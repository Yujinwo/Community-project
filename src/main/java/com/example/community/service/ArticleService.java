package com.example.community.service;

import com.example.community.config.CustomUserDetails;
import com.example.community.dto.*;
import com.example.community.entity.*;
import com.example.community.repository.ArticleRepository;
import com.example.community.repository.BoardImageRepository;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.ReplyRepositoty;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Service
public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BoardImageRepository boardImageRepository;

    @Autowired
    ReplyRepositoty replyRepositoty;

    @Transactional
    public Page<ArticleResponseDto> index(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3; // 한페이지에 보여줄 글 개수
        Page<ArticleResponseDto> articleDtos = articleRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.ASC, "id"))).map(Article::toDto);
        return articleDtos;
    }
    @Transactional
    public void write(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {


        Member member = AuthenticationUtil.getCurrentMember();
        articleRequestDto.setMember(member);
        Article savedArticle = articleRepository.save(articleRequestDto.toEntity());
        if (savedArticle == null) {
            throw new RuntimeException("게시글 저장에 실패했습니다.");
        }

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {

                String uploadDir = new File("src/main/resources/static/images").getAbsolutePath();
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                UUID uuid = UUID.randomUUID();
                String imageFileName = uuid + "_" + file.getOriginalFilename();
                String filePath = uploadDir + File.separator + imageFileName;
                File destinationFile = new File(filePath);


                try {
                    file.transferTo(destinationFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                BoardImage image = BoardImage.builder()
                        .url("/images/" + imageFileName)
                        .article(savedArticle)
                        .build();

                boardImageRepository.save(image);
            }
        }

    }

    @Transactional
    public void update(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {
        Member member = AuthenticationUtil.getCurrentMember();

        Article article = this.findById(articleRequestDto.getId());
        BoardImage boardImage = boardImageRepository.findByboardId(articleRequestDto.getId()).orElse(null);
        if(member.getId() != article.getMember().getId() || article == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 게시글 조회에 실패했습니다.");
        }
        article.setTitle(articleRequestDto.getTitle());
        article.setContent(articleRequestDto.getContent());
        Article updatedArticle = articleRepository.save(article);
        if (updatedArticle == null) {
            throw new RuntimeException("게시글 수정에 실패했습니다.");
        }

        if (boardImage == null && files != null){
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String uploadDir = new File("src/main/resources/static/images").getAbsolutePath();

                    File uploadPath = new File(uploadDir);
                    if (!uploadPath.exists()) {
                        uploadPath.mkdirs();
                    }

                    UUID uuid = UUID.randomUUID();
                    String imageFileName = uuid + "_" + file.getOriginalFilename();
                    String filePath = uploadDir + File.separator + imageFileName;
                    File destinationFile = new File(filePath);

                    try {
                        file.transferTo(destinationFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    BoardImage image = BoardImage.builder()
                            .url("/images/" + imageFileName)
                            .article(updatedArticle)
                            .build();

                    boardImageRepository.save(image);
                }
            }
        }

        if (boardImage != null && files != null){
            File fileToDelete = new File("src/main/resources/static" + boardImage.getUrl()).getAbsoluteFile();
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }

            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    String uploadDir = new File("src/main/resources/static/images").getAbsolutePath();

                    File uploadPath = new File(uploadDir);
                    if (!uploadPath.exists()) {
                        uploadPath.mkdirs();
                    }

                    UUID uuid = UUID.randomUUID();
                    String imageFileName = uuid + "_" + file.getOriginalFilename();
                    String filePath = uploadDir + File.separator + imageFileName;
                    File destinationFile = new File(filePath);

                    try {
                        file.transferTo(destinationFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    BoardImage image = BoardImage.builder()
                            .id(boardImage.getId())
                            .url("/images/" + imageFileName)
                            .article(updatedArticle)
                            .build();

                    boardImageRepository.save(image);
                }
            }
        }
    }

    @Transactional
    public void delete(ArticleRequestDto articleRequestDto) {
        Member member = AuthenticationUtil.getCurrentMember();
        Article article = this.findById(articleRequestDto.getId());
        if(member.getId() != article.getMember().getId() || article == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 게시글 조회에 실패했습니다.");
        }
        articleRepository.delete(article);
    }


    @Transactional
    public void commentwrite(CommentRequestDto commentRequestDto) {
        Member member = AuthenticationUtil.getCurrentMember();
        Article article = this.findById(commentRequestDto.getBoardid());
        commentRequestDto.setArticle(article);
        commentRequestDto.setMember(member);
        Comment comment = commentRepository.save(commentRequestDto.toEntity());
        if (comment == null) {
            throw new RuntimeException("댓글 작성에 실패했습니다.");
        }

    }

    @Transactional
    public Page<CommentResponseDto> findCommentid(Long id, Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3; // 한페이지에 보여줄 글 개수
        Page<CommentResponseDto> comment = commentRepository.findByboardld(id,PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.ASC, "id"))).map(comments -> comments.toDto());
        return comment;
    }


    @Transactional
    public Article viewcount(Long id, HttpServletRequest request, HttpServletResponse response) {

        String articleidCookie = CookieUtill.getCookieValue(request,"articleid");
        Article article = this.findById(id);
        if(article == null)
        {
            throw new RuntimeException("글 조회에 실패했습니다.");
        }
        if(articleidCookie == null || !articleidCookie.equals(String.valueOf(id))){
            article.setViewcount(article.getViewcount() + 1);
            Article countedArticle = articleRepository.save(article);
            CookieUtill.addCookie(response,"articleid",String.valueOf(id),3600);
            return countedArticle;
        }
        return article;
    }
    @Transactional
    public Article findById(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if(article == null)
        {
            throw new RuntimeException("글 조회에 실패했습니다.");
        }
        return article;
    }
    @Transactional
    public void commentdelete(CommentRequestDto commentRequestDto) {
        Member member = AuthenticationUtil.getCurrentMember();
        Comment comment = commentRepository.findById(commentRequestDto.getId()).orElse(null);
        if(member.getId() != comment.getMember().getId() || comment == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 댓글 조회에 실패했습니다.");
        }
        if(comment.getChild().size() == 0) {
            commentRepository.delete(comment);
        }
        else {
            comment.setDeleted(true);
            commentRepository.save(comment);
        }

    }
    @Transactional
    public void replywrite(ReplyRequestDto replyRequestDto) {
        Member member = AuthenticationUtil.getCurrentMember();
        Comment comment = commentRepository.findById(replyRequestDto.getCommentid()).orElse(null);
        if(member.getId() != comment.getMember().getId() || comment == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 댓글 조회에 실패했습니다.");
        }
        replyRequestDto.setMember(member);
        replyRequestDto.setComment(comment);
        replyRepositoty.save(replyRequestDto.toEntity());
    }

    @Transactional
    public void  replydelete(ReplyRequestDto replyRequestDto) {
        Member member = AuthenticationUtil.getCurrentMember();
        Reply reply = replyRepositoty.findById(replyRequestDto.getId()).orElse(null);
        if(member.getId() != reply.getMember().getId() || reply == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 댓글 조회에 실패했습니다.");
        }

        replyRepositoty.delete(reply);


    }
}

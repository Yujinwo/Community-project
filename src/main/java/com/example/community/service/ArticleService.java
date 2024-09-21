package com.example.community.service;

import com.example.community.dto.*;
import com.example.community.entity.*;
import com.example.community.repository.*;
import com.example.community.util.AuthenticationUtil;
import com.example.community.util.CookieUtill;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final BoardImageRepository boardImageRepository;
    private final BookmarkRepository bookmarkRepository;
    private final S3Uploader s3Uploader;
    private final NotificationService notificationService;
    private final EntityManager em;
    private final AuthenticationUtil authenticationUtil;
    private final TagRepository tagRepository;

    // 글 리스트를 페이지 형태로 불러오기
    @Transactional(readOnly = true)
    public ArticleindexResultDto index(Pageable pageable, String sort) {
        // page 위치에 있는 값은 0부터 시작한다.
        int page = pageable.getPageNumber() - 1;
        PageRequest pageRequest = PageRequest.of(page, pageable.getPageSize());
        // 페이지 형태로 글 불러오기
        Page<ArticleindexResponseDto> articleDtos = articleRepository.findByArticlelist(pageRequest, sort).map(article -> ArticleindexResponseDto.builder().article(article).build());
        return ArticleindexResultDto.createArticleindexResultDto(articleDtos);
    }

    @Transactional(readOnly = true)
    public ArticleindexResultDto searchArticles(String query, Pageable pageable, Boolean tagsearch, String sort, String search) {
        int page = pageable.getPageNumber() - 1;
        PageRequest pageRequest = PageRequest.of(page, pageable.getPageSize());

        if(tagsearch)
        {
            Page<ArticleindexResponseDto> articleDtos = articleRepository.findByTagContaining(sort,query, pageRequest,tagsearch).map(tag -> ArticleindexResponseDto.builder().article(tag.getArticle()).build());
            return ArticleindexResultDto.createArticleindexResultDto(articleDtos);
        }
        else {
            Page<ArticleindexResponseDto> articleDtos = articleRepository.findByTitleOrContentContaining(sort,query, pageRequest,search).map(article -> ArticleindexResponseDto.builder().article(article).build());
            return ArticleindexResultDto.createArticleindexResultDto(articleDtos);
        }

    }

    // 글 저장
    @Transactional
    public void write(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();

        // dto에 Member Entity set
        ArticleSaveDto articleSaveDto = new ArticleSaveDto();
        articleSaveDto.changeSaveArticleData(articleRequestDto.getTitle(),articleRequestDto.getContent(),member);

        // dto -> Article Entity생성 -> 글 저장
        Article savedArticle = articleRepository.save(articleSaveDto.createArticleEntity());
        // 저장된 article Entity가 null일시 예외 상황 발생
        if (savedArticle == null) {
            throw new RuntimeException("게시글 저장에 실패했습니다.");
        }
        // 이미지가 첨부 되었으면
        if (files != null && !files.isEmpty()) {
            // 이미지를 하나씩 꺼낸다
            for (MultipartFile file : files) {
                try {
                    if(!file.isEmpty())
                    {
                        String fileName = s3Uploader.upload(file);
                        // 저장된 파일 디렉터리를 저장한다.
                        BoardImage image = BoardImage.builder()
                                .url(fileName)
                                .article(savedArticle)
                                .build();
                        boardImageRepository.save(image);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        if(articleRequestDto.getTags()!= null){
            for (String tagname : articleRequestDto.getTags()) {
                if(!tagname.isBlank())
                {
                    Tag tag = Tag.builder()
                            .content(tagname)
                            .article(savedArticle)
                            .build();
                    tagRepository.save(tag);
                }

            }
        }

    }
    // 글 수정
    @Transactional
    public Article update(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {

        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(articleRequestDto.getId());
        // 기존 저장된 내용과 수정 요청한 내용이 같을 시 return 한다
        if (article.getTitle().equals(articleRequestDto.getTitle()) && article.getContent().equals(articleRequestDto.getContent())){
            return null;
        }
        // 인증된 유저와 글 작성한 유저 비교 || 요청한 글이 데이터베이스에 없을시
        if(member.getId() != article.getMember().getId() || article == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 게시글 조회에 실패했습니다.");
        }
        // 요청한 제목으로 글 Entity 정보를 수정한다
        article.changeTitleandContent(articleRequestDto.getTitle(),articleRequestDto.getContent());
        em.flush();
        // 글 이미지가 없었는데 첨부가 되었으면
        if (articleRequestDto.getImageUrls().isEmpty() && files != null){
            // 이미지가 첨부 되었으면
            if (files != null && !files.isEmpty()) {
                // 이미지를 하나씩 꺼낸다
                for (MultipartFile file : files) {

                    try {
                        String fileName = s3Uploader.upload(file);
                        // 저장된 파일 디렉터리를 저장한다.
                        BoardImage image = BoardImage.builder()
                                .url(fileName)
                                .article(article)
                                .build();
                        boardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        // 글 이미지를 다 삭제하고 이미지 첨부 파일이 있으면
        if (articleRequestDto.getImageUrls().isEmpty() && files != null){
            // 이전에 저장되어 있던 이미지 파일이 존재할 시 파일과 데이터를 삭제한다.
            // 글에 저장된 이미지 불러오기
            List<BoardImage> boardImage = boardImageRepository.findByboardId(articleRequestDto.getId());
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
                                .article(article)
                                .build();
                        boardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        // 글 이미지를 다 삭제하고 이미지 첨부 파일이 없으면
        if (articleRequestDto.getImageUrls().isEmpty() && files == null){
            // 이전에 저장되어 있던 이미지 파일이 존재할 시 파일과 데이터를 삭제한다.
            // 글에 저장된 이미지 불러오기
            List<BoardImage> boardImage = boardImageRepository.findByboardId(articleRequestDto.getId());
            for (BoardImage image : boardImage) {
                s3Uploader.deleteFile(image.getUrl());
                boardImageRepository.delete(image);
            }

        }

        // 글 이미지를 하나만 삭제하고 이미지 첨부 파일이 있으면
        if (!articleRequestDto.getImageUrls().isEmpty() && files != null){
            // 수정된 이미지 첨부 파일로 저장한다
            if (files != null && !files.isEmpty()) {
                // 이미지를 하나씩 꺼낸다
                for (MultipartFile file : files) {

                    try {
                        String fileName = s3Uploader.upload(file);
                        // 저장된 파일 디렉터리를 저장한다.
                        BoardImage image = BoardImage.builder()
                                .url(fileName)
                                .article(article)
                                .build();
                        boardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        // 태그 리스트가 없을 시 저장했던 태그 모두 삭제
        if(articleRequestDto.getTags().isEmpty() ) {
            Iterable<Long> iterable = new ArrayList<>();

            Optional<Article> articleOptional = articleRepository.findById(articleRequestDto.getId());
            if(articleOptional.isPresent()){

                for (Tag tag : articleOptional.get().getTags()){
                    ((ArrayList<Long>) iterable).add(tag.getId());
                }
                tagRepository.deleteAllByIdInBatch(iterable);
            }
        }
        else {
            Iterable<Long> iterable = new ArrayList<>();

            Optional<Article> articleOptional = articleRepository.findById(articleRequestDto.getId());
            if(articleOptional.isPresent()){

                for (Tag tag : articleOptional.get().getTags()){
                    ((ArrayList<Long>) iterable).add(tag.getId());
                }
                tagRepository.deleteAllByIdInBatch(iterable);
                em.flush();

                for (String tagContent : articleRequestDto.getTags()) {
                    Tag tag = Tag.builder().content(tagContent).article(articleOptional.get()).build();
                    articleOptional.get().getTags().add(tag);
                }

            }
        }

        return article;
    }
    // 글 삭제
    @Transactional
    public void delete(Long id) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(id);
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
    public Notification commentwrite(CommentRequestDto commentRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(commentRequestDto.getBoardid());
        // 글 댓글 개수를 증가시킨다
        article.chagneCommentCount(article.getCommentcount() + 1);
        em.flush();
        // 댓글 dto에 댓글 개수 증가된 Article Entity를 저장한다
        CommentSaveDto commentSaveDto = new CommentSaveDto();
        commentSaveDto.changeCommentSaveData(commentRequestDto.getContent(),article, member);
        Optional<Comment> comment = Optional.ofNullable(commentRepository.save(commentSaveDto.createCommentEntity()));
        if (!comment.isPresent()) {
            throw new RuntimeException("댓글 작성에 실패했습니다.");
        }
        Notification notification = notificationService.sendNotification(article.getMember(),member,article,commentRequestDto.getContent(),false);
        return notification;
    }


    // 댓글 리스트를 페이지 형태로 불러오기
    @Transactional(readOnly = true)
    public CommentResultDto findCommentid(Long id, Pageable pageable) {
        // page 위치에 있는 값은 0부터 시작한다.
        int page = pageable.getPageNumber() - 1;
        // 한페이지에 보여줄 글 개수
        int pageLimit = 10;

        // 페이지 형태로 댓글 불러오기
        Page<CommentResponseDto> comment = commentRepository.findByCommentlist(id,PageRequest.of(page, pageLimit)).map(comments -> comments.toDto());
        return CommentResultDto.createCommentResultDto(comment);
    }

    // 글 조회수 올리기
    @Transactional
    public Article viewcount(Long id,HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에 articleid 파라미터값 불러오기
        String articleidCookie = CookieUtill.getCookieValue(request,"articleid");
        // 글 Article Entity 불러오기
        Article article = articleRepository.findByArticleAndMemberlist(id);
        // Article Entity가 null 일시 예외 상황 발생
        if(article == null)
        {
            throw new RuntimeException("글 조회 실패 및 데이터가 존재하지 않습니다");
        }
        // 쿠키카 Null일시 || 쿠키에 저장된 글 Id값과 조회한 글 Id값이 다를시 조회수를 올린다
        if(articleidCookie == null || !articleidCookie.equals(String.valueOf(id))){
            // Article Entity viewcount를 1 증가시킨다
            article.updatecount();
            // 쿠키에 조회수 올린 글 Id값을 저장한다. 유효기간은 1시간으로 설정한다.
            CookieUtill.addCookie(response,"articleid",String.valueOf(id),3600);
            return article;
        }
        return article;
    }



    // id로 Article Entity(글) 불러오기
    @Transactional(readOnly = true)
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
    public void commentdelete(Long id) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        // 댓글 Comment Entity 불러오기
        Comment comment = commentRepository.findById(id).orElse(null);
        // 인증된 유저와 댓글 작성한 유저 비교 || 요청한 댓글이 데이터베이스에 없을시
        if(member.getId() != comment.getMember().getId() || comment == null)
        {
            throw new RuntimeException("회원 정보 불일치 및 댓글 조회에 실패했습니다.");
        }

        // 글 댓글 개수를 감소시킨다
        Article article = comment.getArticle();
        article.setCommentcount(article.getCommentcount() - 1);

        // 대댓글이 없을 시 댓글을 삭제한다.
        if(comment.getParent() == null && comment.getChild().size() == 0) {
            commentRepository.delete(comment);
        }
        else if(comment.getParent() != null && comment.getChild().size() == 0) {
            commentRepository.delete(comment);
        }
        // 대댓글이 있을 시 Comment Deleted를 true로 바꾼다
        else {
            // Deleted true된 Entity로 수정한다
            comment.setDeleted(true);
        }

    }
    // 대댓글 작성
    @Transactional
    public void replywrite(CommentRequestDto replyRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        // 글 Article Entity 불러오기
        Article article = this.findById(replyRequestDto.getBoardid());
        // 글 댓글 개수를 증가시킨다
        article.chagneCommentCount(article.getCommentcount() + 1);
        em.flush();
        // 부모 댓글 Comment Entity를 가져온다.
        Comment parentcomment = commentRepository.findById(replyRequestDto.getParentid()).orElse(null);
        // 대댓글 정보를 수정한다
        CommentSaveDto commentSaveDto = new CommentSaveDto();
        commentSaveDto.changeReplySaveData(replyRequestDto.getContent(),article,member,parentcomment);
        // 댓글 넘버를 저장한다.
        if(parentcomment.getCommentorder() == 0)
        {
            commentSaveDto.changeReplySaveOrderData(parentcomment.getCommentnumber(),(long) article.getCommentcount(),parentcomment.getRedepth() + 1);
        }
        else {
            commentSaveDto.changeReplySaveOrderData(parentcomment.getCommentnumber(),parentcomment.getCommentorder(),parentcomment.getRedepth() + 1);
        }
        // 댓글을 저장한다
        Comment comment = commentRepository.save(commentSaveDto.createCommentEntity());
        // 저장한 댓글 Entity가 null 일시 예외 상황 발생
        if (comment == null) {
            throw new RuntimeException("댓글 작성에 실패했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public MyArticleResultDto findMyArticleList(Member user, Pageable pageable) {
        Page<MyArticleResponseDto> bymyArticlelist = articleRepository.findBymyArticlelist(user, pageable).map(m-> m.changeMyArticleResponseDto());
        return MyArticleResultDto.createMyArticleResultDto(bymyArticlelist);

    }
    @Transactional(readOnly = true)
    public MyCommentResultDto findMyCommentList(Member user, Pageable pageable) {
        Page<MyCommentResponseDto> bymyCommentlist = articleRepository.findBymyCommentlist(user, pageable).map(m-> m.changeMyCommentResponseDto());
        return MyCommentResultDto.createMyCommentResultDto(bymyCommentlist);
    }
    @Transactional(readOnly = true)
    public MyBookmarkResultDto findMyBookmarkList(Member user, Pageable pageable) {
        Page<MyBookmarkResponseDto> bymyBookmarklist = bookmarkRepository.findBymyBookmarklist(user, pageable).map(m-> MyBookmarkResponseDto.builder().bookmark_id(m.getId()).article_id(m.getArticle().getId()).article_title(m.getArticle().getTitle()).build());
        return MyBookmarkResultDto.createMyBookmarkResultDto(bymyBookmarklist);
    }

    @Transactional
    public String setBookmark(Long id, String type) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        Optional<Member> byId = memberRepository.findById(authenticationUtil.getCurrentMember().getId());

        if(articleOptional.isPresent() && byId.isPresent() && type.equals("create")) {
            bookmarkRepository.save(Bookmark.builder().article(articleOptional.get()).member(byId.get()).build());
            return "즐겨 찾기 완료했습니다.";
        }
        else if (articleOptional.isPresent() && byId.isPresent() && type.equals("delete")) {
            Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByarticle(articleOptional.get());
                    if(bookmarkOptional.isPresent())
                    {
                        bookmarkRepository.delete(bookmarkOptional.get());
                    }
            return "즐겨 찾기 삭제 완료했습니다.";
        }
        else {
            return "올바른 데이터 접근이 아닙니다.";
        }
    }
    @Transactional(readOnly = true)
    public Boolean checkBookmark(Long articleId) {
        return bookmarkRepository.findByMemberAndArticle(authenticationUtil.getCurrentMember().getId(),articleId).isPresent();
    }
}

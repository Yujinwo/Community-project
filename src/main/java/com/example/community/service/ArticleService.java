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

    // 글 조회
    @Transactional(readOnly = true)
    public ArticleindexResultDto findArticles(Pageable pageable, String sort) {
        // page 위치에 있는 값은 0부터 시작한다.
        int page = pageable.getPageNumber() - 1;
        PageRequest pageRequest = PageRequest.of(page, pageable.getPageSize());
        // 페이지 형태로 글 불러오기
        Page<ArticleindexResponseDto> articleDtos = articleRepository.findByArticlelist(pageRequest, sort).map(article -> ArticleindexResponseDto.builder().article(article).build());
        return ArticleindexResultDto.createArticleindexResultDto(articleDtos);
    }
    // 글 검색 및 태그 조회
    @Transactional(readOnly = true)
    public ArticleindexResultDto searchArticles(String query, Pageable pageable, Boolean tagsearch, String sort, String search) {
        // page 위치에 있는 값은 0부터 시작한다.
        int page = pageable.getPageNumber() - 1;
        PageRequest pageRequest = PageRequest.of(page, pageable.getPageSize());
        // 글 태그 조회 || 글 검색 조회
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

    // 글 작성
    @Transactional
    public Optional<Object> write(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }

        // 글 저장 Dto 인스턴스 생성
        ArticleSaveDto articleSaveDto = new ArticleSaveDto();
        articleSaveDto.changeSaveArticleData(articleRequestDto.getTitle(),articleRequestDto.getContent(),member);
        
        Optional<Article> savedArticle = Optional.ofNullable(articleRepository.save(articleSaveDto.createArticleEntity()));
        if(savedArticle.isEmpty())
        {
            return Optional.ofNullable(null);
        }

        // 이미지가 첨부 되었으면
        if (files != null && !files.isEmpty()) {
            // 이미지를 하나씩 꺼낸다
            for (MultipartFile file : files) {
                try {
                    if(!file.isEmpty())
                    {
                        String fileName = s3Uploader.upload(file);
                        // 저장된 파일 디렉터리를 저장
                        BoardImage image = BoardImage.builder()
                                .url(fileName)
                                .article(savedArticle.get())
                                .build();
                        boardImageRepository.save(image);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        // 태그가 작성 되었으면
        if(articleRequestDto.getTags() != null){
            for (String tagname : articleRequestDto.getTags()) {
                if(!tagname.isBlank())
                {
                    // 태그 저장
                    Tag tag = Tag.builder()
                            .content(tagname)
                            .article(savedArticle.get())
                            .build();
                    tagRepository.save(tag);
                }

            }
        }

        return Optional.of(savedArticle);
    }
    // 글 수정
    @Transactional
    public Optional<Object> update(ArticleRequestDto articleRequestDto, List<MultipartFile> files) {

        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }
        
        Optional<Article> article = this.findById(articleRequestDto.getId());
        // 글 조회 실패 시
        if(article.isEmpty())
        {
            return Optional.ofNullable(null);
        }

        // 기존 저장된 내용과 수정 요청한 내용이 같을 시
        if (article.get().getTitle().equals(articleRequestDto.getTitle()) && article.get().getContent().equals(articleRequestDto.getContent())){
            return Optional.ofNullable(null);
        }

        // 인증된 유저와 글 작성한 유저 비교
        if(member.getId() != article.get().getMember().getId())
        {
            return Optional.ofNullable(null);
        }
        
        article.get().changeTitleandContent(articleRequestDto.getTitle(),articleRequestDto.getContent());
        // 강제 플러시로 DB 쿼리 바로 호출
        em.flush();
        
        // 글 이미지가 없었는데 첨부가 되었으면 || 글 이미지를 다 삭제하고 이미지 첨부 파일이 있으면
        if (articleRequestDto.getImageUrls().isEmpty() && files != null){
            // 이전에 저장되어 있던 이미지 파일이 존재할 시 파일과 데이터를 삭제한다.
            // 글에 저장된 이미지 불러오기
            List<BoardImage> boardImage = boardImageRepository.findByboardId(articleRequestDto.getId());
            if(boardImage.size() != 0)
            {
                for (BoardImage image : boardImage) {
                    s3Uploader.deleteFile(image.getUrl());
                    boardImageRepository.delete(image);
                }
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
                                .article(article.get())
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
            if(boardImage.size() != 0) {
                for (BoardImage image : boardImage) {
                    s3Uploader.deleteFile(image.getUrl());
                    boardImageRepository.delete(image);
                }
            }
        }
        // 다시 확인 요망
        // 글 이미지를 하나만 삭제하고 이미지 첨부 파일이 있으면
        if (!articleRequestDto.getImageUrls().isEmpty() && files != null){
            // 이전에 저장되어 있던 이미지 파일이 존재할 시 파일과 데이터를 삭제한다.
            // 글에 저장된 이미지 불러오기
            List<BoardImage> boardImage = boardImageRepository.findByboardId(articleRequestDto.getId());
            if(boardImage.size() != 0)
            {
                for (BoardImage image : boardImage) {
                    s3Uploader.deleteFile(image.getUrl());
                    boardImageRepository.delete(image);
                }
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
                                .article(article.get())
                                .build();
                        boardImageRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        // 태그 리스트가 없을 시 저장했던 태그가 있으면 모두 삭제
        if(articleRequestDto.getTags().isEmpty() ) {
            Iterable<Long> iterable = new ArrayList<>();

            Optional<Article> articleOptional = articleRepository.findById(articleRequestDto.getId());
            // 글이 존재하면
            if(articleOptional.isPresent()){
                // 태그를 하나씩 꺼내서 id를 리스트에 추가
                for (Tag tag : articleOptional.get().getTags()){
                    ((ArrayList<Long>) iterable).add(tag.getId());
                }
                // Batch를 이용해 한번에 삭제
                tagRepository.deleteAllByIdInBatch(iterable);
            }
        }
        else {
            Iterable<Long> iterable = new ArrayList<>();

            Optional<Article> articleOptional = articleRepository.findById(articleRequestDto.getId());
            // 글이 존재하면
            if(articleOptional.isPresent()){
                // 태그를 하나씩 꺼내서 id를 리스트에 추가
                for (Tag tag : articleOptional.get().getTags()){
                    ((ArrayList<Long>) iterable).add(tag.getId());
                }
                // Batch를 이용해 한번에 삭제
                tagRepository.deleteAllByIdInBatch(iterable);
                em.flush();
                // 태그 추가
                for (String tagContent : articleRequestDto.getTags()) {
                    Tag tag = Tag.builder().content(tagContent).article(articleOptional.get()).build();
                    articleOptional.get().getTags().add(tag);
                }

            }
        }

        return Optional.of(article);
    }
    // 글 삭제
    @Transactional
    public Optional<Object> delete(Long id) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }
        
        Optional<Article> article = this.findById(id);
        // 글 조회 실패 시
        if(article.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        // 인증된 유저와 글 작성한 유저 비교
        if(member.getId() != article.get().getMember().getId())
        {
            return Optional.ofNullable(null);
        }
        articleRepository.delete(article.get());
        return Optional.of(article);
    }

    // 댓글 작성
    @Transactional
    public Optional<Object> commentwrite(CommentRequestDto commentRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }

        Optional<Article> article = this.findById(commentRequestDto.getBoardid());
        // 글 조회 실패 시
        if(article.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        
        article.get().chagneCommentCount(article.get().getCommentcount() + 1);
        em.flush();

        // 댓글 저장 Dto 인스턴스 생성
        CommentSaveDto commentSaveDto = new CommentSaveDto();
        commentSaveDto.changeCommentSaveData(commentRequestDto.getContent(),article.get(), member);
        
        Optional<Comment> comment = Optional.ofNullable(commentRepository.save(commentSaveDto.createCommentEntity()));
        if (comment.isEmpty()) {
            return Optional.ofNullable(null);
        }
        // 내 자신에게 알림 발송 차단
        if(article.get().getMember().getId() == member.getId())
        {
            Optional.ofNullable(comment);
        }

        // 글 작성자에게 댓글 알림 전송
        Notification notification = notificationService.sendNotification(article.get().getMember(),member,article.get(),commentRequestDto.getContent());
        return Optional.ofNullable(notification);
    }


    // 댓글 조회
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
    public Optional<Article> viewcount(Long id,HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에 articleid 파라미터값 불러오기
        String articleidCookie = CookieUtill.getCookieValue(request,"articleid");

        Optional<Article> article = articleRepository.findByArticleAndMemberlist(id);

        // 쿠키카 Null일시 || 쿠키에 저장된 글 Id값과 조회한 글 Id값이 다를시 조회수를 올린다
        if(articleidCookie == null || !articleidCookie.equals(String.valueOf(id)) ){
            // Article Entity viewcount를 1 증가시킨다
            article.ifPresent(Article::updatecount);
            // 쿠키에 조회수 올린 글 Id값을 저장한다. 유효기간은 1시간으로 설정한다.
            CookieUtill.addCookie(response,"articleid",String.valueOf(id),3600);
            return article;
        }
        return article;
    }

    @Transactional(readOnly = true)
    public Optional<Article> findById(Long id) {
        Optional<Article> article = articleRepository.findById(id);
        return article;
    }
    // 댓글 삭제
    @Transactional
    public Optional<Object> commentdelete(Long id) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }
        Optional<Comment> comment = commentRepository.findById(id);
        // 댓글 조회 실패 시
        if(comment.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        // 인증된 유저와 댓글 작성한 유저 비교 ||
        if(member.getId() != comment.get().getMember().getId())
        {
            return Optional.ofNullable(null);
        }

        // 글 댓글 개수 1 감소
        Article article = comment.get().getArticle();
        article.setCommentcount(article.getCommentcount() - 1);

        // 대댓글이 없을 시
        if(comment.get().getChild().size() == 0) {
            commentRepository.delete(comment.get());
        }
        // 대댓글이 있을 시
        else {
            // 삭제 여부를 true
            comment.get().setDeleted(true);
        }

        return Optional.ofNullable(comment);
    }
    // 대댓글 작성
    @Transactional
    public Optional<Object> replywrite(CommentRequestDto replyRequestDto) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }
        Optional<Article> article = this.findById(replyRequestDto.getBoardid());
        // 글 조회 실패 시
        if(article.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        // 글 댓글 개수 1 증가
        article.get().chagneCommentCount(article.get().getCommentcount() + 1);
        em.flush();

        // 부모 댓글 Comment Entity 조회
        Optional<Comment> parentcomment = commentRepository.findById(replyRequestDto.getParentid());
        if(parentcomment.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        // 대댓글 작성 Dto 인스턴스 생성
        CommentSaveDto commentSaveDto = new CommentSaveDto();
        commentSaveDto.changeReplySaveData(replyRequestDto.getContent(),article.get(),member,parentcomment.get());

        // 부모댓글이 최상위이면
        if(parentcomment.get().getCommentorder() == 0)
        {
            // commentorder 값을 현재 댓글수로 설정
            commentSaveDto.changeReplySaveOrderData(parentcomment.get().getCommentnumber(),(long) article.get().getCommentcount(),parentcomment.get().getRedepth() + 1);
        }
        else {
            // commentorder 값을 부모 댓글 값으로 설정
            commentSaveDto.changeReplySaveOrderData(parentcomment.get().getCommentnumber(),parentcomment.get().getCommentorder(),parentcomment.get().getRedepth() + 1);
        }

        Optional<Comment> comment = Optional.of(commentRepository.save(commentSaveDto.createCommentEntity()));
        if(comment.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(comment);
    }
    // 내 글 조회
    @Transactional(readOnly = true)
    public MyArticleResultDto findMyArticleList(Member user, Pageable pageable) {
        Page<MyArticleResponseDto> bymyArticlelist = articleRepository.findBymyArticlelist(user, pageable).map(m-> m.changeMyArticleResponseDto());
        return MyArticleResultDto.createMyArticleResultDto(bymyArticlelist);

    }

    // 내 댓글 조회
    @Transactional(readOnly = true)
    public MyCommentResultDto findMyCommentList(Member user, Pageable pageable) {
        Page<MyCommentResponseDto> bymyCommentlist = articleRepository.findBymyCommentlist(user, pageable).map(m-> m.changeMyCommentResponseDto());
        return MyCommentResultDto.createMyCommentResultDto(bymyCommentlist);
    }

    // 내 즐겨찾기 조회
    @Transactional(readOnly = true)
    public MyBookmarkResultDto findMyBookmarkList(Member user, Pageable pageable) {
        Page<MyBookmarkResponseDto> bymyBookmarklist = bookmarkRepository.findBymyBookmarklist(user, pageable).map(m-> MyBookmarkResponseDto.builder().bookmark_id(m.getId()).article_id(m.getArticle().getId()).article_title(m.getArticle().getTitle()).build());
        return MyBookmarkResultDto.createMyBookmarkResultDto(bymyBookmarklist);
    }
    // 즐겨찾기 추가
    @Transactional
    public Optional<Object> setBookmark(Long id) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }
        Optional<Member> byId = memberRepository.findById(member.getId());
        if(byId.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        Optional<Article> articleOptional = articleRepository.findById(id);
        if(articleOptional.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        Optional<Bookmark> savedbookmark = Optional.ofNullable(bookmarkRepository.save(Bookmark.builder().article(articleOptional.get()).member(byId.get()).build()));
        return Optional.of(savedbookmark);
    }
    // 즐겨찾기 삭제
    @Transactional
    public Optional<Object> deleteBookmark(Long id) {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member == null)
        {
            return Optional.ofNullable(null);
        }
        Optional<Member> byId = memberRepository.findById(member.getId());
        if(byId.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        Optional<Article> articleOptional = articleRepository.findById(id);
        if(articleOptional.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByarticle(articleOptional.get());
        if(bookmarkOptional.isEmpty())
        {
            return Optional.ofNullable(null);
        }
        bookmarkRepository.delete(bookmarkOptional.get());
        return Optional.of(bookmarkOptional);

    }
    // 즐겨찾기 조회
    @Transactional(readOnly = true)
    public Boolean checkBookmark(Long articleId) {
        return bookmarkRepository.findByMemberAndArticle(authenticationUtil.getCurrentMember().getId(),articleId).isPresent();
    }
}

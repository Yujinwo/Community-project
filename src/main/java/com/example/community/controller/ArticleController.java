package com.example.community.controller;

import com.example.community.dto.ArticleResponseDto;
import com.example.community.dto.CommentResponseDto;
import com.example.community.entity.Article;
import com.example.community.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class ArticleController {


    private final ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String page(@RequestParam(value = "lastId", required = false,defaultValue = "0") Long lastId, @RequestParam(value = "previousId", required = false,defaultValue = "0") Long previousId,@RequestParam(value = "previous", required = false,defaultValue = "0") int previous,@PageableDefault(page = 1) Pageable pageable, Model model) {
        Long PreviousLastId = Math.max(lastId - pageable.getPageSize(),1);
        if(PreviousLastId == 1)
            PreviousLastId = lastId;
        Long PreviousbeforeId = Math.max(previousId - pageable.getPageSize(),1);
        model.addAttribute("lastId", PreviousLastId);
        model.addAttribute("previousId", PreviousbeforeId);
        // 다음 누를시
        if (previous == 0) {

            // 페이지 데이터 불러오기
            Slice<ArticleResponseDto> slicepage = articleService.index(lastId, pageable);
            boolean hasPrevious = false;
            model.addAttribute("article", slicepage.getContent());
            model.addAttribute("hasNext", slicepage.hasNext());
            model.addAttribute("hasResults", !slicepage.getContent().isEmpty());
            model.addAttribute("hasPrevious", slicepage.getContent().get(0).getId() != 1); // 이전 페이지가 있는지 여부
            Long previousLastId = 1L;
            if (!slicepage.getContent().isEmpty()) {

                previousLastId = slicepage.getContent().get(0).getId();

                Long nextLastId = slicepage.getContent().get(slicepage.getContent().size() - 1).getId();
                model.addAttribute("previousLastId", previousLastId);
                model.addAttribute("nextLastId", nextLastId);
            }
        //이전 누를시
        } else {
            Slice<ArticleResponseDto> slicepage = articleService.index(previousId-1, pageable);
            boolean hasPrevious = false;
            model.addAttribute("article", slicepage.getContent());
            model.addAttribute("hasNext", slicepage.hasNext());
            model.addAttribute("hasResults", !slicepage.getContent().isEmpty());
            model.addAttribute("hasPrevious", slicepage.getContent().get(0).getId() != 1); // 이전 페이지가 있는지 여부

            if (!slicepage.getContent().isEmpty()) {
                Long previousLastId = previousId - pageable.getPageSize();

                Long nextLastId = lastId - pageable.getPageSize();
                model.addAttribute("previousLastId", previousLastId);
                model.addAttribute("nextLastId", nextLastId);

            }

        }
        return "index";
    }
    // 글 작성 페이지
    @GetMapping("/article/write")
    public String write() {
        return "write";
    }


    // 글 상세 페이지
    @GetMapping("/article/detail/{id}")
    public String detail(@PageableDefault(page = 1) Pageable pageable , @PathVariable("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        // 조회수 1 증가하고 글 불러오기
        Article article = articleService.viewcount(id,request, response);
        // 글 댓글 불러오기
        Page<CommentResponseDto> comments = articleService.findCommentid(id,pageable);
        // 페이지 최대 개수 설정
        int blockLimit = 3;
        // 시작 페이지
        int startPage = Math.max(1, comments.getPageable().getPageNumber() - blockLimit);
        // 마지막 페이지
        int endPage = Math.min(comments.getPageable().getPageNumber()+4, comments.getTotalPages());

        // 뷰에 데이터 전달
        model.addAttribute("article",article.toDto());
        model.addAttribute("comment",comments);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "detail";
    }

    @GetMapping("/articles/search")
    public String searchArticles(@RequestParam("query") String query, @RequestParam(value = "lastId", required = false) Long lastId, @RequestParam(value = "previousId", required = false) Long previousId,@RequestParam(value = "previous", required = false,defaultValue = "0") int previous,Model model, @PageableDefault(page = 1)  Pageable pageable) {
        if (previous == 0) {
            Slice<ArticleResponseDto> slicepage = articleService.searchArticles(lastId, query, pageable);
            model.addAttribute("article", slicepage.getContent());
            model.addAttribute("query", query);
            model.addAttribute("hasNext", slicepage.hasNext());
            model.addAttribute("hasResults", !slicepage.getContent().isEmpty());
            model.addAttribute("hasPrevious", lastId != null); // 이전 페이지가 있는지 여부

            if (!slicepage.getContent().isEmpty()) {
                if (slicepage.hasNext()) {
                    previousId = slicepage.getContent().get(0).getId();
                }

                Long nextLastId = slicepage.getContent().get(slicepage.getContent().size() - 1).getId();
                model.addAttribute("previousLastId", previousId);
                model.addAttribute("nextLastId", nextLastId);
            }
        }

        return "searchindex";
    }

    // 글 수정 페이지
    @GetMapping("/article/update/{id}")
    public String updatepage(@PathVariable Long id, Model model) {
        // 글 불러오기
        Article article = articleService.findById(id);
        // 뷰에 데이터 전달
        model.addAttribute("article",article.toDto());
        return "update";
    }
}

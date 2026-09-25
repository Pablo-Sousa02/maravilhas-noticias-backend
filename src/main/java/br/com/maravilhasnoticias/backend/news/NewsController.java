package br.com.maravilhasnoticias.backend.news;

import br.com.maravilhasnoticias.backend.news.dto.NewsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;
    public NewsController(NewsService newsService) { this.newsService = newsService; }
    @GetMapping public Page<NewsResponse> list(@RequestParam(required = false) String category, @PageableDefault(size = 20, sort = "publishedAt") Pageable pageable) { return newsService.listPublic(category, pageable); }
    @GetMapping("/featured") public Page<NewsResponse> featured(@PageableDefault(size = 10, sort = "publishedAt") Pageable pageable) { return newsService.featured(pageable); }
    @GetMapping("/search") public Page<NewsResponse> search(@RequestParam String q, @RequestParam(required = false) String category, @PageableDefault(size = 20, sort = "publishedAt") Pageable pageable) { return newsService.search(q, category, pageable); }
    @GetMapping("/{slug}") public NewsResponse get(@PathVariable String slug) { return newsService.getPublic(slug); }
}

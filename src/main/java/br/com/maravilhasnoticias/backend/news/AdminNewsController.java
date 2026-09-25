package br.com.maravilhasnoticias.backend.news;

import br.com.maravilhasnoticias.backend.news.dto.NewsRequest;
import br.com.maravilhasnoticias.backend.news.dto.NewsResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {
    private final NewsService newsService;
    public AdminNewsController(NewsService newsService) { this.newsService = newsService; }
    @GetMapping public Page<NewsResponse> list(@RequestParam(required = false) NewsStatus status, @RequestParam(required = false) String category, @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) { return newsService.listAdmin(status, category, pageable); }
    @GetMapping("/{id}") public NewsResponse get(@PathVariable UUID id) { return newsService.getAdmin(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public NewsResponse create(@Valid @RequestBody NewsRequest request, @AuthenticationPrincipal Jwt jwt) { return newsService.create(request, jwt); }
    @PutMapping("/{id}") public NewsResponse update(@PathVariable UUID id, @Valid @RequestBody NewsRequest request) { return newsService.update(id, request); }
    @PatchMapping("/{id}/publish") public NewsResponse publish(@PathVariable UUID id) { return newsService.publish(id); }
    @PatchMapping("/{id}/archive") public NewsResponse archive(@PathVariable UUID id) { return newsService.archive(id); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { newsService.delete(id); }
}

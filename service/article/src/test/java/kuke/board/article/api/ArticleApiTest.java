package kuke.board.article.api;

import kuke.board.article.service.request.ArticleCreateRequest;
import kuke.board.article.service.response.ArticlePageResponse;
import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest(){
        final ArticleResponse articleResponse = create(new ArticleCreateRequest("hi", "my content", 1L, 1L));
        System.out.println(articleResponse);
    }

    ArticleResponse create(ArticleCreateRequest request){
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest(){
        ArticleResponse response = read(137170796841914368L);
        System.out.println("response = " + response);
    }

    private ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class)
                ;
    }

    @Test
    void updateTest(){

        final ArticleResponse update = update(137170796841914368L);
        System.out.println("response = " + update);
    }

    ArticleResponse update(Long articleId){
        return restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 22", "my content 22"))
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void deleteTest(){
        restClient.delete()
                .uri("/v1/articles/{articleId}", 137170796841914368L)
                .retrieve();
    }

    @Test
    void readAllTest(){
        final ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("article : " + article);
        }
    }


    @Test
    void readAllInfiniteScrollTest(){
        final List<ArticleResponse> articles1 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("firstPage");
        for (ArticleResponse article : articles1) {
            System.out.println("article : " + article.getArticleId());
        }

        final Long lastArticleId = articles1.getLast().getArticleId();
        final List<ArticleResponse> articles2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });


        System.out.println("secondPage");
        for (ArticleResponse article : articles2) {
            System.out.println("article : " + article.getArticleId());
        }


    }




    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;

    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;

    }


}

package kuke.board.article.api;

import kuke.board.article.service.request.ArticleCreateRequest;
import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

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

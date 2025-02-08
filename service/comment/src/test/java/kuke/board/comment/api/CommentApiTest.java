package kuke.board.comment.api;

import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9002");

    @Test
    void create(){
        final CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        final CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
        final CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));

        System.out.println("commentId=%s".formatted(response1.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));
    }

    CommentResponse createComment(CommentCreateRequest request){
        return restClient.post()
                .uri("/v1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read(){
        final CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 146500670306177024L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println(response);
    }

    @Test
    void delete(){

//        146500670306177024
//        146500672168448000
//        146500672256528384
        final CommentResponse body = restClient.delete()
                .uri("/v1/comments/{commentId}", 146500672256528384L)
                .retrieve()
                .body(CommentResponse.class);
    }



    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writer;
    }

}

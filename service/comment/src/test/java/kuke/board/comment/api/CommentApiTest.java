package kuke.board.comment.api;

import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

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

    @Test
    void readAll(){
        final CommentPageResponse response = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() =" + response.getCommentCount());

        for (CommentResponse comment : response.getComments()) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())){
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
        /**
         * 1번 페이지 수행 결과
         * response.getCommentCount() =101
         * comment.getCommentId() = 146511260322897920
         * 	comment.getCommentId() = 146511260431949825
         * comment.getCommentId() = 146511260322897921
         * 	comment.getCommentId() = 146511260436144143
         * comment.getCommentId() = 146511260322897922
         * 	comment.getCommentId() = 146511260440338439
         * comment.getCommentId() = 146511260322897923
         * 	comment.getCommentId() = 146511260431949834
         * comment.getCommentId() = 146511260322897924
         * 	comment.getCommentId() = 146511260436144152
         */
    }

    @Test
    void readAllInfiniteScroll(){
        System.out.println("firstPage");
        final List<CommentResponse> response1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        for (CommentResponse comment : response1) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())){
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }


        final Long lastParentCommentId = response1.getLast().getParentCommentId();
        final Long lastCommentId = response1.getLast().getCommentId();

        System.out.println("secondPage");

        final List<CommentResponse> response2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                        .formatted(lastParentCommentId, lastCommentId)
                ).retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });


        for (CommentResponse comment : response2) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())){
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        /**
         * firstPage
         * comment.getCommentId() = 146511260322897920
         * 	comment.getCommentId() = 146511260431949825
         * comment.getCommentId() = 146511260322897921
         * 	comment.getCommentId() = 146511260436144143
         * comment.getCommentId() = 146511260322897922
         * secondPage
         * 	comment.getCommentId() = 146511260440338439
         * comment.getCommentId() = 146511260322897923
         * 	comment.getCommentId() = 146511260431949834
         * comment.getCommentId() = 146511260322897924
         * 	comment.getCommentId() = 146511260436144152
         */

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

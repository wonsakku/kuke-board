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

public class CommentApiV2Test {

    RestClient restClient = RestClient.create("http://localhost:9002");

    @Test
    void create(){
        final CommentResponse response1 = createComment(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        final CommentResponse response2 = createComment(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
        final CommentResponse response3 = createComment(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

        System.out.println("response1.getPath()=%s".formatted(response1.getPath()));
        System.out.println("response1.getCommentId()=%s".formatted(response1.getCommentId()));
        System.out.println("\tresponse2.getPath()=%s".formatted(response2.getPath()));
        System.out.println("\tresponse2.getCommentId()=%s".formatted(response2.getCommentId()));
        System.out.println("\t\tresponse3.getPath()=%s".formatted(response3.getPath()));
        System.out.println("\t\tresponse3.getCommentId()=%s".formatted(response3.getCommentId()));

        /**
         * response1.getPath()=00008
         * response1.getCommentId()=149034152784764928
         * 	response2.getPath()=0000800000
         * 	response2.getCommentId()=149034153346801664
         * 		response3.getPath()=000080000000000
         * 		response3.getCommentId()=149034153434882048
         */
    }

    CommentResponse createComment(CommentCreateRequestV2 request){
        return restClient.post()
                .uri("/v2/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read(){
        final CommentResponse response = restClient.get()
                .uri("/v2/comments/{commentId}", 149034153346801664L)
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
                .uri("/v2/comments/{commentId}", 146500672256528384L)
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




    @AllArgsConstructor
    @Getter
    public class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writer;
    }


}

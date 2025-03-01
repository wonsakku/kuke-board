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

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create(){
        final CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        final CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
        final CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

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

    CommentResponse create(CommentCreateRequestV2 request){
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
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v2/comments?articleId=1&pageSize=10&page=50000")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        /**
         * comment.getCommentId() = 124136527842209792
         * comment.getCommentId() = 124136528337137664
         * comment.getCommentId() = 124136528408440832
         * comment.getCommentId() = 124136572368941056
         * comment.getCommentId() = 124136572561879040
         * comment.getCommentId() = 124136572616404992
         * comment.getCommentId() = 124136618837635072
         * comment.getCommentId() = 124136619009601536
         * comment.getCommentId() = 124136619068321792
         * comment.getCommentId() = 124136886845272064
         */
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse response : responses1) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        String lastPath = responses1.getLast().getPath();
        List<CommentResponse> responses2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse response : responses2) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }
    }

    @Test
    void countTest() {
        CommentResponse commentResponse = create(new CommentCreateRequestV2(2L, "my comment1", null, 1L));

        Long count1 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1); // 1

        restClient.delete()
                .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
                .retrieve();

        Long count2 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2); // 0
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

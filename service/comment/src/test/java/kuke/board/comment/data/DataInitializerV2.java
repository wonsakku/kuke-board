package kuke.board.comment.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kuke.board.comment.entity.Comment;
import kuke.board.comment.entity.CommentPath;
import kuke.board.comment.entity.CommentV2;
import kuke.board.common.snowflake.Snowflake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class DataInitializerV2 {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    TransactionTemplate transactionTemplate;
    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    static final int BULK_INSERT_SIZE = 2_000;
    static final int EXECUTE_COUNT = 6_000;

    @Test
    void initialize() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for(int i = 0 ; i < EXECUTE_COUNT ; i++){
                int start = i * BULK_INSERT_SIZE;
                int end = (i + 1) * BULK_INSERT_SIZE;
            executorService.submit(() -> {
                insert(start, end);
                latch.countDown();
                System.out.println("latch.getCount() = " + latch.getCount());
            });
        }
        latch.await();

    }

    private void insert(int start, int end) {
        transactionTemplate.executeWithoutResult(status -> {
            CommentV2 prev = null;
            for(int i = start ; i < end ; i++){
                final CommentV2 comment = CommentV2.create(
                        snowflake.nextId(),
                        "content" + i,
                        1L,
                        1L,
                        toPath(i)
                );
                prev = comment;
                entityManager.persist(comment);
            }
        });

    }

    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int DEPTH_CHUNK_SIZE = 5;

    CommentPath toPath(int value){
        String path = "";
        for (int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
            path = CHARSET.charAt(value % CHARSET.length()) + path;
            value /= CHARSET.length();
        }
        return CommentPath.create(path);
    }

}

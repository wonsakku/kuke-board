package kuke.board.comment.repository;

import kuke.board.comment.entity.CommentV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepositoryV2 extends JpaRepository<CommentV2, Long> {

    @Query("SELECT c FROM CommentV2 c WHERE c.commentPath.path = :path")
    Optional<CommentV2> findByPath(@Param("path") String path);


    @Query(value = """
        SELECT path
        FROM comment_v2
        WHERE article_id = :articleId and path > :pathPrefix AND path LIKE :pathPrefix%
        ORDER BY path desc limit 1
    """, nativeQuery = true)
    Optional<String> findDescendantTopPath(@Param("articleId") Long articleId, @Param("pathPrefix") String pathPrefix);



}

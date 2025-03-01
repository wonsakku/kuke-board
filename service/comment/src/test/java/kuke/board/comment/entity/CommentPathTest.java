package kuke.board.comment.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentPathTest {

    @Test
    void createChildCommentTest(){
        // 00000 <-- 생성
        createChildCommentTest(CommentPath.create(""), null, "00000");

        // 00000
        //       00000 <-- 생성
        createChildCommentTest(CommentPath.create("00000"), null, "0000000000");

        // 00000
        // 00001 <-- 생성
        createChildCommentTest(CommentPath.create(""), "00000", "00001");

        // 0000z
        //       abcdz
        //            zzzzz
        //                 zzzzz
        //       abce0 <-- 생성
        createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0");
    }

    @Test
    void createChildCommentPathIfMaxDepthPathTest(){
        assertThatThrownBy(
                () -> CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createChildCommentPathIfChunkOverflowTest(){
        final CommentPath commentPath =
                CommentPath.create("");

        assertThatThrownBy(
                () -> commentPath.createChildCommentPath("zzzzz")
        ).isInstanceOf(IllegalStateException.class);
    }

    void createChildCommentTest(CommentPath commentPath, String descendantTopPath, String expectedChildPath){
        final CommentPath childCommentPath = commentPath.createChildCommentPath(descendantTopPath);
        assertThat(childCommentPath.getPath()).isEqualTo(expectedChildPath);
    }

}
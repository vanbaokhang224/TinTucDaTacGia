package org.example.tintuctacgia.exception;

public class PostNotFoundException
        extends RuntimeException {

    public PostNotFoundException(
            Long id
    ) {

        super(
                "Post not found with id: " + id
        );
    }
}
package com.bengous.e2e.tests.blogdemo.context;

import lombok.Builder;

@Builder(toBuilder = true)
public record BlogDemoContext(
    int userId,
    int postId,
    String uploadedFileName
) {/**/}

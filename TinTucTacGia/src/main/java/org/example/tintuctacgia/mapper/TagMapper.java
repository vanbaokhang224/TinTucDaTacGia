package org.example.tintuctacgia.mapper;

import org.example.tintuctacgia.dto.tag.TagResponse;
import org.example.tintuctacgia.entity.Tag;

public class TagMapper {
    public static TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}

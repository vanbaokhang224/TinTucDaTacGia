package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.dto.tag.TagRequest;
import org.example.tintuctacgia.dto.tag.TagResponse;
import org.example.tintuctacgia.entity.Tag;
import org.example.tintuctacgia.mapper.TagMapper;
import org.example.tintuctacgia.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public TagResponse create(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        return TagMapper.toResponse(tagRepository.save(tag));
    }

    public List<TagResponse> getAll() {
        return tagRepository.findAll()
                .stream()
                .map(TagMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        tagRepository.deleteById(id);
    }
}

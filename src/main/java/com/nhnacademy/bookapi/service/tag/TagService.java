package com.nhnacademy.bookapi.service.tag;

import com.nhnacademy.bookapi.dto.tag.TagRequestDto;
import com.nhnacademy.bookapi.dto.tag.TagResponseDto;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.TagAlreadyExistException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class TagService {

    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public boolean addTag(TagRequestDto tagRequestDto) {
        if (tagRepository.existsByName(tagRequestDto.getName())) {
            throw new TagAlreadyExistException(tagRequestDto.getName() + " already exists");
        }
        tagRepository.save(new Tag(tagRequestDto.getName()));
        return true;
    }

    public boolean updateTag(Long tagId, TagRequestDto tagRequestDto) {
        if (!tagRepository.existsById(tagId)) { // 업데이트하려는 tagId가 없을 시
            throw new TagNotFoundException("TagID : "+ tagId + " does not exist");
        }
        Tag tag = tagRepository.findById(tagId).get();
        tag.updateTagName(tagRequestDto.getName()); // tagRequestDto의 name은 새로 바꾸려는 newTagName이다.
        return true;
    }

    public boolean deleteTag(Long tagId) {
        if (!tagRepository.existsById(tagId)) { // 없는 태그를 삭제할 시
            throw new TagNotFoundException("TagID : " + tagId + " does not exist");
        }
        tagRepository.deleteById(tagId);
        return true;
    }
    // 태그 id로 조회
    public TagResponseDto getTagById(Long tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        if (tag.isEmpty()) {
            throw new TagNotFoundException("TagID : "+ tagId + " does not exist");
        }
        return new TagResponseDto(tag.get().getId(), tag.get().getName());
    }
    // 태그 이름으로 조회
    public TagResponseDto getTagByName(String name) {
        Optional<Tag> tag = tagRepository.findByName(name);
        if (tag.isEmpty()) {
            throw new TagNotFoundException(name + " does not exist");
        }
        return new TagResponseDto(tag.get().getId(), tag.get().getName());
    }
    // 모든 태그 조회
    public List<TagResponseDto> getAllTags() {
        List<TagResponseDto> result = new ArrayList<>();
        if (tagRepository.findAll().isEmpty()) {
            throw new TagNotFoundException("No tags found");
        }

        for (Tag tag : tagRepository.findAll()) {
            result.add(new TagResponseDto(tag.getId(), tag.getName()));
        }
        return result;
    }
}

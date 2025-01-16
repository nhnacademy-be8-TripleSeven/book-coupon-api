package com.nhnacademy.bookapi.service.tag;

import com.nhnacademy.bookapi.dto.tag.TagRequestDto;
import com.nhnacademy.bookapi.dto.tag.TagResponseDto;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.TagAlreadyExistException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TagService {

    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void addTag(TagRequestDto tagRequestDto) {
        if (tagRepository.existsByName(tagRequestDto.getName())) {
            throw new TagAlreadyExistException(tagRequestDto.getName() + " already exists");
        }
        tagRepository.save(new Tag(tagRequestDto.getName()));
    }

    public void deleteTag(Long tagId) {
        if (!tagRepository.existsById(tagId)) { // 없는 태그를 삭제할 시
            throw new TagNotFoundException("TagID : " + tagId + " does not exist");
        }
        tagRepository.deleteById(tagId);
    }

    public List<String> getTagName(long bookId){
        return tagRepository.findTagNameByBookId(bookId);
    }

    public Page<TagResponseDto> getAllTags(Pageable pageable) {
        Page<Tag> tags = tagRepository.findAll(pageable);
        List<TagResponseDto> result = new ArrayList<>();
        for (Tag tag : tags) {
            result.add(new TagResponseDto(tag.getId(), tag.getName()));
        }
        return new PageImpl<>(result, pageable, tags.getTotalElements());
    }
}

package com.nhnacademy.bookapi.service.tag;

import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.exception.TagAlreadyExistException;
import com.nhnacademy.bookapi.exception.TagNotFoundException;
import com.nhnacademy.bookapi.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void addTag(String name) {
        if (tagRepository.existsByName(name)) {
            throw new TagAlreadyExistException(name + " already exists");
        }
        tagRepository.save(new Tag(name));
    }

    @Transactional
    public void updateTag(Long tagId, String newName) {
        if (!tagRepository.existsById(tagId)) {
            throw new TagNotFoundException(tagId + " does not exist");
        }
        Tag tag = tagRepository.findById(tagId).get();
        tag.setName(newName);
    }

    public void deleteTag(String name) {
        if (!tagRepository.existsByName(name)) { // 없는 태그를 삭제할 시
            throw new TagNotFoundException(name + " does not exist");
        }
        tagRepository.deleteByName(name);
    }

    public void deleteTag(Long tagId) {
        if (!tagRepository.existsById(tagId)) { // 없는 태그를 삭제할 시
            throw new TagNotFoundException(tagId + " does not exist");
        }
        tagRepository.deleteById(tagId);
    }
}

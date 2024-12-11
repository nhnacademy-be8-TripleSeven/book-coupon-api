package com.nhnacademy.bookapi.service;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Likes;
import com.nhnacademy.bookapi.repository.LikeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    private LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public void addLike(Book book, Long userId) {
        likeRepository.save(new Likes(book, userId));
    }

    public void deleteLike(Book book, Long userId) {
        Optional<Likes> like = likeRepository.findByBookAndUserId(book, userId);
        if (like.isPresent()) {
            likeRepository.delete(like.get());
        }
    }
}

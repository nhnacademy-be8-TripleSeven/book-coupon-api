package com.nhnacademy.bookapi.service.likes;

import com.nhnacademy.bookapi.dto.likes.LikesRequestDto;
import com.nhnacademy.bookapi.dto.likes.LikesResponseDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Likes;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.LikeAlreadyExistException;
import com.nhnacademy.bookapi.exception.LikeNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.LikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//@Service
//@Transactional
//public class LikeService {
//
//    private final LikeRepository likeRepository;
//    private final BookRepository bookRepository;
//
//    public LikeService(LikeRepository likeRepository, BookRepository bookRepository) {
//        this.likeRepository = likeRepository;
//        this.bookRepository = bookRepository;
//    }
//
//    // 좋아요 추가
//    public void addLike(LikesRequestDto likesRequestDto) {
//        Book book = getBook(likesRequestDto.getBookId());
//        // 이미 좋아요가 존재하는 경우 예외 처리
//        if (likeRepository.findByBookAndUserId(book, likesRequestDto.getUserId()).isPresent()) {
//            throw new LikeAlreadyExistException("Like already exists");
//        }
//        Likes like = new Likes(book, likesRequestDto.getUserId());
//        likeRepository.save(like);
//    }
//
//    // 좋아요 삭제
//    public void deleteLike(LikesRequestDto likesRequestDto) {
//        Book book = getBook(likesRequestDto.getBookId());
//        Likes like = getLikes(book, likesRequestDto.getUserId());
//        likeRepository.delete(like);
//    }
//
//    //유저 아이디를 사용해 좋아요를 누른 모든 도서 조회
//    public List<Likes> getAllLikesByUserId(Long userId) {
//        return likeRepository.findAllByUserId(userId);
//    }
//
//    private Book getBook(Long bookId) {
//        return bookRepository.findById(bookId)
//                .orElseThrow(() -> new BookNotFoundException("Book not found"));
//    }
//
//    private Likes getLikes(Book book, Long userId) {
//        return likeRepository.findByBookAndUserId(book, userId).orElseThrow(() -> new LikeNotFoundException("Like not found"));
//    }
//}

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;

    public LikeService(LikeRepository likeRepository, BookRepository bookRepository) {
        this.likeRepository = likeRepository;
        this.bookRepository = bookRepository;
    }

    // 좋아요 추가
    public void addLike(Long userId, Long bookId) {
        Book book = getBook(bookId);

        if (likeRepository.existsByBookAndUserId(book, userId)) {
            throw new LikeAlreadyExistException("The User is already liked");
        }
        Likes like = new Likes(book, userId);
        likeRepository.save(like);
    }

    public boolean isLiked(Long userId, Long bookId) {
        Book book = getBook(bookId);
        return likeRepository.existsByBookAndUserId(book, userId);
    }

    // 좋아요 삭제
    public void deleteLike(Long userId, Long bookId) {
        Book book = getBook(bookId);
        Likes like = getLikes(book, userId);
        likeRepository.delete(like);
    }

    // 유저 아이디를 사용해 좋아요를 누른 모든 도서 조회
    public List<LikesResponseDto> getAllLikesByUserId(Long userId) {
        List<LikesResponseDto> result = new ArrayList<>();

        List<Likes> list = likeRepository.findAllByUserIdWithBook(userId);

        for (Likes like : list) {
            result.add(new LikesResponseDto(like.getBook().getId()));
        }
        return result;
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    private Likes getLikes(Book book, Long userId) {
        return likeRepository.findByBookAndUserId(book, userId).orElseThrow(() -> new LikeNotFoundException("Like not found"));
    }
}
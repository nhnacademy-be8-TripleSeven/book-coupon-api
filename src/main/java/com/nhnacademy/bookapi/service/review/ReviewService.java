package com.nhnacademy.bookapi.service.review;

import com.nhnacademy.bookapi.dto.review.ReviewRequestDto;
import com.nhnacademy.bookapi.dto.review.ReviewResponseDto;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.Review;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.exception.ReviewAlreadyExistException;
import com.nhnacademy.bookapi.exception.ReviewNotFoundException;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.ReviewRepository;
import com.nhnacademy.bookapi.service.object.ObjectService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.aspectj.apache.bcel.generic.LineNumberGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    @Setter
    private ObjectService objectService;

    @Transactional
    public boolean addReview(Long userId, ReviewRequestDto reviewRequestDto, MultipartFile file) {
        Book book = getBook(reviewRequestDto.getBookId());

        // 이미 리뷰가 있는지 확인
        if (reviewRepository.existsByBookAndUserId(book, userId)) {
            throw new ReviewAlreadyExistException("이미 이 책에 리뷰를 작성했습니다.");
        }

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            objectService.generateAuthToken();
            try (InputStream inputStream = file.getInputStream()) {
                String objectName = "reviews/" + "review"+"_"+userId+"_"+reviewRequestDto.getBookId();
                objectService.uploadObject("triple-seven", objectName, inputStream);
                imageUrl = objectService.getStorageUrl() + "/triple-seven/" + objectName;
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
            }
        }
        // 리뷰 생성 및 저장
        Review review = new Review(
                reviewRequestDto.getText(),
                LocalDateTime.now(),
                reviewRequestDto.getRating(),
                book,
                userId,
                imageUrl
        );
        reviewRepository.save(review);
        return true;
    }

    @Transactional
    public boolean updateReview(Long userId, ReviewRequestDto reviewRequestDto, MultipartFile file, boolean isRemoveImage) {
        Book book = getBook(reviewRequestDto.getBookId());
        Review review = getReview(book, userId);
        //기존 이미지를 삭제하기를 클릭하는 경우 - 1. 기존이미지를 삭제하고 새로운 이미지를 업로드, 2. 기존이미지를삭제하고 아예 이미지를 삭제하고싶은 경우
        //기존 이미지 삭제하기를 클릭하지 않는 경우 1. 리뷰 내용만 수정하고 이미지는 그대로 둔다.(isRemoveImage는 false이고 file도 empty)
        String imageUrl = null;
        objectService.generateAuthToken();
        if (isRemoveImage) { // 일단 기존 이미지를 삭제하는 것은 확정
            if (file != null && !file.isEmpty()) { // 기존 이미지를 삭제하고 새로운 이미지를 업로드
                try (InputStream inputStream = file.getInputStream()) {
                    String objectName = "reviews/" + "review" + "_" + userId + "_" + reviewRequestDto.getBookId();
                    objectService.uploadObject("triple-seven", objectName, inputStream);
                    imageUrl = objectService.getStorageUrl() + "/triple-seven/" + objectName;
                } catch (IOException e) {
                    throw new RuntimeException("이미지 수정 실패: " + e.getMessage());
                }
            } else { // 아예 리뷰에 이미지를 삭제
                String objectName = "reviews/"+ "review" + "_" + userId + "_" + reviewRequestDto.getBookId();
                objectService.deleteObject("triple-seven", objectName);
            }
        } else {
            if (file != null && !file.isEmpty()) { // 기존 이미지가 없었고 수정할 때 이미지를 업로드
                try (InputStream inputStream = file.getInputStream()) {
                    String objectName = "reviews/" + "review" + "_" + userId + "_" + reviewRequestDto.getBookId();
                    objectService.uploadObject("triple-seven", objectName, inputStream);
                    imageUrl = objectService.getStorageUrl() + "/triple-seven/" + objectName;
                } catch (IOException e) {
                    throw new RuntimeException("이미지 수정 실패: " + e.getMessage());
                }
            } else {
                imageUrl = review.getImageUrl();
            }
        }
        review.updateText(reviewRequestDto.getText());
        review.updateRating(reviewRequestDto.getRating());
        review.updateCreatedAT(LocalDateTime.now());
        review.updateImageUrl(imageUrl);
        return true;
    }

//    @Transactional
//    public boolean deleteReview(Long userId, Long bookId) {
//        Book book = getBook(bookId);
//        Review review = getReview(book, userId);
//        reviewRepository.delete(review);
//        return true;
//    }
    // 도서 삭제 시 도서에 달려있는 리뷰들 삭제
    @Transactional
    public void deleteAllReviewsWithBook(Long bookId) {
        List<Long> userIds = reviewRepository.findAllUserIdsByBookId(bookId);
        reviewRepository.deleteByBookId(bookId);
        objectService.generateAuthToken();
        for (Long userId : userIds) {
            String objectName = "reviews/"+ "review" + "_" + userId + "_" + bookId;
            objectService.deleteObject("triple-seven", objectName);
        }
    }

    @Transactional
    // 유저가 쓴 모든 리뷰 조회
    public List<ReviewResponseDto> getAllReviewsByUserId(Long userId) {
        List<ReviewResponseDto> result = new ArrayList<>();
        List<Review> reviews = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        for (Review review : reviews) {
            result.add(new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt(), review.getImageUrl()));
        }

        return result;
    }

    @Transactional
    // 도서에 달려있는 개인 리뷰 조회
    public ReviewResponseDto getReview(Long bookId, Long userId) {
        Book book = getBook(bookId);
        Review review = getReview(book, userId);
        return new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt(), review.getImageUrl());
    }

    @Transactional
    // 도서에 달려있는 리뷰 페이징 처리 메소드
    public Page<ReviewResponseDto> getPagedReviewsByBookId(Long bookId, Pageable pageable) {
        Book book = getBook(bookId);
        Page<Review> reviews = reviewRepository.findAllByBookOrderByCreatedAtDesc(book, pageable);
        return reviews.map(review ->
                new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt(), review.getImageUrl()));
    }

    @Transactional
    // 도서에 달려있는 모든 리뷰 조회
    public List<ReviewResponseDto> getAllReviewsByBookId(Long bookId) {
        Book book = getBook(bookId);
        List<Review> reviews = reviewRepository.findAllByBookOrderByCreatedAtDesc(book);
        List<ReviewResponseDto> result = new ArrayList<>();
        for (Review review : reviews) {
            result.add(new ReviewResponseDto(review.getUserId(), review.getText(), review.getRating(), review.getCreatedAt(), review.getImageUrl()));
        }
        return result;
    }

    private Book getBook(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    private Review getReview(Book book, Long userId) {
        return reviewRepository.findByBookAndUserId(book, userId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
    }

}

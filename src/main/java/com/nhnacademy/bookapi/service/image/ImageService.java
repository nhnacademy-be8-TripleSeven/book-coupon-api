package com.nhnacademy.bookapi.service.image;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCoverImage;
import com.nhnacademy.bookapi.entity.BookImage;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookCoverImageRepository;
import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import java.util.List;

import com.nhnacademy.bookapi.service.object.ObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final BookCoverImageRepository bookCoverImageRepository;
    private final BookImageRepository bookImageRepository;
    private final BookRepository bookRepository;

    public void bookCoverSave(Image image, BookCoverImage bookCoverImage) {
        imageRepository.save(image);
        bookCoverImageRepository.save(bookCoverImage);
    }

    public void bookDetailSave(Image image, BookImage bookImage) {
        imageRepository.save(image);
        bookImageRepository.save(bookImage);
    }

    public List<String> getBookCoverImages(long bookId) {
        return imageRepository.findBookCoverImageByBookId(bookId);

    }

    public List<String> getBookDetailImages(long bookId) {
        return imageRepository.findBookImageByBookId(bookId);
    }

    @Transactional
    public void deleteBookCoverImageAndBookDetailImage(long bookId) {

       List<Image> imageIdByBookId = bookImageRepository.findImageByBookId(bookId);


        List<Image> imageByBookId = bookCoverImageRepository.findImageByBookId(bookId);

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("book not found"));
      
   
        ObjectService objectService = this.getObjectService(); // 토큰 발급이 완료된 객체
      
              // 2. BookImage 삭제
        if (imageIdByBookId != null && !imageIdByBookId.isEmpty()) {
            bookImageRepository.deleteByBookId(bookId); // JPQL DELETE 쿼리
            imageRepository.deleteAll(imageIdByBookId); // 삭제할 Image 리스트
            objectService.deleteObject("triple-seven", book.getIsbn13() + "_cover.jpg");
        }

        // 3. BookCoverImage 삭제
        if (imageByBookId != null && !imageByBookId.isEmpty()) {
            bookCoverImageRepository.deleteByBookId(bookId); // JPQL DELETE 쿼리
            imageRepository.deleteAll(imageByBookId); // 삭제할 Image 리스트
            objectService.deleteObject("triple-seven", book.getIsbn13() + "_detail.jpg");
        }


    }

    // 도서 이미지 삭제를 위한 ObjectService 객체 생성 및 토큰 발급
    private ObjectService getObjectService() {
        ObjectService objectService = new ObjectService("https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e");
        try {
            objectService.generateAuthToken("https://api-identity.infrastructure.cloud.toast.com/v2.0/tokens",
                    "c20e3b10d61749a2a52346ed0261d79e",
                    "rlgus4531@naver.com",
                    "team3");
        } catch (RuntimeException e) {
            throw new RuntimeException("토큰 발급에 실패했습니다: " + e.getMessage());
        }
        return objectService;


    }

}

package com.nhnacademy.bookapi.service.image;

import com.nhnacademy.bookapi.entity.BookCoverImage;
import com.nhnacademy.bookapi.entity.BookImage;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.repository.BookCoverImageRepository;
import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final BookCoverImageRepository bookCoverImageRepository;
    private final BookImageRepository bookImageRepository;

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


        // 2. BookImage 삭제
        if (imageIdByBookId != null && !imageIdByBookId.isEmpty()) {
            bookImageRepository.deleteByBookId(bookId); // JPQL DELETE 쿼리
            imageRepository.deleteAll(imageIdByBookId); // 삭제할 Image 리스트
        }

        // 3. BookCoverImage 삭제
        if (imageByBookId != null && !imageByBookId.isEmpty()) {
            bookCoverImageRepository.deleteByBookId(bookId); // JPQL DELETE 쿼리
            imageRepository.deleteAll(imageByBookId); // 삭제할 Image 리스트
        }
    }

}

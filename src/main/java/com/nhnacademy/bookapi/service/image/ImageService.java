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
    public void deleteBookCoverImageAndBookDeleteImage(long bookId) {
        Image imageIdByBookId = bookImageRepository.findImageByBookId(bookId);


        Image imageByBookId = bookCoverImageRepository.findImageByBookId(bookId);

        if(imageIdByBookId != null) {
            bookImageRepository.deleteByBookId(bookId);
            imageRepository.deleteById(imageIdByBookId.getId());
        }
        if(imageByBookId != null) {
            bookCoverImageRepository.deleteByBookId(bookId);
            imageRepository.deleteById(imageByBookId.getId());
        }


    }
}

package com.nhnacademy.bookapi.service.image;

import com.netflix.discovery.converters.Auto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final BookCoverImageRepository bookCoverImageRepository;
    private final BookImageRepository bookImageRepository;
    private final BookRepository bookRepository;
    @Autowired
    private ObjectService objectService;

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

    public Image getCoverImage(long bookId){
        return imageRepository.findCoverImageByBookId(bookId).orElse(null);
    }


    public Image getDetailImage(long bookId){
        return imageRepository.findDetailImageByBookId(bookId).orElse(null);
    }
    @Transactional
    public void deleteBookCoverImageAndBookDetailImage(long bookId) {

       List<Image> detailImages = bookImageRepository.findImageByBookId(bookId);


        List<Image> coverImages = bookCoverImageRepository.findImageByBookId(bookId);

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("book not found"));
      
   
        objectService.generateAuthToken();
      
              // 2. BookImage 삭제
        if (detailImages != null && !detailImages.isEmpty()) {
            bookImageRepository.deleteByBookId(bookId); // JPQL DELETE 쿼리
            imageRepository.deleteAll(detailImages); // 삭제할 Image 리스트
            objectService.deleteObject("triple-seven", book.getIsbn13() + "_detail.jpg");
        }

        // 3. BookCoverImage 삭제
        if (coverImages != null && !coverImages.isEmpty()) {
            bookCoverImageRepository.deleteByBookId(bookId); // JPQL DELETE 쿼리
            imageRepository.deleteAll(coverImages); // 삭제할 Image 리스트
            objectService.deleteObject("triple-seven", book.getIsbn13() + "_cover.jpg");
        }


    }
}

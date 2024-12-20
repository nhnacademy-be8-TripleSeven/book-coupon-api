package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book.CreateBookRequest;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.book.UpdateBookRequest;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDetail;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCoverImage;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.entity.BookIntroduce;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.entity.Publisher;
import com.nhnacademy.bookapi.entity.Role;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.exception.BookIndexNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.BookCoverImageRepository;
import com.nhnacademy.bookapi.repository.BookCreatorMapRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookIntroduceRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;
import com.nhnacademy.bookapi.service.book.BookService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCreatorRepository bookCreatorRepository;
    private final BookIndexRepository bookIndexRepository;
    private final BookIntroduceRepository bookIntroduceRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookImageRepository bookImageRepository;
    private final ImageRepository imageRepository;
    private final BookCreatorMapRepository bookCreatorMapRepository;
    private final PublisherRepository publisherRepository;
    private final BookCoverImageRepository bookCoverImageRepository;
    private final ElasticSearchBookSearchRepository elasticSearchBookSearchRepository;

    @Override
    public CreateBookRequest createBook(CreateBookRequest createBookRequest) {
        Book book = CreateBookRequest.createBook(createBookRequest);
        book = bookRepository.save(book);
        //이미지 저장
        String imageUrl = createBookRequest.getImageUrl();

        Image image = new Image();
        image.setUrl(imageUrl);
        image = imageRepository.save(image);
        BookCoverImage bookCoverImage = BookCoverImage.bookCoverImageMapper(image, book);
        bookCoverImageRepository.save(bookCoverImage);

        //출판사저장
        String publisher = createBookRequest.getPublisher();

        Publisher existsPublisher = publisherRepository.existsByName(publisher);
        if(existsPublisher == null) {
            Publisher newPub = new Publisher();
            newPub.setName(createBookRequest.getPublisher());
            book.setPublisher(newPub);
        }

        // 작가저장
        String author = createBookRequest.getAuthor();

        BookCreator bookCreator = new BookCreator();
        bookCreator.setName(author);
        bookCreator.setRole(Role.AUTHOR);
        bookCreatorRepository.save(bookCreator);

        BookCreatorMap bookCreatorMap = new BookCreatorMap();
        bookCreatorMap.setBook(book);
        bookCreatorMap.setCreator(bookCreator);
        bookCreatorMapRepository.save(bookCreatorMap);

        return null;
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public UpdateBookRequest update(UpdateBookRequest request) {

        Book book = bookRepository.findById(request.getBookId()).orElse(null);
        if(book == null) {
            throw new BookNotFoundException("Book not found");
        }
        String title = request.getTitle();

        int price = request.getPrice();
        LocalDate publishedDate = request.getPublishedDate();

        book.setTitle(title);
        book.setRegularPrice(price);

        String bookIntroduction = request.getBookIntroduction();
        BookIntroduce bookIntroduce = BookIntroduce.bookIntroduceCreate(bookIntroduction, book);
        bookIntroduceRepository.save(bookIntroduce);

        String categories = request.getCategory();

        List<Category> categoryByBook = bookCategoryRepository.findCategoryByBook(book);



        return request;
    }


    @Override
    public void delete(Long id) {
        boolean exist = bookRepository.existsById(id);
        if(!exist) {
            throw new BookNotFoundException("book not found");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public SearchBookDetail searchBookDetailByBookId(Long id) {

        SearchBookDetail searchBookDetail = bookRepository.searchBookById(id).orElse(null);
        if(searchBookDetail == null) {
            throw new BookNotFoundException("Book not found");
        }

        List<BookCreatorDetail> creatorDetails = bookCreatorRepository.findCreatorByBookId(id).stream()
            .map(bookCreator -> {
                BookCreatorDetail bookCreatorDetail = new BookCreatorDetail();
                bookCreatorDetail.setName(bookCreator.getName());
                bookCreatorDetail.setRole(bookCreator.getRole().getDescription());
                return bookCreatorDetail;
            })
            .collect(Collectors.toList());

        // bookCreator -> bookCreatorDetails 변환 role을 한글로 변환

        if(creatorDetails.isEmpty()) {
            throw new BookCreatorNotFoundException("BookCreator not found");
        }
        searchBookDetail.setBookCreators(creatorDetails);

//        List<BookIndex> byBookId = bookIndexRepository.findByBookId(id);

//        searchBookDetail.setBookIndices(byBookId);

        return searchBookDetail;
    }

    /*
    아래 메서드 Dto로 수정해야함
    */

    // 타이틀 또는 작가 이름으로 검색
    public Page<BookDocument> searchByTitleOrAuthor(String keyword, Pageable pageable) {
        return elasticSearchBookSearchRepository.findByTitleContaining(keyword, keyword, pageable);
    }

    // 조건별 검색
    public Page<BookDocument> searchByCondition(String condition, String keyword, Pageable pageable) {
        switch (condition) {
            case "title":
                return elasticSearchBookSearchRepository.findByTitleContaining(keyword, pageable);
            case "author":
                return elasticSearchBookSearchRepository.findByBookcreatorContaining(keyword, pageable);
            case "publisher":
                return elasticSearchBookSearchRepository.findByPublisherNameContaining(keyword, pageable);
            case "isbn":
                return elasticSearchBookSearchRepository.findByIsbn13(keyword, pageable);
            default:
                throw new IllegalArgumentException("Invalid search condition: " + condition);
        }
    }

}

package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book_index.BookIndexResponseDto;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.CreateBookRequest;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;
import com.nhnacademy.bookapi.dto.book.UpdateBookRequest;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDetail;
import com.nhnacademy.bookapi.elasticsearch.document.BookDocument;
import com.nhnacademy.bookapi.elasticsearch.repository.ElasticSearchBookSearchRepository;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.BookCreatorNotFoundException;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
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
    private final BookCreatorService bookCreatorService;
    private final BookTagRepository bookTagRepository;

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
        if (existsPublisher == null) {
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
        if (book == null) {
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
        if (!exist) {
            throw new BookNotFoundException("book not found");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public SearchBookDetail searchBookDetailByBookId(Long id) {
        Book book = bookRepository.findBookWithPublisherById(id).orElseThrow(() -> new BookNotFoundException("book not found"));
        BookImage bookImage = bookImageRepository.findFirstByBookOrderByIdAsc(book).orElse(null);
        String imageUrl = bookImage.getImage().getUrl(); // 이미지의 Url
        List<BookCreatorMap> bookCreatorMaps = bookCreatorMapRepository.findByBook(book);
        List<BookCreatorDetail> bookCreators = new ArrayList<>();
        for (BookCreatorMap bookCreatorMap : bookCreatorMaps) {
            bookCreators.add(new BookCreatorDetail(bookCreatorMap.getCreator().getName(), bookCreatorMap.getCreator().getRole().toString()));
        }

        SearchBookDetail searchBookDetail = new SearchBookDetail(book.getTitle(), book.getDescription(),
                book.getPublishDate(), book.getRegularPrice(), book.getSalePrice(), book.getIsbn13(),
                book.getStock(), book.getPage(), imageUrl, book.getPublisher().getName());
        searchBookDetail.setBookCreators(bookCreators);

        List<BookCategory> bookCategories = bookCategoryRepository.findAllByBook(book);
        List<List<String>> categoryHierarchies = new ArrayList<>();

        for (BookCategory bookCategory : bookCategories) {
            Category category = bookCategory.getCategory(); // 하나의 카테고리 객체
            List<String> hierarchy = getCategoryHierarchy(category);
            categoryHierarchies.add(hierarchy);
        }

        StringBuilder categories = getCategoryResult(categoryHierarchies.getLast());
        searchBookDetail.setCategories(categories);

        List<BookTag> bookTags = bookTagRepository.findAllByBookWithTags(book);

        return searchBookDetail;
    }

    private StringBuilder getCategoryResult(List<String> categoryHierarchies) {
        StringBuilder categoryResult = new StringBuilder();
        for (String categoryName : categoryHierarchies) {
            categoryResult.append(categoryName).append(">");
        }
        categoryResult.deleteCharAt(categoryResult.length() - 1);
        return categoryResult;
    }

    private List<String> getCategoryHierarchy(Category category) {
        List<String> hierarchy = new ArrayList<>();
        while (category != null) {
            hierarchy.add(0, category.getName());
            category = category.getParent();
        }
        return hierarchy;
    }

    // 이달의 베스트 페이징을 사용하지 않고 캐싱으로
    public Page<BookDetailResponseDTO> getMonthlyBestBooks() {
        Pageable pageable = Pageable.ofSize(10);

        Page<BookDetailResponseDTO> bookTypeBestsellerByRankAsc = bookRepository.findBookTypeBestsellerByRankAsc(pageable);

        for (BookDetailResponseDTO bookDetailResponseDTO : bookTypeBestsellerByRankAsc) {
            long id = bookDetailResponseDTO.getId();
            BookCreatorResponseDTO bookCreatorResponseDTO = bookCreatorService.BookCreatorListByBookId(
                    id);
            bookDetailResponseDTO.setCreator(bookCreatorResponseDTO.getCreators());
        }
        return bookTypeBestsellerByRankAsc;

    }

    //type별 조회 이도서는 어때요? , 편집자의 선택, e북
    public Page<BookDetailResponseDTO> getBookTypeBooks(Type bookType, Pageable pageable) {

        Page<BookDetailResponseDTO> bookTypeItemByType = bookRepository.findBookTypeItemByType(
                bookType, pageable);
        for (BookDetailResponseDTO bookDetailResponseDTO : bookTypeItemByType) {
            long id = bookDetailResponseDTO.getId();
            BookCreatorResponseDTO bookCreatorResponseDTO = bookCreatorService.BookCreatorListByBookId(id);
            bookDetailResponseDTO.setCreator(bookCreatorResponseDTO.getCreators());
        }
        return bookTypeItemByType;
    }


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

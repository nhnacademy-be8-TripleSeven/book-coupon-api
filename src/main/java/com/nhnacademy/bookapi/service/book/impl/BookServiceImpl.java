package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book.BookSearchResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.dto.book_index.BookIndexResponseDto;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.CreateBookRequestDTO;
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

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final BookTypeRepository bookTypeRepository;

    private final EntityManager em;

    @Override
    public CreateBookRequestDTO createBook(CreateBookRequestDTO createBookRequest) {
        Book book = new Book();
        book.create(createBookRequest.getTitle(), createBookRequest.getDescription(), createBookRequest.getPublicationDate(), createBookRequest.getRegularPrice()
                , createBookRequest.getSalePrice(), createBookRequest.getIsbn(), createBookRequest.getStock(), createBookRequest.getPages(), null);
        book = bookRepository.save(book);
        //이미지 저장
        String imageUrl = createBookRequest.getImageUrl();

        Image image = new Image(imageUrl);
        image = imageRepository.save(image);
        BookCoverImage bookCoverImage = new BookCoverImage(image, book);
        bookCoverImageRepository.save(bookCoverImage);

        //출판사저장
        String publisher = createBookRequest.getPublisher();


        Publisher existsPublisher = publisherRepository.findByName(publisher);

        if (existsPublisher == null) {
            Publisher newPub = new Publisher(createBookRequest.getPublisher());
            book.publisherUpdate(newPub);
        } else {
            book.publisherUpdate(existsPublisher);
        }
        // 작가저장
        String author = createBookRequest.getAuthor();

        BookCreator bookCreator = new BookCreator(author, Role.AUTHOR);

        bookCreatorRepository.save(bookCreator);

        BookCreatorMap bookCreatorMap = new BookCreatorMap(book, bookCreator);

        bookCreatorMapRepository.save(bookCreatorMap);

        return createBookRequest;
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

        book.update(title, publishedDate, price);

        String bookIntroduction = request.getBookIntroduction();
        BookIntroduce bookIntroduce = new BookIntroduce(bookIntroduction, book);
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

    private List<BookCreatorDetail> getBookCreators(List<BookCreatorMap> bookCreatorMap) {
        List<BookCreatorDetail> bookCreatorDetails = new ArrayList<>();
        for (BookCreatorMap bookCreatorMap1 : bookCreatorMap) {
            bookCreatorDetails.add(new BookCreatorDetail(bookCreatorMap1.getCreator().getName(), bookCreatorMap1.getCreator().getRole().toString()));
        }
        return bookCreatorDetails;
    }
    @Transactional(readOnly = true)
    @Override
    public SearchBookDetail searchBookDetailByBookId(Long id) {
        Book book = bookRepository.findBookWithPublisherById(id).orElseThrow(() -> new BookNotFoundException("book not found"));
        BookCoverImage bookCoverImage = bookCoverImageRepository.findByBook(book);
        String imageUrl = bookCoverImage != null ? bookCoverImage.getImage().getUrl() : null;
        List<BookCreatorMap> bookCreatorMaps = bookCreatorMapRepository.findByBook(book);
        SearchBookDetail searchBookDetail = new SearchBookDetail(book.getTitle(), book.getDescription(),
                book.getPublishDate(), book.getRegularPrice(), book.getSalePrice(), book.getIsbn13(),
                book.getStock(), book.getPage(), imageUrl, book.getPublisher().getName());
        searchBookDetail.setBookCreators(getBookCreators(bookCreatorMaps));

        List<BookCategory> bookCategories = bookCategoryRepository.findAllByBook(book);
        List<List<String>> categoryHierarchies = new ArrayList<>();

        if (!bookCategories.isEmpty()) {
            for (BookCategory bookCategory : bookCategories) {
                Category category = bookCategory.getCategory(); // 하나의 카테고리 객체
                List<String> hierarchy = getCategoryHierarchy(category);
                categoryHierarchies.add(hierarchy);
            }
            StringBuilder categories = getCategoryResult(categoryHierarchies.getLast());
            searchBookDetail.setCategories(categories);
        }

        List<BookTag> bookTags = bookTagRepository.findAllByBookWithTags(book);
        searchBookDetail.setTags(getBookTags(bookTags));
        BookIndex bookIndex = bookIndexRepository.findByBook(book).orElse(null);
        searchBookDetail.setBookIndex(getBookIndex(bookIndex));
        List<BookType> bookTypes = bookTypeRepository.findAllByBook(book);
        searchBookDetail.setBookTypes(getBookTypes(bookTypes));
        List<BookImage> bookImages = bookImageRepository.findAllByBookWithImage(book);
        searchBookDetail.setDetailImages(getDetailImages(bookImages));
        return searchBookDetail;
    }

    private List<String> getDetailImages(List<BookImage> bookImages) {
        //https://image.yes24.com/momo/Noimg_XL.gif -> 이미지 없은 url
        List<String> detailImages = new ArrayList<>();
        for (BookImage bookImage : bookImages) {
            if (!bookImage.getImage().getUrl().contains("Noimg_XL")) {
                detailImages.add(bookImage.getImage().getUrl());
            }
        }
        return detailImages;
    }
    private StringBuilder getBookTypes(List<BookType> bookTypes) {
        StringBuilder types = new StringBuilder();
        for (BookType bookType : bookTypes) {
            types.append(bookType.getTypes()).append(",");
        }
        types.deleteCharAt(types.lastIndexOf(","));
        return types;
    }

    private String getBookIndex(BookIndex bookIndex) {
        if (Objects.isNull(bookIndex)) {
            return "";
        }
        return bookIndex.getIndexes();
    }

    private StringBuilder getBookTags(List<BookTag> bookTags) {
        StringBuilder tags = new StringBuilder();
        for (BookTag bookTag : bookTags) {
            tags.append("#").append(bookTag.getTag().getName()).append(",");
        }
        return tags;
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

    @Override
    public Page<BookDetailResponseDTO> getCategorySearchBooks(List<String> categories,
        String keyword, Pageable pageable) {
        Page<BookDetailResponseDTO> byCategoryAndTitle = bookRepository.findByCategoryAndTitle(
            categories, keyword, pageable);

        return byCategoryAndTitle;
    }




}

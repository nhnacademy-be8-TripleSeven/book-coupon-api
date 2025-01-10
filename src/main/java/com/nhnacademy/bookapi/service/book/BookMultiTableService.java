package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.BookCreatDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.dto.book_type.BookTypeDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDTO;
import com.nhnacademy.bookapi.dto.category.CategoryDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCoverImage;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.BookImage;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.entity.BookPopularity;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.entity.Publisher;
import com.nhnacademy.bookapi.entity.Role;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.BookCouponRepository;
import com.nhnacademy.bookapi.repository.BookPopularityRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;
import com.nhnacademy.bookapi.repository.ReviewRepository;
import com.nhnacademy.bookapi.repository.WrapperRepository;
import com.nhnacademy.bookapi.service.book_index.BookIndexService;
import com.nhnacademy.bookapi.service.book_type.BookTypeService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;
import com.nhnacademy.bookapi.service.category.CategoryService;
import com.nhnacademy.bookapi.service.image.ImageService;
import com.nhnacademy.bookapi.service.object.ObjectService;
import com.nhnacademy.bookapi.service.review.ReviewService;
import com.nhnacademy.bookapi.service.tag.TagService;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BookMultiTableService {

    //여기부터는 object storage에 이미지를 올리기 위한 필드 변수, 아래 변수들은 고정값이다.
    private final String storageUrl = "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e";
    private final String authUrl = "https://api-identity.infrastructure.cloud.toast.com/v2.0/tokens";
    private final String tenantId = "c20e3b10d61749a2a52346ed0261d79e";
    private final String username = "rlgus4531@naver.com";
    private final String password = "team3";
    private final String containerName = "triple-seven";

    private final BookService bookService;
    private final ImageService imageService;
    private final CategoryService categoryService;
    private final BookCreatorService bookCreatorService;
    private final TagService tagService;
    private final BookIndexService bookIndexService;
    private final BookTypeService bookTypeService;
    private final PublisherRepository publisherRepository;
    private final BookCouponRepository couponRepository;
    private final ReviewRepository reviewRepository;
    private final WrapperRepository wrapperRepository;
    private final BookCouponRepository bookCouponRepository;
    private final BookPopularityRepository popularityRepository;
    private final BookPopularityRepository bookPopularityRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final ReviewService reviewService;

    @Transactional(readOnly = true)
    public BookDTO getAdminBookById(Long id) {
        BookDTO bookById = bookService.getBookById(id);
        Long bookId = bookById.getId();
        bookById.addImage(imageService.getBookCoverImages(bookId), imageService.getBookDetailImages(bookId));
        bookById.addCategory(categoryService.updateCategoryList(bookId));
        bookById.addAuthor(bookCreatorService.bookCreatorList(bookId));
        bookById.addTags(tagService.getTagName(bookId));
        bookById.addBookType(bookTypeService.getUpdateBookTypeList(bookId));
        bookById.addIndex(bookIndexService.getBookIndexList(bookId));

        return bookById;
    }

    @Transactional(readOnly = true)
    public Page<BookDTO> getAdminBookSearch(String keyword, Pageable pageable) {
        Page<BookDTO> bookList = bookService.getBookList(keyword, pageable);

        for (BookDTO bookUpdateDTO : bookList) {
            bookUpdateDTO.addImage(imageService.getBookCoverImages(
                bookUpdateDTO.getId()), imageService.getBookDetailImages(
                bookUpdateDTO.getId()));
            bookUpdateDTO.addCategory(categoryService.updateCategoryList(
                bookUpdateDTO.getId()));
            bookUpdateDTO.addAuthor(bookCreatorService.bookCreatorList(bookUpdateDTO.getId()));
            bookUpdateDTO.addTags(tagService.getTagName(bookUpdateDTO.getId()));
            bookUpdateDTO.addBookType(bookTypeService.getUpdateBookTypeList(bookUpdateDTO.getId()));
            bookUpdateDTO.addIndex(bookIndexService.getBookIndexList(bookUpdateDTO.getId()));
        }


        return bookList;
    }

    @Transactional
    public void updateBook(BookUpdateDTO bookUpdateDTO) throws IOException {
        //object storage에 저장
        ObjectService objectService = new ObjectService(storageUrl);
        objectService.generateAuthToken(authUrl, tenantId, username, password); // 토큰 발급

        Book book = bookService.getBook(bookUpdateDTO.getId());
        book.update(bookUpdateDTO.getTitle(),bookUpdateDTO.getIsbn(), bookUpdateDTO.getPublishedDate(),
            bookUpdateDTO.getRegularPrice(),bookUpdateDTO.getSalePrice(),bookUpdateDTO.getDescription());

        List<MultipartFile> bookCoverImages = Optional.ofNullable(bookUpdateDTO.getCoverImage()).orElse(Collections.emptyList());
        for (MultipartFile bookCoverImage : bookCoverImages) {
            String path = uploadCoverImageToStorage(objectService, bookCoverImage,
                bookUpdateDTO.getIsbn() + "_cover.jpg");
            imageService.deleteBookCoverImage(path);
            Image image = new Image(path);
            BookCoverImage coverImage = new BookCoverImage(image, book);
            imageService.bookCoverSave(image, coverImage);
        }
        List<MultipartFile> detailImages = Optional.ofNullable(bookUpdateDTO.getDetailImage()).orElse(Collections.emptyList());
        for (MultipartFile detailImage : detailImages) {
            String path = uploadCoverImageToStorage(objectService, detailImage, bookUpdateDTO.getIsbn() + "_detail.jpg");
            imageService.deleteBookDetailImage(path);
            Image image = new Image(path);
            BookImage bookImage = new BookImage();
            imageService.bookDetailSave(image, bookImage);
        }

        List<CategoryDTO> categories = bookUpdateDTO.getCategories();
        Category categoryById = null;
        for (CategoryDTO categoryDTO : categories) {
            // 현재 카테고리를 데이터베이스에서 조회
            categoryById = categoryService.getCategoryById(categoryDTO.getId());

            Category parentCategory = null;
            if (categoryDTO.getParent() != null) {
                parentCategory = categoryService.getCategoryById(categoryDTO.getParent().getId()); // 부모 조회
            }// 부모 조회
            if (categoryById != null) {
                // 이미 존재하는 경우 이름과 부모 업데이트
                categoryById.update(categoryDTO.getName(), categoryDTO.getLevel(), parentCategory);
            } else {
                // 존재하지 않는 경우 새로 생성
                categoryById = new Category(categoryDTO.getName(), categoryDTO.getLevel(), parentCategory);
                BookCategory bookCategory = new BookCategory(book, categoryById);
                categoryService.categorySave(categoryById, bookCategory);
            }
        }

        List<BookCreatorDTO> authors = bookUpdateDTO.getAuthors();
        for (BookCreatorDTO bookCreatorDTO : authors) {
            BookCreator bookCreatorByCreatorId = bookCreatorService.getBookCreatorByCreatorId(
                bookCreatorDTO.getId());
            if(bookCreatorByCreatorId != null) {
                bookCreatorByCreatorId.update(bookCreatorDTO.getName(),
                    Role.valueOf(bookCreatorDTO.getRole().toUpperCase(Locale.ROOT)));
            }
            BookCreator bookCreator = new BookCreator(bookCreatorDTO.getName(),
                Role.valueOf(bookCreatorDTO.getRole().toUpperCase()));
            BookCreatorMap bookCreatorMap = new BookCreatorMap(book, bookCreator);
            bookCreatorService.saveBookCreator(bookCreator, bookCreatorMap);
        }
        if(!bookUpdateDTO.getIndex().isEmpty()) {
            BookIndex bookIndex = bookIndexService.getBookIndex(bookUpdateDTO.getId());
            if(bookIndex == null) {
                bookIndex = new BookIndex(bookUpdateDTO.getIndex(), book);
                bookIndexService.createBookIndex(bookIndex);
            }else {
                bookIndex.updateIndexText(bookUpdateDTO.getIndex());
            }
        }

        List<BookType> bookTypeByBookId = bookTypeService.getBookTypeByBookId(
            bookUpdateDTO.getId());

        List<BookTypeDTO> bookTypes = bookUpdateDTO.getBookTypes();
        for (BookType bookType : bookTypeByBookId) {
            bookTypeService.deleteBookType(bookType.getId());
        }
        for (BookTypeDTO type : bookTypes) {
            BookType bookType = new BookType(type.getType(), type.getRanks(), book);
            bookTypeService.createBookType(bookType);
        }
    }

    @Transactional
    public void createBook(BookCreatDTO bookCreatDTO) throws IOException {

        //object storage에 저장
        ObjectService objectService = new ObjectService(storageUrl);
        objectService.generateAuthToken(authUrl, tenantId, username, password); // 토큰 발급

        boolean existed = bookService.existsBookByIsbn(bookCreatDTO.getIsbn());
        if(existed){
            throw new IllegalArgumentException("Book with ISBN " + bookCreatDTO.getIsbn() + " already exists.");
        }

        Book book = new Book(
            bookCreatDTO.getTitle(),
            bookCreatDTO.getDescription(),
            bookCreatDTO.getPublishedDate(),
            bookCreatDTO.getRegularPrice(),
            bookCreatDTO.getSalePrice(),
            bookCreatDTO.getIsbn(),
            bookCreatDTO.getStock(),
            bookCreatDTO.getPage());

        bookService.createBook(book);

        Publisher byName = publisherRepository.findByName(bookCreatDTO.getPublisherName());
        if (byName != null) {
            book.createPublisher(byName);
        }else {
            Publisher publisher = new Publisher(bookCreatDTO.getPublisherName());
            publisherRepository.save(publisher);
            book.createPublisher(publisher);
        }

        List<BookCreatorDTO> authors = bookCreatDTO.getAuthors();
        for (BookCreatorDTO author : authors) {
            BookCreator bookCreatorByCreatorId = bookCreatorService.getBookCreatorByName(
                author.getName());
            if(bookCreatorByCreatorId != null) {
                BookCreatorMap bookCreatorMap = new BookCreatorMap(book, bookCreatorByCreatorId);
                bookCreatorService.saveBookCreatorMap(bookCreatorMap);
            }else {
                BookCreator bookCreator = new BookCreator(author.getName(),
                    Role.valueOf(author.getRole().toUpperCase()));
                BookCreatorMap bookCreatorMap = new BookCreatorMap(book, bookCreator);
                bookCreatorService.saveBookCreator(bookCreator, bookCreatorMap);
            }

        }

        List<BookTypeDTO> bookTypes = bookCreatDTO.getBookTypes();
        for (BookTypeDTO bookType : bookTypes) {


            BookType type = new BookType(bookType.getType(), bookType.getRanks(), book);
            bookTypeService.createBookType(type);
        }

        List<CategoryDTO> categories = bookCreatDTO.getCategories();
        if(categories.isEmpty()){
            throw new IllegalArgumentException("Book does not have any categories.");
        }
        for (CategoryDTO categoryDTO : categories) {
            // 카테고리 이름으로 조회
            Category category = categoryService.getCategoryByName(categoryDTO.getName());

            if (category != null) {
                // 이미 존재하는 경우 BookCategory만 저장
                BookCategory bookCategory = new BookCategory(book, category);
                categoryService.bookCategorySave(bookCategory);
            } else {
                // 부모 카테고리를 설정 (ID가 존재하는 경우만)
                Category parentCategory = null;
                if (categoryDTO.getParent() != null && categoryDTO.getParent().getId() != null) {
                    parentCategory = categoryService.getCategoryById(categoryDTO.getParent().getId());
                }

                // 새 카테고리 생성
                category = new Category(categoryDTO.getName(), categoryDTO.getLevel(), parentCategory);
                BookCategory bookCategory = new BookCategory(book, category);
                // 카테고리와 BookCategory 저장
                categoryService.categorySave(category, bookCategory);
            }
        }



        BookIndex bookIndex = new BookIndex(bookCreatDTO.getIndex(), book);
        bookIndexService.createBookIndex(bookIndex);

        BookPopularity bookPopularity = new BookPopularity(book, 0, 0, 0);
        bookPopularityRepository.save(bookPopularity);

        List<MultipartFile> coverImages = Optional.ofNullable(bookCreatDTO.getCoverImages())
            .orElse(Collections.emptyList());
        if(!coverImages.isEmpty()) {
            for (MultipartFile multipartFile : coverImages) {
                String path = uploadCoverImageToStorage(objectService, multipartFile,
                    bookCreatDTO.getIsbn() + "_cover.jpg");
                Image image = new Image(path);
                BookCoverImage bookCoverImage = new BookCoverImage(image, book);
                imageService.bookCoverSave(image, bookCoverImage);
            }
        }
        List<MultipartFile> detailImage = Optional.ofNullable(bookCreatDTO.getDetailImages())
            .orElse(Collections.emptyList());
        if(!detailImage.isEmpty()) {
            for (MultipartFile multipartFile : detailImage) {
                String path = uploadCoverImageToStorage(objectService, multipartFile,
                    bookCreatDTO.getIsbn() + "_detail.jpg");
                Image image = new Image(path);
                BookImage bookImage = new BookImage(book, image);
                imageService.bookDetailSave(image, bookImage);
            }
        }



    }
    @Transactional
    public void deleteBook(long bookId) {
        // Book Type 삭제
        bookTypeService.deleteBookType(bookId);

        // Book Index 삭제
        bookIndexService.deleteIndex(bookId);

        // Book Creator 삭제
        bookCreatorService.deleteBookCreatorMap(bookId);

        // Book Category 삭제
        bookCategoryRepository.deleteAllByBookId(bookId);

        // Book Cover Image 삭제
        imageService.deleteBookCoverImageAndBookDetailImage(bookId);

        // Tags 삭제
        tagService.deleteBookTag(bookId);

        // 리뷰 삭제
        reviewService.deleteAllReviewsWithBook(bookId);

        // Book Coupon 삭제
        bookCouponRepository.deleteByBookId(bookId);

        // Wrapper 삭제
        wrapperRepository.deleteByBookId(bookId);

        // Book Popularity 삭제
        bookPopularityRepository.deleteByBookId(bookId);

        // Book 삭제
        bookService.deleteBook(bookId);
    }

    //object storage에 이미지 업로드 메소드
    public String uploadCoverImageToStorage(ObjectService objectService, MultipartFile imageFile, String objectName)
        throws IOException {
        InputStream inputStream = imageFile.getInputStream();
        objectService.uploadObject(containerName, objectName, inputStream);
        return storageUrl + "/" + containerName + "/" + objectName;
    }


    public MultipartFile loadImageTOStorage(ObjectService objectService, String objectName) {
        return objectService.loadImageFromStorage(containerName, objectName);
    }
}

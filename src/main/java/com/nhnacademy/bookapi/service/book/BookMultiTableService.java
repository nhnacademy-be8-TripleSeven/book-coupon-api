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
import com.nhnacademy.bookapi.entity.Type;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookMultiTableService {

    private final ObjectService objectService;

    //여기부터는 object storage에 이미지를 올리기 위한 필드 변수, 아래 변수들은 고정값이다.
    private final String storageUrl = "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e";
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
    private final ObjectService objectService;

    public BookMultiTableService(BookService bookService, ImageService imageService,
        CategoryService categoryService, BookCreatorService bookCreatorService,
        TagService tagService, BookIndexService bookIndexService, BookTypeService bookTypeService,
        PublisherRepository publisherRepository, BookCouponRepository couponRepository,
        ReviewRepository reviewRepository, WrapperRepository wrapperRepository,
        BookCouponRepository bookCouponRepository, BookPopularityRepository popularityRepository,
        BookPopularityRepository bookPopularityRepository,
        BookCategoryRepository bookCategoryRepository, ReviewService reviewService) {
        this.bookService = bookService;
        this.imageService = imageService;
        this.categoryService = categoryService;
        this.bookCreatorService = bookCreatorService;
        this.tagService = tagService;
        this.bookIndexService = bookIndexService;
        this.bookTypeService = bookTypeService;
        this.publisherRepository = publisherRepository;
        this.couponRepository = couponRepository;
        this.reviewRepository = reviewRepository;
        this.wrapperRepository = wrapperRepository;
        this.bookCouponRepository = bookCouponRepository;
        this.popularityRepository = popularityRepository;
        this.bookPopularityRepository = bookPopularityRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.reviewService = reviewService;
        objectService = new ObjectService(storageUrl);
    }

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
        objectService.generateAuthToken();

        Book book = bookService.getBook(bookUpdateDTO.getId());
        book.update(bookUpdateDTO.getTitle(),bookUpdateDTO.getIsbn(), bookUpdateDTO.getPublishedDate(),
            bookUpdateDTO.getRegularPrice(),bookUpdateDTO.getSalePrice(),bookUpdateDTO.getDescription());


        for (MultipartFile multipartFile : coverImages) {
            Image coverImage = imageService.getCoverImage(book.getId());
            String path = uploadCoverImageToStorage(objectService, multipartFile, isbn + "_cover.jpg");
            if (coverImage != null) {
                coverImage.update(path);

            }else {
                Image newImage = new Image(path);
                BookCoverImage bookCoverImage = new BookCoverImage(newImage, book);
                imageService.bookCoverSave(newImage, bookCoverImage);

            }
        }

    }
  
   private void bookCoverImageUpdateOrCreate(List<MultipartFile> coverImages, Book book, String isbn)
        throws IOException {

        for (MultipartFile multipartFile : coverImages) {
            Image coverImage = imageService.getCoverImage(book.getId());
            String path = uploadCoverImageToStorage(objectService, multipartFile, isbn + "_cover.jpg");
            if (coverImage != null) {
                coverImage.update(path);
            }else {
                Image newImage = new Image(path);
                BookCoverImage bookCoverImage = new BookCoverImage(newImage, book);
                imageService.bookCoverSave(newImage, bookCoverImage);
            }
        }

    }
  
  
  


    private void bookDetailImageUpdateOrCreate(List<MultipartFile> detailImages, Book book, String isbn)
        throws IOException {

        for (MultipartFile multipartFile : detailImages) {
            Image detailImage = imageService.getDetailImage(book.getId());
            String path = uploadCoverImageToStorage(objectService, multipartFile, isbn + "_detail.jpg");
            if (detailImage != null) {
                detailImage.update(path);
            }else {
                Image newImage = new Image(path);
                BookImage bookImage = new BookImage(book, newImage);
                imageService.bookDetailSave(newImage, bookImage);
            }
        }

    }

    private void categoryUpdateOrCreate(List<CategoryDTO> categoryList, Book book) {
        Category category = null;
        for (CategoryDTO categoryDTO : categoryList) {
            if (categoryDTO.getId() != null){
                category = categoryService.getCategoryById(categoryDTO.getId());
            }
            Category parentCategory = null;
            if(categoryDTO.getParent() != null){
                parentCategory = categoryService.getCategoryById(categoryDTO.getParent().getId());
            }
            if(category != null){
                category.update(categoryDTO.getName(), categoryDTO.getLevel(), parentCategory);
            }else {
                category = new Category(categoryDTO.getName(), categoryDTO.getLevel(), parentCategory);
                BookCategory bookCategory = new BookCategory(book, category);
                categoryService.categorySave(category, bookCategory);
            }

        }
    }

    private void creatorUpdateOrCreate(List<BookCreatorDTO> creatorList, Book book) {
        for (BookCreatorDTO bookCreatorDTO : creatorList) {
            BookCreator bookCreatorByCreatorId = null;
            if(bookCreatorDTO.getId() != null){
                bookCreatorByCreatorId = bookCreatorService.getBookCreatorByCreatorId(
                    bookCreatorDTO.getId());
            }
            if(bookCreatorByCreatorId != null) {
                bookCreatorByCreatorId.update(bookCreatorDTO.getName(),
                    Role.valueOf(bookCreatorDTO.getRole().toUpperCase(Locale.ROOT)));
            }else {
                BookCreator bookCreator = new BookCreator(bookCreatorDTO.getName(),
                    Role.valueOf(bookCreatorDTO.getRole().toUpperCase()));
                BookCreatorMap bookCreatorMap = new BookCreatorMap(book, bookCreator);
                bookCreatorService.saveBookCreator(bookCreator, bookCreatorMap);
            }
        }
    }

    private void indexCreateOrUpdate(String index ,Book book) {

        if(index != null){
            BookIndex indexBook = bookIndexService.getBookIndex(book.getId());
            if(indexBook != null){
                indexBook.updateIndexText(index);
            }else {
                BookIndex bookIndex = new BookIndex(index, book);
                bookIndexService.createBookIndex(bookIndex);
            }
        }
    }

    private void bookTypeUpdateOrCreate(List<BookType> bookTypeList,List<BookTypeDTO> bookTypeDTOList, Book book) {

        int index = 0;
        if(!bookTypeDTOList.isEmpty()) {
            for (BookType bookType : bookTypeList) {
                BookTypeDTO bookTypeDTO = bookTypeDTOList.get(index);
                bookType.update(Type.valueOf(bookTypeDTO.getType()), bookTypeDTO.getRanks(), book);
                index++;
            }
        }
    }

    @Transactional
    public void updateBook(BookUpdateDTO bookUpdateDTO) throws IOException {

        Book book = bookService.getBook(bookUpdateDTO.getId());
        book.update(bookUpdateDTO.getTitle(),bookUpdateDTO.getIsbn(), bookUpdateDTO.getPublishedDate(),
            bookUpdateDTO.getRegularPrice(),bookUpdateDTO.getSalePrice(),bookUpdateDTO.getDescription());

        List<MultipartFile> bookCoverImages = Optional.ofNullable(bookUpdateDTO.getCoverImage()).orElse(Collections.emptyList());
        bookCoverImageUpdateOrCreate(bookCoverImages, book, bookUpdateDTO.getIsbn());

        List<MultipartFile> detailImages = Optional.ofNullable(bookUpdateDTO.getDetailImage()).orElse(Collections.emptyList());
        bookDetailImageUpdateOrCreate(detailImages, book, bookUpdateDTO.getIsbn());

        List<CategoryDTO> categories = bookUpdateDTO.getCategories();
        categoryUpdateOrCreate(categories, book);

        List<BookCreatorDTO> authors = bookUpdateDTO.getAuthors();
        creatorUpdateOrCreate(authors, book);
        indexCreateOrUpdate(bookUpdateDTO.getIndex(), book);
        List<BookType> bookTypeByBookId = bookTypeService.getBookTypeByBookId(
            bookUpdateDTO.getId());
        bookTypeUpdateOrCreate(bookTypeByBookId, bookUpdateDTO.getBookTypes(), book);
    }


    private void publisherCreate(String publisher, Book book) {
        Publisher byName = publisherRepository.findByName(publisher);
        if (byName != null) {
            book.createPublisher(byName);
        }else {
            Publisher newPublisher = new Publisher(publisher);
            publisherRepository.save(newPublisher);
            book.createPublisher(newPublisher);
        }
    }

    @Transactional
    public void createBook(BookCreatDTO bookCreatDTO) throws IOException {

        //object storage에 저장
        objectService.generateAuthToken();


        boolean existed = bookService.existsBookByIsbn(bookCreatDTO.getIsbn());
        if(existed){
            return;
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

        publisherCreate(bookCreatDTO.getPublisherName(), book);

        List<BookCreatorDTO> authors = bookCreatDTO.getAuthors();

        creatorUpdateOrCreate(authors, book);

        List<BookTypeDTO> bookTypes = bookCreatDTO.getBookTypes();
        List<BookType> bookTypeList = new ArrayList<>();
        bookTypeUpdateOrCreate(bookTypeList, bookTypes ,book);


        List<CategoryDTO> categories = bookCreatDTO.getCategories();

        categoryUpdateOrCreate(categories, book);

        indexCreateOrUpdate(bookCreatDTO.getIndex(), book);


        BookPopularity bookPopularity = new BookPopularity(book, 0, 0, 0);
        bookPopularityRepository.save(bookPopularity);

        List<MultipartFile> coverImages = Optional.ofNullable(bookCreatDTO.getCoverImages())
            .orElse(Collections.emptyList());

        bookCoverImageUpdateOrCreate(coverImages, book, bookCreatDTO.getIsbn());

        List<MultipartFile> detailImage = Optional.ofNullable(bookCreatDTO.getDetailImages())
            .orElse(Collections.emptyList());

        bookDetailImageUpdateOrCreate(detailImage, book, bookCreatDTO.getIsbn());


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

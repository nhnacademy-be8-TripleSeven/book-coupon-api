package com.nhnacademy.bookapi.service.book;

import com.nhnacademy.bookapi.dto.book.BookCreatDTO;
import com.nhnacademy.bookapi.dto.book.BookDTO;
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
import com.nhnacademy.bookapi.service.tag.TagService;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookMultiTableService {

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
    public void updateBook(BookDTO bookUpdateDTO) {
        Book book = bookService.getBook(bookUpdateDTO.getId());

        book.update(bookUpdateDTO.getTitle(),bookUpdateDTO.getIsbn(), bookUpdateDTO.getPublishedDate(),
            bookUpdateDTO.getRegularPrice(),bookUpdateDTO.getSalePrice(),bookUpdateDTO.getDescription());

        List<String> bookCoverImages = bookUpdateDTO.getCoverImage();
        for (String bookCoverImage : bookCoverImages) {
            Image image = new Image(bookCoverImage);
            BookCoverImage coverImage = new BookCoverImage(image, book);
            imageService.bookCoverSave(image, coverImage);
        }
        List<String> detailImages = bookUpdateDTO.getDetailImage();
        for (String detailImage : detailImages) {
            Image image = new Image(detailImage);
            BookImage bookImage = new BookImage();
            imageService.bookDetailSave(image, bookImage);
        }

        List<CategoryDTO> categories = bookUpdateDTO.getCategories();
        Category categoryById = null;
        for (CategoryDTO categoryDTO : categories) {
            categoryById = categoryService.getCategoryById(categoryDTO.getId());
            if(categoryById != null) {
                categoryById.update(categoryDTO.getName(), categoryById.getLevel());
            }else {
                categoryById = new Category(categoryDTO.getName(), categoryDTO.getLevel());
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

        BookIndex bookIndex = bookIndexService.getBookIndex(bookUpdateDTO.getId());
        bookIndex.updateIndexText(bookUpdateDTO.getIndex());

        List<BookType> bookTypeByBookId = bookTypeService.getBookTypeByBookId(
            bookUpdateDTO.getId());

        List<BookTypeDTO> bookTypes = bookUpdateDTO.getBookTypes();
        for (BookTypeDTO bookType : bookTypes) {
            bookTypeService.deleteBookType(bookType.getId());
        }
        for (BookType type : bookTypeByBookId) {
            BookType bookType = new BookType(type.getTypes(), type.getRanks(), book);
            bookTypeService.createBookType(bookType);
        }
    }


    public void createBook(BookCreatDTO bookCreatDTO) {
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
            BookCreator bookCreatorByCreatorId = bookCreatorService.getBookCreatorByCreatorId(
                author.getId());
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
        Category category = null;

        for (CategoryDTO categorys : categories) {
            Category categoryByName = categoryService.getCategoryByName(categorys.getName());
            if(categoryByName != null) {
                BookCategory bookCategory = new BookCategory(book, categoryByName);
                categoryService.bookCategorySave(bookCategory);
            }else{
                category = new Category(categorys.getName(), categorys.getLevel());
                BookCategory bookCategory = new BookCategory(book, category);
                categoryService.categorySave(category, bookCategory);
            }
        }

        BookIndex bookIndex = new BookIndex(bookCreatDTO.getIndex(), book);
        bookIndexService.createBookIndex(bookIndex);

        BookPopularity bookPopularity = new BookPopularity(book, 0, 0, 0);
        bookPopularityRepository.save(bookPopularity);

    }

    @Transactional
    public void deleteBook(long bookId) {
        bookTypeService.deleteBookType(bookId);

        bookIndexService.deleteBookIndex(bookId);

        bookCreatorService.deleteBookCreatorMap(bookId);

        bookCategoryRepository.deleteAllByBookId(bookId);

        imageService.deleteBookCoverImageAndBookDeleteImage(bookId);

        tagService.deleteBookTag(bookId);

        reviewRepository.deleteByBookId(bookId);

        bookCouponRepository.deleteByBookId(bookId);

        wrapperRepository.deleteByBookId(bookId);

        bookPopularityRepository.deleteByBookId(bookId);

        bookService.deleteBook(bookId);
    }
}

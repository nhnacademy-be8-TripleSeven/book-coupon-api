package com.nhnacademy.bookapi.service.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookImage;
import com.nhnacademy.bookapi.entity.BookPopularity;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.entity.Publisher;
import com.nhnacademy.bookapi.entity.Role;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.BookPopularRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTypeRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookApiSaveService {

    private final BookApiService bookApiService;

    private final BookRepository bookRepository;
    private final BookCreatorRepository bookCreatorRepository;
    private final BookPopularRepository bookPopularRepository;
    private final BookImageRepository bookImageRepository;
    private final ImageRepository imageRepository;
    private final BookTypeRepository bookTypeRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;



    public void saveBook(String bookType) throws Exception {
        JsonNode bookList = bookApiService.getBookList(bookType);

        for (JsonNode book : bookList) {
            String isbn = book.path("isbn13").asText();
            Book saveBook = new Book();
            BookPopularity bookPopularity = new BookPopularity();
            BookImage bookImage = new BookImage();
            Image image = new Image();
            BookType saveBookType = new BookType();
            Publisher publisher = new Publisher();


            JsonNode node = bookApiService.getBook(isbn).get(0);

            publisher.setName(book.path("publisher").asText());
            publisherRepository.save(publisher);
            image.setUrl(book.path("cover").asText());
            Image imageFk = imageRepository.save(image);

            saveBook.setTitle(book.path("title").asText());
            saveBook.setDescription(book.path("description").asText());
            saveBook.setIsbn13(isbn);
            saveBook.setPublishDate(LocalDate.now());
            saveBook.setStock(1000);
            saveBook.setPublisher(publisher);
            saveBook.setRegularPrice(book.path("priceStandard").asInt());
            saveBook.setSalePrice(book.path("priceSales").asInt());
            saveBook.setImage(imageFk);

            JsonNode subInfo = node.path("subInfo");
            saveBook.setPage(subInfo.path("itemPage").asInt());

            Book bookFk = bookRepository.save(saveBook);

            //북타입 저장
            saveBookType.setType(Type.valueOf(bookType.toUpperCase(Locale.ROOT)));
            saveBookType.setRank(node.path("bestRank").asInt());
            bookTypeRepository.save(saveBookType);


            bookImage.setBook(bookFk);
            bookImage.setImage(imageFk);
            bookImageRepository.save(bookImage);

            String author = book.path("author").asText().trim();
            String category = book.path("categoryName").asText();

            //도서 제작자 저장
            authorParseSave(author, bookFk);
            //카테고리 제작자 저장
            categoryParseSave(category, bookFk);

            //책인기도 초기화
            bookPopularity.setSearchRank(0);
            bookPopularity.setClickRank(0);
            bookPopularity.setCartCount(0);
            bookPopularity.setBook(bookFk);

            bookPopularRepository.save(bookPopularity);

        }
        
    }



    public void authorParseSave(String author, Book book) throws Exception {
        List<BookCreator> bookCreators = new ArrayList<>();

        // "지은이)", "그림)", "엮은이)", "원작)", "옮긴이)" 로 끝나는 구분자를 기준으로 분리
        String[] split = author.split("\\),");
        for (String s : split) {
            s = s.trim(); // 공백 제거
            Role role = null;

            // 역할 결정
            if (s.endsWith("지은이")) {
                role = Role.AUTHOR;
            } else if (s.endsWith("그림")) {
                role = Role.ILLUSTRATOR;
            } else if (s.endsWith("엮은이")) {
                role = Role.EDITOR;
            } else if (s.endsWith("원작")) {
                role = Role.ORIGINAL_AUTHOR;
            } else if (s.endsWith("옮긴이")) {
                role = Role.TRANSLATOR;
            }

            // 역할이 있는 경우
            if (role != null) {
                s = s.substring(0, s.lastIndexOf("(")).trim(); // 역할 제거
            } else {
                role = Role.AUTHOR; // 기본 역할
            }

            // 이름 분리 및 저장
            String[] nameList = s.split(",");
            for (String name : nameList) {
                BookCreator bookCreator = new BookCreator();
                bookCreator.setName(name.trim());
                bookCreator.setRole(role);
                bookCreator.setBook(book); // Book 객체 연결
                bookCreators.add(bookCreator);
            }
        }

        // 결과 확인 (예: 저장 또는 반환)
        bookCreatorRepository.saveAll(bookCreators);
    }

    private void categoryParseSave(String category, Book book) throws Exception {


        String[] categories = category.split(">");
        for (String s : categories) {
            Category saveCategory = new Category();
            BookCategory saveBookCategory = new BookCategory();
            saveCategory.setName(s.trim());
            categoryRepository.save(saveCategory);
            saveBookCategory.setBook(book);
            saveBookCategory.setCategory(saveCategory);
            bookCategoryRepository.save(saveBookCategory);

        }



    }


}

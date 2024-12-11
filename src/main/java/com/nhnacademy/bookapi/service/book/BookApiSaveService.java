package com.nhnacademy.bookapi.service.book;

import com.fasterxml.jackson.databind.JsonNode;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.BookImage;
import com.nhnacademy.bookapi.entity.BookPopularity;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.Image;
import com.nhnacademy.bookapi.entity.Publisher;
import com.nhnacademy.bookapi.entity.Role;
import com.nhnacademy.bookapi.entity.Type;
import com.nhnacademy.bookapi.mapper.RoleMapper;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.BookCreatorMapRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;
import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.BookPopularRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTypeRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;
import java.time.LocalDate;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
    private final BookCreatorMapRepository bookCreatorMapRepository;



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


            JsonNode bookDetail = bookApiService.getBook(isbn).get(0);

            String publisherName = book.path("publisher").asText();

            Publisher selectPublisher = publisherRepository.existsByName(publisherName);

            if(selectPublisher == null) {
                publisher.setName(publisherName);
                publisherRepository.save(publisher);
                saveBook.setPublisher(publisher);
            }else {
                saveBook.setPublisher(selectPublisher);
            }



            image.setUrl(book.path("cover").asText());
            Image imageFk = imageRepository.save(image);

            saveBook.setTitle(book.path("title").asText());
            saveBook.setDescription(book.path("description").asText());
            saveBook.setIsbn13(isbn);
            saveBook.setPublishDate(LocalDate.now());
            saveBook.setStock(1000);

            saveBook.setRegularPrice(book.path("priceStandard").asInt());
            saveBook.setSalePrice(book.path("priceSales").asInt());
            saveBook.setImage(imageFk);

            if(bookDetail != null) {
                JsonNode subInfo = bookDetail.path("subInfo");
                saveBook.setPage(subInfo.path("itemPage").asInt());
            }

            saveBookType.setRanks(book.path("bestRank").asInt());

            //북타입 저장
            saveBookType.setTypes(Type.valueOf(bookType.toUpperCase(Locale.ROOT)));

            Book bookFk = bookRepository.save(saveBook);
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


        BookCreator bookCreator = new BookCreator();
        BookCreatorMap bookCreatorMap = new BookCreatorMap();

        // "지은이)", "그림)", "엮은이)", "원작)", "옮긴이)" 로 끝나는 구분자를 기준으로 분리
        String[] split = author.split("\\),");
        for (String s : split) {
            s = s.trim(); // 공백 제거
            Role role = null;
            String roleName = null;
            if(s.contains("(") && s.contains(")")) {
                roleName = s.substring(s.indexOf("(") + 1, s.indexOf(")")).trim();
            }
            role = RoleMapper.getRole(roleName);

            String[] nameList = s.split(", ");
            for (String name : nameList) {
                if(name.contains("(")) {
                    name = name.substring(0, name.indexOf("(")).trim();
                }
                bookCreator = bookCreatorRepository.existByNameAndRole(name, role);
                if(bookCreator == null){
                    bookCreator = new BookCreator();
                    bookCreator.setName(name.trim());
                    bookCreator.setRole(role);
                    bookCreatorMap.setBook(book);
                    bookCreatorMap.setCreator(bookCreator);
                    bookCreatorRepository.save(bookCreator);
                    bookCreatorMapRepository.save(bookCreatorMap);
                }else {
                    bookCreatorMap.setCreator(bookCreator);
                    bookCreatorMap.setBook(book);
                    bookCreatorMapRepository.save(bookCreatorMap);
                }

            }



        }


    }

    private void categoryParseSave(String category, Book book) throws Exception {


        String[] categories = category.split(">");
        Category parentCategory = null;
        for (String s : categories) {
            Category saveCategory = new Category();
            BookCategory saveBookCategory = new BookCategory();
            saveCategory.setName(s.trim());
            saveCategory.setParent(parentCategory);
            parentCategory = categoryRepository.save(saveCategory);
            saveBookCategory.setBook(book);
            saveBookCategory.setCategory(saveCategory);
            bookCategoryRepository.save(saveBookCategory);

        }



    }


}

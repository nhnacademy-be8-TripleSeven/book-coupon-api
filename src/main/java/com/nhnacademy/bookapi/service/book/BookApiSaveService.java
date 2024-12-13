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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

            LocalDate pubDate = null;

            String pubDateStr = book.path("pubDate").asText();
            if(pubDateStr != null || !pubDateStr.isEmpty()) {
                pubDate = LocalDate.parse(pubDateStr);
            }

            saveBook.setPublishDate(pubDate);

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



            //책인기도 초기화
            bookPopularity.setSearchRank(0);
            bookPopularity.setClickRank(0);
            bookPopularity.setCartCount(0);
            bookPopularity.setBook(bookFk);

            bookPopularRepository.save(bookPopularity);



            //도서 제작자 저장
            List<BookCreator> bookCreators = authorParseSave(author, bookFk);
            //카테고리 제작자 저장
            List<Category> categoryList = categoryParseSave(category, bookFk);
            //엘라스틱서치 저장
//            saveBookDocument(saveBook,image.getUrl(), publisherName, bookCreators,categoryList);

        }
        
    }


    public List<BookCreator> authorParseSave(String author, Book book) throws Exception {


        List<BookCreator> bookCreatorList = new ArrayList<>();

        BookCreator bookCreator;
        BookCreatorMap bookCreatorMap;

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
                    bookCreatorMap = new BookCreatorMap();
                    bookCreator = new BookCreator();
                    bookCreator.setName(name.trim());
                    bookCreator.setRole(role);
                    bookCreatorMap.setBook(book);
                    bookCreatorMap.setCreator(bookCreator);
                    BookCreator saveBookCreator = bookCreatorRepository.save(bookCreator);
                    bookCreatorList.add(saveBookCreator);
                    bookCreatorMapRepository.save(bookCreatorMap);
                }else {
                    bookCreatorMap = new BookCreatorMap();
                    bookCreatorMap.setCreator(bookCreator);
                    bookCreatorMap.setBook(book);
                    bookCreatorMapRepository.save(bookCreatorMap);
                }
            }
        }
        return bookCreatorList;
    }


    public List<Category> categoryParseSave(String category, Book book) {
        List<Category> categoryList = new ArrayList<>();
        String[] categories = category.split(">");
        Category parentCategory = null;

        for (String categoryName : categories) {
            categoryName = categoryName.trim();

            // 중복 확인 및 기존 카테고리 조회
            Optional<Category> existingCategory = categoryRepository.findByNameAndParent(categoryName, parentCategory);
            Category saveCategory;

            if (existingCategory.isPresent()) {
                saveCategory = existingCategory.get();
            } else {
                saveCategory = new Category();
                saveCategory.setName(categoryName);
                saveCategory.setParent(parentCategory);
                saveCategory = categoryRepository.save(saveCategory);
                categoryList.add(saveCategory);
            }

            // 도서와 카테고리 매핑 저장
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBook(book);
            bookCategory.setCategory(saveCategory);
            bookCategoryRepository.save(bookCategory);

            // 부모 카테고리 갱신
            parentCategory = saveCategory;
        }
        return categoryList;
    }

//    private void saveBookDocument(Book book, String coverUrl, String publisherName, List<BookCreator> bookCreators, List<Category> categories) {
//        // 도큐먼트 생성
//        BookDocument document = new BookDocument();
//        document.setId(book.getIsbn13());
//        document.setTitle(book.getTitle());
//        document.setDescription(book.getDescription());
//        document.setIsbn13(book.getIsbn13());
//        document.setPublishDate(book.getPublishDate());
//        document.setRegularPrice(book.getRegularPrice());
//        document.setSalePrice(book.getSalePrice());
//        document.setStock(book.getStock());
//        document.setPage(book.getPage());
//        document.setCoverUrl(coverUrl);
//        document.setPublisherName(publisherName);
//        document.setCategories(categories);
//        document.setBookCreators(bookCreators);
//
//
//        // 엘라스틱서치에 저장
//        bookSearchService.saveBook(document);
//    }

}











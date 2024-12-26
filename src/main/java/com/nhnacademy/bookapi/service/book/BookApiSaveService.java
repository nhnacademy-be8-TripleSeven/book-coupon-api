package com.nhnacademy.bookapi.service.book;

import com.fasterxml.jackson.databind.JsonNode;

import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCoverImage;
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
import com.nhnacademy.bookapi.repository.BookCoverImageRepository;
import com.nhnacademy.bookapi.repository.BookCreatorMapRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;

import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookPopularRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTypeRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.nhnacademy.bookapi.service.object.ObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
    private final BookCoverImageRepository bookCoverImageRepository;

  //여기부터는 object storage에 이미지를 올리기 위한 필드 변수, 아래 변수들은 고정값이다.
    private final String storageUrl = "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e";
    private final String authUrl = "https://api-identity.infrastructure.cloud.toast.com/v2.0/tokens";
    private final String tenantId = "c20e3b10d61749a2a52346ed0261d79e";
    private final String username = "rlgus4531@naver.com";
    private final String password = "team3";
    private final String containerName = "triple-seven";
    //여기까지
    
    private final BookIndexRepository bookIndexRepository;

    public void aladinApiSaveBook(String bookType, String searchTarget, int start, int max) throws Exception {
        JsonNode bookList = bookApiService.getBookList(bookType, searchTarget, start, max);
        //object storage에 저장
        ObjectService objectService = new ObjectService(storageUrl);
        objectService.generateAuthToken(authUrl, tenantId, username, password); // 토큰 발급
        //여기까지

        for (JsonNode book : bookList) {

            String isbn = book.path("isbn13").asText();
            boolean findIsbn = bookRepository.existsByIsbn13(isbn);

            if(isbn.isEmpty()){
                continue;

            }
            if(findIsbn) {
                continue;
            }

            Book saveBook = new Book();
            BookPopularity bookPopularity = new BookPopularity();
            BookImage bookImage = new BookImage();
            Image image;
            BookType saveBookType = new BookType();
            Publisher publisher = new Publisher();

            JsonNode bookDetail = bookApiService.getBook(isbn).get(0);

            String publisherName = book.path("publisher").asText();

            Publisher selectPublisher = publisherRepository.findByName(publisherName);

            if(publisherName.isEmpty()){
                publisherName = "출판사 없음";
            }
            if(selectPublisher == null ) {
                publisher = new Publisher(publisherName);
                publisherRepository.save(publisher);

                saveBook.publisherUpdate(publisher);
            }else {
                publisher = selectPublisher;
                saveBook.publisherUpdate(selectPublisher);
            }

            String coverUrl = book.path("cover").asText();
            String uploadedImageUrl = uploadCoverImageToStorage(objectService, coverUrl, isbn + ".jpg");

            image= new Image(book.path("cover").asText());
            imageRepository.save(image);

            //bookcoverimage mapping
            BookCoverImage bookCoverImage = new BookCoverImage(image, saveBook);
            bookCoverImageRepository.save(bookCoverImage);

            LocalDate pubDate = null;

            String pubDateStr = book.path("pubDate").asText();
            if(pubDateStr != null || !pubDateStr.isEmpty()) {
                pubDate = LocalDate.parse(pubDateStr);
            }

            saveBook.create(book.path("title").asText(), book.path("description").asText(), pubDate,
                book.path("priceStandard").asInt(),
                book.path("priceSales").asInt(), isbn, 1000, 0, publisher);

            if(bookDetail != null) {
                JsonNode subInfo = bookDetail.path("subInfo");
                saveBook.updatePage(subInfo.path("itemPage").asInt());
            }
            List<BookType> bookTypes = new ArrayList<>();
            //북타입 추가1
                saveBookType = new BookType(Type.valueOf(bookType.toUpperCase(Locale.ROOT)), book.path("bestRank").asInt(), saveBook);
                bookTypes.add(saveBookType);

            bookRepository.save(saveBook);


            BookType newBookType = new BookType();
            newBookType = new BookType(Type.valueOf(searchTarget.toUpperCase(Locale.ROOT)), 0, saveBook);
            bookTypes.add(newBookType);

            bookTypeRepository.saveAll(bookTypes);

            bookImage.setBook(saveBook);
            bookImage.setImage(image);
            bookImageRepository.save(bookImage);

            String author = book.path("author").asText().trim();
            String category = book.path("categoryName").asText();

            authorParseSave(author, saveBook);
            categoryParseSave(category, saveBook);

            //책인기도 초기화
            bookPopularity.create(saveBook);
            bookPopularRepository.save(bookPopularity);

        }
    }


    public void aladinApiEditorChoiceSaveBook(String bookType, String searchTarget, int start, int max, int categoryId) throws Exception {
        JsonNode bookList = bookApiService.getEditorChoiceBookList(bookType, searchTarget, start, max, categoryId);

        //object storage에 저장
        ObjectService objectService = new ObjectService(storageUrl);
        objectService.generateAuthToken(authUrl, tenantId, username, password); // 토큰 발급
        //여기까지

        for (JsonNode book : bookList) {

            String isbn = book.path("isbn13").asText();
            boolean findIsbn = bookRepository.existsByIsbn13(isbn);

            if(isbn.isEmpty()){
                continue;

            }
            if(findIsbn) {
                BookType saveBookType = new BookType(Type.valueOf(bookType.toUpperCase()), book.path("bestRank").asInt(), bookRepository.findByIsbn13(isbn).get());
                bookTypeRepository.save(saveBookType);
                continue;
            }

            Book saveBook = new Book();
            BookPopularity bookPopularity = new BookPopularity();
            BookImage bookImage = new BookImage();
            Image image;
            BookType saveBookType = new BookType();
            Publisher publisher = new Publisher();

            JsonNode bookDetail = bookApiService.getBook(isbn).get(0);

            String publisherName = book.path("publisher").asText();

            Publisher selectPublisher = publisherRepository.findByName(publisherName);

            if(publisherName.isEmpty()){
                publisherName = "출판사 없음";
            }
            if(selectPublisher == null ) {
                publisher = new Publisher(publisherName);
                publisherRepository.save(publisher);

                saveBook.publisherUpdate(publisher);
            }else {
                publisher = selectPublisher;
                saveBook.publisherUpdate(selectPublisher);
            }

            String coverUrl = book.path("cover").asText();
            String uploadedImageUrl = uploadCoverImageToStorage(objectService, coverUrl, isbn + ".jpg");

            image= new Image(book.path("cover").asText());
            imageRepository.save(image);

            //bookcoverimage mapping
            BookCoverImage bookCoverImage = new BookCoverImage(image, saveBook);
            bookCoverImageRepository.save(bookCoverImage);

            LocalDate pubDate = null;

            String pubDateStr = book.path("pubDate").asText();
            if(pubDateStr != null || !pubDateStr.isEmpty()) {
                pubDate = LocalDate.parse(pubDateStr);
            }

            saveBook.create(book.path("title").asText(), book.path("description").asText(), pubDate,
                book.path("priceStandard").asInt(),
                book.path("priceSales").asInt(), isbn, 1000, 0, publisher);

            if(bookDetail != null) {
                JsonNode subInfo = bookDetail.path("subInfo");
                saveBook.updatePage(subInfo.path("itemPage").asInt());
            }
            List<BookType> bookTypes = new ArrayList<>();
            //북타입 추가1
            saveBookType = new BookType(Type.valueOf(bookType.toUpperCase(Locale.ROOT)), book.path("bestRank").asInt(), saveBook);
            bookTypes.add(saveBookType);

            bookRepository.save(saveBook);


            BookType newBookType = new BookType();
            newBookType = new BookType(Type.valueOf(searchTarget.toUpperCase(Locale.ROOT)), 0, saveBook);
            bookTypes.add(newBookType);

            bookTypeRepository.saveAll(bookTypes);

            bookImage.setBook(saveBook);
            bookImage.setImage(image);
            bookImageRepository.save(bookImage);

            String author = book.path("author").asText().trim();
            String category = book.path("categoryName").asText();

            authorParseSave(author, saveBook);
            categoryParseSave(category, saveBook);

            //책인기도 초기화
            bookPopularity.create(saveBook);
            bookPopularRepository.save(bookPopularity);

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
                    bookCreator.create(name.trim(), role);

                    bookCreatorMap.create(book, bookCreator);
                    BookCreator saveBookCreator = bookCreatorRepository.save(bookCreator);
                    bookCreatorList.add(saveBookCreator);
                    bookCreatorMapRepository.save(bookCreatorMap);
                }else {
                    bookCreatorMap = new BookCreatorMap();
                    bookCreatorMap.create(book, bookCreator);
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
                saveCategory.create(categoryName, parentCategory);
                saveCategory = categoryRepository.save(saveCategory);
                categoryList.add(saveCategory);
            }

            // 도서와 카테고리 매핑 저장
            BookCategory bookCategory = new BookCategory();
            bookCategory.create(book,saveCategory);
            bookCategoryRepository.save(bookCategory);

            // 부모 카테고리 갱신
            parentCategory = saveCategory;
        }
        return categoryList;
    }
    //object storage에 이미지 업로드 메소드
    public String uploadCoverImageToStorage(ObjectService objectService, String imageUrl, String objectName) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream()) {
                objectService.uploadObject(containerName, objectName, inputStream);
                return storageUrl + "/" + containerName + "/" + objectName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}











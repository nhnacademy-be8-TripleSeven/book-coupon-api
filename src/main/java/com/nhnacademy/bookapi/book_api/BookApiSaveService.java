package com.nhnacademy.bookapi.book_api;

import com.fasterxml.jackson.databind.JsonNode;

import com.nhnacademy.bookapi.dto.book.BookApiDTO;
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
import com.nhnacademy.bookapi.entity.Wrapper;
import com.nhnacademy.bookapi.exception.AladinApiException;
import com.nhnacademy.bookapi.exception.BookAlreadyExistsException;
import com.nhnacademy.bookapi.mapper.RoleMapper;
import com.nhnacademy.bookapi.repository.BookCategoryRepository;
import com.nhnacademy.bookapi.repository.BookCoverImageRepository;
import com.nhnacademy.bookapi.repository.BookCreatorMapRepository;
import com.nhnacademy.bookapi.repository.BookCreatorRepository;

import com.nhnacademy.bookapi.repository.BookImageRepository;
import com.nhnacademy.bookapi.repository.BookIndexRepository;
import com.nhnacademy.bookapi.repository.BookPopularityRepository;
import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.BookTypeRepository;
import com.nhnacademy.bookapi.repository.CategoryRepository;
import com.nhnacademy.bookapi.repository.ImageRepository;
import com.nhnacademy.bookapi.repository.PublisherRepository;

import com.nhnacademy.bookapi.repository.WrapperRepository;
import com.nhnacademy.bookapi.service.image.ImageService;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
//    private final BookIndexRepository bookIndexRepository;
//    private final ImageService imageService;
    private final BookRepository bookRepository;
//    private final BookCreatorRepository bookCreatorRepository;
//    private final BookPopularityRepository bookPopularRepository;
//    private final BookImageRepository bookImageRepository;
//    private final ImageRepository imageRepository;
//    private final BookTypeRepository bookTypeRepository;
//    private final PublisherRepository publisherRepository;
//    private final CategoryRepository categoryRepository;
//    private final BookCategoryRepository bookCategoryRepository;
//    private final BookCreatorMapRepository bookCreatorMapRepository;
//    private final BookCoverImageRepository bookCoverImageRepository;

//    private final ObjectService objectService;
  //여기부터는 object storage에 이미지를 올리기 위한 필드 변수, 아래 변수들은 고정값이다.
//    private final String storageUrl = "https://kr1-api-object-storage.nhncloudservice.com/v1/AUTH_c20e3b10d61749a2a52346ed0261d79e";
//    private final String containerName = "triple-seven";
//    private final WrapperRepository wrapperRepository;


    public BookApiDTO getAladinBookByIsbn(String isbn){
        JsonNode book = null;
        try {
            book = bookApiService.getBook(isbn).get(0);
        }catch (Exception e){
            throw new AladinApiException(e.getMessage());
        }

        String isbn13 = book.path("isbn13").asText();
        if(!isbn13.equals(isbn)) {
            throw new BookAlreadyExistsException("이미 존재하는 isbn 입니다.");
        }
        boolean findIsbn = bookRepository.existsByIsbn13(isbn);

        if(findIsbn){
            return new BookApiDTO();
        }
        String pubDateStr = book.path("pubDate").asText();
        LocalDate pubDate = null;

        if(!pubDateStr.isEmpty()) {

            pubDate = LocalDate.parse(pubDateStr);
        }
        String cover = book.path("cover").asText();

        BookApiDTO apiDTO = new BookApiDTO(
            book.path("title").asText(),
            book.path("isbn13").asText(),
            pubDate,
            book.path("description").asText(),
            book.path("priceStandard").asInt(),
            book.path("priceSales").asInt(),
            List.of(cover),
            1000,
            0,
            book.path("publisher").asText()
        );


        apiDTO.createBookTypeParse(book.path("bestRank").asInt());
        apiDTO.createAuthorParse(book.path("author").asText());
        apiDTO.createCategoryParse(book.path("categoryName").asText());

        return apiDTO;
    }

//
//    public void aladinApiSaveBook(String bookType, String searchTarget, int start, int max) throws Exception {
//        JsonNode bookList = bookApiService.getBookList(bookType, searchTarget, start, max);
//
//        for (JsonNode book : bookList) {
//            int level = 1;
//            String isbn = book.path("isbn13").asText();
//            boolean findIsbn = bookRepository.existsByIsbn13(isbn);
//
//            if(isbn.isEmpty()){
//                continue;
//
//            }
//            if(findIsbn) {
//                continue;
//            }
//
//            Book saveBook = new Book();
//            BookPopularity bookPopularity = new BookPopularity();
//            BookImage bookImage = new BookImage();
//            Image image;
//            BookType saveBookType = null;
//            Publisher publisher = null;
//
//            JsonNode bookDetail = bookApiService.getBook(isbn).get(0);
//
//            String publisherName = book.path("publisher").asText();
//
//            Publisher selectPublisher = publisherRepository.findByName(publisherName);
//
//
//            if(selectPublisher == null ) {
//                publisher = new Publisher(publisherName);
//                publisherRepository.save(publisher);
//
//                saveBook.publisherUpdate(publisher);
//            }else {
//                publisher = selectPublisher;
//                saveBook.publisherUpdate(selectPublisher);
//            }
//
//            String coverUrl = book.path("cover").asText();
//            String uploadedImageUrl = uploadCoverImageToStorage(objectService, coverUrl, isbn + "_cover.jpg");
//
//            image= new Image(uploadedImageUrl);
//            imageRepository.save(image);
//
//            //bookcoverimage mapping
//            BookCoverImage bookCoverImage = new BookCoverImage(image, saveBook);
//            bookCoverImageRepository.save(bookCoverImage);
//
//            LocalDate pubDate = null;
//
//            String pubDateStr = book.path("pubDate").asText();
//            if(pubDateStr != null || !pubDateStr.isEmpty()) {
//                pubDate = LocalDate.parse(pubDateStr);
//            }
//
//            saveBook.create(book.path("title").asText(), book.path("description").asText(), pubDate,
//                book.path("priceStandard").asInt(),
//                book.path("priceSales").asInt(), isbn, 1000, 0, publisher);
//
//            if(bookDetail != null) {
//                JsonNode subInfo = bookDetail.path("subInfo");
//                saveBook.updatePage(subInfo.path("itemPage").asInt());
//            }
//            List<BookType> bookTypes = new ArrayList<>();
//            //북타입 추가1
//                saveBookType = new BookType(Type.valueOf(bookType.toUpperCase(Locale.ROOT)), book.path("bestRank").asInt(), saveBook);
//                bookTypes.add(saveBookType);
//
//            bookRepository.save(saveBook);
//
//
//            BookType newBookType = new BookType();
//            newBookType = new BookType(Type.valueOf(searchTarget.toUpperCase(Locale.ROOT)), 0, saveBook);
//            bookTypes.add(newBookType);
//            bookTypeRepository.saveAll(bookTypes);
//
//            if(saveBookType.getTypes() != Type.EBOOK){
//                Wrapper wrapper = Wrapper.builder().book(saveBook).wrappable(true).build();
//                wrapperRepository.save(wrapper);
//            }else {
//                wrapperRepository.save(Wrapper.builder().book(saveBook).wrappable(false).build());
//            }
//
//            String author = book.path("author").asText().trim();
//            String category = book.path("categoryName").asText();
//
//
//
//            authorParseSave(author, saveBook);
//            categoryParseSave(category, saveBook, level);
//
//            //책인기도 초기화
//            bookPopularity.create(saveBook);
//            bookPopularRepository.save(bookPopularity);
//
//        }
//    }
//
//
//    public void aladinApiEditorChoiceSaveBook(String bookType, String searchTarget, int start, int max, int categoryId) throws Exception {
//        JsonNode bookList = bookApiService.getEditorChoiceBookList(bookType, searchTarget, start, max, categoryId);
//
//
//
//        for (JsonNode book : bookList) {
//            int level = 1;
//            String isbn = book.path("isbn13").asText();
//            boolean findIsbn = bookRepository.existsByIsbn13(isbn);
//
//            if(isbn.isEmpty()){
//                continue;
//
//            }
//            if(findIsbn) {
//                BookType saveBookType = new BookType(Type.valueOf(bookType.toUpperCase()), book.path("bestRank").asInt(), bookRepository.findByIsbn13(isbn).get());
//                bookTypeRepository.save(saveBookType);
//                continue;
//            }
//
//            Book saveBook = new Book();
//            BookPopularity bookPopularity = new BookPopularity();
//
//            Image image;
//            BookType saveBookType = new BookType();
//            Publisher publisher = new Publisher();
//
//            JsonNode bookDetail = bookApiService.getBook(isbn).get(0);
//
//            String publisherName = book.path("publisher").asText();
//
//            Publisher selectPublisher = publisherRepository.findByName(publisherName);
//
//            if(publisherName.isEmpty()){
//                publisherName = "출판사 없음";
//            }
//            if(selectPublisher == null ) {
//                publisher = new Publisher(publisherName);
//                publisherRepository.save(publisher);
//
//                saveBook.publisherUpdate(publisher);
//            }else {
//                publisher = selectPublisher;
//                saveBook.publisherUpdate(selectPublisher);
//            }
//
//            String coverUrl = book.path("cover").asText();
//            String uploadedImageUrl = uploadCoverImageToStorage(objectService, coverUrl, isbn + "cover_.jpg");
//
//            image = new Image(uploadedImageUrl);
//            imageRepository.save(image);
//
//            //bookcoverimage mapping
//            BookCoverImage bookCoverImage = new BookCoverImage(image, saveBook);
//            bookCoverImageRepository.save(bookCoverImage);
//
//            LocalDate pubDate = null;
//
//            String pubDateStr = book.path("pubDate").asText();
//            if(pubDateStr != null || !pubDateStr.isEmpty()) {
//                pubDate = LocalDate.parse(pubDateStr);
//            }
//
//            saveBook.create(book.path("title").asText(), book.path("description").asText(), pubDate,
//                book.path("priceStandard").asInt(),
//                book.path("priceSales").asInt(), isbn, 1000, 0, publisher);
//
//            if(bookDetail != null) {
//                JsonNode subInfo = bookDetail.path("subInfo");
//                saveBook.updatePage(subInfo.path("itemPage").asInt());
//            }
//            List<BookType> bookTypes = new ArrayList<>();
//            //북타입 추가1
//            saveBookType = new BookType(Type.valueOf(bookType.toUpperCase(Locale.ROOT)), book.path("bestRank").asInt(), saveBook);
//            bookTypes.add(saveBookType);
//
//            bookRepository.save(saveBook);
//
//            if(saveBookType.getTypes() != Type.EBOOK){
//                Wrapper wrapper = Wrapper.builder().book(saveBook).wrappable(true).build();
//                wrapperRepository.save(wrapper);
//            }
//
//
//            BookType newBookType = new BookType();
//            newBookType = new BookType(Type.valueOf(searchTarget.toUpperCase(Locale.ROOT)), 0, saveBook);
//            bookTypes.add(newBookType);
//
//            bookTypeRepository.saveAll(bookTypes);
//
//
//            String author = book.path("author").asText().trim();
//            String category = book.path("categoryName").asText();
//
//
//            authorParseSave(author, saveBook);
//            categoryParseSave(category, saveBook, level);
//
//            //책인기도 초기화
//            bookPopularity.create(saveBook);
//            bookPopularRepository.save(bookPopularity);
//
//
//        }
//
//    }


  
//    public List<BookCreator> authorParseSave(String author, Book book) {
//
//
//        List<BookCreator> bookCreatorList = new ArrayList<>();
//
//        BookCreator bookCreator;
//        BookCreatorMap bookCreatorMap;
//
//        // "지은이)", "그림)", "엮은이)", "원작)", "옮긴이)" 로 끝나는 구분자를 기준으로 분리
//        String[] split = author.split("\\),");
//        for (String s : split) {
//            s = s.trim();
//            Role role = null;
//            String roleName = null;
//            if(s.contains("(") && s.contains(")")) {
//                roleName = s.substring(s.indexOf("(") + 1, s.indexOf(")")).trim();
//            }
//            role = RoleMapper.getRole(roleName);
//
//            String[] nameList = s.split(", ");
//            for (String name : nameList) {
//                if(name.contains("(")) {
//                    name = name.substring(0, name.indexOf("(")).trim();
//                }
//                bookCreator = bookCreatorRepository.existByNameAndRole(name, role);
//                if(bookCreator == null){
//                    bookCreatorMap = new BookCreatorMap();
//                    bookCreator = new BookCreator();
//                    bookCreator.create(name.trim(), role);
//
//                    bookCreatorMap.create(book, bookCreator);
//                    BookCreator saveBookCreator = bookCreatorRepository.save(bookCreator);
//                    bookCreatorList.add(saveBookCreator);
//                    bookCreatorMapRepository.save(bookCreatorMap);
//                }else {
//                    bookCreatorMap = new BookCreatorMap();
//                    bookCreatorMap.create(book, bookCreator);
//                    bookCreatorMapRepository.save(bookCreatorMap);
//                }
//            }
//        }
//        return bookCreatorList;
//    }
//
//    @Transactional
//    public void categoryParseSave(String category, Book book, int level)  {
//
//        String[] categories = category.split(">");
//        Category parentCategory = null; // 초기화
//
//        for (String categoryName : categories) {
//            categoryName = categoryName.trim();
//
//            // 중복 확인 및 기존 카테고리 조회
//            Category categoryByName = categoryRepository.findCategoryByName(categoryName).orElse(null);
//            Category saveCategory;
//
//            if (categoryByName != null) {
//                saveCategory = categoryByName;
//            } else {
//                Category newCategory = new Category();
//                newCategory.create(categoryName, level, parentCategory); // parentCategory 설정
//
//                // 저장 후 saveCategory에 할당
//                saveCategory = categoryRepository.save(newCategory);
//            }
//
//            // 상위 카테고리로 설정
//            parentCategory = saveCategory;
//            level++;
//
//            // 도서와 카테고리 매핑 저장
//            BookCategory bookCategory = new BookCategory();
//            bookCategory.create(book, saveCategory);
//            bookCategoryRepository.save(bookCategory);
//        }
//    }
  
//    //object storage에 이미지 업로드 메소드
//    public String uploadCoverImageToStorage(ObjectService objectService, String imageUrl, String objectName) {
//        try {
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            try (InputStream inputStream = connection.getInputStream()) {
//                objectService.uploadObject(containerName, objectName, inputStream);
//                return storageUrl + "/" + containerName + "/" + objectName;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}











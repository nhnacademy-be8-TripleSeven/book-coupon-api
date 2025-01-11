package com.nhnacademy.bookapi.service.book.impl;

import com.nhnacademy.bookapi.dto.book.*;
import com.nhnacademy.bookapi.dto.book.BookDTO;
import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorResponseDTO;
import com.nhnacademy.bookapi.dto.book.BookDetailResponseDTO;
import com.nhnacademy.bookapi.dto.book.SearchBookDetail;

import com.nhnacademy.bookapi.dto.bookcreator.BookCreatorDetail;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.BookNotFoundException;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.book.BookService;
import com.nhnacademy.bookapi.service.bookcreator.BookCreatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCoverImageRepository bookCoverImageRepository;
    private final BookCreatorMapRepository bookCreatorMapRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookTagRepository bookTagRepository;
    private final BookIndexRepository bookIndexRepository;
    private final BookTypeRepository bookTypeRepository;
    private final BookImageRepository bookImageRepository;
    private final BookCreatorService bookCreatorService;

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
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

// 카테고리 계층 구조 생성
        List<BookCategory> bookCategories = bookCategoryRepository.findAllByBook(book);
        StringBuilder categoriesBuilder = new StringBuilder();
        if (!bookCategories.isEmpty()) {
            for (BookCategory bookCategory : bookCategories) {
                Category category = bookCategory.getCategory();
                List<String> hierarchy = getCategoryHierarchy(category); // 계층 구조 생성
                categoriesBuilder.append(hierarchy).append(">");
            }
            // 마지막 쉼표 제거
            if (!categoriesBuilder.isEmpty()) {
                categoriesBuilder.deleteCharAt(categoriesBuilder.length() - 1);
            }
        }
        searchBookDetail.setCategories(categoriesBuilder);

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
            hierarchy.add(0, category.getName()); // 부모를 리스트 맨 앞에 추가
            category = findParentCategory(category); // 부모 카테고리 탐색
        }
        return hierarchy;
    }

    // 부모 카테고리를 찾는 메소드 (필요 시 구현)
    private Category findParentCategory(Category category) {
        // 부모 카테고리를 가져오는 로직 구현
        // 예: categoryRepository.findParentById(category.getId())
        return null; // 구현 필요
    }


    // 이달의 베스트 페이징을 사용하지 않고 캐싱으로
    @Transactional(readOnly = true)
    public Page<BookDetailResponseDTO> getMonthlyBestBooks() {
        Pageable pageable = Pageable.ofSize(10);

        Page<BookDetailResponseDTO> bookTypeBestsellerByRankAsc = bookRepository.findBookTypeBestseller(pageable);

        for (BookDetailResponseDTO bookDetailResponseDTO : bookTypeBestsellerByRankAsc) {
            long id = bookDetailResponseDTO.getId();
            BookCreatorResponseDTO bookCreatorResponseDTO = bookCreatorService.bookCreatorListByBookId(
                    id);
            bookDetailResponseDTO.setCreator(bookCreatorResponseDTO.getCreators());
        }
        return bookTypeBestsellerByRankAsc;

    }

    //type별 조회 이도서는 어때요? , 편집자의 선택, e북
    @Transactional(readOnly = true)
    public Page<BookDetailResponseDTO> getBookTypeBooks(Type bookType, Pageable pageable) {

        if(bookType == Type.BESTSELLER && pageable.getSort().isUnsorted()){
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Direction.ASC, "ranks"));
        }

        Page<BookDetailResponseDTO> bookTypeItemByType = bookRepository.findBookTypeItemByType(
                bookType, pageable);
        for (BookDetailResponseDTO bookDetailResponseDTO : bookTypeItemByType) {
            long id = bookDetailResponseDTO.getId();
            BookCreatorResponseDTO bookCreatorResponseDTO = bookCreatorService.bookCreatorListByBookId(id);
            bookDetailResponseDTO.setCreator(bookCreatorResponseDTO.getCreators());
        }
        return bookTypeItemByType;
    }



    public Page<BookDetailResponseDTO> getCategorySearchBooks(List<String> categories, String keyword, Pageable pageable) {
        return bookRepository.findByCategoryAndTitle(
                categories, keyword, pageable);
    }

    @Override
    public boolean existsBookByIsbn(String isbn) {
        return bookRepository.existsByIsbn13(isbn);
    }

    @Override
    public BookDTO getBookById(Long id) {
        return bookRepository.findBookById(id);
    }




    public Page<BookDTO> getBookList(String keyword, Pageable pageable) {
        return bookRepository.findBookByKeyword(keyword, pageable);
    }


    public Book getBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }



    @Transactional(readOnly = true)
    public List<BookSearchDTO> searchBooksByName(String query) {
        List<Book> books = bookRepository.findByTitleContaining(query);
        return books.stream()
                .map(book -> new BookSearchDTO(book.getId(), book.getTitle(), book.getIsbn13())).toList();
    }

    @Override
    public Page<BookDetailResponseDTO> searchBookByCategoryId(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public List<CartItemDTO> getCartItemsByIds(List<Long> bookIds) {
        List<Book> books = bookRepository.findAllById(bookIds);
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for (Book book : books) {
            cartItemDTOS.add(new CartItemDTO(
                    book.getId(),
                    book.getStock(),
                    book.getSalePrice(),
                    book.getRegularPrice()));
        }
        return cartItemDTOS;
    }

    @Override
    public String getBookName(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if(bookOptional.isEmpty()){
            throw new BookNotFoundException("book not found");
        }

        Book book = bookOptional.get();

        return book.getTitle();
    }

}

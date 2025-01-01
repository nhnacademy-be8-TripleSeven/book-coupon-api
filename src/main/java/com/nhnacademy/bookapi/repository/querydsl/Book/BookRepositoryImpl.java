package com.nhnacademy.bookapi.repository.querydsl.Book;

import com.nhnacademy.bookapi.dto.book.BookUpdateDTO;
import com.nhnacademy.bookapi.entity.Book;
import com.nhnacademy.bookapi.entity.BookCategory;
import com.nhnacademy.bookapi.entity.BookCreator;
import com.nhnacademy.bookapi.entity.BookCreatorMap;
import com.nhnacademy.bookapi.entity.BookIndex;
import com.nhnacademy.bookapi.entity.BookTag;
import com.nhnacademy.bookapi.entity.BookType;
import com.nhnacademy.bookapi.entity.Category;
import com.nhnacademy.bookapi.entity.QBook;
import com.nhnacademy.bookapi.entity.QBookCategory;
import com.nhnacademy.bookapi.entity.QBookCoverImage;
import com.nhnacademy.bookapi.entity.QBookCreator;
import com.nhnacademy.bookapi.entity.QBookCreatorMap;
import com.nhnacademy.bookapi.entity.QBookImage;
import com.nhnacademy.bookapi.entity.QBookIndex;
import com.nhnacademy.bookapi.entity.QBookTag;
import com.nhnacademy.bookapi.entity.QBookType;
import com.nhnacademy.bookapi.entity.QCategory;
import com.nhnacademy.bookapi.entity.QImage;
import com.nhnacademy.bookapi.entity.QTag;
import com.nhnacademy.bookapi.entity.Tag;
import com.nhnacademy.bookapi.entity.Type;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookRepositoryImpl extends QuerydslRepositorySupport implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public BookRepositoryImpl() {
        super(Book.class);
    }


    @Override
    public List<BookUpdateDTO> findBookByKeyword(String keyword) {
        QBook book = QBook.book;
        QCategory category = QCategory.category;
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookType bookType = QBookType.bookType;
        QBookTag bookTag = QBookTag.bookTag;
        QTag tag = QTag.tag;
        QBookCreatorMap bookCreatorMap = QBookCreatorMap.bookCreatorMap;
        QBookCreator bookCreator = QBookCreator.bookCreator;
        QImage image = QImage.image;
        QBookCoverImage bookCoverImage = QBookCoverImage.bookCoverImage;
        QBookImage bookImage = QBookImage.bookImage;
        QBookIndex bookIndex = QBookIndex.bookIndex;

        return from(book)
            .leftJoin(bookCategory).on(bookCategory.book.id.eq(book.id))
            .leftJoin(category).on(category.id.eq(bookCategory.category.id))
            .leftJoin(bookType).on(bookType.id.eq(book.id))
            .leftJoin(bookTag).on(bookTag.book.id.eq(book.id))
            .leftJoin(tag).on(tag.id.eq(bookTag.tag.id))
            .leftJoin(bookImage).on(bookImage.book.id.eq(book.id))
            .leftJoin(bookCoverImage).on(bookCoverImage.book.id.eq(book.id))
            .leftJoin(image).on(image.id.eq(bookImage.image.id))
            .leftJoin(bookCreatorMap).on(bookCreatorMap.book.id.eq(book.id))
            .leftJoin(bookCreator).on(bookCreator.id.eq(bookCreatorMap.book.id))
            .leftJoin(bookIndex).on(bookIndex.book.id.eq(book.id))
            .where(book.title.containsIgnoreCase(keyword)
                .or(tag.name.containsIgnoreCase(keyword))
                .or(category.name.containsIgnoreCase(keyword)))
            .select(Projections.constructor(
                BookUpdateDTO.class,
                book.id,
                book.title,
                book.isbn13,
                // Collect categories as a list
                Expressions.list(category.name).as("categories"),
                // Collect book types as a list
                Expressions.list(bookType.types).as("bookTypes"),
                // Collect authors as a list
                Expressions.list(bookCreator.name).as("authors"),
                // Collect tags as a list
                Expressions.list(tag.name).as("tags"),
                book.publishDate,
                book.description,
                book.regularPrice,
                book.salePrice,
                bookIndex.indexes,
                bookCoverImage.image.url.as("coverImage"),
                bookImage.image.url.as("detailImage")
            ))
            .fetch();
    }
    @Override
    @Transactional
    public void updateBook(BookUpdateDTO bookUpdateDTO) {
        // 1. Book 엔티티 조회
        Book existingBook = entityManager.find(Book.class, bookUpdateDTO.getId());
        if (existingBook == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookUpdateDTO.getId());
        }

        // 2. Book 기본 정보 업데이트
        existingBook.update(
            bookUpdateDTO.getTitle(),
            bookUpdateDTO.getIsbn(),
            bookUpdateDTO.getPublishedDate(),
            bookUpdateDTO.getRegularPrice(),
            bookUpdateDTO.getSalePrice(),
            bookUpdateDTO.getDescription()
        );

        // ───────────────────────────────────────────────────────
        // 3. 카테고리 업데이트 (BookCategory 테이블 기준)
        // ───────────────────────────────────────────────────────
        // 3.1 "현재 Book에 매핑된 카테고리" 목록 조회
        List<Long> oldCategoryIds = from(QBookCategory.bookCategory)
            .select(QBookCategory.bookCategory.category.id)
            .where(QBookCategory.bookCategory.book.id.eq(existingBook.getId()))
            .fetch();

        // 3.2 "새로 들어온 카테고리 이름"에서 ID 목록을 추출
        List<String> newCategoryNames = bookUpdateDTO.getCategories();
        // 새 카테고리들을 ID로 바꿔서 관리
        List<Long> newCategoryIds = new ArrayList<>();
        for (String catName : newCategoryNames) {
            Long catId = from(QCategory.category)
                .select(QCategory.category.id)
                .where(QCategory.category.name.eq(catName))
                .fetchOne();
            if (catId != null) {
                newCategoryIds.add(catId);
            }
        }

        // 3.3 제거 대상: oldCategoryIds 중에 newCategoryIds에 없는 것
        List<Long> removeCategoryIds = oldCategoryIds.stream()
            .filter(oldId -> !newCategoryIds.contains(oldId))
            .toList();

        // 실제 제거 수행
        if (!removeCategoryIds.isEmpty()) {
            // BookCategory 삭제
            // in 절을 써서 일괄 삭제 가능
            delete(QBookCategory.bookCategory)
                .where(QBookCategory.bookCategory.book.id.eq(existingBook.getId())
                    .and(QBookCategory.bookCategory.category.id.in(removeCategoryIds)))
                .execute();
        }

        // 3.4 추가 대상: newCategoryIds 중에 oldCategoryIds에 없는 것
        List<Long> addCategoryIds = newCategoryIds.stream()
            .filter(newId -> !oldCategoryIds.contains(newId))
            .toList();

        // 실제 추가 수행
        for (Long addId : addCategoryIds) {
            // BookCategory 매핑 엔티티 생성
            BookCategory bookCategory = new BookCategory();
            Category categoryRef = entityManager.getReference(Category.class, addId);
            bookCategory.create(existingBook,categoryRef);
            entityManager.persist(bookCategory);
        }

        // ───────────────────────────────────────────────────────
        // 4. 태그 업데이트 (BookTag 테이블 기준)
        // ───────────────────────────────────────────────────────
        // 4.1 "현재 Book에 매핑된 태그" ID 목록 조회
        List<Long> oldTagIds = from(QBookTag.bookTag)
            .select(QBookTag.bookTag.tag.id)
            .where(QBookTag.bookTag.book.id.eq(existingBook.getId()))
            .fetch();

        // 4.2 새로 들어온 태그 ID 목록 추출
        List<String> newTagNames = bookUpdateDTO.getTags();
        List<Long> newTagIds = new ArrayList<>();
        for (String tagName : newTagNames) {
            Long tagId = from(QTag.tag)
                .select(QTag.tag.id)
                .where(QTag.tag.name.eq(tagName))
                .fetchOne();
            if (tagId != null) {
                newTagIds.add(tagId);
            }
        }

        // 4.3 제거 대상 태그
        List<Long> removeTagIds = oldTagIds.stream()
            .filter(oldId -> !newTagIds.contains(oldId))
            .toList();

        if (!removeTagIds.isEmpty()) {
            delete(QBookTag.bookTag)
                .where(QBookTag.bookTag.book.id.eq(existingBook.getId())
                    .and(QBookTag.bookTag.tag.id.in(removeTagIds)))
                .execute();
        }

        // 4.4 추가 대상 태그
        List<Long> addTagIds = newTagIds.stream()
            .filter(newId -> !oldTagIds.contains(newId))
            .toList();

        for (Long addId : addTagIds) {
            Tag tagRef = entityManager.getReference(Tag.class, addId);
            BookTag bookTag = new BookTag(existingBook,tagRef);
            entityManager.persist(bookTag);
        }

        // ───────────────────────────────────────────────────────
        // 5. BookType 업데이트
        // ───────────────────────────────────────────────────────
        // BookType 테이블에 ranks가 있으므로, ranks 처리도 함께 수행
        // 5.1 기존 BookType 목록
        List<BookType> oldBookTypes = from(QBookType.bookType)
            .select(QBookType.bookType)
            .where(QBookType.bookType.book.id.eq(existingBook.getId()))
            .fetch();

        // 5.2 새 BookType 문자열 목록
        List<String> newTypeStrs = bookUpdateDTO.getBookTypes();

        // 5.3 제거 대상 (문자열 비교)
        for (BookType oldBT : oldBookTypes) {
            // oldBT.getTypes()가 Enum이므로 대소문자 변환해서 비교
            String oldTypeName = oldBT.getTypes().name().toLowerCase(Locale.ROOT);
            if (!newTypeStrs.contains(oldTypeName)) {
                // 매핑 엔티티 제거
                entityManager.remove(oldBT);
            }
        }

        // 5.4 추가 대상
        for (String typeStr : newTypeStrs) {
            Type typeEnum = Type.valueOf(typeStr.toUpperCase(Locale.ROOT));

            // 이미 존재하는지 체크
            boolean exists = oldBookTypes.stream()
                .anyMatch(bt -> bt.getTypes() == typeEnum);
            if (!exists) {
                // ranks 정보 가져오기 (필요하다면)
                Integer rank = from(QBookType.bookType)
                    .select(QBookType.bookType.ranks)
                    .where(QBookType.bookType.types.eq(typeEnum)
                        .and(QBookType.bookType.book.id.eq(existingBook.getId())))
                    .fetchOne();
                int rankToUse = (rank != null) ? rank : 0;

                // 매핑 엔티티 생성
                BookType newBookType = new BookType(typeEnum,rankToUse, existingBook);

                entityManager.persist(newBookType);
            }
        }

        // ───────────────────────────────────────────────────────
        // 6. 작가(크리에이터) 업데이트 (BookCreatorMap 활용)
        // ───────────────────────────────────────────────────────
        // 6.1 기존 매핑된 Creator 목록
        List<Long> oldCreatorIds = from(QBookCreatorMap.bookCreatorMap)
            .select(QBookCreatorMap.bookCreatorMap.creator.id)
            .where(QBookCreatorMap.bookCreatorMap.book.id.eq(existingBook.getId()))
            .fetch();

        // 6.2 새 Creator 목록
        List<String> newAuthorNames = bookUpdateDTO.getAuthors();
        List<Long> newCreatorIds = new ArrayList<>();
        for (String authorName : newAuthorNames) {
            Long creatorId = from(QBookCreator.bookCreator)
                .select(QBookCreator.bookCreator.id)
                .where(QBookCreator.bookCreator.name.eq(authorName))
                .fetchOne();
            if (creatorId != null) {
                newCreatorIds.add(creatorId);
            }
        }

        // 6.3 제거 대상
        List<Long> removeCreatorIds = oldCreatorIds.stream()
            .filter(oldId -> !newCreatorIds.contains(oldId))
            .toList();
        if (!removeCreatorIds.isEmpty()) {
            delete(QBookCreatorMap.bookCreatorMap)
                .where(QBookCreatorMap.bookCreatorMap.book.id.eq(existingBook.getId())
                    .and(QBookCreatorMap.bookCreatorMap.creator.id.in(removeCreatorIds)))
                .execute();
        }

        // 6.4 추가 대상
        List<Long> addCreatorIds = newCreatorIds.stream()
            .filter(newId -> !oldCreatorIds.contains(newId))
            .toList();
        for (Long addId : addCreatorIds) {
            BookCreator creatorRef = entityManager.getReference(BookCreator.class, addId);
            BookCreatorMap map = new BookCreatorMap(existingBook, creatorRef);
            entityManager.persist(map);
        }

        // ───────────────────────────────────────────────────────
        // 7. 인덱스 업데이트
        // ───────────────────────────────────────────────────────
        // BookIndex 자체도 매핑 테이블일 경우, 기존 BookIndex 목록을 가져온 뒤 비교 후 추가/삭제하면 된다.
        // 만약 Book 엔티티에 단방향으로 updateIndex() 같은 메서드가 있다면 그대로 사용
        // 여기서는 예시로 from(QBookIndex.bookIndex) ~ 식으로 처리
        // 필요에 따라 적절히 구현
        if (bookUpdateDTO.getIndex() != null) {
            // 예시) 단순히 BookIndex 하나를 가진다고 가정
            BookIndex oldIndex = from(QBookIndex.bookIndex)
                .select(QBookIndex.bookIndex)
                .where(QBookIndex.bookIndex.book.id.eq(existingBook.getId()))
                .fetchOne();

            if (oldIndex == null) {
                // 새로 생성
                BookIndex newIndex = new BookIndex(bookUpdateDTO.getIndex(), existingBook);
                entityManager.persist(newIndex);
            } else {
                // 업데이트
                oldIndex.updateIndexText(bookUpdateDTO.getIndex());
                // JPA 변경 감지 자동 반영
            }
        }

        // ───────────────────────────────────────────────────────
        // 8. 최종 반영 (트랜잭션이 끝나거나 flush 시점에 반영)
        // ───────────────────────────────────────────────────────
    }
}

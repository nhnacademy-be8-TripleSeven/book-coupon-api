
package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.client.MemberFeignClient;
import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyOrderResponseDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.dto.member.MemberDto;
import com.nhnacademy.bookapi.dto.member.MemberNotFoundException;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.couponpolicy.CouponPolicyService;
import com.nhnacademy.bookapi.service.couponpolicy.CouponPolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CouponServiceImplTest {


    @InjectMocks
    private CouponServiceImpl couponService;

    @InjectMocks
    private CouponPolicyServiceImpl couponPolicyServiceImpl;

    @Mock
    private CouponService couponServiceMock;

    @Mock
    private CouponPolicyService couponPolicyService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCouponRepository bookCouponRepository;

    @Mock
    private CategoryCouponRepository categoryCouponRepository;

    @Mock
    private MemberFeignClient memberFeignClient;

    @Mock
    private CouponPolicyOrderResponseDTO couponPolicyOrderResponseDTO;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCoupon_Success() {
        // Given
        CouponPolicy policy = CouponPolicy.builder().name("Test Policy").build();

        Coupon coupon = Coupon.builder().id(1L).name("Test Coupon").couponPolicy(policy).build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // When
        BaseCouponResponseDTO response = couponService.createCoupon(new CouponCreationRequestDTO("Test Coupon", 1L));

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Coupon", response.getName());
        assertEquals(policy, response.getCouponPolicy());
    }

    @Test
    void testCreateCoupon_Failure_PolicyNotFound() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        CouponCreationRequestDTO requestDTO = new CouponCreationRequestDTO("Coupon Name", 1L);
        assertThrows(CouponPolicyNotFoundException.class, () -> couponService.createCoupon(requestDTO));
    }

    @Test
    void testCreateBookCoupon_Success() {
        // Given
        Book book = Book.builder().title("Test Book").build();

        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Test Policy").build();

        Coupon coupon = Coupon.builder().id(1L).name("Book Coupon").couponPolicy(policy).build();

        BookCoupon bookCoupon = BookCoupon.builder().book(book).coupon(coupon).id(1L).build();


        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(bookCouponRepository.save(any(BookCoupon.class))).thenReturn(bookCoupon);

        // When
        BookCouponResponseDTO response = couponService.createBookCoupon(new BookCouponCreationRequestDTO(1L, 1L, "Book Coupon"));

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Book Coupon", response.getName());
        assertEquals(policy, response.getCouponPolicy());
        assertEquals("Test Book", response.getBookTitle());
    }

    @Test
    void testCreateBookCoupon_BookNotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(BookNotFoundException.class, () ->
                couponService.createBookCoupon(new BookCouponCreationRequestDTO(1L, 1L, "Book Coupon")));
    }

    @Test
    void testCreateCategoryCoupon_Success() {
        // Given
        Category category = Category.builder().id(1L).name("Test Category").level(0).build();

        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Test Policy").build();

        Coupon coupon = Coupon.builder().id(1L).name("Category Coupon").couponPolicy(policy).build();

        CategoryCoupon categoryCoupon = CategoryCoupon.builder().id(1L).category(category).coupon(coupon).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(categoryCouponRepository.save(any(CategoryCoupon.class))).thenReturn(categoryCoupon);

        // When
        CategoryCouponResponseDTO response = couponService.createCategoryCoupon(new CategoryCouponCreationRequestDTO(1L, 1L, "Category Coupon"));

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Category Coupon", response.getName());
        assertEquals(policy, response.getCouponPolicy());
        assertEquals("Test Category", response.getCategoryName());
    }

    @Test
    void testCreateCategoryCoupon_CategoryNotFound() {
        // Given
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(CategoryNotFoundException.class, () ->
                couponService.createCategoryCoupon(new CategoryCouponCreationRequestDTO(1L, 1L, "Category Coupon")));
    }

    @Test
    void testCreateCouponsInBulk_Success() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Test Policy").couponValidTime(30).build();
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Coupon coupon = Coupon.builder().id(1L).name("Bulk Coupon").couponPolicy(policy).build();
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
                "Bulk Coupon", 1L, null, null, 5L
        );

        BulkCouponCreationResponseDTO response = couponService.createCouponsInBulk(request);

        assertTrue(response.isSuccess());
        assertEquals(5, response.getCreatedCount());
    }

    @Test
    void testCreateCouponsInBulk_CategoryCouponSuccess() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Category Policy").couponValidTime(30).build();
        Category category = Category.builder().id(1L).name("Books").build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(couponRepository.save(any(Coupon.class)))
                .thenReturn(Coupon.builder().id(1L).name("Category Coupon").couponPolicy(policy).build());

        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
                "Category Bulk Coupon", 1L, 1L, null, 3L
        );

        BulkCouponCreationResponseDTO response = couponService.createCouponsInBulk(request);

        assertTrue(response.isSuccess());
        assertEquals(3, response.getCreatedCount()); // 요청한 수만큼 생성 성공
    }

    @Test
    void testCreateCouponsInBulk_BookCouponSuccess() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Book Policy").couponValidTime(30).build();
        Book book = Book.builder().id(1L).title("Effective Java").build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(couponRepository.save(any(Coupon.class)))
                .thenReturn(Coupon.builder().id(1L).name("Book Coupon").couponPolicy(policy).build());

        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
                "Book Bulk Coupon", 1L, null, 1L, 2L
        );

        BulkCouponCreationResponseDTO response = couponService.createCouponsInBulk(request);

        assertTrue(response.isSuccess());
        assertEquals(2, response.getCreatedCount()); // 요청한 수만큼 생성 성공
    }

    @Test
    void testCreateCouponsInBulk_PartialSuccess() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Test Policy").couponValidTime(30).build();
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // 첫 번째와 두 번째 쿠폰은 성공적으로 생성
        when(couponRepository.save(any(Coupon.class)))
                .thenReturn(Coupon.builder().id(1L).name("Coupon 1").couponPolicy(policy).build())
                .thenReturn(Coupon.builder().id(2L).name("Coupon 2").couponPolicy(policy).build())
                .thenThrow(new RuntimeException("Failed to create coupon 3")) // 세 번째 쿠폰은 실패
                .thenReturn(Coupon.builder().id(4L).name("Coupon 4").couponPolicy(policy).build());

        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
                "Partial Bulk Coupon", 1L, null, null, 4L
        );

        BulkCouponCreationResponseDTO response = couponService.createCouponsInBulk(request);

        assertTrue(response.isSuccess());
        assertEquals(3, response.getCreatedCount()); // 성공적으로 생성된 쿠폰은 3개
    }

    @Test
    void testCreateCouponsInBulk_AllFailures() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Test Policy").couponValidTime(30).build();
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // 모든 쿠폰 생성에서 예외 발생
        when(couponRepository.save(any(Coupon.class))).thenThrow(new RuntimeException("Failed to create coupon"));

        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
                "Failure Bulk Coupon", 1L, null, null, 5L
        );

        BulkCouponCreationResponseDTO response = couponService.createCouponsInBulk(request);

        assertFalse(response.isSuccess());
        assertEquals(0, response.getCreatedCount()); // 성공적으로 생성된 쿠폰이 없음
    }

    @Test
    void testCreateCouponsInBulk_Failure_NoPolicy() {
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
                "Bulk Coupon", 1L, null, null, 5L
        );

        BulkCouponCreationResponseDTO response = couponService.createCouponsInBulk(request);

        assertFalse(response.isSuccess());
        assertEquals(0, response.getCreatedCount());
    }




    @Test
    void testExpireCoupons_Success() {
        // Given
        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Test Policy").build();

        Coupon coupon1 = Coupon.builder().id(1L).name("Test Coupon").
                couponExpiryDate(LocalDate.now().minusDays(1)).couponPolicy(policy).couponStatus(CouponStatus.NOTUSED).build();

        Coupon coupon2 = Coupon.builder().id(1L).name("Test Coupon").
                couponExpiryDate(LocalDate.now().minusDays(1)).couponPolicy(policy).couponStatus(CouponStatus.NOTUSED).build();

        List<Coupon> expiredCoupons = List.of(coupon1, coupon2);

        when(couponRepository.findByCouponStatusAndCouponExpiryDateBefore(CouponStatus.NOTUSED, LocalDate.now()))
                .thenReturn(expiredCoupons);

        // When
        couponService.expireCoupons();

        // Then
        assertEquals(CouponStatus.EXPIRED, coupon1.getCouponStatus());
        assertEquals(CouponStatus.EXPIRED, coupon2.getCouponStatus());
    }

    @Test
    void testExpireCoupons_NoCouponsToExpire() {
        // Given
        when(couponRepository.findByCouponStatusAndCouponExpiryDateBefore(CouponStatus.NOTUSED, LocalDate.now()))
                .thenReturn(List.of());

        // When
        couponService.expireCoupons();

        // Then
        verify(couponRepository, times(1)).findByCouponStatusAndCouponExpiryDateBefore(CouponStatus.NOTUSED, LocalDate.now());
    }


    @Test
    void testUseCoupon_Success_BaseCoupon() {
        // Given
        long userId = 123L;
        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Base Coupon").couponPolicy(policy).memberId(userId).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // When
        CouponUseResponseDTO response = couponService.useCoupon(userId, couponId, 1L);

        // Then
        assertNotNull(response);
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
        assertEquals("Base Coupon", response.getName());
    }

    @Test
    void testUseCoupon_NotAssigned() {
        long userId = 123L;
        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Base Coupon").couponPolicy(policy).couponStatus(CouponStatus.USED).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        assertThrows(CouponNotAssignedException.class, () -> couponService.useCoupon(userId, couponId, 1L));

    }

    @Test
    void testUseCoupon_AlreadyUsed() {
        long userId = 123L;
        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Base Coupon").couponPolicy(policy).memberId(userId).couponStatus(CouponStatus.USED).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        assertThrows(CouponAlreadyUsedException.class, () ->  couponService.useCoupon(userId, couponId, 1L));

    }

    @Test
    void testUseCoupon_Expired() {
        long userId = 123L;
        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Base Coupon").couponPolicy(policy).memberId(userId).couponStatus(CouponStatus.EXPIRED).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        assertThrows(CouponExpiredException.class, () ->  couponService.useCoupon(userId, couponId, 1L));

    }


    @Test
    void testUseCoupon_Success_BookCoupon() {
        // Given
        long userId = 123L;
        long couponId = 1L;
        long bookId = 1L;

        Book book = Book.builder().id(bookId).title("Test Book").build();

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Book Coupon").couponPolicy(policy).memberId(userId).build();

        BookCoupon bookCoupon = BookCoupon.builder().book(book).coupon(coupon).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(bookCouponRepository.existsByCoupon(coupon)).thenReturn(true);
        when(bookCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(bookCoupon));

        // When
        CouponUseResponseDTO response = couponService.useCoupon(userId, couponId, bookId);

        // Then
        assertNotNull(response);
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
        assertEquals("Book Coupon", response.getName());
    }


    @Test
    void testUseCoupon_CouponNotFound_BookCoupon() {

        long userId = 123L;
        long couponId = 1L;
        long bookId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Book Coupon").couponPolicy(policy).memberId(userId).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(bookCouponRepository.existsByCoupon(coupon)).thenReturn(true);

        assertThrows(CouponNotFoundException.class, () -> couponService.useCoupon(userId, couponId, bookId));

    }


    @Test
    void testUseCoupon_InvalidCouponUse_BookCoupon() {

        long userId = 123L;
        long couponId = 1L;
        long bookId = 1L;

        Book book = Book.builder().id(bookId).title("Test Book").build();

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Book Coupon").couponPolicy(policy).memberId(userId).build();

        BookCoupon bookCoupon = BookCoupon.builder().book(book).coupon(coupon).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(bookCouponRepository.existsByCoupon(coupon)).thenReturn(true);
        when(bookCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(bookCoupon));

        assertThrows(InvalidCouponUsageException.class, () -> couponService.useCoupon(userId, couponId, 2L));
    }


    @Test
    void testUseCoupon_Success_CategoryCoupon() {
        // Given
        long userId = 123L;
        long couponId = 2L;
        long bookId = 1L;

        Category category = Category.builder().id(10L).name("Test Category").build();

        // 쿠폰과 정책 설정
        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();
        Coupon coupon = Coupon.builder().id(couponId).name("Category Coupon").couponPolicy(policy).memberId(userId).build();
        CategoryCoupon categoryCoupon = CategoryCoupon.builder().category(category).coupon(coupon).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(bookCouponRepository.existsByCoupon(coupon)).thenReturn(false);
        when(categoryCouponRepository.existsByCoupon(coupon)).thenReturn(true);
        when(categoryCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(categoryCoupon));
        when(bookCategoryRepository.findCategoriesByBookId(bookId)).thenReturn(List.of(category));

        // When
        CouponUseResponseDTO response = couponService.useCoupon(userId, couponId, bookId);

        // Then
        assertNotNull(response);
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
        assertEquals("Category Coupon", response.getName());
    }


    @Test
    void testUseCoupon_CouponNotFound_CategoryCoupon() {
        // Given
        long userId = 123L;
        long couponId = 2L;
        long bookId = 1L;

        // 도서와 카테고리 설정
        Category category = Category.builder().id(10L).name("Test Category").build();
        Book book = Book.builder().id(bookId).title("Test Book").build();
        BookCategory bookCategory = new BookCategory(book, category);

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();
        Coupon coupon = Coupon.builder().id(couponId).name("Category Coupon").couponPolicy(policy).memberId(userId).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(bookCouponRepository.existsByCoupon(coupon)).thenReturn(false);
        when(categoryCouponRepository.existsByCoupon(coupon)).thenReturn(true);

        // When
        assertThrows(CouponNotFoundException.class, () -> couponService.useCoupon(userId, couponId, bookId));
    }


    @Test
    void testUseCoupon_InvalidUse_CategoryCoupon() {
        // Given
        long userId = 123L;
        long couponId = 2L;
        long bookId = 1L;

        Category category = Category.builder().id(10L).name("Test Category").build();

        Category category2 = Category.builder().id(11L).name("Test Category2").build();

        // 쿠폰과 정책 설정
        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();
        Coupon coupon = Coupon.builder().id(couponId).name("Category Coupon").couponPolicy(policy).memberId(userId).build();
        CategoryCoupon categoryCoupon = CategoryCoupon.builder().category(category).coupon(coupon).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(bookCouponRepository.existsByCoupon(coupon)).thenReturn(false);
        when(categoryCouponRepository.existsByCoupon(coupon)).thenReturn(true);
        when(categoryCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(categoryCoupon));
        when(bookCategoryRepository.findCategoriesByBookId(bookId)).thenReturn(List.of(category2));


        assertThrows(InvalidCouponUsageException.class, () ->  couponService.useCoupon(userId, couponId, bookId));
    }

    @Test
    void testUseBaseCoupon_Success() {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Base Policy")
                .couponDiscountRate(BigDecimal.valueOf(0.1))
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Base Coupon")
                .couponPolicy(policy)
                .couponStatus(CouponStatus.NOTUSED)
                .couponExpiryDate(LocalDate.now().plusDays(10))
                .build();

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        CouponUseResponseDTO result = couponService.useBaseCoupon(1L);

        assertNotNull(result);
        assertEquals("Base Coupon", result.getName());
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
    }

    @Test
    void testUseBaseCoupon_Used() {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Base Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Base Coupon")
                .couponPolicy(policy)
                .couponStatus(CouponStatus.USED)
                .couponExpiryDate(LocalDate.now().minusDays(1))
                .build();

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        assertThrows(CouponAlreadyUsedException.class, () -> couponService.useBaseCoupon(1L));
    }

    @Test
    void testUseBaseCoupon_Expired() {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Base Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Base Coupon")
                .couponPolicy(policy)
                .couponStatus(CouponStatus.EXPIRED)
                .couponExpiryDate(LocalDate.now().minusDays(1))
                .build();

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        assertThrows(CouponExpiredException.class, () -> couponService.useBaseCoupon(1L));
    }

    @Test
    void testGetCouponsForUser() {
        long userId = 123L;
        String keyword = "Discount";
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Base Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Discount Coupon")
                .couponIssueDate(LocalDate.now().minusDays(5))
                .couponStatus(CouponStatus.NOTUSED)
                .couponPolicy(policy)
                .build();

        when(couponRepository.findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(anyLong(), any(LocalDate.class)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getCouponsForUser(userId, keyword, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Discount Coupon", result.get(0).getName());
    }

    @Test
    void testGetCouponsForUser_WithKeywordFilter() {
        long userId = 123L;
        String keyword = "Special";
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Special Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Special Discount Coupon")
                .couponIssueDate(LocalDate.now().minusDays(30))
                .couponPolicy(policy)
                .couponStatus(CouponStatus.NOTUSED)
                .build();

        when(couponRepository.findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(eq(userId), eq(fiveYearsAgo)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getCouponsForUser(userId, keyword, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Special Discount Coupon", result.get(0).getName());
    }

    @Test
    void testGetCouponsForUser_WithDateRangeFilter() {
        long userId = 123L;
        LocalDate startDate = LocalDate.now().minusDays(40);
        LocalDate endDate = LocalDate.now().minusDays(10);

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Date Filter Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Date Filter Coupon")
                .couponIssueDate(LocalDate.now().minusDays(20))
                .couponPolicy(policy)
                .couponStatus(CouponStatus.NOTUSED)
                .build();

        when(couponRepository.findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(eq(userId), any(LocalDate.class)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getCouponsForUser(userId, null, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Date Filter Coupon", result.get(0).getName());
    }

    @Test
    void testGetCouponsForUser_WithKeywordAndDateRangeFilter() {
        long userId = 123L;
        String keyword = "Combo";
        LocalDate startDate = LocalDate.now().minusDays(40);
        LocalDate endDate = LocalDate.now().minusDays(10);

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Combo Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Combo Deal Coupon")
                .couponIssueDate(LocalDate.now().minusDays(20))
                .couponPolicy(policy)
                .couponStatus(CouponStatus.NOTUSED)
                .build();

        when(couponRepository.findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(eq(userId), any(LocalDate.class)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getCouponsForUser(userId, keyword, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Combo Deal Coupon", result.get(0).getName());
    }

    @Test
    void testGetUsedCouponsForUser() {
        long userId = 123L;
        String keyword = "Used";
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Base Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Used Coupon")
                .couponStatus(CouponStatus.USED)
                .couponIssueDate(LocalDate.now().minusDays(5))
                .couponPolicy(policy)
                .build();

        when(couponRepository.findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(anyLong(), eq(CouponStatus.USED), any(LocalDate.class)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getUsedCouponsForUser(userId, keyword, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Used Coupon", result.get(0).getName());
    }

    @Test
    void testGetUsedCouponsForUser_WithKeywordFilter() {
        long userId = 123L;
        String keyword = "Exclusive";
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Exclusive Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Exclusive Offer Coupon")
                .couponStatus(CouponStatus.USED)
                .couponIssueDate(LocalDate.now().minusDays(30))
                .couponPolicy(policy)
                .build();

        when(couponRepository.findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(eq(userId), eq(CouponStatus.USED), eq(fiveYearsAgo)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getUsedCouponsForUser(userId, keyword, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Exclusive Offer Coupon", result.get(0).getName());
    }

    @Test
    void testGetUsedCouponsForUser_WithDateRangeFilter() {
        long userId = 123L;
        LocalDate startDate = LocalDate.now().minusDays(40);
        LocalDate endDate = LocalDate.now().minusDays(10);

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Date Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Date Coupon")
                .couponStatus(CouponStatus.USED)
                .couponIssueDate(LocalDate.now().minusDays(20))
                .couponPolicy(policy)
                .build();

        when(couponRepository.findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(eq(userId), eq(CouponStatus.USED), any(LocalDate.class)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getUsedCouponsForUser(userId, null, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Date Coupon", result.get(0).getName());
    }

    @Test
    void testGetUsedCouponsForUser_WithKeywordAndDateRangeFilter() {
        long userId = 123L;
        String keyword = "Special";
        LocalDate startDate = LocalDate.now().minusDays(50);
        LocalDate endDate = LocalDate.now().minusDays(10);

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Special Policy")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Special Used Coupon")
                .couponStatus(CouponStatus.USED)
                .couponIssueDate(LocalDate.now().minusDays(20))
                .couponPolicy(policy)
                .build();

        when(couponRepository.findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(eq(userId), eq(CouponStatus.USED), any(LocalDate.class)))
                .thenReturn(List.of(coupon));

        List<CouponDetailsDTO> result = couponService.getUsedCouponsForUser(userId, keyword, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Special Used Coupon", result.get(0).getName());
    }


    @Test
    void testGetCouponPolicyByCouponId_Success() {

        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .couponMinAmount(1000L)
                .couponMaxAmount(5000L)
                .couponDiscountRate(new BigDecimal("0.10"))
                .couponDiscountAmount(500L)
                .build();

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .couponPolicy(policy)
                .couponStatus(CouponStatus.NOTUSED)
                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponPolicyRepository.findById(policy.getId())).thenReturn(Optional.of(policy));

        CouponPolicyOrderResponseDTO response = couponService.getCouponPolicyByCouponId(couponId);

        assertNotNull(response);
        assertEquals(1000L, response.getCouponMinAmount());
        assertEquals(5000L, response.getCouponMaxAmount());
        assertEquals(new BigDecimal("0.10"), response.getCouponDiscountRate());
        assertEquals(500L, response.getCouponDiscountAmount());
        assertEquals(CouponStatus.NOTUSED, response.getCouponStatus());
    }

    @Test
    void testGetCouponPolicyByCouponId_CouponNotFound() {

        long couponId = 1L;

        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        CouponNotFoundException exception = assertThrows(CouponNotFoundException.class,
                () -> couponService.getCouponPolicyByCouponId(couponId));

        assertEquals("No coupons found for ID: " + couponId, exception.getMessage());
    }

    @Test
    void testGetCouponPolicyByCouponId_CouponPolicyNotFound() {

        long couponId = 1L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .couponPolicy(CouponPolicy.builder().id(2L).build())
                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponPolicyRepository.findById(2L)).thenReturn(Optional.empty());

        CouponPolicyNotFoundException exception = assertThrows(CouponPolicyNotFoundException.class,
                () -> couponService.getCouponPolicyByCouponId(couponId));

        assertEquals("No coupons found for policy with ID: 2", exception.getMessage());
    }




    @Test
    void testCreateAndAssignCoupons_Success() {

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Test Policy")
                .couponValidTime(30)
                .build();

        MemberDto member = MemberDto.builder().id(1L).build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Test Coupon")
                .couponPolicy(policy)
                .build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(memberFeignClient.getMembers(null, null, 0, 100, null, "ASC"))
                .thenReturn(new PageImpl<>(List.of(member)));
        when(couponRepository.saveAll(anyList())).thenReturn(List.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                null,
                "전체"
        );
        List<CouponAssignResponseDTO> response = couponService.createAndAssignCoupons(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getCouponId());
    }


    @Test
    void testCreateAndAssignCoupons_Failure_CouponPolicyNotFound() {
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                null,
                "전체"
        );

        assertThrows(CouponPolicyNotFoundException.class, () -> couponService.createAndAssignCoupons(request));
    }


    @Test
    void testCreateAndAssignCoupons_Failure_InvalidRecipientType() {

        CouponPolicy policy = CouponPolicy.builder().id(1L).build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(memberFeignClient.getMembers(null, null, 0, 100, null, "ASC"))
                .thenReturn(new PageImpl<>(List.of()));


        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                null,
                "test"
        );
        assertThrows(InvalidRecipientTypeException.class, () -> couponService.createAndAssignCoupons(request));
    }

    @Test
    void testCreateAndAssignCoupons_Failure_EmptyRecipients() {
        CouponPolicy policy = CouponPolicy.builder().id(1L).build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(memberFeignClient.getMembers(null, null, 0, 100, null, "ASC"))
                .thenReturn(new PageImpl<>(List.of())); // Empty list for members

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                null,
                "전체"
        );

        assertThrows(MemberNotFoundException.class, () -> couponService.createAndAssignCoupons(request));
    }

    @Test
    void testCreateAndAssignCoupons_Success_GradeRecipient() {

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Test Policy")
                .couponValidTime(30)
                .build();

        MemberDto member = MemberDto.builder().id(1L).build();
        Coupon coupon = Coupon.builder()
                .id(1L)
                .couponPolicy(policy)
                .name("Test Coupon")
                .build();

        CouponCreationAndAssignRequestDTO request = CouponCreationAndAssignRequestDTO.builder().name("Test Coupon").
                bookId(null).categoryId(null).couponPolicyId(1L).recipientType("등급별").grade("GOLD").build();

        when(couponPolicyRepository.findById(request.getCouponPolicyId())).thenReturn(Optional.of(policy));
        when(memberFeignClient.getMembers(null, MemberGrade.GOLD, 0, 100, null, "ASC"))
                .thenReturn(new PageImpl<>(List.of(member)));
        when(couponRepository.saveAll(anyList())).thenReturn(List.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));




        List<CouponAssignResponseDTO> response = couponService.createAndAssignCoupons(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getCouponId());
    }

    @Test
    void testCreateAndAssignCoupons_Success_IndividualRecipient() {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Test Policy")
                .couponValidTime(30)
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("Test Coupon")
                .couponPolicy(policy)
                .build();

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponRepository.saveAll(anyList())).thenReturn(List.of(coupon));
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                "Test Coupon",
                1L,
                List.of(1L),
                "개인별"
        );

        List<CouponAssignResponseDTO> response = couponService.createAndAssignCoupons(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getCouponId());
    }


    @Test
    void testConvertGrade_InvalidGrade() {
        String invalidGrade = "TestGrade";

        InvalidRecipientTypeException exception = assertThrows(
                InvalidRecipientTypeException.class,
                () -> couponService.convertGrade(invalidGrade)
        );

        assertEquals("유효하지 않은 등급: " + invalidGrade, exception.getMessage());
    }



    @Test
    void issueWelcomeCoupon_Success() {
        // given
        Long memberId = 1L;


        Coupon welcomeCoupon = Coupon.builder()
                .id(1L)
                .name("회원가입 선착순 쿠폰")
                .build();

        CouponAssignRequestDTO assignRequest = new CouponAssignRequestDTO(1L, memberId);

        when(couponPolicyService.searchCouponPoliciesByName("Welcome"))
                .thenReturn(Collections.singletonList(
                        CouponPolicyResponseDTO.builder()
                                .id(1L)
                                .name("Welcome Policy")
                                .couponValidTime(30)
                                .build()
                ));

// 정책이 없을 경우를 명확히 확인
        when(couponPolicyService.searchCouponPoliciesByName("Welcome")).thenReturn(Collections.emptyList());


        when(couponRepository.findAndLockFirstByName("회원가입 선착순 쿠폰"))
                .thenReturn(Optional.of(welcomeCoupon));

        doNothing().when(rabbitTemplate)
                .convertAndSend(eq(RabbitConfig.EXCHANGE_NAME), eq(RabbitConfig.ROUTING_KEY), eq(assignRequest));

        List<CouponAssignResponseDTO> responses = couponService.issueWelcomeCoupon(memberId);


        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Welcome coupon assigned successfully.", responses.get(0).getStatusMessage());

        verify(couponPolicyService, times(1)).searchCouponPoliciesByName("Welcome");
        verify(couponRepository, times(1)).findAndLockFirstByName("회원가입 선착순 쿠폰");
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitConfig.EXCHANGE_NAME),
                eq(RabbitConfig.ROUTING_KEY),
                any(CouponAssignRequestDTO.class)
        );
    }


    @Test
    void testIssueWelcomeCoupon_NoFirstComeCoupon() {
        Long memberId = 1L;

        when(couponRepository.findAndLockFirstByName("회원가입 선착순 쿠폰")).thenReturn(Optional.empty());

        List<CouponAssignResponseDTO> results = couponService.issueWelcomeCoupon(memberId);

        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(CouponAssignRequestDTO.class));
    }

    @Test
    void testIssueWelcomeCoupon_WelcomePoliciesError() {
        // given
        Long memberId = 1L;

        when(couponPolicyService.searchCouponPoliciesByName("Welcome"))
                .thenThrow(new RuntimeException("Error during welcome policies processing"));

        // when
        CouponIssueWelcomeException exception = assertThrows(CouponIssueWelcomeException.class, () -> {
            couponService.issueWelcomeCoupon(memberId);
        });

        // then
        assertEquals("Some policies failed during coupon issuance.", exception.getMessage());
        verify(couponPolicyService, times(1)).searchCouponPoliciesByName("Welcome");
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(CouponAssignRequestDTO.class));
    }

    @Test
    void testIssueWelcomeCoupon_FirstComeCouponError() {
        Long memberId = 1L;

        CouponPolicyResponseDTO policyResponse = CouponPolicyResponseDTO.builder().id(1L).name("Welcome Policy").build();
        when(couponPolicyService.searchCouponPoliciesByName("Welcome")).thenReturn(List.of(policyResponse));

        doThrow(new RuntimeException("Error during first come coupon processing"))
                .when(couponRepository).findAndLockFirstByName("회원가입 선착순 쿠폰");

        // when
        CouponIssueWelcomeException exception = assertThrows(CouponIssueWelcomeException.class, () -> {
            couponService.issueWelcomeCoupon(memberId);
        });

        assertEquals("Some policies failed during coupon issuance.", exception.getMessage());
    }



    @Test
    void issueWelcomeCoupon_AllPoliciesFail() {
        // given
        Long memberId = 1L;

        when(couponPolicyService.searchCouponPoliciesByName("Welcome"))
                .thenThrow(new RuntimeException("Error during welcome policies processing"));

        when(couponRepository.findAndLockFirstByName("회원가입 선착순 쿠폰"))
                .thenThrow(new RuntimeException("Error during first come coupon processing"));

        // when
        CouponIssueWelcomeException exception = assertThrows(CouponIssueWelcomeException.class, () -> {
            couponService.issueWelcomeCoupon(memberId);
        });

        // then
        assertEquals("Some policies failed during coupon issuance.", exception.getMessage());
        verify(couponPolicyService, times(1)).searchCouponPoliciesByName("Welcome");
        verify(couponRepository, times(1)).findAndLockFirstByName("회원가입 선착순 쿠폰");
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(CouponAssignRequestDTO.class));
    }


    @Test
    void issueWelcomeCoupon_UnexpectedError() {
        // given
        Long memberId = 1L;

        doThrow(new RuntimeException("Unexpected error"))
                .when(couponPolicyService).searchCouponPoliciesByName("Welcome");

        // when
        CouponIssueWelcomeException exception = assertThrows(CouponIssueWelcomeException.class, () -> {
            couponService.issueWelcomeCoupon(memberId);
        });

        // then
        assertEquals("Some policies failed during coupon issuance.", exception.getMessage());
        verify(couponPolicyService, times(1)).searchCouponPoliciesByName("Welcome");
        verifyNoInteractions(rabbitTemplate);
    }




    @Test
    void testCreateCouponBasedOnTarget_CategoryCoupon() {
        // Mocked 데이터 준비

        Category category = Category.builder().id(100L).build();

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Category Policy")
                .couponValidTime(30)
                .build();

        CouponCreationAndAssignRequestDTO request = CouponCreationAndAssignRequestDTO.builder()
                .couponPolicyId(1L)
                .categoryId(100L) // 카테고리 ID 설정
                .name("카테고리 할인 쿠폰")
                .build();


        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("카테고리 할인 쿠폰")
                .couponPolicy(policy)
                .build();


        when(categoryRepository.findById(100L)).thenReturn(Optional.of(category));
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);


        // 메서드 호출
        Coupon result = couponService.createCouponBasedOnTarget(request, policy);

        // 결과 검증
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("카테고리 할인 쿠폰", result.getName());
    }



    @Test
    void testCreateCouponBasedOnTarget_BookCoupon() {

        Book book = Book.builder().id(1L).build();

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Book Policy")
                .couponValidTime(30)
                .build();

        CouponCreationAndAssignRequestDTO request = CouponCreationAndAssignRequestDTO.builder()
                .couponPolicyId(1L)
                .bookId(1L)
                .name("도서 할인 쿠폰")
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("도서 할인 쿠폰")
                .couponPolicy(policy)
                .build();


        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);


        // 메서드 호출
        Coupon result = couponService.createCouponBasedOnTarget(request, policy);

        // 결과 검증
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("도서 할인 쿠폰", result.getName());
    }





    @Test
    void testSearchCouponPoliciesByName_NotFound() {
        // Mock 설정
        when(couponPolicyRepository.findByNameContainingIgnoreCase("NonExistingPolicy")).thenReturn(List.of());

        // 예외 검증
        assertThrows(CouponPolicyNotFoundException.class, () -> {
            couponPolicyServiceImpl.searchCouponPoliciesByName("NonExistingPolicy");
        });
    }

    @Test
    void testSearchCouponPoliciesByName_Found() {

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("Welcome Policy")
                .build();

        when(couponPolicyRepository.findByNameContainingIgnoreCase("Welcome")).thenReturn(List.of(policy));

        // 메서드 호출
        List<CouponPolicyResponseDTO> results = couponPolicyServiceImpl.searchCouponPoliciesByName("Welcome");

        // 결과 검증
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("Welcome Policy", results.get(0).getName());
    }








    @Test
    void testAssignMonthlyBirthdayCoupons_Success() {

        MemberDto member1 = MemberDto.builder().id(1L).birth(new Date()).build();
        MemberDto member2 = MemberDto.builder().id(2L).birth(new Date()).build();
        Page<MemberDto> memberPage = new PageImpl<>(List.of(member1, member2));

        CouponPolicy policy = CouponPolicy.builder().id(1L).name("생일 축하 쿠폰 - 1월").couponValidTime(30).build();
        Coupon createdCoupon = Coupon.builder()
                .id(1L)
                .name("생일 축하 쿠폰")
                .couponPolicy(policy)
                .build();

        // Mock 설정
        when(memberFeignClient.getMembers(any(), any(), anyInt(), anyInt(), any(), anyString()))
                .thenReturn(memberPage);
        when(couponPolicyRepository.findByNameContaining("생일 축하 쿠폰 - 1월"))
                .thenReturn(List.of(policy));
        when(couponPolicyRepository.findById(anyLong()))
                .thenReturn(Optional.of(policy));
        when(couponRepository.findById(1L))
                .thenReturn(Optional.of(createdCoupon));

        doAnswer(invocation -> {
            Coupon coupon = invocation.getArgument(0);
            return Coupon.builder()
                    .id(1L)
                    .name(coupon.getName())
                    .couponPolicy(policy)
                    .build();
        }).when(couponRepository).save(any(Coupon.class));

        // Act: 테스트 메서드 호출
        BulkAssignResponseDTO response = couponService.assignMonthlyBirthdayCoupons();

        // Assert: 결과 검증
        assertTrue(response.isSuccess());
    }

    @Test
    void testAssignMonthlyBirthdayCoupons_FebruaryLeapYear() {
        // Arrange: 2024년 윤년 2월로 설정
        LocalDate leapYearDate = LocalDate.of(2024, 2, 15);
        Date birthDate = Date.from(leapYearDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        MemberDto member1 = MemberDto.builder().id(1L).birth(birthDate).build();
        MemberDto member2 = MemberDto.builder().id(2L).birth(birthDate).build();
        Page<MemberDto> memberPage = new PageImpl<>(List.of(member1, member2));

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("생일 축하 쿠폰 - 2월 (윤년)")
                .couponValidTime(30)
                .build();

        Coupon coupon1 = Coupon.builder().id(1L).couponPolicy(policy).memberId(1L).build();
        Coupon coupon2 = Coupon.builder().id(2L).couponPolicy(policy).memberId(2L).build();

        // Mock 설정
        when(memberFeignClient.getMembers(any(), any(), anyInt(), anyInt(), any(), anyString()))
                .thenReturn(memberPage);
        when(couponPolicyRepository.findByNameContaining("생일 축하 쿠폰 - 2월 (윤년)"))
                .thenReturn(List.of(policy));
        when(couponPolicyRepository.findById(anyLong()))
                .thenReturn(Optional.of(policy));
        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(new Answer<Coupon>() {
                    private int count = 0; // 호출 횟수를 추적하기 위한 변수
                    @Override
                    public Coupon answer(InvocationOnMock invocation) throws Throwable {
                        Coupon coupon = invocation.getArgument(0); // 전달된 Coupon 객체 가져오기
                        count++;
                        return count == 1 ? coupon1 : coupon2; // 첫 번째 호출은 coupon1, 두 번째 호출은 coupon2
                    }
                });

        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(coupon1));
        when(couponRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 전달된 리스트를 그대로 반환

        // Spy 객체 생성 및 날짜 Mock
        CouponServiceImpl couponService = spy(new CouponServiceImpl(
                couponRepository,
                couponPolicyRepository,
                bookRepository,
                categoryRepository,
                bookCouponRepository,
                bookCategoryRepository,
                categoryCouponRepository,
                rabbitTemplate,
                memberFeignClient,
                couponPolicyService
        ));
        doReturn(LocalDate.of(2024, 2, 1)) // 테스트용 고정된 날짜
                .when(couponService).getCurrentDate();

        // Act: 테스트 실행
        BulkAssignResponseDTO response = couponService.assignMonthlyBirthdayCoupons();

        // Assert: 결과 검증
        assertTrue(response.isSuccess());
        assertEquals(2, response.getIssuedCount());
    }



    @Test
    void testAssignMonthlyBirthdayCoupons_FebruaryCommonYear() {
        // Arrange: 2023년 2월로 설정 (윤년 아님)
        LocalDate commonYearDate = LocalDate.of(2023, 2, 15);
        Date birthDate = Date.from(commonYearDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        MemberDto member1 = MemberDto.builder().id(1L).birth(birthDate).build();
        MemberDto member2 = MemberDto.builder().id(2L).birth(birthDate).build();
        Page<MemberDto> memberPage = new PageImpl<>(List.of(member1, member2));

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("생일 축하 쿠폰 - 2월 (평년)")
                .couponValidTime(30)
                .build();

        Coupon coupon1 = Coupon.builder().id(1L).couponPolicy(policy).memberId(1L).build();
        Coupon coupon2 = Coupon.builder().id(2L).couponPolicy(policy).memberId(2L).build();

        // Mock 설정
        when(memberFeignClient.getMembers(any(), any(), anyInt(), anyInt(), any(), anyString()))
                .thenReturn(memberPage);
        when(couponPolicyRepository.findByNameContaining("생일 축하 쿠폰 - 2월 (평년)"))
                .thenReturn(List.of(policy));
        when(couponPolicyRepository.findById(anyLong()))
                .thenReturn(Optional.of(policy));
        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(new Answer<Coupon>() {
                    private int count = 0; // 호출 횟수를 추적하기 위한 변수
                    @Override
                    public Coupon answer(InvocationOnMock invocation) throws Throwable {
                        Coupon coupon = invocation.getArgument(0); // 전달된 Coupon 객체 가져오기
                        count++;
                        return count == 1 ? coupon1 : coupon2; // 첫 번째 호출은 coupon1, 두 번째 호출은 coupon2
                    }
                });

        when(couponRepository.findById(anyLong())).thenReturn(Optional.of(coupon1));
        when(couponRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 전달된 리스트를 그대로 반환

        // Spy 객체 생성 및 날짜 Mock
        CouponServiceImpl couponService = spy(new CouponServiceImpl(
                couponRepository,
                couponPolicyRepository,
                bookRepository,
                categoryRepository,
                bookCouponRepository,
                bookCategoryRepository,
                categoryCouponRepository,
                rabbitTemplate,
                memberFeignClient,
                couponPolicyService
        ));
        doReturn(LocalDate.of(2023, 2, 1)) // 테스트용 고정된 날짜
                .when(couponService).getCurrentDate();

        // Act: 테스트 실행
        BulkAssignResponseDTO response = couponService.assignMonthlyBirthdayCoupons();

        // Assert: 결과 검증
        assertTrue(response.isSuccess());
        assertEquals(2, response.getIssuedCount());
    }

    @Test
    void testAssignMonthlyBirthdayCoupons_NoBirthdays() {
        when(memberFeignClient.getMembers(any(), any(), anyInt(), anyInt(), any(), anyString()))
                .thenReturn(new PageImpl<>(List.of()));

        couponService.assignMonthlyBirthdayCoupons();

        verify(couponPolicyRepository, never()).findByNameContaining(anyString());
    }

    @Test
    void testAssignMonthlyBirthdayCoupons_NoPolicies() {
        MemberDto member = MemberDto.builder().id(1L).birth(new Date()).build();

        when(memberFeignClient.getMembers(any(), any(), anyInt(), anyInt(), any(), anyString()))
                .thenReturn(new PageImpl<>(List.of(member)));
        when(couponPolicyRepository.findByNameContaining(anyString())).thenReturn(List.of());

        couponService.assignMonthlyBirthdayCoupons();

        verify(couponPolicyRepository, times(1)).findByNameContaining(anyString());
        verify(couponRepository, never()).save(any(Coupon.class));
    }


    @Test
    void testAssignBirthdayCoupons_Failure_CouponSaveError() {

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("생일 축하 쿠폰 정책")
                .couponValidTime(30)
                .build();

        List<CouponAssignRequestDTO> requests = List.of(
                new CouponAssignRequestDTO(1L, 1L)
        );

        when(couponPolicyRepository.findById(anyLong()))
                .thenReturn(Optional.of(policy));

        doThrow(new RuntimeException("Coupon creation failed"))
                .when(couponRepository).saveAll(anyList());


        int issuedCount = couponService.assignBirthdayCoupons(requests, policy);

        assertEquals(0, issuedCount, "쿠폰 생성 실패 시 발급된 쿠폰은 0이어야 합니다.");
    }




    @Test
    void testGetAvailableCoupons() {
        Long userId = 1L;
        List<Long> bookIds = List.of(101L, 102L);
        Long paymentAmount = 10000L;

        // Mock 데이터 준비
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("10% 할인")
                .couponDiscountRate(new BigDecimal("0.1"))
                .couponDiscountAmount(null)
                .couponMaxAmount(2000L)
                .build();

        Coupon coupon = Coupon.builder()
                .id(1L)
                .name("회원 특별 쿠폰")
                .couponPolicy(policy)
                .couponExpiryDate(LocalDate.now().plusDays(5))
                .build();

        // Mock 설정
        when(couponRepository.findAvailableCoupons(userId, paymentAmount, bookIds))
                .thenReturn(List.of(coupon));


        // 메서드 호출
        List<AvailableCouponResponseDTO> result = couponService.getAvailableCoupons(userId, bookIds, paymentAmount);

        // 결과 검증
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("회원 특별 쿠폰", result.get(0).getCouponName());
        assertEquals(policy.getCouponDiscountRate(), result.get(0).getDiscountRate());
        assertEquals(policy.getCouponMaxAmount(), result.get(0).getMaxDiscountAmount());
    }


    @Test
    void testApplyCoupon_FixedDiscountAmount() {
        Long couponId = 1L;
        Long paymentAmount = 10000L;

        // Mock 데이터 준비 (고정 할인 금액)
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("고정 할인")
                .couponDiscountAmount(3000L)
                .couponMaxAmount(2000L)
                .build();

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .name("고정 할인 쿠폰")
                .couponPolicy(policy)
                .build();


        when(couponRepository.findById(couponId))
                .thenReturn(Optional.of(coupon));


        Long discountAmount = couponService.applyCoupon(couponId, paymentAmount);


        assertNotNull(discountAmount);
        assertEquals(2000L, discountAmount);
    }

    @Test
    void testApplyCoupon_DiscountRate() {
        Long couponId = 1L;
        Long paymentAmount = 10000L;

        CouponPolicy policy = CouponPolicy.builder()
                .id(2L)
                .name("10% 할인")
                .couponDiscountRate(new BigDecimal("0.1"))
                .couponMaxAmount(800L)
                .build();

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .name("할인율 쿠폰")
                .couponPolicy(policy)
                .build();


        when(couponRepository.findById(couponId))
                .thenReturn(Optional.of(coupon));


        Long discountAmount = couponService.applyCoupon(couponId, paymentAmount);


        assertNotNull(discountAmount);
        assertEquals(800L, discountAmount);
    }

    @Test
    void testApplyCoupon_InvalidPolicy() {
        Long couponId = 1L;
        Long paymentAmount = 10000L;

        CouponPolicy policy = CouponPolicy.builder()
                .id(3L)
                .name("잘못된 정책")
                .couponDiscountRate(null)
                .couponDiscountAmount(null)
                .build();

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .name("잘못된 정책 쿠폰")
                .couponPolicy(policy)
                .build();

        when(couponRepository.findById(couponId))
                .thenReturn(Optional.of(coupon));

        assertThrows(IllegalArgumentException.class, () -> couponService.applyCoupon(couponId, paymentAmount));
    }










    // 미사용, 사용 가능성 있는 기능 테스트
    @Test
    void testDeleteCoupon_Success() {
        // Given
        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").build();

        Coupon coupon = Coupon.builder().id(1L).name("Test Coupon").couponPolicy(policy).build();

        when(couponRepository.existsById(couponId)).thenReturn(true);
        when(couponRepository.getReferenceById(couponId)).thenReturn(coupon);

        // When
        couponService.deleteCoupon(couponId);

        // Then
        verify(couponRepository, times(1)).existsById(couponId);
        verify(couponRepository, times(1)).getReferenceById(couponId);
        verify(couponRepository, times(1)).deleteById(couponId);
    }

    @Test
    void testDeleteCoupon_AlreadyAssigned() {
        // Given
        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").build();

        Coupon coupon = Coupon.builder().id(1L).name("Test Coupon").memberId(123L).couponPolicy(policy).build();

        when(couponRepository.existsById(couponId)).thenReturn(true);
        when(couponRepository.getReferenceById(couponId)).thenReturn(coupon);

        // When & Then
        assertThrows(CouponAlreadyAssignedException.class, () -> couponService.deleteCoupon(couponId));
    }


    @Test
    void testDeleteCoupon_Failure_NotFound() {
        // Given
        long couponId = 1L;
        when(couponRepository.existsById(couponId)).thenReturn(false);

        // When & Then
        assertThrows(CouponNotFoundException.class, () -> couponService.deleteCoupon(couponId));
    }

    @Test
    void testAssignCoupon_Success() {
        // Given
        long couponId = 1L;
        long memberId = 123L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Test Coupon").couponPolicy(policy).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // When
        CouponAssignRequestDTO requestDTO = new CouponAssignRequestDTO(couponId, memberId);
        CouponAssignResponseDTO response = couponService.assignCoupon(requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(couponId, response.getCouponId());
    }






    @Test
    void testAssignCoupon_Failure_CouponNotFound() {
        // Given
        long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // When & Then
        CouponAssignRequestDTO requestDTO = new CouponAssignRequestDTO(couponId, 123L);
        assertThrows(CouponNotFoundException.class, () -> couponService.assignCoupon(requestDTO));
        verify(couponRepository, times(1)).findById(couponId);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testAssignCoupon_Failure_AlreadyAssigned() {
        // Given
        long couponId = 1L;
        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").couponValidTime(30).build();

        Coupon coupon = Coupon.builder().id(couponId).name("Test Coupon").memberId(1L).couponPolicy(policy).build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When & Then
        CouponAssignRequestDTO requestDTO = new CouponAssignRequestDTO(couponId, 456L);
        assertThrows(CouponAlreadyAssignedException.class, () -> couponService.assignCoupon(requestDTO));
    }


}
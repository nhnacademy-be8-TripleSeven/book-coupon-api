
package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.client.MemberFeignClient;
import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.dto.member.MemberDto;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CouponServiceImplTest {

    @InjectMocks
    private CouponServiceImpl couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCouponRepository bookCouponRepository;

    @Mock
    private CategoryCouponRepository categoryCouponRepository;

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

        Coupon coupon = Coupon.builder().id(couponId).name("Base Coupon").couponPolicy(policy).build();
        coupon.setCouponAssignData(userId, LocalDate.now(), LocalDate.now().plusDays(30), CouponStatus.NOTUSED);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // When
        CouponUseResponseDTO response = couponService.useCoupon(userId, couponId, 1L);

        // Then
        assertNotNull(response);
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
        assertEquals("Base Coupon", response.getName());
    }


//    @Test
//    void testCreateCouponsInBulk_Success() {
//        // Given
//        CouponPolicy policy = CouponPolicy.builder().id(1L).name("Test Policy").couponValidTime(30).build();
//
//        Category category = Category.builder().id(10L).name("Test Category").level(0).build();
//
//        Book book = Book.builder().id(1L).title("Test Book").build();
//
//        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
//        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
//
//        // When
//        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
//                "Bulk Coupon",
//                1L,
//                10L,
//                null,
//                10L
//        );
//
//        couponService.createCouponsInBulk(request);
//
//        // Then
//        verify(couponPolicyRepository, times(1)).findById(1L);
//        verify(categoryRepository, times(1)).findById(1L);
//        verify(couponRepository, times(10)).save(any(Coupon.class));
//        verify(categoryCouponRepository, times(10)).save(any(CategoryCoupon.class));
//    }


//
//    @Test
//    void testCreateCouponsInBulk_Failure_PolicyNotFound() {
//        // Given
//        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());
//
//        CouponBulkCreationRequestDTO request = new CouponBulkCreationRequestDTO(
//                "Bulk Coupon",
//                1L,
//                10L,
//                null, // bookId
//                1L   // categoryId
//        );
//
//        // When & Then
//        assertThrows(CouponPolicyNotFoundException.class, () -> couponService.createCouponsInBulk(request));
//        verify(couponPolicyRepository, times(1)).findById(1L);
//        verifyNoMoreInteractions(couponRepository);
//    }






    @Test
    void testUseCoupon_Failure_AlreadyUsed() {
        // Given
        long userId = 123L;
        long couponId = 1L;

        CouponPolicy policy = CouponPolicy.builder().id(couponId).name("Test Policy").build();
        Coupon coupon = Coupon.builder().id(couponId).build();

        coupon.setCouponAssignData(userId, LocalDate.now(), LocalDate.now().plusDays(30), CouponStatus.USED);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When & Then
        assertThrows(CouponAlreadyUsedException.class, () -> couponService.useCoupon(userId, couponId, null));
        verify(couponRepository, times(1)).findById(couponId);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testUseCoupon_Failure_Expired() {
        // Given
        long userId = 123L;
        long couponId = 1L;

        CouponPolicy policy = new CouponPolicy("Policy Name", 1000L, 5000L, BigDecimal.TEN, 100L, 30);
        Coupon coupon = new Coupon("Coupon Name", policy);
        coupon.setTestId(couponId);
        coupon.setCouponAssignData(userId, LocalDate.now().minusDays(60), LocalDate.now().minusDays(30), CouponStatus.EXPIRED);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When & Then
        assertThrows(CouponExpiredException.class, () -> couponService.useCoupon(userId, couponId, null));
        verify(couponRepository, times(1)).findById(couponId);
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void testUseCoupon_Failure_NotAssignedToUser() {
        // Given
        long userId = 123L;
        long couponId = 1L;

        CouponPolicy policy = new CouponPolicy("Policy Name", 1000L, 5000L, BigDecimal.TEN, 100L, 30);
        Coupon coupon = new Coupon("Coupon Name", policy);
        coupon.setTestId(couponId);
        coupon.setCouponAssignData(999L, LocalDate.now(), LocalDate.now().plusDays(30), CouponStatus.NOTUSED); // 다른 유저에게 할당됨

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When & Then
        assertThrows(CouponNotAssignedException.class, () -> couponService.useCoupon(userId, couponId, null));
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
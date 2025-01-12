
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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
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
    private MemberFeignClient memberFeignClient;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCoupon_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        policy.setTestId(1L);

        Coupon coupon = new Coupon("Test Coupon", policy);
        coupon.setTestId(1L);

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
    void testCreateCoupon_PolicyNotFound() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());
        // Then
        assertThrows(CouponPolicyNotFoundException.class, () -> couponService.createCoupon(new CouponCreationRequestDTO("Test Coupon", 1L)));
    }

    @Test
    void testCreateBookCoupon_Success() {
        // Given
        Publisher publisher = new Publisher("Test Publisher");

        Book book = new Book(
                "Test Book",
                "Initial Description",
                LocalDate.of(2020, 1, 1),
                20000,
                15000,
                "9781234567897",
                100,
                300,
                publisher
        );
        ReflectionTestUtils.setField(book, "id", 1L);

        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        policy.setTestId(1l);

        Coupon coupon = new Coupon("Book Coupon", policy);
        coupon.setTestId(1L);

        BookCoupon bookCoupon = new BookCoupon(book, coupon);
        bookCoupon.setTestId(1L);

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
        Category category = new Category("Test Category", 0);
        category.setTestId(1L);

        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        policy.setTestId(1l);

        Coupon coupon = new Coupon("Category Coupon", policy);
        coupon.setTestId(1L);

        CategoryCoupon categoryCoupon = new CategoryCoupon(category, coupon);
        categoryCoupon.setTestId(1L);

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

//
//    @Test
//    void testAssignCoupon_Success() {
//        // Given
//        CouponPolicy couponPolicy = new CouponPolicy(1L, "Test Policy", 1000L,
//                10000L, BigDecimal.ZERO, 500L, 30);
//
//
//        Coupon coupon = new Coupon();
//        coupon.setTestId(1L);
//        coupon.setCouponStatus(CouponStatus.NOTUSED);
//        coupon.setCouponPolicy(couponPolicy);
//
//        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
//
//        // When
//        CouponAssignResponseDTO response = couponService.assignCoupon(new CouponAssignRequestDTO(1L, 123L));
//
//        // Then
//        assertEquals(1L, response.getCouponId());
//        assertEquals(123L, response.getMemberId());
//        assertEquals(CouponStatus.NOTUSED, coupon.getCouponStatus());
//        verify(couponRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void testAssignCoupon_CouponNotFound() {
//        // Given
//        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
//
//        CouponAssignRequestDTO request = new CouponAssignRequestDTO(1L, 100L);
//
//        // Then
//        assertThrows(CouponNotFoundException.class, () -> couponService.assignCoupon(request));
//        verify(couponRepository, times(1)).findById(1L);
//        verify(couponRepository, never()).save(any(Coupon.class));
//    }
//
//    @Test
//    void testAssignCoupon_AlreadyAssigned() {
//        // Given
//        Coupon coupon = new Coupon();
//        coupon.setTestId(1L);
//        coupon.setMemberId(123L);
//
//        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
//
//        // When & Then
//        CouponAssignRequestDTO request = new CouponAssignRequestDTO(1L, 456L);
//        assertThrows(CouponAlreadyAssignedException.class, () -> couponService.assignCoupon(request));
//        verify(couponRepository, times(1)).findById(1L);
//        verifyNoMoreInteractions(couponRepository);
//    }


//
//    @Test
//    void testUseCoupon_Success() {
//        // Given
//        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
//                10000L, BigDecimal.ZERO, 500L, 30);
//        policy.setTestId(1l);
//
//
//        Coupon coupon = new Coupon("Test Coupon", policy);
//        coupon.setTestId(1L);
//        coupon.setCouponAssignData(123L, LocalDate.now(),
//                LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), CouponStatus.NOTUSED);
//
//        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
//
//        // When
//        couponService.useCoupon(123L,1L);
//
//        // Then
//        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
//    }
//
//    @Test
//    void testUseCoupon_AlreadyUsed() {
//        // Given
//        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
//                10000L, BigDecimal.ZERO, 500L, 30);
//        policy.setTestId(1l);
//
//        Coupon coupon = new Coupon("Test Coupon", policy);
//        coupon.setTestId(1L);
//        coupon.setCouponAssignData(123L, LocalDate.now(),
//                LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), CouponStatus.USED);
//
//
//        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
//
//        // When & Then
//        assertThrows(CouponAlreadyUsedExceeption.class, () -> couponService.useCoupon(1L,1L));
//    }
//
//    @Test
//    void testUseCoupon_CouponNotFound() {
//        // Given
//        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Then
//        assertThrows(CouponNotFoundException.class, () -> couponService.useCoupon(1L,1L));
//    }
//
//    @Test
//    void testUseCoupon_NotAssigned() {
//        // Given
//        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
//                10000L, BigDecimal.ZERO, 500L, 30);
//        policy.setTestId(1l);
//
//        Coupon coupon = new Coupon("Test Coupon", policy);
//        coupon.setTestId(1L);
//
//        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
//
//        // When & Then
//        assertThrows(CouponNotAssignedException.class, () -> couponService.useCoupon(1L,1L));
//    }


    @Test
    void testExpireCoupons_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        policy.setTestId(1l);

        Coupon coupon1 = new Coupon("Test Coupon", policy);
        coupon1.setTestId(1L);
        coupon1.setCouponAssignData(123L, LocalDate.now(),
                LocalDate.now().minusDays(1), CouponStatus.NOTUSED);

        Coupon coupon2 = new Coupon("Test Coupon2", policy);
        coupon2.setTestId(2L);
        coupon2.setCouponAssignData(123L, LocalDate.now(),
                LocalDate.now().minusDays(2), CouponStatus.NOTUSED);

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
    void testDeleteCoupon_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);

        long couponId = 1L;
        Coupon coupon = new Coupon("Test Coupon", policy);
        coupon.setTestId(couponId);

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

        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        policy.setTestId(1l);

        Coupon coupon = new Coupon("Test Coupon", policy);
        coupon.setTestId(couponId);
        coupon.setCouponAssignData(123L, LocalDate.now(),
                LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), CouponStatus.USED);

        when(couponRepository.existsById(couponId)).thenReturn(true);
        when(couponRepository.getReferenceById(couponId)).thenReturn(coupon);

        // When & Then
        assertThrows(CouponAlreadyAssignedException.class, () -> couponService.deleteCoupon(couponId));
    }

    @Test
    void testDeleteCoupon_NotFound() {
        // Given
        when(couponRepository.existsById(1L)).thenReturn(false);

        // Then
        assertThrows(CouponNotFoundException.class, () -> couponService.deleteCoupon(1L));
    }


    @Test
    void testGetAllCouponsByMemberId() {
        // Given
        CouponPolicy policy = new CouponPolicy("Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        policy.setTestId(1L);

        Coupon coupon1 = new Coupon("Coupon 1", policy);
        coupon1.setTestId(1L);
        coupon1.setCouponAssignData(100L, LocalDate.now(), LocalDate.now().plusDays(30), CouponStatus.NOTUSED);

        Coupon coupon2 = new Coupon("Coupon 2", policy);
        coupon2.setTestId(2L);
        coupon2.setCouponAssignData(100L, LocalDate.now(), LocalDate.now().plusDays(30), CouponStatus.USED);

        when(couponRepository.findByMemberId(100L)).thenReturn(Arrays.asList(coupon1, coupon2));

        // When
        List<CouponDetailsDTO> coupons = couponService.getAllCouponsByMemberId(100L);

        // Then
        assertNotNull(coupons);
        assertEquals(2, coupons.size());

        assertEquals("Coupon 1", coupons.get(0).getName());
        assertEquals(500L , coupons.get(0).getDiscountAmount());

        assertEquals("Coupon 2", coupons.get(1).getName());
        assertEquals(BigDecimal.ZERO, coupons.get(1).getDiscountRate());
    }

    @Test
    void testGetAllCouponsByMemberId_NotFound() {
        when(couponRepository.findByMemberId(100L)).thenReturn(Collections.emptyList());
        assertThrows(CouponsNotFoundException.class, () -> couponService.getAllCouponsByMemberId(100L));
    }

    @Test
    void testGetUnusedCouponsByMemberId() {
        // Given
        CouponPolicy couponPolicy = new CouponPolicy("Policy 1", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        couponPolicy.setTestId(1L);

        Coupon coupon1 = new Coupon("Unused Coupon", couponPolicy);
        coupon1.setTestId(1L);
        coupon1.setCouponAssignData(100L, LocalDate.now(), LocalDate.now().plusDays(30), CouponStatus.NOTUSED);

        when(couponRepository.findByMemberIdAndCouponStatus(100L, CouponStatus.NOTUSED))
                .thenReturn(List.of(coupon1));

        // When
        List<CouponDetailsDTO> coupons = couponService.getUnusedCouponsByMemberId(100L);

        // Then
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        assertEquals("Unused Coupon", coupons.get(0).getName());
        assertEquals(500L, coupons.get(0).getDiscountAmount());
    }

    @Test
    void testGetUnusedCouponsByMemberId_NotFound() {
        when(couponRepository.findByMemberIdAndCouponStatus(100L, CouponStatus.NOTUSED))
                .thenReturn(Collections.emptyList());
        assertThrows(CouponsNotFoundException.class, () -> couponService.getUnusedCouponsByMemberId(100L));
    }

    @Test
    void testGetUsedCouponsByMemberId() {
        // Given
        CouponPolicy couponPolicy = new CouponPolicy("Policy 1", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);
        couponPolicy.setTestId(1L);

        Coupon coupon1 = new Coupon("Used Coupon", couponPolicy);
        coupon1.setTestId(1L);
        coupon1.setCouponAssignData(100L, LocalDate.now(), LocalDate.now().plusDays(30), CouponStatus.USED);

        when(couponRepository.findByMemberIdAndCouponStatus(100L, CouponStatus.USED))
                .thenReturn(List.of(coupon1));

        // When
        List<CouponDetailsDTO> coupons = couponService.getUsedCouponsByMemberId(100L);

        // Then
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        assertEquals("Used Coupon", coupons.get(0).getName());
        assertEquals(BigDecimal.ZERO, coupons.get(0).getDiscountRate());
    }

    @Test
    void testGetUsedCouponsByMemberId_NotFound() {
        when(couponRepository.findByMemberId(100L)).thenReturn(Collections.emptyList());
        assertThrows(CouponsNotFoundException.class, () -> couponService.getUsedCouponsByMemberId(100L));
    }

//    @Test
//    void testGetCouponsByPolicyId() {
//        // Given
//        CouponPolicy couponPolicy = new CouponPolicy(1L, "Test Policy", 1000L,
//                10000L, BigDecimal.ZERO, 500L, 30);
//
//        Coupon coupon1 = new Coupon();
//        coupon1.setTestId(1L);
//        coupon1.setName("Policy Coupon 1");
//        coupon1.setCouponPolicy(couponPolicy);
//        coupon1.setCouponStatus(CouponStatus.NOTUSED);
//
//        Coupon coupon2 = new Coupon();
//        coupon2.setTestId(2L);
//        coupon2.setName("Policy Coupon 2");
//        coupon2.setCouponPolicy(couponPolicy);
//        coupon2.setCouponStatus(CouponStatus.USED);
//
//        when(couponRepository.findByCouponPolicyId(1L)).thenReturn(List.of(coupon1, coupon2));
//
//        // When
//        List<CouponDetailsDTO> coupons = couponService.getCouponsByPolicyId(1L);
//
//        // Then
//        assertNotNull(coupons);
//        assertEquals(2, coupons.size());
//
//        // Verify first coupon
//        CouponDetailsDTO firstCoupon = coupons.getFirst();
//        assertEquals(1L, firstCoupon.getId());
//        assertEquals("Policy Coupon 1", firstCoupon.getName());
//        assertEquals("Test Policy", firstCoupon.getPolicyName());
//
//        // Verify second coupon
//        CouponDetailsDTO secondCoupon = coupons.get(1);
//        assertEquals(2L, secondCoupon.getId());
//        assertEquals("Policy Coupon 2", secondCoupon.getName());
//        assertEquals("Test Policy", secondCoupon.getPolicyName());
//    }


//
//    @Test
//    void assignMonthlyBirthdayCoupons() {
//        // Given
//        LocalDate fixedDate = LocalDate.of(2025, 1, 1); // 1월 고정
//        MemberDto member1 = new MemberDto(1L, "user1@example.com", "010-1234-5678", "User1",
//                Date.from(fixedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), "M", "VIP");
//        MemberDto member2 = new MemberDto(2L, "user2@example.com", "010-9876-5432", "User2",
//                Date.from(fixedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), "F", "REGULAR");
//
//        CouponPolicy couponPolicy = new CouponPolicy(
//                "January Birthday Coupon",
//                1000L,
//                5000L,
//                BigDecimal.valueOf(0.1),
//                500L,
//                30
//        );
//        couponPolicy.setTestId(101L);
//
//        when(memberFeignClient.getMembers(isNull(), isNull(), anyInt(), anyInt(), any(), any()))
//                .thenReturn(new PageImpl<>(List.of(member1, member2)));
//
//        when(couponPolicyRepository.findById(101L)).thenReturn(Optional.of(couponPolicy));
//        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // When
//        couponService.assignMonthlyBirthdayCoupons();
//
//        // Then
//        verify(memberFeignClient, times(1)).getMembers(isNull(), isNull(), anyInt(), anyInt(), any(), any());
//        verify(couponPolicyRepository, times(2)).findById(101L); // Adjusted expectation
//        verify(rabbitTemplate, times(2))
//                .convertAndSend(
//                        eq(RabbitConfig.EXCHANGE_NAME),
//                        eq(RabbitConfig.ROUTING_KEY),
//                        any(CouponAssignRequestDTO.class),
//                        any(MessagePostProcessor.class)
//                );
//    }



}
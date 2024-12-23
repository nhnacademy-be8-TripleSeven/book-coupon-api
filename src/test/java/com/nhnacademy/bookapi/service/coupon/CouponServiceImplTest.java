package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCoupon_Success() {
        // Given
        CouponPolicy policy = new CouponPolicy();
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setName("Test Coupon");
        coupon.setCouponPolicy(policy);

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
        Book book = new Book();
        book.setTestId(1L);
        book.setTitle("Test Book");

        CouponPolicy policy = new CouponPolicy();
        policy.setTestId(1L);

        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setName("Book Coupon");
        coupon.setCouponPolicy(policy);

        BookCoupon bookCoupon = new BookCoupon();
        bookCoupon.setTestId(1L);
        bookCoupon.setBook(book);
        bookCoupon.setCoupon(coupon);

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
        Category category = new Category();
        category.setTestId(1L);
        category.setName("Test Category");

        CouponPolicy policy = new CouponPolicy();
        policy.setTestId(1L);

        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setName("Category Coupon");
        coupon.setCouponPolicy(policy);

        CategoryCoupon categoryCoupon = new CategoryCoupon();
        categoryCoupon.setTestId(1L);
        categoryCoupon.setCategory(category);
        categoryCoupon.setCoupon(coupon);

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

    @Test
    void testUseCoupon_Success() {
        // Given
        CouponPolicy couponPolicy = new CouponPolicy(1L, "Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);


        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setMemberId(123L);
        coupon.setCouponStatus(CouponStatus.NOTUSED);
        coupon.setCouponPolicy(couponPolicy);
        coupon.setMemberId(1L);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // When
        couponService.useCoupon(1L,1L);

        // Then
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
    }

    @Test
    void testUseCoupon_AlreadyUsed() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setMemberId(123L);
        coupon.setCouponStatus(CouponStatus.USED);
        coupon.setMemberId(1L);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // When & Then
        assertThrows(CouponAlreadyUsedExceeption.class, () -> couponService.useCoupon(1L,1L));
    }

    @Test
    void testUseCoupon_CouponNotFound() {
        // Given
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        // Then
        assertThrows(CouponNotFoundException.class, () -> couponService.useCoupon(1L,1L));
    }

    @Test
    void testUseCoupon_NotAssigned() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setMemberId(null);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // When & Then
        assertThrows(CouponNotAssignedException.class, () -> couponService.useCoupon(1L,1L));
    }

    @Test
    void testUseBookCoupon_Success() {
        // Given
        Book book = new Book();
        book.setTestId(1L);
        book.setTitle("Test Book");

        CouponPolicy policy = new CouponPolicy(1L, "Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);

        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setMemberId(123L);
        coupon.setCouponStatus(CouponStatus.NOTUSED);
        coupon.setCouponPolicy(policy);
        coupon.setMemberId(1L);

        BookCoupon bookCoupon = new BookCoupon();
        bookCoupon.setTestId(1L);
        bookCoupon.setCoupon(coupon);
        bookCoupon.setBook(book);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(bookCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(bookCoupon));

        // When
        couponService.useBookCoupon(1L, 1L, 1L);

        // Then
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
    }

    @Test
    void testUseBookCoupon_InvalidBook() {
        // Given
        Book book = new Book();
        book.setTestId(1L);

        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setMemberId(1L);

        BookCoupon bookCoupon = new BookCoupon();
        bookCoupon.setCoupon(coupon);
        bookCoupon.setBook(book);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(bookCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(bookCoupon));

        // When & Then
        assertThrows(InvalidCouponUsageException.class, () -> couponService.useBookCoupon(1L,1L, 2L));
    }

    @Test
    void testUseCategoryCoupon_Success() {
        // Given
        Category category = new Category();
        category.setTestId(1L);
        category.setName("Test Category");

        CouponPolicy policy = new CouponPolicy(1L, "Test Policy", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);

        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setMemberId(123L);
        coupon.setCouponStatus(CouponStatus.NOTUSED);
        coupon.setCouponPolicy(policy);
        coupon.setMemberId(1L);

        CategoryCoupon categoryCoupon = new CategoryCoupon();
        categoryCoupon.setTestId(1L);
        categoryCoupon.setCategory(category);
        categoryCoupon.setCoupon(coupon);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(categoryCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(categoryCoupon));
        when(categoryRepository.findSubcategories(1L)).thenReturn(List.of(1L, 2L, 3L));

        // When
        couponService.useCategoryCoupon(1L,1L, 1L);

        // Then
        assertEquals(CouponStatus.USED, coupon.getCouponStatus());
    }

    @Test
    void testUseCategoryCoupon_InvalidCategory() {
        // Given
        Category category = new Category();
        category.setTestId(1L);

        Coupon coupon = new Coupon();
        coupon.setTestId(1L);
        coupon.setMemberId(1L);

        CategoryCoupon categoryCoupon = new CategoryCoupon();
        categoryCoupon.setCategory(category);
        categoryCoupon.setCoupon(coupon);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(categoryCouponRepository.findByCoupon(coupon)).thenReturn(Optional.of(categoryCoupon));
        when(categoryRepository.findSubcategories(1L)).thenReturn(List.of(2L, 3L));

        // When & Then
        assertThrows(InvalidCouponUsageException.class, () -> couponService.useCategoryCoupon(1L,1L, 4L));
    }

    @Test
    void testExpireCoupons_Success() {
        // Given
        Coupon coupon1 = new Coupon();
        coupon1.setTestId(1L);
        coupon1.setCouponStatus(CouponStatus.NOTUSED);
        coupon1.setCouponExpiryDate(LocalDate.now().minusDays(1));

        Coupon coupon2 = new Coupon();
        coupon2.setTestId(2L);
        coupon2.setCouponStatus(CouponStatus.NOTUSED);
        coupon2.setCouponExpiryDate(LocalDate.now().minusDays(2));

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
        long couponId = 1L;

        Coupon coupon = new Coupon();
        coupon.setTestId(couponId);
        coupon.setMemberId(null);

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

        Coupon coupon = new Coupon();
        coupon.setTestId(couponId);
        coupon.setMemberId(123L);

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
        CouponPolicy couponPolicy = new CouponPolicy(1L, "Policy 1", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);

        Coupon coupon1 = new Coupon();
        coupon1.setTestId(1L);
        coupon1.setName("Coupon 1");
        coupon1.setMemberId(100L);
        coupon1.setCouponPolicy(couponPolicy);
        coupon1.setCouponStatus(CouponStatus.NOTUSED);

        Coupon coupon2 = new Coupon();
        coupon2.setTestId(2L);
        coupon2.setName("Coupon 2");
        coupon2.setMemberId(100L);
        coupon2.setCouponPolicy(couponPolicy);
        coupon2.setCouponStatus(CouponStatus.USED);

        when(couponRepository.findByMemberId(100L)).thenReturn(Arrays.asList(coupon1, coupon2));

        // When
        List<CouponDetailsDTO> coupons = couponService.getAllCouponsByMemberId(100L);

        // Then
        assertNotNull(coupons);
        assertEquals(2, coupons.size());

        assertEquals("Coupon 1", coupons.get(0).getName());
        assertEquals("Policy 1", coupons.get(0).getPolicyName());

        assertEquals("Coupon 2", coupons.get(1).getName());
        assertEquals("Policy 1", coupons.get(1).getPolicyName());
    }

    @Test
    void testGetAllCouponsByMemberId_NotFound() {
        when(couponRepository.findByMemberId(100L)).thenReturn(Collections.emptyList());
        assertThrows(CouponsNotFoundException.class, () -> couponService.getAllCouponsByMemberId(100L));
    }

    @Test
    void testGetUnusedCouponsByMemberId() {
        // Given
        CouponPolicy couponPolicy = new CouponPolicy(1L, "Policy 1", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);

        Coupon coupon1 = new Coupon();
        coupon1.setTestId(1L);
        coupon1.setName("Unused Coupon");
        coupon1.setMemberId(100L);
        coupon1.setCouponPolicy(couponPolicy);
        coupon1.setCouponStatus(CouponStatus.NOTUSED);

        when(couponRepository.findByMemberIdAndCouponStatus(100L, CouponStatus.NOTUSED))
                .thenReturn(List.of(coupon1));

        // When
        List<CouponDetailsDTO> coupons = couponService.getUnusedCouponsByMemberId(100L);

        // Then
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        assertEquals("Unused Coupon", coupons.getFirst().getName());
        assertEquals("Policy 1", coupons.getFirst().getPolicyName());
    }

    @Test
    void testGetUnusedCouponsByMemberId_NotFound() {
        when(couponRepository.findByMemberId(100L)).thenReturn(Collections.emptyList());
        assertThrows(CouponsNotFoundException.class, () -> couponService.getUnusedCouponsByMemberId(100L));
    }

    @Test
    void testGetUsedCouponsByMemberId() {
        // Given
        CouponPolicy couponPolicy = new CouponPolicy(1L, "Policy 1", 1000L,
                10000L, BigDecimal.ZERO, 500L, 30);

        Coupon coupon1 = new Coupon();
        coupon1.setTestId(1L);
        coupon1.setName("Used Coupon");
        coupon1.setMemberId(100L);
        coupon1.setCouponPolicy(couponPolicy);
        coupon1.setCouponStatus(CouponStatus.USED);

        when(couponRepository.findByMemberIdAndCouponStatus(100L, CouponStatus.USED))
                .thenReturn(List.of(coupon1));

        // When
        List<CouponDetailsDTO> coupons = couponService.getUsedCouponsByMemberId(100L);

        // Then
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        assertEquals("Used Coupon", coupons.getFirst().getName());
        assertEquals("Policy 1", coupons.getFirst().getPolicyName());
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


}


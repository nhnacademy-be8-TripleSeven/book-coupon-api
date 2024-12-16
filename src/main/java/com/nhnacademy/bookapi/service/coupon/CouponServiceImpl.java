package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;

    // 쿠폰 생성 (이름, 정책)
    @Override
    @Transactional
    public BaseCouponResponseDTO createCoupon(CouponCreationRequestDTO request) {
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("Coupon policy not found"));

        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setCouponPolicy(policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        return new BaseCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy());
    }

    // 도서 쿠폰 생성 (이름, 정책 아이디, 도서 아이디)
    @Override
    @Transactional
    public BookCouponResponseDTO createBookCoupon(BookCouponCreationRequestDTO request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("Coupon policy not found"));

        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setCouponPolicy(policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        BookCoupon bookCoupon = new BookCoupon();
        bookCoupon.setBook(book);
        bookCoupon.setCoupon(savedCoupon);

        bookCouponRepository.save(bookCoupon);

        return new BookCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy(), book.getTitle());
    }

    // 카테고리 쿠폰 생성 (이름, 정책 아이디, 카테고리 아이디)
    @Override
    @Transactional
    public CategoryCouponResponseDTO createCategoryCoupon(CategoryCouponCreationRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("Coupon policy not found"));

        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setCouponPolicy(policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        CategoryCoupon categoryCoupon = new CategoryCoupon();
        categoryCoupon.setCategory(category);
        categoryCoupon.setCoupon(savedCoupon);

        categoryCouponRepository.save(categoryCoupon);

        return new CategoryCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy(), category.getName());
    }

    // 쿠폰 발급 (쿠폰 아이디, 회원 아이디)
    @Override
    @Transactional
    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (coupon.getMemberId() != null) {
            throw new CouponAlreadyAssignedException("Coupon is already assigned");
        }

        Integer validTime = coupon.getCouponPolicy().getCouponValidTime();

        coupon.setMemberId(request.getMemberId());
        coupon.setCouponIssueDate(LocalDate.now());
        coupon.setCouponExpiryDate(LocalDate.now().plusDays(validTime));
        coupon.setCouponStatus(CouponStatus.NOTUSED);

        return new CouponAssignResponseDTO(coupon);
    }

    // 쿠폰 사용 (쿠폰 아이디)
    @Override
    @Transactional
    public CouponUseResponseDTO useCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (coupon.getMemberId() == null) {
            throw new CouponNotAssignedException("Member not found");
        }

        if (coupon.getCouponStatus() == CouponStatus.USED) {
            throw new CouponAlreadyUsedExceeption("Coupon is already used");
        }

        if (coupon.getCouponStatus() == CouponStatus.EXPIRED) {
            throw new CouponExpiredException("Coupon is expired");
        }

        coupon.setCouponStatus(CouponStatus.USED);
        coupon.setCouponUseAt(LocalDateTime.now());

        return new CouponUseResponseDTO(coupon);
    }

    // 쿠폰 사용 (책 쿠폰)
    @Override
    @Transactional
    public CouponUseResponseDTO useBookCoupon(Long couponId, Long bookId) {
        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        // 책 쿠폰 여부 확인
        BookCoupon bookCoupon = bookCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException("This coupon is not associated with a book"));

        // 유효한 책인지 확인
        if (!bookCoupon.getBook().getId().equals(bookId)) {
            throw new InvalidCouponUsageException("Coupon cannot be used for this book");
        }

        // 공통 사용 로직 호출
        return useCoupon(couponId);
    }

    // 쿠폰 사용 (카테고리 쿠폰)
    @Override
    @Transactional
    public CouponUseResponseDTO useCategoryCoupon(Long couponId, Long categoryId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        CategoryCoupon categoryCoupon = categoryCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException("This coupon is not associated with a category"));

        if (!isValidCategory(categoryCoupon.getCategory(), categoryId)) {
            throw new InvalidCouponUsageException("Coupon cannot be used in this category");
        }

        return useCoupon(couponId);
    }

    // 유효한 카테고리인지 확인
    private boolean isValidCategory(Category couponCategory, Long requestedCategoryId) {
        List<Long> validCategories = categoryRepository.findSubcategories(couponCategory.getId());

        // 쿠폰의 하위 카테고리에 요청된 카테고리가 포함되면 유효
        return validCategories.contains(requestedCategoryId);
    }


    // 쿠폰 삭제 (쿠폰 아이디)
    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException("Coupon not found");
        }
        if (couponRepository.getReferenceById(id).getMemberId() != null) {
            throw new CouponAlreadyAssignedException("Coupon is already assigned");
        }
        couponRepository.deleteById(id);
    }

    // 쿠폰 만료 (스케쥴러)
    @Override
    @Transactional
    public void expireCoupons() {
        List<Coupon> coupons = couponRepository.findByCouponStatusAndCouponExpiryDateBefore(CouponStatus.NOTUSED, LocalDate.now());

        for (Coupon coupon : coupons) {
            coupon.setCouponStatus(CouponStatus.EXPIRED);
        }
    }

    // 멤버 아이디 기반 쿠폰 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getAllCouponsByMemberId(Long memberId) {
        return couponRepository.findByMemberId(memberId).stream()
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }

    // 멤버 아이디 기반 미사용 쿠폰 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getUnusedCouponsByMemberId(Long memberId) {
        return couponRepository.findByMemberIdAndCouponStatus(memberId, CouponStatus.NOTUSED).stream()
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }

    // 쿠폰 정책 아이디 기반 쿠폰 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getCouponsByPolicyId(Long policyId) {
        return couponRepository.findByCouponPolicyId(policyId).stream()
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }

    // 쿠폰 상세 정보 DTO 변환
    private CouponDetailsDTO mapToCouponDetailsDTO(Coupon coupon) {
        String bookTitle = bookCouponRepository.findByCoupon(coupon)
                .map(BookCoupon::getBook)
                .map(Book::getTitle)
                .orElse(null);

        String categoryName = categoryCouponRepository.findByCoupon(coupon)
                .map(CategoryCoupon::getCategory)
                .map(Category::getName)
                .orElse(null);

        return new CouponDetailsDTO(
                coupon,
                bookTitle,
                categoryName
        );
    }
}
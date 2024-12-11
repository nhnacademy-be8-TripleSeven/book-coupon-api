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

    // 도서 쿠폰 생성 (이름, 정책, 도서 아이디)
    @Override
    @Transactional
    public BookCouponResponseDTO createBookCoupon(BookCouponCreationRequestDTO request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("Coupon policy not found"));

        // Create a new Coupon
        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setCouponPolicy(policy);

        // Save Coupon
        Coupon savedCoupon = couponRepository.save(coupon);

        // Create a new BookCoupon entry
        BookCoupon bookCoupon = new BookCoupon();
        bookCoupon.setBook(book);
        bookCoupon.setCoupon(savedCoupon);

        // Save BookCoupon
        bookCouponRepository.save(bookCoupon);

        return new BookCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy(), book.getTitle());
    }

    // 카테고리 쿠폰 생성 (이름, 정책, 카테고리 아이디)
    @Override
    @Transactional
    public CategoryCouponResponseDTO createCategoryCoupon(CategoryCouponCreationRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("Coupon policy not found"));

        // Create a new Coupon
        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setCouponPolicy(policy);

        // Save Coupon
        Coupon savedCoupon = couponRepository.save(coupon);

        // Create a new CategoryCoupon entry
        CategoryCoupon categoryCoupon = new CategoryCoupon();
        categoryCoupon.setCategory(category);
        categoryCoupon.setCoupon(savedCoupon);

        // Save CategoryCoupon
        categoryCouponRepository.save(categoryCoupon);

        return new CategoryCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy(), category.getName());
    }

    // 쿠폰 발급 (쿠폰 아이디, 회원 아이디)
    @Override
    @Transactional
    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        coupon.setMemberId(request.getMemberId());
        coupon.setCouponIssueDate(LocalDate.now());
        coupon.setCouponStatus(CouponStatus.NOTUSED);

        Coupon savedCoupon = couponRepository.save(coupon);

        return new CouponAssignResponseDTO(
                savedCoupon.getId(),
                savedCoupon.getName(),
                savedCoupon.getCouponPolicy(),
                savedCoupon.getMemberId(),
                savedCoupon.getCouponIssueDate(),
                savedCoupon.getCouponStatus().name()
        );
    }

    // 쿠폰 사용 (쿠폰 아이디)
    @Override
    @Transactional
    public CouponUseResponseDTO useCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        coupon.setCouponStatus(CouponStatus.USED);
        coupon.setCouponUseAt(LocalDateTime.now());

        Coupon savedCoupon = couponRepository.save(coupon);

        return new CouponUseResponseDTO(
                savedCoupon.getId(),
                savedCoupon.getName(),
                savedCoupon.getCouponPolicy(),
                savedCoupon.getMemberId(),
                savedCoupon.getCouponIssueDate(),
                savedCoupon.getCouponStatus().name(),
                savedCoupon.getCouponUseAt()
        );
    }

    // 쿠폰 삭제 (쿠폰 아이디)
    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException("Coupon not found");
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
            couponRepository.save(coupon);
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

    // 멤버 아이디 기반 미사용 쿠폰 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getUnusedCouponsByMemberId(Long memberId) {
        return couponRepository.findByMemberIdAndCouponStatus(memberId, CouponStatus.NOTUSED).stream()
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }

    // 쿠폰 정책 아이디 기반 쿠폰 조회
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
                coupon.getId(),
                coupon.getCouponPolicy().getName(),
                coupon.getName(),
                coupon.getMemberId(),
                coupon.getCouponIssueDate(),
                coupon.getCouponExpiryDate(),
                coupon.getCouponStatus().name(),
                coupon.getCouponUseAt(),
                bookTitle,
                categoryName
        );
    }
}

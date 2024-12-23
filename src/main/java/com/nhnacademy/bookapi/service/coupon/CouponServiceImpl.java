package com.nhnacademy.bookapi.service.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCouponRepository bookCouponRepository;
    private final CategoryCouponRepository categoryCouponRepository;

    private final RabbitTemplate rabbitTemplate;



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

        if (coupons.isEmpty()) {
            throw new CouponNotFoundException("No coupons found to expire");
        }

        for (Coupon coupon : coupons) {
            coupon.setCouponStatus(CouponStatus.EXPIRED);
        }
    }

//    // 쿠폰 발급 (쿠폰 아이디, 회원 아이디)
//    @Override
//    @Transactional
//    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
//        Coupon coupon = couponRepository.findById(request.getCouponId())
//                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
//
//        if (coupon.getMemberId() != null) {
//            throw new CouponAlreadyAssignedException("Coupon is already assigned");
//        }
//
//        Integer validTime = coupon.getCouponPolicy().getCouponValidTime();
//
//        coupon.setMemberId(request.getMemberId());
//        coupon.setCouponIssueDate(LocalDate.now());
//        coupon.setCouponExpiryDate(LocalDate.now().plusDays(validTime));
//        coupon.setCouponStatus(CouponStatus.NOTUSED);
//
//        return new CouponAssignResponseDTO(coupon);
//    }



//    @Override
//    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
//        // 요청 유효성 검사
//        if (!couponRepository.existsById(request.getCouponId())) {
//            throw new CouponNotFoundException("Coupon not found");
//        }
//
//        // 메시지 전송 및 응답 수신
//        try {
//            // 메시지 큐를 통해 요청을 전송하고 응답을 기다림
//            CouponAssignResponseDTO response = (CouponAssignResponseDTO) rabbitTemplate.convertSendAndReceive(
//                    EXCHANGE,
//                    ROUTING_KEY,
//                    request
//            );
//
//            if (response == null) {
//                throw new RuntimeException("Coupon assignment failed: No response received.");
//            }
//
//            return response;
//        } catch (Exception e) {
//            throw new RuntimeException("Error during coupon assignment: " + e.getMessage(), e);
//        }
//    }


    // 쿠폰 발급 (쿠폰 아이디, 회원 아이디)
    @Override
    @Transactional
    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
        try {
            log.debug("Sending message: {}", request);
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    request
            );
            log.info("Sent coupon assign request to RabbitMQ: {}", request);
            return new CouponAssignResponseDTO(request.getCouponId(), "Coupon assignment request sent successfully");
        } catch (Exception e) {
            log.error("Failed to send coupon assign request: {}", e.getMessage(), e);
            throw new MessageConversionException("Error sending coupon assign message", e);
        }
    }















    // 쿠폰 사용 (사용자용)
    @Transactional
    public CouponUseResponseDTO useCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (!Objects.equals(coupon.getMemberId(), userId)) {
            throw new CouponNotAssignedException("Coupon does not belong to the authenticated user");
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

    // 책 쿠폰 사용 (사용자용)
    @Transactional
    public CouponUseResponseDTO useBookCoupon(Long userId, Long couponId, Long bookId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (!Objects.equals(coupon.getMemberId(), userId)) {
            throw new CouponNotAssignedException("Coupon does not belong to the authenticated user");
        }

        BookCoupon bookCoupon = bookCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException("This coupon is not associated with a book"));

        if (!bookCoupon.getBook().getId().equals(bookId)) {
            throw new InvalidCouponUsageException("Coupon cannot be used for this book");
        }

        return useCoupon(userId, couponId);
    }

    // 카테고리 쿠폰 사용 (사용자용)
    @Transactional
    public CouponUseResponseDTO useCategoryCoupon(Long userId, Long couponId, Long categoryId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (!Objects.equals(coupon.getMemberId(), userId)) {
            throw new CouponNotAssignedException("Coupon does not belong to the authenticated user");
        }

        CategoryCoupon categoryCoupon = categoryCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException("This coupon is not associated with a category"));

        if (!isValidCategory(categoryCoupon.getCategory(), categoryId)) {
            throw new InvalidCouponUsageException("Coupon cannot be used in this category");
        }

        return useCoupon(userId, couponId);
    }

    // 유효한 카테고리인지 확인
    private boolean isValidCategory(Category couponCategory, Long requestedCategoryId) {
        List<Long> validCategories = categoryRepository.findSubcategories(couponCategory.getId());
        return validCategories.contains(requestedCategoryId);
    }

    // 사용자 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getAllCouponsByMemberId(Long userId) {
        List<Coupon> coupons = couponRepository.findByMemberId(userId);
        if (coupons.isEmpty()) {
            throw new CouponsNotFoundException("No coupons found for user ID: " + userId);
        }
        return coupons.stream()
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }

    // 사용자 미사용 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getUnusedCouponsByMemberId(Long userId) {
        List<Coupon> coupons = couponRepository.findByMemberIdAndCouponStatus(userId, CouponStatus.NOTUSED);
        if (coupons.isEmpty()) {
            throw new CouponsNotFoundException("No unused coupons found for user ID: " + userId);
        }
        return coupons.stream()
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }

    // 사용자 사용 쿠폰 조회
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getUsedCouponsByMemberId(Long userId) {
        List<Coupon> coupons = couponRepository.findByMemberIdAndCouponStatus(userId, CouponStatus.USED);
        if (coupons.isEmpty()) {
            throw new CouponsNotFoundException("No Used coupons found for user ID: " + userId);
        }
        return coupons.stream()
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }

    // 쿠폰 정책 아이디 기반 쿠폰 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getCouponsByPolicyId(Long policyId) {
        if (!couponPolicyRepository.existsById(policyId)) {
            throw new CouponPolicyNotFoundException("Coupon policy with ID " + policyId + " does not exist");
        }

        List<Coupon> coupons = couponRepository.findByCouponPolicyId(policyId);

        if (coupons.isEmpty()) {
            throw new CouponsNotFoundException("No coupons found for policy with ID: " + policyId);
        }

        return coupons.stream()
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
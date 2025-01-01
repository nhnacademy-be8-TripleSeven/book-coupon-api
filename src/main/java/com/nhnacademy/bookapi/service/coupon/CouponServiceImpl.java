package com.nhnacademy.bookapi.service.coupon;


import com.nhnacademy.bookapi.client.MemberFeignClient;
import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.dto.member.CouponMemberDTO;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpTimeoutException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
    private final MemberFeignClient memberFeignClient;


    // 쿠폰 생성 (이름, 정책)
    @Override
    @Transactional
    public BaseCouponResponseDTO createCoupon(CouponCreationRequestDTO request) {
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("Coupon policy not found"));

        Coupon coupon = new Coupon(request.getName(), policy);

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

        Coupon coupon = new Coupon(request.getName(), policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        BookCoupon bookCoupon = new BookCoupon(book, savedCoupon);

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

        Coupon coupon = new Coupon(request.getName(), policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        CategoryCoupon categoryCoupon = new CategoryCoupon(category, savedCoupon);

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
            coupon.updateCouponStatus(CouponStatus.EXPIRED);
        }
    }


    // 쿠폰 발급 (쿠폰 아이디, 회원 아이디)
    @Override
    @Transactional
    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
        try {
            log.debug("Sending message: {}", request);
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    request,
                    message -> {
                        message.getMessageProperties().setMessageId(UUID.randomUUID().toString()); // 고유 메시지 ID 설정
                        return message;
                    }
            );
            log.info("Sent coupon assign request to RabbitMQ: {}", request);
            return new CouponAssignResponseDTO(request.getCouponId(), "Coupon assignment request sent successfully");
        } catch (AmqpConnectException e) {
            log.info("RabbitMQ connection failed: {}", e.getMessage(), e);
            throw new CouponAssingAmqErrorException("RabbitMQ service unavailable: " + e.getMessage());
        } catch (AmqpTimeoutException e) {
            log.info("RabbitMQ response timed out: {}", e.getMessage(), e);
            throw new CouponAssingAmqErrorException("RabbitMQ communication timeout: " + e.getMessage());
        } catch (Exception e) {
            log.info("General RabbitMQ error: {}", e.getMessage(), e);
            throw new CouponAssingAmqErrorException("RabbitMQ communication error: " + e.getMessage());
        }
    }





    // 쿠폰 사용 (사용자용)
    @Transactional
    public CouponUseResponseDTO useCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (coupon.getCouponStatus() == CouponStatus.USED) {
            throw new CouponAlreadyUsedExceeption("Coupon is already used");
        }

        if (coupon.getCouponStatus() == CouponStatus.EXPIRED) {
            throw new CouponExpiredException("Coupon is expired");
        }

        if (!Objects.equals(coupon.getMemberId(), userId)) {
            throw new CouponNotAssignedException("Coupon does not belong to the authenticated user");
        }

        coupon.updateCouponStatus(CouponStatus.USED);
        coupon.updateCouponUseAt(LocalDateTime.now());

        return new CouponUseResponseDTO(coupon);
    }


    @Transactional
    public CouponUseResponseDTO useBaseCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (coupon.getCouponStatus() == CouponStatus.USED) {
            throw new CouponAlreadyUsedExceeption("Coupon is already used");
        }

        if (coupon.getCouponStatus() == CouponStatus.EXPIRED) {
            throw new CouponExpiredException("Coupon is expired");
        }

        coupon.updateCouponStatus(CouponStatus.USED);
        coupon.updateCouponUseAt(LocalDateTime.now());

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


    // 5년 이내 발급된 쿠폰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate) {
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);

        // 쿠폰 조회 (발급일자 5년 이내)
        List<Coupon> coupons = couponRepository.findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(userId, fiveYearsAgo);

        // 필터 적용
        return coupons.stream()
                .filter(coupon -> (keyword == null || coupon.getName().toLowerCase().contains(keyword.toLowerCase())) &&
                        (startDate == null || !coupon.getCouponIssueDate().isBefore(startDate)) &&
                        (endDate == null || !coupon.getCouponIssueDate().isAfter(endDate)))
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }


    // 5년 이내 사용된 쿠폰 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getUsedCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate) {
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);

        // 사용된 쿠폰 조회 (발급일자 5년 이내)
        List<Coupon> coupons = couponRepository.findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(
                userId, CouponStatus.USED, fiveYearsAgo);

        // 필터 적용
        return coupons.stream()
                .filter(coupon -> (keyword == null || coupon.getName().toLowerCase().contains(keyword.toLowerCase())) &&
                        (startDate == null || !coupon.getCouponIssueDate().isBefore(startDate)) &&
                        (endDate == null || !coupon.getCouponIssueDate().isAfter(endDate)))
                .map(this::mapToCouponDetailsDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<CouponAssignResponseDTO> createAndAssignCoupons(CouponCreationAndAssignRequestDTO request) {
        // 1. 쿠폰 정책 조회
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("쿠폰 정책을 찾을 수 없습니다."));

        // 2. 발급 대상 조회
        List<Long> memberIds = getMemberIdsByRecipientType(request);

        // 3. 쿠폰 생성 및 발급
        List<CouponAssignResponseDTO> responses = new ArrayList<>();
        for (Long memberId : memberIds) {
            Coupon coupon = createCouponBasedOnTarget(request, policy);
            responses.add(assignCouponToMember(coupon, memberId));
        }
        return responses;
    }

    private CouponAssignResponseDTO assignCouponToMember(Coupon coupon, Long memberId) {
        CouponAssignRequestDTO assignRequest = new CouponAssignRequestDTO(coupon.getId(), memberId);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    assignRequest,
                    message -> {
                        message.getMessageProperties().setMessageId(UUID.randomUUID().toString()); // 고유 메시지 ID 설정
                        return message;
                    }
            );
            log.info("쿠폰 ID {}가 회원 ID {}에게 발급 신청되었습니다.", coupon.getId(), memberId);
            return new CouponAssignResponseDTO(coupon.getId(), "쿠폰 발급 성공");
        } catch (AmqpConnectException | AmqpTimeoutException e) {
            log.info("쿠폰 발급 실패 - RabbitMQ 통신 오류: {}", e.getMessage(), e);
            throw new CouponAssingAmqErrorException("RabbitMQ 통신 오류로 인해 쿠폰 발급 실패");
        } catch (Exception e) {
            log.info("쿠폰 발급 실패 - 시스템 오류: {}", e.getMessage(), e);
            throw new CouponAssingAmqErrorException("시스템 오류로 인해 쿠폰 발급 실패");
        }
    }

    public Coupon createCouponBasedOnTarget(CouponCreationAndAssignRequestDTO request, CouponPolicy policy) {
        // 1. 쿠폰 객체 생성
        Coupon coupon = new Coupon(request.getName(), policy);
        Coupon savedCoupon = couponRepository.save(coupon);

        // 2. 쿠폰 타입별 추가 처리
        if (request.getCategoryId() != null) {
            // 카테고리 쿠폰 생성
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));
            CategoryCoupon categoryCoupon = new CategoryCoupon(category, savedCoupon);
            categoryCouponRepository.save(categoryCoupon);

        } else if (request.getBookId() != null) {
            // 도서 쿠폰 생성
            Book book = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다."));
            BookCoupon bookCoupon = new BookCoupon(book, savedCoupon);
            bookCouponRepository.save(bookCoupon);
        }

        // 3. 일반 쿠폰은 추가 처리 없음
        return savedCoupon;
    }


    private List<Long> getMemberIdsByRecipientType(CouponCreationAndAssignRequestDTO request) {
        switch (request.getRecipientType()) {
            case "전체":
                return memberFeignClient.getAllMembers().stream()
                        .map(CouponMemberDTO::getId)
                        .toList();
            case "등급별":
                return memberFeignClient.getMembersByGrade(request.getGrade()).stream()
                        .map(CouponMemberDTO::getId)
                        .toList();
            case "개인별":
                return request.getMemberIds();
            default:
                throw new InvalidRecipientTypeException("유효하지 않은 대상 유형: " + request.getRecipientType());
        }
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

        log.info("Coupon ID: {}, Book Title: {}, Category Name: {}", coupon.getId(), bookTitle, categoryName);

        return new CouponDetailsDTO(
                coupon,
                bookTitle,
                categoryName
        );
    }
}
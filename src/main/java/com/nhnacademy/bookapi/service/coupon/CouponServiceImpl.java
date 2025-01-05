package com.nhnacademy.bookapi.service.coupon;


import com.nhnacademy.bookapi.client.MemberFeignClient;
import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.dto.member.CouponMemberDTO;
import com.nhnacademy.bookapi.dto.member.MemberDto;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpTimeoutException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
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

//        if (!isValidCategory(categoryCoupon.getCategory(), categoryId)) {
//            throw new InvalidCouponUsageException("Coupon cannot be used in this category");
//        }

        return useCoupon(userId, couponId);
    }

    // 유효한 카테고리인지 확인
//    private boolean isValidCategory(Category couponCategory, Long requestedCategoryId) {
//        List<Long> validCategories = categoryRepository.findSubcategories(couponCategory.getId());
//        return validCategories.contains(requestedCategoryId);
//    }

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

        log.info("Retrieved coupon policy: {}", policy);

        // 2. 발급 대상 조회
        List<Long> memberIds = getMemberIdsByRecipientType(request);
        if (memberIds.isEmpty()) {
            throw new InvalidRecipientTypeException("발급 대상 회원이 없습니다.");
        }

        log.info("Target members for coupon assignment: {}", memberIds);

        // 3. 쿠폰 생성 및 발급
        Coupon coupon = createCouponBasedOnTarget(request, policy);

        log.info("Created coupon: {}", coupon);

        return assignCouponsToMembers(coupon, memberIds);
    }

    private List<Long> getMemberIdsByRecipientType(CouponCreationAndAssignRequestDTO request) {
        List<Long> memberIds = new ArrayList<>();
        int page = 0, size = 100;
        Page<MemberDto> memberPage;

        switch (request.getRecipientType()) {
            case "전체":
                do {
                    memberPage = memberFeignClient.getMembers(null, null, page, size, null, "ASC");
                    memberIds.addAll(memberPage.getContent().stream().map(MemberDto::getId).toList());
                    page++;
                } while (memberPage.hasNext());
                break;

            case "등급별":
                MemberGrade grade = convertGrade(request.getGrade());
                do {
                    memberPage = memberFeignClient.getMembers(null, grade, page, size, null, "ASC");
                    memberIds.addAll(memberPage.getContent().stream().map(MemberDto::getId).toList());
                    page++;
                } while (memberPage.hasNext());
                break;

            case "개인별":
                memberIds.addAll(request.getMemberIds());
                break;

            default:
                throw new InvalidRecipientTypeException("유효하지 않은 대상 유형: " + request.getRecipientType());
        }
        return memberIds;
    }

    private Coupon createCouponBasedOnTarget(CouponCreationAndAssignRequestDTO request, CouponPolicy policy) {
        // 1. 쿠폰 객체 생성
        Coupon coupon = new Coupon(request.getName(), policy);
        Coupon savedCoupon = couponRepository.save(coupon);

        // 2. 쿠폰 타입별 추가 처리
        if (request.getCategoryId() != null) {
            createCategoryCoupon(request.getCategoryId(), savedCoupon);
        } else if (request.getBookId() != null) {
            createBookCoupon(request.getBookId(), savedCoupon);
        }

        // 3. 일반 쿠폰 처리 (추가 로직 없음)
        return savedCoupon;
    }

    private void createCategoryCoupon(Long categoryId, Coupon savedCoupon) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));
        CategoryCoupon categoryCoupon = new CategoryCoupon(category, savedCoupon);
        categoryCouponRepository.save(categoryCoupon);
    }

    private void createBookCoupon(Long bookId, Coupon savedCoupon) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다."));
        BookCoupon bookCoupon = new BookCoupon(book, savedCoupon);
        bookCouponRepository.save(bookCoupon);
    }

    private List<CouponAssignResponseDTO> assignCouponsToMembers(Coupon coupon, List<Long> memberIds) {
        List<CouponAssignRequestDTO> requests = memberIds.stream()
                .map(memberId -> new CouponAssignRequestDTO(coupon.getId(), memberId))
                .toList();

        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    requests,
                    message -> {
                        message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                        return message;
                    }
            );
            log.info("Successfully sent coupon assignment requests for {} members.", memberIds.size());
            return requests.stream()
                    .map(request -> new CouponAssignResponseDTO(request.getCouponId(), "쿠폰 발급 요청 성공"))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to send coupon assignment requests: {}", e.getMessage(), e);
            throw new CouponAssingAmqErrorException("RabbitMQ 통신 오류로 쿠폰 발급 실패");
        }
    }

    private MemberGrade convertGrade(String grade) {
        try {
            return MemberGrade.valueOf(grade.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRecipientTypeException("유효하지 않은 등급: " + grade);
        }
    }




//    @Override
//    @Transactional
//    public List<CouponAssignResponseDTO> createAndAssignCoupons(CouponCreationAndAssignRequestDTO request) {
//        // 1. 쿠폰 정책 조회
//        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
//                .orElseThrow(() -> new CouponPolicyNotFoundException("쿠폰 정책을 찾을 수 없습니다."));
//
//        // 2. 발급 대상 조회
//        List<Long> memberIds = getMemberIdsByRecipientType(request);
//
//        // 3. 쿠폰 생성 및 발급
//        List<CouponAssignResponseDTO> responses = new ArrayList<>();
//        for (Long memberId : memberIds) {
//            Coupon coupon = createCouponBasedOnTarget(request, policy);
//            responses.add(assignCouponToMember(coupon, memberId));
//        }
//        return responses;
//    }
//
//
//    // memberId를 List<Long>으로 사용하여 한번에 처리
//    private CouponAssignResponseDTO assignCouponToMember(Coupon coupon, Long memberId) {
//        CouponAssignRequestDTO assignRequest = new CouponAssignRequestDTO(coupon.getId(), memberId);
//
//        try {
//            rabbitTemplate.convertAndSend(
//                    RabbitConfig.EXCHANGE_NAME,
//                    RabbitConfig.ROUTING_KEY,
//                    assignRequest,
//                    message -> {
//                        message.getMessageProperties().setMessageId(UUID.randomUUID().toString()); // 고유 메시지 ID 설정
//                        return message;
//                    }
//            );
//            log.info("쿠폰 ID {}가 회원 ID {}에게 발급 신청되었습니다.", coupon.getId(), memberId);
//            return new CouponAssignResponseDTO(coupon.getId(), "쿠폰 발급 성공");
//        } catch (AmqpConnectException | AmqpTimeoutException e) {
//            log.info("쿠폰 발급 실패 - RabbitMQ 통신 오류: {}", e.getMessage(), e);
//            throw new CouponAssingAmqErrorException("RabbitMQ 통신 오류로 인해 쿠폰 발급 실패");
//        } catch (Exception e) {
//            log.info("쿠폰 발급 실패 - 시스템 오류: {}", e.getMessage(), e);
//            throw new CouponAssingAmqErrorException("시스템 오류로 인해 쿠폰 발급 실패");
//        }
//    }
//
//    public Coupon createCouponBasedOnTarget(CouponCreationAndAssignRequestDTO request, CouponPolicy policy) {
//        // 1. 쿠폰 객체 생성
//        Coupon coupon = new Coupon(request.getName(), policy);
//        Coupon savedCoupon = couponRepository.save(coupon);
//
//        // 2. 쿠폰 타입별 추가 처리
//        if (request.getCategoryId() != null) {
//            // 카테고리 쿠폰 생성
//            Category category = categoryRepository.findById(request.getCategoryId())
//                    .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다."));
//            CategoryCoupon categoryCoupon = new CategoryCoupon(category, savedCoupon);
//            categoryCouponRepository.save(categoryCoupon);
//
//        } else if (request.getBookId() != null) {
//            // 도서 쿠폰 생성
//            Book book = bookRepository.findById(request.getBookId())
//                    .orElseThrow(() -> new BookNotFoundException("도서를 찾을 수 없습니다."));
//            BookCoupon bookCoupon = new BookCoupon(book, savedCoupon);
//            bookCouponRepository.save(bookCoupon);
//        }
//
//        // 3. 일반 쿠폰은 추가 처리 없음
//        return savedCoupon;
//    }
//
//
//    private List<Long> getMemberIdsByRecipientType(CouponCreationAndAssignRequestDTO request) {
//        switch (request.getRecipientType()) {
//            case "전체":
//                return memberFeignClient.getAllMembers().stream()
//                        .map(CouponMemberDTO::getId)
//                        .toList();
//            case "등급별":
//                MemberGrade grade = convertGrade(request.getGrade()); // String to MemberGrade 변환
//                return memberFeignClient.getMembersByGrade(grade).stream()
//                        .map(CouponMemberDTO::getId)
//                        .toList();
//            case "개인별":
//                return request.getMemberIds();
//            default:
//                throw new InvalidRecipientTypeException("유효하지 않은 대상 유형: " + request.getRecipientType());
//        }
//    }
//
//    private MemberGrade convertGrade(String grade) {
//        try {
//            return MemberGrade.valueOf(grade.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new InvalidRecipientTypeException("유효하지 않은 등급: " + grade);
//        }
//    }

    @Override
    @Transactional
    public void assignMonthlyBirthdayCoupons() {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();

        log.info("Checking for members with birthdays in month: {}", currentMonth);

        // 해당 월 생일 회원 조회
        List<MemberDto> membersWithBirthdays = fetchMembersByMonth(currentMonth);
        if (membersWithBirthdays.isEmpty()) {
            log.info("No members with birthdays in month: {}", currentMonth);
            return;
        }

        // 쿠폰 정책 조회
        CouponPolicy birthdayPolicy = fetchBirthdayCouponPolicy(currentMonth);
        if (birthdayPolicy == null) {
            log.warn("No coupon policy found for month: {}", currentMonth);
            return;
        }

        log.info("Using coupon policy: {}", birthdayPolicy.getName());

        // 쿠폰 발급 요청 생성
        List<CouponAssignRequestDTO> couponRequests = membersWithBirthdays.stream()
                .map(member -> new CouponAssignRequestDTO(null, member.getId()))
                .toList();

        // 쿠폰 발급 처리
        assignBirthdayCoupons(couponRequests, birthdayPolicy);
    }

    private List<MemberDto> fetchMembersByMonth(int month) {
        int page = 0, size = 100;
        List<MemberDto> members = new ArrayList<>();
        Page<MemberDto> memberPage;

        do {
            memberPage = memberFeignClient.getMembers(null, null, page, size, null, "ASC");
            members.addAll(memberPage.getContent().stream()
                    .filter(member -> {
                        LocalDate birthDate = member.getBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return birthDate.getMonthValue() == month;
                    })
                    .toList());
            page++;
        } while (memberPage.hasNext());

        return members;
    }

    private CouponPolicy fetchBirthdayCouponPolicy(int month) {
        // 2월 윤년 여부 확인
        if (month == 2) {
            boolean isLeapYear = Year.now().isLeap();
            return couponPolicyRepository.findById(isLeapYear ? 103L : 102L)
                    .orElseThrow(() -> new CouponPolicyNotFoundException(
                            "No coupon policy found for February in " + (isLeapYear ? "leap year" : "normal year")));
        }

        // 정책 ID를 월별로 매핑
        Long policyId = switch (month) {
            case 1 -> 101L;
            case 3 -> 104L;
            case 4 -> 105L;
            case 5 -> 106L;
            case 6 -> 107L;
            case 7 -> 108L;
            case 8 -> 109L;
            case 9 -> 110L;
            case 10 -> 111L;
            case 11 -> 112L;
            case 12 -> 113L;
            default -> null;
        };

        if (policyId == null) {
            return null;
        }

        return couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponPolicyNotFoundException("No coupon policy found for ID: " + policyId));
    }


    private void assignBirthdayCoupons(List<CouponAssignRequestDTO> requests, CouponPolicy policy) {
        CouponCreationAndAssignRequestDTO requestDTO = new CouponCreationAndAssignRequestDTO(
                "생일 축하 쿠폰",
                policy.getId(),
                requests.stream().map(CouponAssignRequestDTO::getMemberId).toList(),
                "개인별"
        );

        try {
            // createAndAssignCoupons를 사용해 트랜잭션 처리
            createAndAssignCoupons(requestDTO);
        } catch (Exception e) {
            log.error("Failed to assign birthday coupons.", e);
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
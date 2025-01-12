package com.nhnacademy.bookapi.service.coupon;


import com.nhnacademy.bookapi.client.MemberFeignClient;
import com.nhnacademy.bookapi.config.RabbitConfig;
import com.nhnacademy.bookapi.dto.coupon.*;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyOrderResponseDTO;
import com.nhnacademy.bookapi.dto.couponpolicy.CouponPolicyResponseDTO;
import com.nhnacademy.bookapi.dto.member.MemberDto;
import com.nhnacademy.bookapi.entity.*;
import com.nhnacademy.bookapi.exception.*;
import com.nhnacademy.bookapi.repository.*;
import com.nhnacademy.bookapi.service.couponpolicy.CouponPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.*;
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
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoryCouponRepository categoryCouponRepository;

    private final RabbitTemplate rabbitTemplate;
    private final MemberFeignClient memberFeignClient;

    private final CouponPolicyService couponPolicyService;


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

    // 쿠폰 단체 생성
    @Override
    @Transactional
    public void createCouponsInBulk(CouponBulkCreationRequestDTO request) {

        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("Coupon policy not found."));

        for (long i = 0; i < request.getQuantity(); i++) {

            if (request.getCategoryId() != null) {
                // Create category coupon
                CategoryCouponCreationRequestDTO categoryRequest = new CategoryCouponCreationRequestDTO(
                        request.getCategoryId(),
                        policy.getId(),
                        request.getName()
                );
                createCategoryCoupon(categoryRequest);
            } else if (request.getBookId() != null) {
                // Create book coupon
                BookCouponCreationRequestDTO bookRequest = new BookCouponCreationRequestDTO(
                        request.getBookId(),
                        policy.getId(),
                        request.getName()
                );
                createBookCoupon(bookRequest);
            } else {
                // Create general coupon
                CouponCreationRequestDTO generalRequest = new CouponCreationRequestDTO(
                        request.getName(),
                        policy.getId()
                );
                createCoupon(generalRequest);
            }

        }
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


    // 쿠폰 사용 (사용자용)
    @Transactional
    public CouponUseResponseDTO useCoupon(Long userId, Long couponId, Long bookId) {
        // 1. 쿠폰 조회 및 기본 검증
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        // 2. 쿠폰 소유자 검증
        if (!Objects.equals(coupon.getMemberId(), userId)) {
            throw new CouponNotAssignedException("Coupon does not belong to the authenticated user");
        }

        // 3. 쿠폰 상태 검증
        validateCouponStatus(coupon);

        // 4. 쿠폰 유형별 처리
        if (isBookCoupon(coupon)) {
            return handleBookCoupon(coupon, bookId);
        } else if (isCategoryCoupon(coupon)) {
            return handleCategoryCoupon(coupon, bookId);
        } else {
            return handleBaseCoupon(coupon);
        }
    }

    private void validateCouponStatus(Coupon coupon) {
        if (coupon.getCouponStatus() == CouponStatus.USED) {
            throw new CouponAlreadyUsedException("Coupon is already used");
        }

        if (coupon.getCouponStatus() == CouponStatus.EXPIRED) {
            throw new CouponExpiredException("Coupon is expired");
        }
    }

    private boolean isBookCoupon(Coupon coupon) {
        return bookCouponRepository.existsByCoupon(coupon);
    }

    private boolean isCategoryCoupon(Coupon coupon) {
        return categoryCouponRepository.existsByCoupon(coupon);
    }

    @Transactional
    public CouponUseResponseDTO handleBaseCoupon(Coupon coupon) {
        // 쿠폰 상태 업데이트
        coupon.updateCouponStatus(CouponStatus.USED);
        coupon.updateCouponUseAt(LocalDateTime.now());

        return new CouponUseResponseDTO(coupon);
    }

    @Transactional
    public CouponUseResponseDTO handleBookCoupon(Coupon coupon, Long bookId) {
        BookCoupon bookCoupon = bookCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException("This coupon is not associated with a book"));

        // 도서 ID 검증
        if (!bookCoupon.getBook().getId().equals(bookId)) {
            throw new InvalidCouponUsageException("Coupon cannot be used for this book");
        }

        return handleBaseCoupon(coupon);
    }

    @Transactional
    public CouponUseResponseDTO handleCategoryCoupon(Coupon coupon, Long bookId) {
        CategoryCoupon categoryCoupon = categoryCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException("This coupon is not associated with a category"));

        // 도서의 카테고리 목록 조회
        List<Category> bookCategories = bookCategoryRepository.findCategoriesByBookId(bookId);

        // 쿠폰 카테고리와 도서 카테고리의 유효성 검사
        if (!isCategoryCouponValid(categoryCoupon.getCategory(), bookCategories)) {
            throw new InvalidCouponUsageException("Coupon cannot be used for this book");
        }

        return handleBaseCoupon(coupon);
    }

    private boolean isCategoryCouponValid(Category couponCategory, List<Category> bookCategories) {
        // 쿠폰 카테고리가 책 카테고리 계층에 포함되는지 확인
        return bookCategories.contains(couponCategory);
    }


    // 유저 인증없이 쿠폰 사용
    @Override
    @Transactional
    public CouponUseResponseDTO useBaseCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        if (coupon.getCouponStatus() == CouponStatus.USED) {
            throw new CouponAlreadyUsedException("Coupon is already used");
        }

        if (coupon.getCouponStatus() == CouponStatus.EXPIRED) {
            throw new CouponExpiredException("Coupon is expired");
        }

        coupon.updateCouponStatus(CouponStatus.USED);
        coupon.updateCouponUseAt(LocalDateTime.now());

        return new CouponUseResponseDTO(coupon);
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

    // 쿠폰 아이디 기반 쿠폰 정책 조회
    @Override
    @Transactional(readOnly = true)
    public CouponPolicyOrderResponseDTO getCouponPolicyByCouponId(Long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (optionalCoupon.isEmpty()) {
            throw new CouponNotFoundException("No coupons found for ID: " + couponId);
        }

        Coupon coupon = optionalCoupon.get();
        Optional<CouponPolicy> optionalCouponPolicy = couponPolicyRepository.findById(coupon.getCouponPolicy().getId());

        if (optionalCouponPolicy.isEmpty()) {
            throw new CouponPolicyNotFoundException("No coupons found for policy with ID: " + coupon.getCouponPolicy().getId());
        }

        CouponPolicy couponPolicy = optionalCouponPolicy.get();

        return new CouponPolicyOrderResponseDTO(
                couponPolicy.getCouponMinAmount(),
                couponPolicy.getCouponMaxAmount(),
                couponPolicy.getCouponDiscountRate(),
                couponPolicy.getCouponDiscountAmount(),
                coupon.getCouponStatus());
    }





    // 쿠폰 발급 (쿠폰 아이디, 회원 아이디)
    @Override
    @Transactional
    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
        // 1. 쿠폰 조회
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

        // 2. 회원 ID와 연관지어 발급 데이터 설정
        coupon.setCouponAssignData(
                request.getMemberId(),
                LocalDate.now(), // 발급일
                LocalDate.now().plusDays(coupon.getCouponPolicy().getCouponValidTime()), // 만료일
                CouponStatus.NOTUSED // 초기 상태
        );

        // 3. 쿠폰 저장 (갱신)
        Coupon updatedCoupon = couponRepository.save(coupon);

        // 4. 응답 DTO 생성
        return new CouponAssignResponseDTO(
                updatedCoupon.getId(),
                "쿠폰 발급 성공"
        );
    }



    // 생성 및 발급
    @Override
    @Transactional
    public List<CouponAssignResponseDTO> createAndAssignCoupons(CouponCreationAndAssignRequestDTO request) {
        // 1. 쿠폰 정책 조회
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException("쿠폰 정책을 찾을 수 없습니다."));

        // 2. 발급 대상 조회
        List<Long> memberIds = getMemberIdsByRecipientType(request);
        if (memberIds.isEmpty()) {
            throw new InvalidRecipientTypeException("발급 대상 회원이 없습니다.");
        }

        // 3. 쿠폰 생성 및 발급
        List<Coupon> coupons = memberIds.stream()
                .map(memberId -> {
                    Coupon coupon = createCouponBasedOnTarget(request, policy);
                    coupon.setCouponAssignData(
                            memberId,
                            LocalDate.now(),
                            LocalDate.now().plusDays(policy.getCouponValidTime()),
                            CouponStatus.NOTUSED
                    );
                    return coupon;
                })
                .toList();

        // 4. 배치 저장
        List<Coupon> savedCoupons = couponRepository.saveAll(coupons);

        // 5. 응답 생성
        return savedCoupons.stream()
                .map(savedCoupon -> new CouponAssignResponseDTO(savedCoupon.getId(), "쿠폰 발급 성공"))
                .toList();
    }


    @Override
    @Transactional
    public Coupon createCouponBasedOnTarget(CouponCreationAndAssignRequestDTO request, CouponPolicy policy) {
        // 기본 쿠폰 생성
        Coupon coupon;

        if (request.getCategoryId() != null) {
            // 카테고리 쿠폰 생성 로직 호출
            CategoryCouponCreationRequestDTO categoryRequest = new CategoryCouponCreationRequestDTO(
                    request.getCategoryId(),
                    policy.getId(),
                    request.getName()
            );
            CategoryCouponResponseDTO response = createCategoryCoupon(categoryRequest);
            coupon = couponRepository.findById(response.getId())
                    .orElseThrow(() -> new CouponNotFoundException("생성된 쿠폰을 찾을 수 없습니다."));
        } else if (request.getBookId() != null) {
            // 도서 쿠폰 생성 로직 호출
            BookCouponCreationRequestDTO bookRequest = new BookCouponCreationRequestDTO(
                    request.getBookId(),
                    policy.getId(),
                    request.getName()
            );
            BookCouponResponseDTO response = createBookCoupon(bookRequest);
            coupon = couponRepository.findById(response.getId())
                    .orElseThrow(() -> new CouponNotFoundException("생성된 쿠폰을 찾을 수 없습니다."));
        } else {
            // 기본 쿠폰 생성 로직 호출
            CouponCreationRequestDTO baseRequest = new CouponCreationRequestDTO(
                    request.getName(),
                    policy.getId()
            );
            BaseCouponResponseDTO response = createCoupon(baseRequest);
            coupon = couponRepository.findById(response.getId())
                    .orElseThrow(() -> new CouponNotFoundException("생성된 쿠폰을 찾을 수 없습니다."));
        }

        return coupon;
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


    private MemberGrade convertGrade(String grade) {
        try {
            return MemberGrade.valueOf(grade.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRecipientTypeException("유효하지 않은 등급: " + grade);
        }
    }


    @Override
    @Transactional
    public List<CouponAssignResponseDTO> issueWelcomeCoupon(Long memberId) {
        List<CouponAssignResponseDTO> responses = new ArrayList<>();

        try {
            // 1. "Welcome"이 포함된 모든 쿠폰 정책 검색 및 처리
            try {
                List<CouponPolicyResponseDTO> couponPolicies = couponPolicyService.searchCouponPoliciesByName("Welcome");
                if (couponPolicies.isEmpty()) {
                    log.warn("No 'Welcome' coupon policies found.");
                } else {
                    for (CouponPolicyResponseDTO policy : couponPolicies) {
                        CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                                policy.getName(), // 쿠폰 이름
                                policy.getId(),   // 정책 ID
                                Collections.singletonList(memberId), // 대상 회원
                                "개인별"           // 발급 대상 유형
                        );
                        responses.addAll(createAndAssignCoupons(request));
                    }
                }
            } catch (Exception e) {
                log.error("Error while processing 'Welcome' coupon policies for member ID: {}", memberId, e);
            }

            // 2. "회원가입 선착순 쿠폰" 발급 처리
            try {
                if (hasAvailableFirstComeWelcomeCoupon()) {
                    CouponAssignResponseDTO response = assignFirstComeWelcomeCoupon(memberId);
                    responses.add(response);
                } else {
                    log.info("No available '회원가입 선착순 쿠폰' for member ID: {}", memberId);
                }
            } catch (Exception e) {
                log.error("Error while assigning '회원가입 선착순 쿠폰' for member ID: {}", memberId, e);
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred during welcome coupon assignment for member ID: {}", memberId, e);
            throw new CouponIssueWelcomeException("Failed to process welcome coupons.");
        }

        return responses;
    }


    @Transactional(readOnly = true)
    public boolean hasAvailableFirstComeWelcomeCoupon() {
        return couponRepository.findAndLockFirstByName("회원가입 선착순 쿠폰").isPresent();
    }

    @Transactional(readOnly = true)
    public CouponAssignResponseDTO assignFirstComeWelcomeCoupon(Long memberId) {
        Coupon coupon = couponRepository.findAndLockFirstByName("회원가입 선착순 쿠폰")
                .orElseThrow(() -> new CouponNotFoundException("No available welcome coupons."));

        // 쿠폰 발급 요청 생성 및 전송
        CouponAssignRequestDTO request = new CouponAssignRequestDTO(coupon.getId(), memberId);
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                request
        );

        log.info("Welcome coupon assign request sent to MQ for coupon ID: {}, member ID: {}", coupon.getId(), memberId);

        return new CouponAssignResponseDTO(coupon.getId(), "Welcome coupon assigned successfully.");
    }



    // 생일 쿠폰 발급
    @Override
    @Transactional
    public void assignMonthlyBirthdayCoupons() {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        boolean isLeapYear = Year.now().isLeap();

        log.info("Checking for members with birthdays in month: {}", currentMonth);

        // 해당 월 생일 회원 조회
        List<MemberDto> membersWithBirthdays = fetchMembersByMonth(currentMonth);
        if (membersWithBirthdays.isEmpty()) {
            log.info("No members with birthdays in month: {}", currentMonth);
            return;
        }

        // 월별 생일 쿠폰 정책 조회 (윤년 여부 포함)
        List<CouponPolicy> birthdayPolicies = fetchBirthdayCouponPolicies(currentMonth, isLeapYear);
        if (birthdayPolicies.isEmpty()) {
            log.warn("No coupon policies found for month: {}, Leap Year: {}", currentMonth, isLeapYear);
            return;
        }

        log.info("Using coupon policies: {}", birthdayPolicies.stream().map(CouponPolicy::getName).toList());

        // 각 정책에 대해 쿠폰 발급
        for (CouponPolicy policy : birthdayPolicies) {
            log.info("Processing coupon policy: {}", policy.getName());

            // 쿠폰 발급 요청 생성
            List<CouponAssignRequestDTO> couponRequests = membersWithBirthdays.stream()
                    .map(member -> new CouponAssignRequestDTO(null, member.getId()))
                    .toList();

            assignBirthdayCoupons(couponRequests, policy);
        }
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


    private List<CouponPolicy> fetchBirthdayCouponPolicies(int month, boolean isLeapYear) {
        String basePattern = "생일 축하 쿠폰 - " + month + "월";
        String yearType = (month == 2) ? (isLeapYear ? "윤년" : "평년") : "";

        // 정책 이름 필터링
        String namePattern = basePattern + " " + yearType;

        return couponPolicyRepository.findByNameContaining(namePattern);
    }


    private void assignBirthdayCoupons(List<CouponAssignRequestDTO> requests, CouponPolicy policy) {
        CouponCreationAndAssignRequestDTO requestDTO = new CouponCreationAndAssignRequestDTO(
                "생일 축하 쿠폰",
                policy.getId(),
                requests.stream().map(CouponAssignRequestDTO::getMemberId).toList(),
                "개인별"
        );

        try {
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
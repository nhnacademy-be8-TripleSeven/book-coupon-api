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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private static final String COUPON_NOT_FOUND_MESSAGE = "Coupon not found";
    private static final String COUPON_ALREADY_ASSIGNED_MESSAGE = "이미 할당된 쿠폰입니다.";
    private static final String COUPON_POLICY_NOT_FOUND = "쿠폰 정책을 찾을 수 없습니다.";


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
    public BaseCouponResponseDTO createCoupon(CouponCreationRequestDTO request) {
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException(COUPON_POLICY_NOT_FOUND));

        Coupon coupon = new Coupon(request.getName(), policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        return new BaseCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy());
    }

    // 도서 쿠폰 생성 (이름, 정책 아이디, 도서 아이디)
    @Override
    public BookCouponResponseDTO createBookCoupon(BookCouponCreationRequestDTO request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException(COUPON_POLICY_NOT_FOUND));

        Coupon coupon = new Coupon(request.getName(), policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        BookCoupon bookCoupon = new BookCoupon(book, savedCoupon);

        bookCouponRepository.save(bookCoupon);

        return new BookCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy(), book.getTitle());
    }

    // 카테고리 쿠폰 생성 (이름, 정책 아이디, 카테고리 아이디)
    @Override
    public CategoryCouponResponseDTO createCategoryCoupon(CategoryCouponCreationRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException(COUPON_POLICY_NOT_FOUND));

        Coupon coupon = new Coupon(request.getName(), policy);

        Coupon savedCoupon = couponRepository.save(coupon);

        CategoryCoupon categoryCoupon = new CategoryCoupon(category, savedCoupon);

        categoryCouponRepository.save(categoryCoupon);

        return new CategoryCouponResponseDTO(savedCoupon.getId(), savedCoupon.getName(), savedCoupon.getCouponPolicy(), category.getName());
    }


    @Override
    @Transactional
    public BulkCouponCreationResponseDTO createCouponsInBulk(CouponBulkCreationRequestDTO request) {
        long createdCount = 0;

        try {
            // 쿠폰 정책 조회
            CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                    .orElseThrow(() -> new CouponPolicyNotFoundException(COUPON_POLICY_NOT_FOUND));

            // 요청 수량만큼 반복 생성
            for (long i = 0; i < request.getQuantity(); i++) {
                if (tryCreateCoupon(request, policy, i)) {
                    createdCount++;
                }
            }
        } catch (Exception e) {
            log.error("Bulk coupon creation failed", e);
            return new BulkCouponCreationResponseDTO(false, createdCount);
        }

        boolean success = createdCount > 0; // 일부라도 성공했으면 true 반환
        log.info("Bulk coupon creation completed. Success? {}", success);
        return new BulkCouponCreationResponseDTO(success, createdCount);
    }

    private boolean tryCreateCoupon(CouponBulkCreationRequestDTO request, CouponPolicy policy, long iteration) {
        try {
            CouponCreationAndAssignRequestDTO individualRequest = new CouponCreationAndAssignRequestDTO(
                    request.getName(),
                    policy.getId(),
                    request.getCategoryId(),
                    request.getBookId()
            );
            createCouponBasedOnType(individualRequest, policy);
            return true;
        } catch (Exception e) {
            log.error("Error creating coupon at iteration {}: {}", iteration, e.getMessage());
            return false;
        }
    }

    protected void createCouponBasedOnType(CouponCreationAndAssignRequestDTO request, CouponPolicy policy) {
        try {
            Coupon coupon = createCouponBasedOnTarget(request, policy);
            log.info("Coupon successfully created: {}", coupon.getId());
        } catch (Exception e) {
            log.error("Failed to create coupon for request: {}, error: {}", request, e.getMessage());
        }
    }




    // 쿠폰 만료 (스케쥴러)
    @Override
    @Transactional
    public void expireCoupons() {
        List<Coupon> coupons = couponRepository.findByCouponStatusAndCouponExpiryDateBefore(CouponStatus.NOTUSED, LocalDate.now());

        if (coupons.isEmpty()) {
            log.info("No coupons to expire at {}", LocalDate.now());
            return;
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
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));

        // 2. 쿠폰 소유자 검증
        if (!Objects.equals(coupon.getMemberId(), userId)) {
            throw new CouponNotAssignedException("Coupon does not belong to the authenticated user");
        }

        // 3. 쿠폰 상태 검증
        validateCouponStatus(coupon);

        // 4. 쿠폰 유형별 처리
        if (bookCouponRepository.existsByCoupon(coupon)) {
            return handleBookCoupon(coupon, bookId);
        } else if (categoryCouponRepository.existsByCoupon(coupon)) {
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


    private CouponUseResponseDTO handleBaseCoupon(Coupon coupon) {
        // 쿠폰 상태 업데이트
        coupon.updateCouponStatus(CouponStatus.USED);
        coupon.updateCouponUseAt(LocalDateTime.now());

        return new CouponUseResponseDTO(coupon);
    }

    private CouponUseResponseDTO handleBookCoupon(Coupon coupon, Long bookId) {
        BookCoupon bookCoupon = bookCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException("This coupon is not associated with a book"));

        // 도서 ID 검증
        if (!bookCoupon.getBook().getId().equals(bookId)) {
            throw new InvalidCouponUsageException("Coupon cannot be used for this book");
        }

        return handleBaseCoupon(coupon);
    }

    private CouponUseResponseDTO handleCategoryCoupon(Coupon coupon, Long bookId) {
        CategoryCoupon categoryCoupon = categoryCouponRepository.findByCoupon(coupon)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));

        // 도서의 카테고리 목록 조회
        List<Category> bookCategories = bookCategoryRepository.findCategoriesByBookId(bookId);

        // 쿠폰 카테고리와 도서 카테고리의 유효성 검사
        if (!bookCategories.contains(categoryCoupon.getCategory())) {
            throw new InvalidCouponUsageException("Coupon cannot be used for this book");
        }

        return handleBaseCoupon(coupon);
    }


    // 유저 인증없이 쿠폰 사용
    @Override
    @Transactional
    public CouponUseResponseDTO useBaseCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));

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

    private boolean isValidCoupon(Coupon coupon, String keyword, LocalDate startDate, LocalDate endDate) {
        return (keyword == null || coupon.getName().toLowerCase().contains(keyword.toLowerCase())) &&
                (startDate == null || !coupon.getCouponIssueDate().isBefore(startDate)) &&
                (endDate == null || !coupon.getCouponIssueDate().isAfter(endDate));
    }

    // 5년 이내 발급된 쿠폰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate) {
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);
        List<Coupon> coupons = couponRepository.findByMemberIdAndCouponIssueDateAfterOrderByCouponIssueDateDesc(userId, fiveYearsAgo);

        return coupons.stream()
                .filter(coupon -> isValidCoupon(coupon, keyword, startDate, endDate))
                .map(this::mapToCouponDetailsDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDetailsDTO> getUsedCouponsForUser(Long userId, String keyword, LocalDate startDate, LocalDate endDate) {
        LocalDate fiveYearsAgo = LocalDate.now().minusYears(5);
        List<Coupon> coupons = couponRepository.findByMemberIdAndCouponStatusAndCouponIssueDateAfterOrderByCouponUseAtDesc(
                userId, CouponStatus.USED, fiveYearsAgo);

        return coupons.stream()
                .filter(coupon -> isValidCoupon(coupon, keyword, startDate, endDate))
                .map(this::mapToCouponDetailsDTO)
                .toList();
    }

    // 쿠폰 아이디 기반 쿠폰 정책 조회
    @Override
    @Transactional(readOnly = true)
    public CouponPolicyOrderResponseDTO getCouponPolicyByCouponId(Long couponId) {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (optionalCoupon.isEmpty()) {
            throw new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE);
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

    @Transactional
    @Override
    public List<CouponAssignResponseDTO> createAndAssignCoupons(CouponCreationAndAssignRequestDTO request) {
        // 트랜잭션 시작
        return createAndAssignCouponsLogic(request);
    }

    // 생성 및 발급
    private List<CouponAssignResponseDTO> createAndAssignCouponsLogic(CouponCreationAndAssignRequestDTO request) {
        // 1. 쿠폰 정책 조회
        CouponPolicy policy = couponPolicyRepository.findById(request.getCouponPolicyId())
                .orElseThrow(() -> new CouponPolicyNotFoundException(COUPON_POLICY_NOT_FOUND));

        // 2. 발급 대상 조회
        List<Long> memberIds = getMemberIdsByRecipientType(request);
        if (memberIds.isEmpty()) {
            throw new MemberNotFoundException("발급 대상 회원이 없습니다.");
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



    // 타겟별 쿠폰 생성
    @Override
    public Coupon createCouponBasedOnTarget(CouponCreationAndAssignRequestDTO request, CouponPolicy policy) {
        try {
            if (request.getCategoryId() != null) {
                CategoryCouponCreationRequestDTO categoryRequest = new CategoryCouponCreationRequestDTO(
                        request.getCategoryId(),
                        policy.getId(),
                        request.getName()
                );
                CategoryCouponResponseDTO response = createCategoryCoupon(categoryRequest);
                return couponRepository.findById(response.getId())
                        .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));
            } else if (request.getBookId() != null) {
                BookCouponCreationRequestDTO bookRequest = new BookCouponCreationRequestDTO(
                        request.getBookId(),
                        policy.getId(),
                        request.getName()
                );
                BookCouponResponseDTO response = createBookCoupon(bookRequest);
                return couponRepository.findById(response.getId())
                        .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));
            } else {
                CouponCreationRequestDTO baseRequest = new CouponCreationRequestDTO(
                        request.getName(),
                        policy.getId()
                );
                BaseCouponResponseDTO response = createCoupon(baseRequest);
                return couponRepository.findById(response.getId())
                        .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));
            }
        } catch (Exception e) {
            log.error("Error while creating coupon: {}", e.getMessage());
            throw new CouponCreationException("Failed to create coupon"); // Custom exception for consistency
        }
    }


    // 조건별 멤버 가져오기
    @Override
    public List<Long> getMemberIdsByRecipientType(CouponCreationAndAssignRequestDTO request) {
        List<Long> memberIds = new ArrayList<>();
        int page = 0;
        int size = 100;
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


    // String - > MemberGrade
    public MemberGrade convertGrade(String grade) {
        try {
            return MemberGrade.valueOf(grade.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRecipientTypeException("유효하지 않은 등급: " + grade);
        }
    }


    // 회원가입 성공시 코드
    @Override
    @Transactional
    public List<CouponAssignResponseDTO> issueWelcomeCoupon(Long memberId) {
        List<CouponAssignResponseDTO> responses = new ArrayList<>();
        boolean hasError = false;

        try {
            processWelcomePolicies(memberId, responses);
        } catch (Exception e) {
            log.error("Error during 'Welcome' policies processing for member ID: {}", memberId, e);
            hasError = true;
        }

        try {
            processFirstComeCoupon(memberId, responses);
        } catch (Exception e) {
            log.error("Error during 'First Come' coupon processing for member ID: {}", memberId, e);
            hasError = true;
        }

        if (hasError) {
            throw new CouponIssueWelcomeException("Some policies failed during coupon issuance.");
        }

        return responses;
    }

    public void processWelcomePolicies(Long memberId, List<CouponAssignResponseDTO> responses) {
        try {
            List<CouponPolicyResponseDTO> couponPolicies = couponPolicyService.searchCouponPoliciesByName("Welcome");
            if (couponPolicies.isEmpty()) {
                log.warn("No 'Welcome' coupon policies found.");
            } else {
                for (CouponPolicyResponseDTO policy : couponPolicies) {
                    CouponCreationAndAssignRequestDTO request = new CouponCreationAndAssignRequestDTO(
                            "회원가입" + policy.getName(), policy.getId(), Collections.singletonList(memberId), "개인별");
                    responses.addAll(createAndAssignCouponsLogic(request));
                }
            }
        } catch (Exception e) {
            log.info("Error during 'Welcome' policies processing for member ID: {}", memberId, e);
            throw new ProcessWelcomeException("Error in processWelcomePolicies");
        }
    }


    public void processFirstComeCoupon(Long memberId, List<CouponAssignResponseDTO> responses) {
        Optional<Coupon> optionalCoupon = couponRepository.findAndLockFirstByName("회원가입 선착순 쿠폰");
        if (optionalCoupon.isEmpty()) {
            log.info("No available '회원가입 선착순 쿠폰' for member ID: {}", memberId);
            return;
        }
        Coupon coupon = optionalCoupon.get();
        try {
                CouponAssignRequestDTO request = new CouponAssignRequestDTO(coupon.getId(), memberId);
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE_NAME,
                        RabbitConfig.ROUTING_KEY,
                        request
                );
                log.info("Welcome coupon assign request sent to MQ for coupon ID: {}, member ID: {}", coupon.getId(), memberId);
                responses.add(new CouponAssignResponseDTO(coupon.getId(), "Welcome coupon assigned successfully."));
        } catch (Exception e) {
            log.info("Error during 'First Come' coupon processing for member ID: {}", memberId, e);
            throw new ProcessFirstcomeException("Error in processFirstComeCoupon");
        }
    }


    // 생일 쿠폰 발급
    @Override
    @Transactional
    public BulkAssignResponseDTO assignMonthlyBirthdayCoupons() {
        LocalDate today = getCurrentDate();
        int currentMonth = today.getMonthValue();
        boolean isLeapYear = Year.from(today).isLeap();

        log.info("Checking for members with birthdays in month: {}", currentMonth);

        List<MemberDto> membersWithBirthdays = fetchMembersByMonth(currentMonth);
        log.info("{} members with birthdays found.", membersWithBirthdays.size());

        if (membersWithBirthdays.isEmpty()) {
            log.info("No members with birthdays in month: {}", currentMonth);
            return new BulkAssignResponseDTO(false, 0);
        }

        List<CouponPolicy> birthdayPolicies = fetchBirthdayCouponPolicies(currentMonth, isLeapYear);
        if (birthdayPolicies.isEmpty()) {
            log.warn("No coupon policies found for month: {}, Leap Year: {}", currentMonth, isLeapYear);
            return new BulkAssignResponseDTO(false, 0);
        }

        log.info("Using coupon policies: {}", birthdayPolicies.stream().map(CouponPolicy::getName).toList());

        int totalIssuedCoupons = 0;
        for (CouponPolicy policy : birthdayPolicies) {
            log.info("Processing coupon policy: {}", policy.getName());
            List<CouponAssignRequestDTO> couponRequests = membersWithBirthdays.stream()
                    .map(member -> new CouponAssignRequestDTO(null, member.getId()))
                    .toList();
            int issuedCount = assignBirthdayCoupons(couponRequests, policy);
            totalIssuedCoupons += issuedCount;
        }

        return new BulkAssignResponseDTO(true, totalIssuedCoupons);
    }

    // 현재 날짜를 반환하는 메서드
    protected LocalDate getCurrentDate() {
        return LocalDate.now();
    }



    private List<MemberDto> fetchMembersByMonth(int month) {
        int page = 0;
        int size = 100;
        List<MemberDto> members = new ArrayList<>();
        Page<MemberDto> memberPage;

        do {
            memberPage = memberFeignClient.getMembers(null, null, page, size, null, "ASC");
            members.addAll(memberPage.getContent().stream()
                    .filter(member -> {
                        LocalDate birthDate = member.getBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        log.info("Member ID: {}, Birth Date: {}, Month Value: {}", member.getId(), birthDate, birthDate.getMonthValue());
                        return birthDate.getMonthValue() == month;
                    })
                    .toList());
            page++;
        } while (memberPage.hasNext());

        log.info("Fetched {} members in page {}.", memberPage.getContent().size(), page);
        log.info("Filtered members with birthdays in month {}: {}", month, members.size());

        return members;
    }


    private List<CouponPolicy> fetchBirthdayCouponPolicies(int month, boolean isLeapYear) {
        String basePattern = "생일 축하 쿠폰 - " + month + "월";
        String namePattern;

        if (month == 2) { // 2월일 경우 윤년/평년 추가
            String yearType = isLeapYear ? "(윤년)" : "(평년)";
            namePattern = basePattern + " " + yearType;
        } else {
            namePattern = basePattern; // 일반 월은 추가 텍스트 없음
        }

        log.info("Fetching coupon policies with name pattern: {}", namePattern);
        return couponPolicyRepository.findByNameContaining(namePattern);
    }



    public int assignBirthdayCoupons(List<CouponAssignRequestDTO> requests, CouponPolicy policy) {
        CouponCreationAndAssignRequestDTO requestDTO = new CouponCreationAndAssignRequestDTO(
                "생일 축하 쿠폰",
                policy.getId(),
                requests.stream().map(CouponAssignRequestDTO::getMemberId).toList(),
                "개인별"
        );

        try {
            // 쿠폰 생성 및 발급
            List<CouponAssignResponseDTO> responses = createAndAssignCouponsLogic(requestDTO);
            return responses.size(); // 발급된 쿠폰 개수 반환
        } catch (Exception e) {
            log.error("Failed to assign birthday coupons for policy: {}", policy.getName(), e);
            return 0; // 실패 시 0 반환
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




    // 결제 가능한 쿠폰 목록
    @Override
    @Transactional(readOnly = true)
    public List<AvailableCouponResponseDTO> getAvailableCoupons(Long userId, Long bookId, Long amount) {
        // 단일 책 ID로 결제 가능한 쿠폰 조회
        List<Coupon> availableCoupons = couponRepository.findAvailableCoupons(userId, amount, bookId);

        // 결과를 DTO로 매핑
        return availableCoupons.stream()
                .map(coupon -> new AvailableCouponResponseDTO(
                        coupon.getId(),
                        coupon.getName(),
                        coupon.getCouponPolicy().getCouponDiscountRate(),
                        coupon.getCouponPolicy().getCouponDiscountAmount(),
                        coupon.getCouponPolicy().getCouponMaxAmount(),
                        coupon.getCouponExpiryDate()
                ))
                .toList();

    }


    @Override
    @Transactional(readOnly = true)
    public Long applyCoupon(Long couponId, Long paymentAmount) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));

        CouponPolicy policy = coupon.getCouponPolicy();

        log.info("Applying coupon: {}, Policy: {}", coupon.getName(), policy.getName());

        if (policy.getCouponDiscountAmount() != null) {
            // 고정 할인 금액 방식
            Long discountAmount = policy.getCouponDiscountAmount();
            return Math.min(discountAmount, policy.getCouponMaxAmount());
        } else if (policy.getCouponDiscountRate() != null) {
            // 할인율 방식 (소수점 처리)
            Long calculatedDiscount = paymentAmount * policy.getCouponDiscountRate().multiply(new BigDecimal(100)).longValue() / 100;
            return Math.min(calculatedDiscount, policy.getCouponMaxAmount());
        }

        throw new IllegalArgumentException("Invalid coupon discount policy");
    }
















    // 쿠폰 삭제 (쿠폰 아이디) -(미사용)
    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE);
        }
        if (couponRepository.getReferenceById(id).getMemberId() != null) {
            throw new CouponAlreadyAssignedException("Coupon is already assigned");
        }
        couponRepository.deleteById(id);
    }


    // 쿠폰 단일 발급 (쿠폰 아이디, 회원 아이디) -(미사용)
    @Override
    @Transactional
    public CouponAssignResponseDTO assignCoupon(CouponAssignRequestDTO request) {
        // 1. 쿠폰 조회
        Coupon coupon = couponRepository.findById(request.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_FOUND_MESSAGE));

        if (coupon.getMemberId() != null) {
            throw new CouponAlreadyAssignedException(COUPON_ALREADY_ASSIGNED_MESSAGE);
        }

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

}
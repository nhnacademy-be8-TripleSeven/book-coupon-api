package com.nhnacademy.bookapi.repository.querydsl.coupon;

import com.nhnacademy.bookapi.entity.*;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponRepositoryImpl extends QuerydslRepositorySupport implements CouponRepositoryCustom {

    public CouponRepositoryImpl() {
        super(Coupon.class);
    }

    @Override
    public List<Coupon> findAvailableCoupons(Long memberId, Long amount, Long bookId) {
        QCoupon coupon = QCoupon.coupon;
        QBookCoupon bookCoupon = QBookCoupon.bookCoupon;
        QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;
        QBookCategory bookCategory = QBookCategory.bookCategory;

        // 서브쿼리: 해당 책과 연결된 카테고리 ID 조회
        var categoryIds = JPAExpressions.select(bookCategory.category.id)
                .from(bookCategory)
                .where(bookCategory.book.id.eq(bookId));

        // 메인 쿼리
        return from(coupon)
                .where(
                        coupon.memberId.eq(memberId), // 멤버 ID가 일치해야 함
                        coupon.couponStatus.eq(CouponStatus.NOTUSED), // 사용되지 않은 쿠폰
                        coupon.couponPolicy.couponMinAmount.loe(amount), // 최소 금액 조건
                        // 북 쿠폰 조건
                        coupon.bookCoupon.isNotNull().and(
                                coupon.id.in(
                                        JPAExpressions.select(bookCoupon.coupon.id)
                                                .from(bookCoupon)
                                                .where(bookCoupon.book.id.eq(bookId))
                                )
                        ).or(
                                // 카테고리 쿠폰 조건
                                coupon.categoryCoupon.isNotNull().and(
                                        coupon.id.in(
                                                JPAExpressions.select(categoryCoupon.coupon.id)
                                                        .from(categoryCoupon)
                                                        .where(categoryCoupon.category.id.in(categoryIds))
                                        )
                                )
                        ).or(
                                // 일반 쿠폰 조건 (둘 다 null)
                                coupon.bookCoupon.isNull().and(coupon.categoryCoupon.isNull())
                        )
                )
                .fetch();
    }
}
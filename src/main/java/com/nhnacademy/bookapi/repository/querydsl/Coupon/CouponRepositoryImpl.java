package com.nhnacademy.bookapi.repository.querydsl.Coupon;

import com.nhnacademy.bookapi.entity.Coupon;
import com.nhnacademy.bookapi.entity.QBookCategory;
import com.nhnacademy.bookapi.entity.QBookCoupon;
import com.nhnacademy.bookapi.entity.QCategoryCoupon;
import com.nhnacademy.bookapi.entity.QCoupon;
import com.nhnacademy.bookapi.entity.CouponStatus;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponRepositoryImpl extends QuerydslRepositorySupport implements CouponRepositoryCustom {

    public CouponRepositoryImpl() {
        super(Coupon.class); // 관리 대상 엔티티 설정
    }

    @Override
    public List<Coupon> findAvailableCoupons(Long memberId, Long amount, List<Long> bookIds) {
        QCoupon coupon = QCoupon.coupon;
        QBookCoupon bookCoupon = QBookCoupon.bookCoupon;
        QCategoryCoupon categoryCoupon = QCategoryCoupon.categoryCoupon;
        QBookCategory bookCategory = QBookCategory.bookCategory;

        // 서브쿼리: 도서에 해당하는 카테고리 ID 조회
        var categoryIds = JPAExpressions.select(bookCategory.category.id)
                .from(bookCategory)
                .where(bookCategory.book.id.in(bookIds));

        // 메인 쿼리: 조건에 맞는 쿠폰 조회
        return from(coupon)
                .where(coupon.memberId.eq(memberId)
                        .and(coupon.couponStatus.eq(CouponStatus.NOTUSED))
                        .and(coupon.couponPolicy.couponMinAmount.loe(amount))
                        .and(
                                coupon.id.in(
                                                JPAExpressions.select(bookCoupon.coupon.id)
                                                        .from(bookCoupon)
                                                        .where(bookCoupon.book.id.in(bookIds))
                                        )
                                        .or(
                                                coupon.id.in(
                                                        JPAExpressions.select(categoryCoupon.coupon.id)
                                                                .from(categoryCoupon)
                                                                .where(categoryCoupon.category.id.in(categoryIds))
                                                )
                                        )
                                        .or(
                                                coupon.bookCoupon.isNull()
                                                        .and(coupon.categoryCoupon.isNull())
                                        )
                        )
                )
                .fetch();
    }
}

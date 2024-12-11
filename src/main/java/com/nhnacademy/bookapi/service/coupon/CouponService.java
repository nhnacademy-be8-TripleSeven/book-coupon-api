package com.nhnacademy.bookapi.service.coupon;

import com.nhnacademy.bookapi.dto.coupon.CouponCreateDTO;
import com.nhnacademy.bookapi.dto.coupon.CouponResponseDTO;

public interface CouponService {

    CouponResponseDTO createCoupon(CouponCreateDTO request);

    CouponResponseDTO updateCoupon(long id, CouponCreateDTO request);

    void deleteCoupon(long id);

}

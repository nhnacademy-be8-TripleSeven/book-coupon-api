//package com.nhnacademy.bookapi.dto.coupon;
//
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import com.nhnacademy.bookapi.entity.Coupon;
//import com.nhnacademy.bookapi.entity.CouponPolicy;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.io.Serializable;
//import java.time.LocalDate;
//
//@AllArgsConstructor
//@Getter
//public class CouponAssignResponseDTO {
//    private Long couponId;
//    private String name;
//    private String couponPolicyName;
//    private Long memberId;
//    private LocalDate couponIssueDate;
//    private LocalDate couponExpiryDate;
//    private String couponStatus;
//
//    public CouponAssignResponseDTO(Coupon coupon) {
//        this.couponId = coupon.getId();
//        this.name = coupon.getName();
//        this.couponPolicyName = coupon.getCouponPolicy().getName();
//        this.memberId = coupon.getMemberId();
//        this.couponIssueDate = coupon.getCouponIssueDate();
//        this.couponExpiryDate = coupon.getCouponExpiryDate();
//        this.couponStatus = coupon.getCouponStatus().name();
//    }
//}
//

package com.nhnacademy.bookapi.dto.coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CouponAssignResponseDTO {
    private Long couponId;        // 쿠폰 ID
    private String statusMessage; // 상태 메시지 (예: 성공/실패 메시지)
}

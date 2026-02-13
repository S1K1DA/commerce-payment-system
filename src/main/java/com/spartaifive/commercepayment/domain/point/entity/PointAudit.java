package com.spartaifive.commercepayment.domain.point.entity;

import com.spartaifive.commercepayment.domain.order.entity.Order;
import com.spartaifive.commercepayment.domain.payment.entity.Payment;
import com.spartaifive.commercepayment.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "point_audits")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PointAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //TODO: 정말 여기에 모든게 ManyToOne이 맞나?
    //TODO: 정말 여기에 nullable이 맞는지?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "order_id")
    Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "payment_id")
    Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "point_id")
    Point point;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PointAuditType pointAuditType;

    // TODO: 왜 nullable인지 설명하기
    @NotNull
    @Column(precision = 10, scale = 2, nullable = true)
    BigDecimal amount;

    @NotNull
    @CreatedDate
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;
}

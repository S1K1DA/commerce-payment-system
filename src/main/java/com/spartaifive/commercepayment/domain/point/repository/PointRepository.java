package com.spartaifive.commercepayment.domain.point.repository;

import com.spartaifive.commercepayment.domain.point.entity.Point;
import com.spartaifive.commercepayment.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findPointByOwnerUser(User ownerUser);

    List<Point> findPointByOwnerUser_Id(Long ownerUserId);

    @Query(
            "SELECT p FROM Point p WHERE p.pointStatus = com.spartaifive.commercepayment.domain.point.entity.PointStatus.CAN_BE_SPENT " +
            "AND p.ownerUser.id = :ownerUserId " +
            // "AND p.pointRemaining > 0 " +
            "order by p.createdAt ASC"
    )
    List<Point> findPointsThatCanBeSpentSortedByCreatedAt(@Param("ownerUserId") Long ownerUserId);
}

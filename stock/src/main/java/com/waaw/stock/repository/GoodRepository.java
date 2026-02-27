package com.waaw.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.waaw.stock.domain.Good;

public interface GoodRepository extends JpaRepository<Good, Long> {

    @Modifying
    @Query("UPDATE Good g SET g.quantity = g.quantity - :quantity  WHERE g.id = :id AND g.quantity >= :quantity")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}

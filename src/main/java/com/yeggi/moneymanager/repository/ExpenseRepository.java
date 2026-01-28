package com.yeggi.moneymanager.repository;

import com.yeggi.moneymanager.entity.ExpenseEntity;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // Find all expenses by profile ID ordered by date descending
    //select * from tbl_expenses where profile_id = ? order by date desc
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Find top 5 expenses by profile ID ordered by date descending
    //select * from tbl_expenses where profile_id = ? order by date desc limit
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // Find total expenses amount by profile ID
    @Query("select SUM(e.amount) from ExpenseEntity e where e.profile.id=:profileId")
    BigDecimal findTotalExpensesByProfileId(@Param("profileId") Long profileId);

    // Search expenses by profile ID, date range, and keyword in name (case in-sensitive)
    //select * from tbl_expenses where profile_id = ? and date between ? and ? and name like %?%
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    // Search expenses by profile ID and date range
    //select * from tbl_expenses where profile_id = ? and date between ? and ?
    List <ExpenseEntity> findByProfileIdAndDateBetween(
            Long profileId, LocalDate startDate, LocalDate endDate);

    // Search expenses by profile ID and exact date
    //select * from tbl_expenses where profile_id = ? and date = ?
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);
}

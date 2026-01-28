package com.yeggi.moneymanager.repository;

import com.yeggi.moneymanager.entity.ExpenseEntity;
import com.yeggi.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // Find all incomes by profile ID ordered by date descending
    //select * from tbl_incomes where profile_id = ? order by date desc
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Find top 5 incomes by profile ID ordered by date descending
    //select * from tbl_incomes where profile_id = ? order by date desc limit 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    // Find total income amount by profile ID
    @Query("select SUM(e.amount) from IncomeEntity e where e.profile.id=:profileId")
    BigDecimal findTotalIncomesByProfileId(@Param("profileId") Long profileId);

    // Search expenses by profile ID, date range, and keyword in name (case in-sensitive)
    //select * from tbl_incomes where profile_id = ? and date between ? and ? and name like %?%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    // Search expenses by profile ID and date range
    //select * from tbl_incomes where profile_id = ? and date between ? and ?
    List <IncomeEntity> findByProfileIdAndDateBetween(
            Long profileId, LocalDate startDate, LocalDate endDate);
}

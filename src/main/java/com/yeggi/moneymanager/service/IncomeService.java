package com.yeggi.moneymanager.service;

import com.yeggi.moneymanager.dto.ExpenseDTO;
import com.yeggi.moneymanager.dto.IncomeDTO;
import com.yeggi.moneymanager.entity.CategoryEntity;
import com.yeggi.moneymanager.entity.ExpenseEntity;
import com.yeggi.moneymanager.entity.IncomeEntity;
import com.yeggi.moneymanager.entity.ProfileEntity;
import com.yeggi.moneymanager.repository.CategoryRepository;
import com.yeggi.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    //save new income
    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newIncomeEntity = toEntity(incomeDTO, categoryEntity, profileEntity);
        newIncomeEntity = incomeRepository.save(newIncomeEntity);
        return toDTO(newIncomeEntity);
    }

    //delete income by id
    public void deleteIncome(Long incomeId) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        IncomeEntity incomeEntity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if(!incomeEntity.getProfile().getId().equals(profileEntity.getId())) {
            throw new RuntimeException("You are not authorized to delete this income");
        }
        incomeRepository.delete(incomeEntity);
    }

    //Get latest 5 incomes for current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<IncomeEntity> incomeEntities = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profileEntity.getId());
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    //Get total incomes amount for current user
    public BigDecimal getTotalIncomesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        BigDecimal totalIncomes = incomeRepository.findTotalIncomesByProfileId(profileEntity.getId());
        return totalIncomes != null ? totalIncomes : BigDecimal.ZERO;
    }

    //get all incomes for current month for current user
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();

        //first and last date of current month
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate startOfMonth = today.withDayOfMonth(1);
        java.time.LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<IncomeEntity> incomeEntities = incomeRepository.findByProfileIdAndDateBetween(profileEntity.getId(), startOfMonth, endOfMonth);
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    //filter incomes by date range and keyword
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        List<IncomeEntity> incomeEntities=incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profileEntity.getId(),startDate,endDate,keyword,sort);
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    //helper methods
    private IncomeEntity toEntity(IncomeDTO incomeDTO, CategoryEntity categoryEntity, ProfileEntity profileEntity) {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .amount(incomeDTO.getAmount())
                .iconUrl(incomeDTO.getIconUrl())
                .date(incomeDTO.getDate())
                .category(categoryEntity)
                .profile(profileEntity)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .amount(incomeEntity.getAmount())
                .iconUrl(incomeEntity.getIconUrl())
                .date(incomeEntity.getDate())
                .categoryId(incomeEntity.getCategory()!=null ? incomeEntity.getCategory().getId() : null)
                .categoryName(incomeEntity.getCategory()!=null ? incomeEntity.getCategory().getName() : "N/A")
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}

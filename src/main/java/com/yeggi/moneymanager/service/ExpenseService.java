package com.yeggi.moneymanager.service;

import com.yeggi.moneymanager.dto.ExpenseDTO;
import com.yeggi.moneymanager.entity.CategoryEntity;
import com.yeggi.moneymanager.entity.ExpenseEntity;
import com.yeggi.moneymanager.entity.ProfileEntity;
import com.yeggi.moneymanager.repository.CategoryRepository;
import com.yeggi.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    //save new expense
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseEntity newExpenseEntity = toEntity(expenseDTO, categoryEntity, profileEntity);
        newExpenseEntity = expenseRepository.save(newExpenseEntity);
        return toDTO(newExpenseEntity);
    }

    //delete expense by id
    public void deleteExpense(Long expenseId) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if(!expenseEntity.getProfile().getId().equals(profileEntity.getId())) {
            throw new RuntimeException("You are not authorized to delete this expense");
        }
        expenseRepository.delete(expenseEntity);
    }

    //Get latest 5 expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntities = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profileEntity.getId());
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    //Get total expense amount for current user
    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        BigDecimal totalExpenses = expenseRepository.findTotalExpensesByProfileId(profileEntity.getId());
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    //get all expenses for current month for current user
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();

        //first and last date of current month
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());


        List<ExpenseEntity> expenseEntities = expenseRepository.findByProfileIdAndDateBetween(profileEntity.getId(), startOfMonth, endOfMonth);
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    //filter expenses by date range and keyword
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntities=expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profileEntity.getId(),startDate,endDate,keyword,sort);
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    //get expenses by exact date
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> expenseEntities=expenseRepository.findByProfileIdAndDate(profileId,date);
        return expenseEntities.stream().map(this::toDTO).toList();
    }

    //helper methods
    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, CategoryEntity categoryEntity, ProfileEntity profileEntity) {
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .amount(expenseDTO.getAmount())
                .iconUrl(expenseDTO.getIconUrl())
                .date(expenseDTO.getDate())
                .category(categoryEntity)
                .profile(profileEntity)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity expenseEntity) {
        return ExpenseDTO.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .amount(expenseEntity.getAmount())
                .iconUrl(expenseEntity.getIconUrl())
                .date(expenseEntity.getDate())
                .categoryId(expenseEntity.getCategory()!=null ? expenseEntity.getCategory().getId() : null)
                .categoryName(expenseEntity.getCategory()!=null ? expenseEntity.getCategory().getName() : "N/A")
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .build();
    }
}

package com.yeggi.moneymanager.controller;

import com.yeggi.moneymanager.dto.FilterDTO;
import com.yeggi.moneymanager.service.ExpenseService;
import com.yeggi.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO) {
        // Implementation for filtering transactions based on criteria
        LocalDate startDate = filterDTO.getStartDate()!= null ? filterDTO.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDTO.getEndDate() != null ? filterDTO.getEndDate() : LocalDate.now();
        String keyword = filterDTO.getKeyword() != null ? filterDTO.getKeyword() : "";
        String sortField = filterDTO.getSortField() != null ? filterDTO.getSortField() : "date";
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(filterDTO.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortField);

        if ("income".equalsIgnoreCase(filterDTO.getType())) {
            return ResponseEntity.ok(incomeService.filterIncomes(startDate, endDate, keyword, sort));
        } else if ("expense".equalsIgnoreCase(filterDTO.getType())) {
            return ResponseEntity.ok(expenseService.filterExpenses(startDate, endDate, keyword, sort));
        }else {
            return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'.");
        }
    }
}

package com.yeggi.moneymanager.controller;

import com.yeggi.moneymanager.dto.ExpenseDTO;
import com.yeggi.moneymanager.dto.IncomeDTO;
import com.yeggi.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO savedIncomeDTO = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedIncomeDTO);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getCurrentMonthIncomes() {
        List<IncomeDTO> incomeDTOS =incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomeDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}

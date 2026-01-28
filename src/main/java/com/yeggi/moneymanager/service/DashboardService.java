package com.yeggi.moneymanager.service;

import com.yeggi.moneymanager.dto.ExpenseDTO;
import com.yeggi.moneymanager.dto.IncomeDTO;
import com.yeggi.moneymanager.dto.RecentTransactionDTO;
import com.yeggi.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String,Object> getDashboardData(){
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        List<RecentTransactionDTO> recentTransactionDTOS= Stream.concat(latestIncomes.stream().map(incomeDTO -> RecentTransactionDTO.builder()
                .id(incomeDTO.getId())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .type("income")
                .name(incomeDTO.getName())
                .iconUrl(incomeDTO.getIconUrl())
                .profileId(profileEntity.getId())
                .createdAt(incomeDTO.getCreatedAt())
                .updatedAt(incomeDTO.getUpdatedAt())
                .build()),
                latestExpenses.stream().map(expenseDTO -> RecentTransactionDTO.builder()
                        .id(expenseDTO.getId())
                        .amount(expenseDTO.getAmount())
                        .date(expenseDTO.getDate())
                        .type("expense")
                        .name(expenseDTO.getName())
                        .iconUrl(expenseDTO.getIconUrl())
                        .profileId(profileEntity.getId())
                        .createdAt(expenseDTO.getCreatedAt())
                        .updatedAt(expenseDTO.getUpdatedAt())
                        .build())
                ).sorted((a, b)->{
                    int cmp=b.getDate().compareTo(a.getDate());
                    if(cmp==0 && b.getCreatedAt()!=null && a.getCreatedAt()!=null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
        }).collect(Collectors.toList());
        returnValue.put("totalBalance", incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomesForCurrentUser());
        returnValue.put("totalExpense", expenseService.getTotalExpensesForCurrentUser());
        returnValue.put("recent5Expenses", latestExpenses);
        returnValue.put("recent5Incomes", latestIncomes);
        returnValue.put("recentTransactions", recentTransactionDTOS);
        return returnValue;
    }
}

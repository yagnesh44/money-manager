package com.yeggi.moneymanager.service;

import com.yeggi.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final EmailService emailService;
    private final ProfileRepository profileRepository;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

//    @Scheduled(cron = "0 * * * * *", zone = "CST") // Every day at 10 PM
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: Sending daily income and expense reminders - sendDailyIncomeExpenseReminder()");
        profileRepository.findAll().forEach(profile -> {
            try {
                String to = profile.getEmail();
                String subject = "Daily Income and Expense Reminder";
                String body = String.format("Dear %s,\n\nThis is a friendly reminder to log your daily incomes and expenses. "
                                + "Keeping track of your finances helps you stay on top of your budget and achieve your financial goals.\n\n"
                                + "You can log your transactions here: %s\n\nBest regards,\nMoney Manager Team",
                        profile.getFullName(), frontendUrl);
                emailService.sendMail(to, subject, body);

            } catch (Exception e) {
                log.error("Failed to send daily reminder to {}: {}", profile.getEmail(), e.getMessage());
            }
        });
        log.info("Job finished: Daily reminder sent successfully to every user - sendDailyIncomeExpenseReminder()");
    }

//    @Scheduled(cron = "0 0 20 * * *", zone = "CST") // Every day at 8 PM
    public void sendDailyExpenseSummary() {
        log.info("Job started: Sending daily expense summary - sendDailyExpenseSummary()");
        profileRepository.findAll().forEach(profile -> {
            try {
                String to = profile.getEmail();
                String subject = "Daily Expense Summary";
                String body = String.format("Dear %s,\n\nHere is your expense summary for today:\n\nTotal Expenses: %s\n\n"
                                + "Keep tracking your expenses to manage your budget effectively.\n\nBest regards,\nMoney Manager Team",
                        profile.getFullName(), expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now()));
                emailService.sendMail(to, subject, body);
            } catch (Exception e) {
                log.error("Failed to send daily expense summary to {}: {}", profile.getEmail(), e.getMessage());
            }
        });
        log.info("Job finished: Daily expense summary sent successfully to every user - sendDailyExpenseSummary()");
    }

}

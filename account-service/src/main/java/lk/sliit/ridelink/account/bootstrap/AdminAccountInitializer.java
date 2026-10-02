package lk.sliit.ridelink.account.bootstrap;

import lk.sliit.ridelink.account.model.Account;
import lk.sliit.ridelink.account.model.AccountStatus;
import lk.sliit.ridelink.account.model.Role;
import lk.sliit.ridelink.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Locale;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminName;

    public AdminAccountInitializer(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:}") String adminEmail,
            @Value("${app.admin.password:}") String adminPassword,
            @Value("${app.admin.name:RideLink Administrator}") String adminName
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
    }

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            LOGGER.info("Admin bootstrap skipped because ADMIN_EMAIL or ADMIN_PASSWORD is not configured");
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (accountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return;
        }

        Instant now = Instant.now();
        Account account = new Account();
        account.setFullName(adminName.trim());
        account.setEmail(normalizedEmail);
        account.setPasswordHash(passwordEncoder.encode(adminPassword));
        account.setPhoneNumber("Not provided");
        account.setRole(Role.ADMIN);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        accountRepository.save(account);
        LOGGER.info("A bootstrap administrator account was created for {}", normalizedEmail);
    }
}

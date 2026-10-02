package lk.sliit.ridelink.account.service;

import lk.sliit.ridelink.account.dto.AccountResponse;
import lk.sliit.ridelink.account.dto.RegisterRequest;
import lk.sliit.ridelink.account.dto.UpdateProfileRequest;
import lk.sliit.ridelink.account.exception.BadRequestException;
import lk.sliit.ridelink.account.exception.ConflictException;
import lk.sliit.ridelink.account.exception.ResourceNotFoundException;
import lk.sliit.ridelink.account.model.Account;
import lk.sliit.ridelink.account.model.AccountStatus;
import lk.sliit.ridelink.account.model.Role;
import lk.sliit.ridelink.account.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new BadRequestException("Public registration cannot create an administrator");
        }

        String normalizedEmail = normalizeEmail(request.email());
        if (accountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email address is already registered");
        }

        Instant now = Instant.now();
        Account account = new Account();
        account.setFullName(request.fullName().trim());
        account.setEmail(normalizedEmail);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setPhoneNumber(request.phoneNumber().trim());
        account.setRole(request.role());
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(now);
        account.setUpdatedAt(now);

        return toResponse(accountRepository.save(account));
    }

    public AccountResponse getById(String accountId) {
        return toResponse(findAccount(accountId));
    }

    public AccountResponse updateProfile(
            String accountId,
            UpdateProfileRequest request
    ) {
        Account account = findAccount(accountId);
        account.setFullName(request.fullName().trim());
        account.setPhoneNumber(request.phoneNumber().trim());
        account.setUpdatedAt(Instant.now());
        return toResponse(accountRepository.save(account));
    }

    public AccountResponse updateStatus(String accountId, AccountStatus status) {
        Account account = findAccount(accountId);
        account.setStatus(status);
        account.setUpdatedAt(Instant.now());
        return toResponse(accountRepository.save(account));
    }

    public AccountResponse updateRole(String accountId, Role role) {
        Account account = findAccount(accountId);
        account.setRole(role);
        account.setUpdatedAt(Instant.now());
        return toResponse(accountRepository.save(account));
    }

    private Account findAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found with id: " + accountId
                ));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getFullName(),
                account.getEmail(),
                account.getPhoneNumber(),
                account.getRole(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}

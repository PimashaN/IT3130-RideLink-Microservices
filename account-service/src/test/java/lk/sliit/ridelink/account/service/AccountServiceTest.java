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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterPassengerAndHashPassword() {
        RegisterRequest request = new RegisterRequest(
                "Nimal Perera",
                "NIMAL@EXAMPLE.COM",
                "Password@123",
                "0771234567",
                Role.PASSENGER
        );
        when(accountRepository.existsByEmailIgnoreCase("nimal@example.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded-password");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId("acc-001");
            return account;
        });

        AccountResponse response = accountService.register(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account storedAccount = captor.getValue();

        assertThat(response.id()).isEqualTo("acc-001");
        assertThat(response.email()).isEqualTo("nimal@example.com");
        assertThat(response.role()).isEqualTo(Role.PASSENGER);
        assertThat(response.status()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(storedAccount.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(storedAccount.getPasswordHash()).isNotEqualTo(request.password());
    }

    @Test
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "Nimal Perera",
                "nimal@example.com",
                "Password@123",
                "0771234567",
                Role.DRIVER
        );
        when(accountRepository.existsByEmailIgnoreCase("nimal@example.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> accountService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email address is already registered");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldRejectPublicAdminRegistration() {
        RegisterRequest request = new RegisterRequest(
                "Fake Admin",
                "admin@example.com",
                "Password@123",
                "0771234567",
                Role.ADMIN
        );

        assertThatThrownBy(() -> accountService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Public registration cannot create an administrator");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldUpdateOwnProfile() {
        Account account = sampleAccount();
        when(accountRepository.findById("acc-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountResponse response = accountService.updateProfile(
                "acc-001",
                new UpdateProfileRequest("Updated Name", "0711111111")
        );

        assertThat(response.fullName()).isEqualTo("Updated Name");
        assertThat(response.phoneNumber()).isEqualTo("0711111111");
        verify(accountRepository).save(account);
    }

    @Test
    void shouldAllowAdminOperationToSuspendAccount() {
        Account account = sampleAccount();
        when(accountRepository.findById("acc-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountResponse response = accountService.updateStatus(
                "acc-001",
                AccountStatus.SUSPENDED
        );

        assertThat(response.status()).isEqualTo(AccountStatus.SUSPENDED);
    }

    @Test
    void shouldAllowAdminOperationToChangeRole() {
        Account account = sampleAccount();
        when(accountRepository.findById("acc-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountResponse response = accountService.updateRole("acc-001", Role.DRIVER);

        assertThat(response.role()).isEqualTo(Role.DRIVER);
    }

    @Test
    void shouldReturnNotFoundForUnknownAccount() {
        when(accountRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getById("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("missing");
    }

    private Account sampleAccount() {
        Account account = new Account();
        account.setId("acc-001");
        account.setFullName("Nimal Perera");
        account.setEmail("nimal@example.com");
        account.setPasswordHash("encoded-password");
        account.setPhoneNumber("0771234567");
        account.setRole(Role.PASSENGER);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(Instant.parse("2026-09-27T10:00:00Z"));
        account.setUpdatedAt(Instant.parse("2026-09-27T10:00:00Z"));
        return account;
    }
}

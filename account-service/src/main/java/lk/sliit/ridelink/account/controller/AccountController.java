package lk.sliit.ridelink.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.sliit.ridelink.account.dto.AccountResponse;
import lk.sliit.ridelink.account.dto.UpdateProfileRequest;
import lk.sliit.ridelink.account.dto.UpdateRoleRequest;
import lk.sliit.ridelink.account.dto.UpdateStatusRequest;
import lk.sliit.ridelink.account.security.AccountPrincipal;
import lk.sliit.ridelink.account.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Profile and account-status operations")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    @Operation(summary = "View the authenticated user's profile")
    public AccountResponse getMyProfile(
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        return accountService.getById(principal.getAccountId());
    }

    @PutMapping("/me")
    @Operation(summary = "Update the authenticated user's profile")
    public AccountResponse updateMyProfile(
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return accountService.updateProfile(principal.getAccountId(), request);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN') or #accountId == authentication.principal.accountId")
    @Operation(summary = "Retrieve an account (self or administrator)")
    @ApiResponse(responseCode = "403", description = "Not allowed to view this account")
    public AccountResponse getAccount(@PathVariable String accountId) {
        return accountService.getById(accountId);
    }

    @PatchMapping("/{accountId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change account status (administrator only)")
    @ApiResponse(responseCode = "403", description = "Administrator role required")
    public AccountResponse updateAccountStatus(
            @PathVariable String accountId,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        return accountService.updateStatus(accountId, request.status());
    }

    @PatchMapping("/{accountId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change account role (administrator only)")
    @ApiResponse(responseCode = "403", description = "Administrator role required")
    public AccountResponse updateAccountRole(
            @PathVariable String accountId,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return accountService.updateRole(accountId, request.role());
    }
}

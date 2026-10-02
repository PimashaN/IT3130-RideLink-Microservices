package lk.sliit.ridelink.account.security;

import lk.sliit.ridelink.account.model.Account;
import lk.sliit.ridelink.account.model.AccountStatus;
import lk.sliit.ridelink.account.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AccountPrincipal implements UserDetails {

    private final String accountId;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final AccountStatus status;

    public AccountPrincipal(
            String accountId,
            String email,
            String passwordHash,
            Role role,
            AccountStatus status
    ) {
        this.accountId = accountId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    public static AccountPrincipal from(Account account) {
        return new AccountPrincipal(
                account.getId(),
                account.getEmail(),
                account.getPasswordHash(),
                account.getRole(),
                account.getStatus()
        );
    }

    public String getAccountId() {
        return accountId;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }
}

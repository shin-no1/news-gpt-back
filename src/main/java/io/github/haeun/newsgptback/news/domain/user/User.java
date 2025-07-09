package io.github.haeun.newsgptback.news.domain.user;

import io.github.haeun.newsgptback.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    private boolean emailVerified;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public User(String email, String username, String encodedPw, boolean emailVerified, UserRole role) {
        this.email = email;
        this.username = username;
        this.password = encodedPw;
        this.emailVerified = emailVerified;
        this.role = role;
    }

    public User(Long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 정책
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 정책
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 정책
    }

    @Override
    public boolean isEnabled() {
        return emailVerified; // 이메일 인증된 사용자만 로그인 가능
    }
}

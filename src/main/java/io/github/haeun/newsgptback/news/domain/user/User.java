package io.github.haeun.newsgptback.news.domain.user;

import io.github.haeun.newsgptback.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public User(String email, String nickname, String encodedPw, UserStatus userStatus) {
        this.email = email;
        this.nickname = nickname;
        this.password = encodedPw;
        this.status = userStatus;
    }
}

package io.github.haeun.newsgptback.news.service;

import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.enums.UserStatus;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.domain.user.UserRepository;
import io.github.haeun.newsgptback.news.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 인증 코드를 생성하여 사용자에게 전송
     * naver.com 또는 kakao.com 도메인의 이메일만 허용
     * 생성된 코드는 Redis에 5분간 저장
     *
     * @param email 인증 코드를 받을 이메일 주소
     * @throws CustomException 허용되지 않는 이메일 도메인인 경우 발생
     */
    public void sendEmailCode(String email) {
        if (!email.endsWith("@naver.com") && !email.endsWith("@kakao.com")) {
            throw new CustomException(ErrorCode.EMAIL_DOMAIN_NOT_ALLOWED);
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        redisTemplate.opsForValue().set("email_code:" + email, code, Duration.ofMinutes(5));
        emailService.sendCode(email, code);
    }

    /**
     * 사용자가 입력한 이메일 인증 코드의 유효성을 검증
     * 인증 성공 시 해당 이메일에 대한 인증 상태를 Redis에 10분간 저장
     *
     * @param email     인증할 이메일 주소
     * @param inputCode 사용자가 입력한 인증 코드
     * @throws CustomException 인증 코드가 일치하지 않거나 만료된 경우 발생
     */
    public void verifyEmailCode(String email, String inputCode) {
        String key = "email_code:" + email;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null || !savedCode.equals(inputCode)) {
            throw new CustomException(ErrorCode.EMAIL_CODE_INVALID);
        }

        redisTemplate.delete(key);
        redisTemplate.opsForValue().set("email_verified:" + email, "true", Duration.ofMinutes(10));
    }

    /**
     * 새로운 사용자를 등록
     * 이메일 인증이 완료되어야 하며, 중복된 이메일이나 닉네임은 사용할 수 없음
     * 비밀번호는 암호화되어 저장
     *
     * @param request 회원가입 요청 정보 (이메일, 닉네임, 비밀번호)
     * @throws CustomException 이메일 미인증, 이메일 중복, 닉네임 중복 시 발생
     */
    public void signup(SignupRequest request) {
        if (!"true".equals(redisTemplate.opsForValue().get("email_verified:" + request.getEmail()))) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        String encodedPw = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(), request.getNickname(), encodedPw, UserStatus.ACTIVE);
        userRepository.save(user);

        redisTemplate.delete("email_verified:" + request.getEmail());
    }
}

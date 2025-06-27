package io.github.haeun.newsgptback.news.service;

import io.github.haeun.newsgptback.common.enums.EmailVerificationAction;
import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.enums.EmailVerificationStatus;
import io.github.haeun.newsgptback.common.enums.UserStatus;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.news.domain.emailVerificationLog.EmailVerificationLog;
import io.github.haeun.newsgptback.news.domain.emailVerificationLog.EmailVerificationLogRepository;
import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.domain.user.UserRepository;
import io.github.haeun.newsgptback.news.dto.EmailCodeVerifyRequest;
import io.github.haeun.newsgptback.news.dto.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
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
    private final EmailVerificationLogRepository emailVerificationLogRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 인증 코드를 생성하여 사용자에게 전송
     * naver.com 또는 kakao.com 도메인의 이메일만 허용
     * 생성된 코드는 Redis에 5분간 저장
     *
     * @param email 인증 코드를 받을 이메일 주소
     * @param request HTTP 요청 정보
     * @throws CustomException 허용되지 않는 이메일 도메인인 경우 발생
     */
    public void sendEmailCode(String email, HttpServletRequest request) {
        if (!email.endsWith("@naver.com") && !email.endsWith("@kakao.com")) {
            emailVerificationLogRepository.save(createFailLogForSendCode(email, ErrorCode.EMAIL_DOMAIN_NOT_ALLOWED, request));
            throw new CustomException(ErrorCode.EMAIL_DOMAIN_NOT_ALLOWED);
        }

        if (userRepository.existsByEmail(email)) {
            emailVerificationLogRepository.save(createFailLogForSendCode(email, ErrorCode.EMAIL_ALREADY_EXISTS, request));
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        redisTemplate.opsForValue().set("email_code:" + email, code, Duration.ofMinutes(5));
        emailService.sendCode(email, code);
        emailVerificationLogRepository.save(createSuccessLog(email, EmailVerificationAction.SEND_CODE, request));
    }

    /**
     * 사용자가 입력한 이메일 인증 코드의 유효성을 검증
     * 인증 성공 시 해당 이메일에 대한 인증 상태를 Redis에 10분간 저장
     *
     * @param verifyRequest 이메일과 인증 코드를 포함한 검증 요청
     * @param request HTTP 요청 정보
     * @throws CustomException 인증 코드가 일치하지 않거나 만료된 경우 발생
     */
    public void verifyEmailCode(EmailCodeVerifyRequest verifyRequest, HttpServletRequest request) {
        String key = "email_code:" + verifyRequest.getEmail();
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null || !savedCode.equals(verifyRequest.getCode())) {
            emailVerificationLogRepository.save(new EmailVerificationLog(
                    verifyRequest.getEmail(),
                    EmailVerificationAction.VERIFY_CODE,
                    EmailVerificationStatus.FAIL,
                    ErrorCode.EMAIL_CODE_INVALID.getMessage(),
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent")
            ));
            throw new CustomException(ErrorCode.EMAIL_CODE_INVALID);
        }

        redisTemplate.delete(key);
        redisTemplate.opsForValue().set("email_verified:" + verifyRequest.getEmail(), "true", Duration.ofMinutes(10));
        emailVerificationLogRepository.save(createSuccessLog(verifyRequest.getEmail(), EmailVerificationAction.SEND_CODE, request));
    }

    /**
     * 새로운 사용자를 등록
     * 이메일 인증이 완료되어야 하며, 중복된 이메일이나 닉네임은 사용할 수 없음
     * 비밀번호는 암호화되어 저장
     *
     * @param signupRequest 회원가입 요청 정보 (이메일, 닉네임, 비밀번호)
     * @param request HTTP 요청 정보
     * @throws CustomException 이메일 미인증, 이메일 중복, 닉네임 중복 시 발생
     */
    public void signup(SignupRequest signupRequest, HttpServletRequest request) {
        if (!"true".equals(redisTemplate.opsForValue().get("email_verified:" + signupRequest.getEmail()))) {
            emailVerificationLogRepository.save(createFailLogForSignup(signupRequest, ErrorCode.EMAIL_NOT_VERIFIED, request));
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            emailVerificationLogRepository.save(createFailLogForSignup(signupRequest, ErrorCode.EMAIL_ALREADY_EXISTS, request));
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByNickname(signupRequest.getNickname())) {
            emailVerificationLogRepository.save(createFailLogForSignup(signupRequest, ErrorCode.NICKNAME_ALREADY_EXISTS, request));
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        String encodedPw = passwordEncoder.encode(signupRequest.getPassword());
        User user = new User(signupRequest.getEmail(), signupRequest.getNickname(), encodedPw, UserStatus.ACTIVE);
        userRepository.save(user);

        redisTemplate.delete("email_verified:" + signupRequest.getEmail());
        emailVerificationLogRepository.save(createSuccessLog(signupRequest.getEmail(), EmailVerificationAction.SEND_CODE, request));
    }

    private EmailVerificationLog createSuccessLog(String email, EmailVerificationAction action, HttpServletRequest request) {
        return new EmailVerificationLog(
                email,
                action,
                EmailVerificationStatus.SUCCESS,
                "",
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
    }

    private EmailVerificationLog createFailLogForSendCode(String email, ErrorCode errorCode, HttpServletRequest request) {
        return new EmailVerificationLog(
                email,
                EmailVerificationAction.SIGN_UP,
                EmailVerificationStatus.FAIL,
                errorCode.getMessage(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
    }

    private EmailVerificationLog createFailLogForSignup(SignupRequest dto, ErrorCode errorCode, HttpServletRequest request) {
        return new EmailVerificationLog(
                dto.getEmail(),
                EmailVerificationAction.SIGN_UP,
                EmailVerificationStatus.FAIL,
                errorCode.getMessage(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
    }
}

package io.github.haeun.newsgptback.news.service;

import io.github.haeun.newsgptback.common.enums.EmailVerificationAction;
import io.github.haeun.newsgptback.common.enums.EmailVerificationStatus;
import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.enums.UserRole;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.common.util.JwtUtil;
import io.github.haeun.newsgptback.news.domain.emailVerificationLog.EmailVerificationLog;
import io.github.haeun.newsgptback.news.domain.emailVerificationLog.EmailVerificationLogRepository;
import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.domain.user.UserRepository;
import io.github.haeun.newsgptback.news.dto.EmailCodeVerifyRequest;
import io.github.haeun.newsgptback.news.dto.LoginRequest;
import io.github.haeun.newsgptback.news.dto.LoginResponse;
import io.github.haeun.newsgptback.news.dto.SignupRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EmailVerificationLogRepository emailVerificationLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

        if (userRepository.existsByUserId(signupRequest.getNickname())) {
            emailVerificationLogRepository.save(createFailLogForSignup(signupRequest, ErrorCode.NICKNAME_ALREADY_EXISTS, request));
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        String encodedPw = passwordEncoder.encode(signupRequest.getPassword());
        User user = new User(signupRequest.getEmail(), signupRequest.getNickname(), encodedPw, true, UserRole.USER);
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

    /**
     * 사용자 로그인 요청을 인증하고 액세스 토큰과 리프레시 토큰을 생성한 후,
     * 리프레시 토큰을 Redis에 7일간의 유효기간과 함께 저장
     *
     * @param request 사용자의 이메일과 비밀번호가 포함된 로그인 요청
     * @return 인증된 사용자의 액세스 토큰, 리프레시 토큰, 닉네임, 역할이 포함된 LoginResponse
     * @throws CustomException 이메일이나 비밀번호가 올바르지 않은 경우, 이메일이 인증되지 않은 경우 발생
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.isEmailVerified()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        String redisKey = "refresh:" + user.getId() + ":" + request.getDeviceId();
        redisTemplate.opsForValue().set(redisKey, refreshToken, 7, TimeUnit.DAYS);

        return new LoginResponse(
                accessToken,
                refreshToken,
                user.getUserId(),
                user.getRole().name()
        );
    }

    /**
     * 사용자 로그아웃 처리
     * Redis에 저장된 리프레시 토큰을 삭제하여 로그아웃 처리
     *
     * @param user     로그아웃할 사용자 정보
     * @param deviceId 사용자의 기기 식별자
     */
    public void logout(User user, String deviceId) {
        String key = "refresh:" + user.getId() + ":" + deviceId;
        redisTemplate.delete(key);
    }

    /**
     * 액세스 토큰과 리프레시 토큰을 재발급
     * 기존 리프레시 토큰의 유효성을 검증하고, 새로운 토큰 쌍을 발급
     *
     * @param refreshToken 기존 리프레시 토큰
     * @param deviceId     사용자의 기기 식별자
     * @return 새로 발급된 액세스 토큰, 리프레시 토큰, 사용자 정보가 포함된 LoginResponse
     * @throws CustomException 리프레시 토큰이 유효하지 않은 경우 발생, 사용자를 찾을 수 없는 경우 발생
     */
    public LoginResponse reissue(String refreshToken, String deviceId) {
        Claims claims = jwtUtil.parseClaims(refreshToken);
        Long userId = Long.parseLong(claims.getSubject());

        String redisKey = "refresh:" + userId + ":" + deviceId;
        String storedToken = redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        redisTemplate.opsForValue().set(redisKey, newRefreshToken, 7, TimeUnit.DAYS);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                user.getUserId(),
                user.getRole().name()
        );
    }
}

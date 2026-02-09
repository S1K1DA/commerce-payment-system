package com.spartaifive.commercepayment.domain.user.service;


import com.spartaifive.commercepayment.domain.user.dto.request.SignupRequest;
import com.spartaifive.commercepayment.domain.user.dto.response.SignupResponse;
import com.spartaifive.commercepayment.domain.user.entity.MembershipGrade;
import com.spartaifive.commercepayment.domain.user.entity.User;
import com.spartaifive.commercepayment.domain.user.repository.MembershipGradeRepository;
import com.spartaifive.commercepayment.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MembershipGradeRepository membershipGradeRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {

        // 이메일, 전화번호 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        // 기본 멤버십 조회
        MembershipGrade normalGrade = membershipGradeRepository.findByName("NORMAL")
                .orElseThrow(() -> new IllegalStateException("기본 멤버십(NORMAL)이 존재하지 않습니다."));

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 생성 (도메인 책임)
        User user = User.create(
                normalGrade,
                request.getName(),
                request.getEmail(),
                encodedPassword,
                request.getPhone()
        );

        // 저장
        User savedUser = userRepository.save(user);

        // Response DTO 반환
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt(),
                savedUser.getMembershipUpdatedDate()
        );
    }
}


package com.refill.account.service;

import com.refill.account.dto.request.HospitalJoinRequest;
import com.refill.account.dto.request.HospitalLoginRequest;
import com.refill.account.dto.request.LoginIdFindRequest;
import com.refill.account.dto.request.LoginPasswordRequest;
import com.refill.account.dto.request.MemberJoinRequest;
import com.refill.account.dto.request.MemberLoginRequest;
import com.refill.account.exception.AccountException;
import com.refill.global.entity.Message;
import com.refill.global.entity.Role;
import com.refill.global.exception.ErrorCode;
import com.refill.global.service.AmazonS3Service;
import com.refill.global.service.AmazonSESService;
import com.refill.hospital.entity.Hospital;
import com.refill.hospital.service.HospitalService;
import com.refill.member.entity.Member;
import com.refill.member.exception.MemberException;
import com.refill.member.service.MemberService;
import com.refill.security.util.JwtProvider;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {

    private final MemberService memberService;
    private final HospitalService hospitalService;
    private final AmazonS3Service amazonS3Service;
    private final AmazonSESService amazonSESService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    @Value("${jwt.token.secret}")
    private String secretKey;

    @Transactional(readOnly = true)
    public void isLoginIdDuplicated(String loginId) {

        boolean memberExists = memberService.existsByLoginId(loginId);
        boolean hospitalExists = hospitalService.existsByLoginId(loginId);

        if(memberExists || hospitalExists) {
            throw new AccountException(
                ErrorCode.LOGIN_ID_DUPLICATED.getCode(),
                ErrorCode.LOGIN_ID_DUPLICATED,
                ErrorCode.LOGIN_ID_DUPLICATED.getMessage()
            );
        }

    }

    @Transactional(readOnly = true)
    public void isEmailDuplicated(String email) {

        boolean memberExists = memberService.existsByEmail(email);
        boolean hospitalExists = hospitalService.existsByEmail(email);

        if(memberExists || hospitalExists) {
            throw new AccountException(
                ErrorCode.EMAIL_DUPLICATED.getCode(),
                ErrorCode.EMAIL_DUPLICATED,
                ErrorCode.EMAIL_DUPLICATED.getMessage()
            );
        }
    }

    @Transactional
    public void memberJoin(MemberJoinRequest memberJoinRequest, MultipartFile profileImg) {

        // 아이디 중복 검사
        isLoginIdDuplicated(memberJoinRequest.loginId());
        // 이메일 중복 검사
        isEmailDuplicated(memberJoinRequest.email());

        Member member = Member.from(memberJoinRequest);
        member.encodePassword(passwordEncoder.encode(member.getLoginPassword()));

        if(profileImg != null) {
            String profileAddress = amazonS3Service.uploadFile(profileImg);
            member.updateFileAddress(profileAddress);
        }

        memberService.save(member);

    }

    @Transactional
    public void hospitalJoin(HospitalJoinRequest hospitalJoinRequest, MultipartFile profileImg, MultipartFile regImg) {

        // 아이디 중복 검사
        isLoginIdDuplicated(hospitalJoinRequest.loginId());
        // 이메일 중복 검사
        isEmailDuplicated(hospitalJoinRequest.email());

        Hospital hospital = Hospital.from(hospitalJoinRequest);
        hospital.encodePassword(passwordEncoder.encode(hospital.getLoginPassword()));

        String profileAddress = amazonS3Service.uploadFile(profileImg);
        String regAddress = amazonS3Service.uploadFile(regImg);

        hospital.updateFileAddress(profileAddress);
        hospital.updateRegAddress(regAddress);

        hospitalService.save(hospital);
    }

    @Transactional(readOnly = true)
    public String memberLogin(MemberLoginRequest memberLoginRequest) {

        Member member = memberService.findByLoginId(memberLoginRequest.loginId());

        // 1. id가 없는 경우는 findByLoginId 에서 처리
        // 2. 패스워드가 일치하지 않음

        if(!passwordEncoder.matches(memberLoginRequest.loginPassword(), member.getLoginPassword())) {
            throw new MemberException(ErrorCode.INVALID_PASSWORD.getCode(), ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
        }

        return jwtProvider.createToken(member.getLoginId(), member.getRole(), secretKey);

    }

    @Transactional(readOnly = true)
    public String hospitalLogin(HospitalLoginRequest hospitalLoginRequest) {

        Hospital hospital = hospitalService.findByLoginId(hospitalLoginRequest.loginId());

        // 1. id가 없는 경우는 findByLoginId 에서 처리
        // 2. 패스워드가 일치하지 않음

        if(!passwordEncoder.matches(hospitalLoginRequest.loginPassword(), hospital.getLoginPassword())) {
            throw new MemberException(ErrorCode.INVALID_PASSWORD.getCode(), ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
        }

        // 3. 승인 대기중인 병원임
        if(hospital.getRole() == Role.ROLE_GUEST) {
            throw new MemberException(ErrorCode.OUTSTANDING_AUTHORIZATION.getCode(), ErrorCode.OUTSTANDING_AUTHORIZATION, ErrorCode.OUTSTANDING_AUTHORIZATION.getMessage());
        }

        return jwtProvider.createToken(hospital.getLoginId(), hospital.getRole(), secretKey);
    }


    @Transactional(readOnly = true)
    public String findMemberLoginId(LoginIdFindRequest loginIdFindRequest) {

        Member member = memberService.findByEmail(loginIdFindRequest.email());

        amazonSESService.sendLoginId(member.getEmail(), member.getLoginId());

        return Message.FIND_LOGIN_ID.getMessage();
    }

    @Transactional(readOnly = true)
    public String findHospitalLoginId(LoginIdFindRequest loginIdFindRequest) {

        Hospital hospital = hospitalService.findByEmail(loginIdFindRequest.email());

        amazonSESService.sendLoginId(hospital.getEmail(), hospital.getLoginId());

        return Message.FIND_LOGIN_ID.getMessage();
    }

    @Transactional
    public String findMemberPassword(LoginPasswordRequest loginPasswordRequest) {

        Member member = memberService.findByLoginIdAndEmail(loginPasswordRequest.loginId(), loginPasswordRequest.email());

        String newPassword = getTempPassword();

        amazonSESService.sendTempPassword(loginPasswordRequest.email(), newPassword);

        member.encodePassword(passwordEncoder.encode(newPassword));

        return Message.FIND_PASSWORD.getMessage();
    }

    @Transactional
    public String findHospitalPassword(LoginPasswordRequest loginPasswordRequest) {

        Hospital hospital = hospitalService.findByLoginIdAndEmail(loginPasswordRequest.loginId(), loginPasswordRequest.email());

        String newPassword = getTempPassword();

        amazonSESService.sendTempPassword(loginPasswordRequest.email(), newPassword);

        hospital.encodePassword(passwordEncoder.encode(newPassword));

        return Message.FIND_PASSWORD.getMessage();
    }

    private String getTempPassword() {
        // 숫자 0
        final int leftLimit = 48;
        // 소문자 'z'
        final int rightLimit = 122;
        final int passwordLength = 10;

        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                     .filter(x -> (x <= 57 || x >= 65) && (x <= 90 || x >= 97))
                     .limit(passwordLength)
                     .collect(StringBuilder::new, StringBuilder::appendCodePoint,
                         StringBuilder::append)
                     .toString();
    }
}

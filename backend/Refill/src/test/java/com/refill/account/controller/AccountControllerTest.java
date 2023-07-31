package com.refill.account.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.refill.account.dto.request.HospitalJoinRequest;
import com.refill.account.dto.request.HospitalLoginRequest;
import com.refill.account.dto.request.MemberJoinRequest;
import com.refill.account.dto.request.MemberLoginRequest;
import com.refill.global.entity.Role;
import com.refill.global.exception.ErrorCode;
import com.refill.hospital.entity.Hospital;
import com.refill.member.exception.MemberException;
import com.refill.util.ControllerTest;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AccountControllerTest extends ControllerTest {

    @MockBean
    BCryptPasswordEncoder passwordEncoder;
    @Test
    @DisplayName("멤버_회원가입_성공한다")
    void joinMemberTest() throws Exception {

        MemberJoinRequest memberJoinRequest = new MemberJoinRequest("member01", "pass01", "상원", "신상원", "hello", "01012345667",
            LocalDate.of(1995, 9, 24), "sangwon01@ssafy.com");

        MockMultipartFile memberJoinRequestPart = new MockMultipartFile("memberJoinRequest", "", "application/json", objectMapper.writeValueAsBytes(memberJoinRequest));

        MockMultipartFile profileImgPart =
            new MockMultipartFile("profileImg", "profile.png", "image/png", "<<png data>>".getBytes(
                StandardCharsets.UTF_8));

        //doNothing().when(accountService).memberJoin(any(), any()); 이게 없어도 되는건가 ...

        mockMvc.perform(
            multipart("/api/v1/account/member/join")
                .file(memberJoinRequestPart)
                .file(profileImgPart)
        ).andExpect(status().isNoContent())
         .andDo(
            document("account/member/join",
                preprocessRequest(prettyPrint()),
                requestParts(
                    partWithName("memberJoinRequest").description("회원가입 폼"),
                    partWithName("profileImg").description("프로필 사진").optional()
                ),
                requestPartFields("memberJoinRequest",
                    fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                    fieldWithPath("loginPassword").type(JsonFieldType.STRING).description("로그인 패스워드"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                    fieldWithPath("tel").type(JsonFieldType.STRING).description("핸드폰번호"),
                    fieldWithPath("birthDay").type(JsonFieldType.STRING).description("생일"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                )
            )
        );
    }

    @Test
    @DisplayName("병원_회원가입_성공한다")
    void joinHospitalTest() throws Exception {

        HospitalJoinRequest hospitalJoinRequest = new HospitalJoinRequest("hospital01", "pass01", "한방병원", "광산구", "12345", BigDecimal.ONE, BigDecimal.TEN, "02-123-4567", "hospital@ssafy.com");

        MockMultipartFile hospitalJoinRequestPart = new MockMultipartFile("hospitalJoinRequest", "", "application/json", objectMapper.writeValueAsBytes(hospitalJoinRequest));

        MockMultipartFile profileImgPart =
            new MockMultipartFile("profileImg", "profile.png", "image/png", "<<png data>>".getBytes(
                StandardCharsets.UTF_8));

        MockMultipartFile regImgPart =
            new MockMultipartFile("regImg", "reg.png", "image/png", "<<png data>>".getBytes(
                StandardCharsets.UTF_8));

        //doNothing().when(accountService).memberJoin(any(), any()); 이게 없어도 되는건가 ...

        mockMvc.perform(
                   multipart("/api/v1/account/hospital/join")
                       .file(hospitalJoinRequestPart)
                       .file(profileImgPart)
                       .file(regImgPart)
               ).andExpect(status().isNoContent())
               .andDo(
                   document("account/hospital/join",
                       preprocessRequest(prettyPrint()),
                       requestParts(
                           partWithName("hospitalJoinRequest").description("병원 회원가입 폼"),
                           partWithName("profileImg").description("프로필 사진"),
                           partWithName("regImg").description("병원 등록증 사진")
                       ),
                       requestPartFields("hospitalJoinRequest",
                           fieldWithPath("loginId").type(JsonFieldType.STRING).description("로그인 아이디"),
                           fieldWithPath("loginPassword").type(JsonFieldType.STRING).description("로그인 패스워드"),
                           fieldWithPath("name").type(JsonFieldType.STRING).description("병원 이름"),
                           fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                           fieldWithPath("postalCode").type(JsonFieldType.STRING).description("우편번호"),
                           fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                           fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                           fieldWithPath("tel").type(JsonFieldType.STRING).description("병원 번호"),
                           fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                       )
                   )
               );
    }

    @DisplayName("회원_로그인_성공한다")
    @Test
    void loginMember_success_with_correct_information() throws Exception {

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("loginId", "loginPassword");

        when(accountService.memberLogin(any())).thenReturn("{\"token\":\"access Token\"}");

        mockMvc.perform(
            post("/api/v1/account/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(memberLoginRequest))
        ).andExpect(status().isOk())
            .andDo(
                document("account/member/login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("loginId").description("로그인 아이디"),
                        fieldWithPath("loginPassword").description("로그인 패스워드")
                    ),
                    responseFields(
                        fieldWithPath("token").description("발급된 토큰")
                    ))
            );
    }

    @DisplayName("병원_로그인_성공한다")
    @Test
    void loginHospital_success_with_correct_information() throws Exception {

        HospitalLoginRequest hospitalLoginRequest = new HospitalLoginRequest("loginId", "loginPassword");

        when(accountService.hospitalLogin(any())).thenReturn("{\"token\":\"access Token\"}");

        mockMvc.perform(
                   post("/api/v1/account/hospital/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsBytes(hospitalLoginRequest))
               ).andExpect(status().isOk())
               .andDo(
                   document("account/hospital/login",
                       preprocessRequest(prettyPrint()),
                       preprocessResponse(prettyPrint()),
                       requestFields(
                           fieldWithPath("loginId").description("로그인 아이디"),
                           fieldWithPath("loginPassword").description("로그인 패스워드")
                       ),
                       responseFields(
                           fieldWithPath("token").description("발급된 토큰")
                       ))
               );
    }

    @DisplayName("승인_대기중인_병원_로그인_실패한다")
    @Test
    void loginHospital_fail_with_unauthorized_hospital() throws Exception {

        HospitalLoginRequest hospitalLoginRequest = new HospitalLoginRequest("loginId", "loginPassword");

        Hospital mockHospital = mock(Hospital.class);
        when(mockHospital.getRole()).thenReturn(Role.ROLE_GUEST);
        when(mockHospital.getLoginPassword()).thenReturn("loginPassword");

//        when(hospitalService.findByLoginId(any())).thenReturn(mockHospital);
//        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        /*
        TODO : mockMVC에서 ControllerAdvice를 사용하지 못하고 있음. 아마 ControllerTest에 붙어있는 @AutoConfigureMockMvc(addFilters = false) 떄문 일 가능성이 있음.
        AccountController accountController = new AccountController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
            .setControllerAdvice(new ExceptionControllerAdvice)
            .build();
        가 찾아본 해결책인데, 이도 동작하지 않음.. 그래서 강제로 error를 return하게함.
        postman으로 위의 method 실행 시 정상적으로 에러를 반환하고 있음
         */
        when(accountService.hospitalLogin(any())).thenThrow(new MemberException(ErrorCode.OUTSTANDING_AUTHORIZATION));
        mockMvc.perform(
                   post("/api/v1/account/hospital/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsBytes(hospitalLoginRequest))
               )
               .andExpect(status().is4xxClientError())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof MemberException));

    }

}
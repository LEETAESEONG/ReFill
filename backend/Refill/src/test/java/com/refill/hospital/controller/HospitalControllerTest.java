package com.refill.hospital.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.refill.doctor.entity.Doctor;
import com.refill.doctor.entity.EducationBackground;
import com.refill.doctor.entity.MajorArea;
import com.refill.global.entity.Role;
import com.refill.hospital.dto.request.HospitalInfoUpdateRequest;
import com.refill.hospital.dto.request.HospitalLocationRequest;
import com.refill.hospital.dto.request.HospitalSearchByLocationRequest;
import com.refill.hospital.dto.response.HospitalDetailResponse;
import com.refill.hospital.dto.response.HospitalResponse;
import com.refill.hospital.dto.response.HospitalSearchByLocationResponse;
import com.refill.hospital.entity.Hospital;
import com.refill.member.entity.Member;
import com.refill.review.entity.Review;
import com.refill.security.util.LoginInfo;
import com.refill.util.ControllerTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class HospitalControllerTest extends ControllerTest {

    private Hospital mockHospital;
    private Review mockReview;
    private Doctor mockDoctor;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        createMember();
        createDoctor();
        createHospital();
        createReview();
    }

    void createHospital() {
        Hospital hospital = Hospital.builder()
                                    .name("호인병원")
                                    .address("경기도 수원시")
                                    .role(Role.ROLE_HOSPITAL)
                                    .postalCode("12345")
                                    .tel("010-1234-1234")
                                    .registrationImg("reg_img")
                                    .id(1L)
                                    .hospitalProfileImg("pro_img")
                                    .longitude(BigDecimal.valueOf(37.5665))
                                    .latitude(BigDecimal.valueOf(126.9780))
                                    .loginId("hospital1")
                                    .email("hos_@naver.com")
                                    .build();
        hospital.addDoctor(mockDoctor);
        mockHospital = hospital;
    }

    void createDoctor() {
        Doctor doctor = Doctor.builder()
                              .licenseNumber("123-123")
                              .profileImg("doc_pro_img")
                              .name("doctor1")
                              .description("모발이식1")
                              .build();
        MajorArea majorArea = MajorArea.builder()
                                       .content("주전공은..")
                                       .build();
        EducationBackground educationBackground = EducationBackground.builder()
                                                                     .content("서울대 졸업")
                                                                     .build();
        educationBackground.setDoctor(doctor);
        majorArea.setDoctor(doctor);

        doctor.addEducationBackground(educationBackground);
        doctor.addMajorArea(majorArea);
        mockDoctor = doctor;
    }

    void createMember() {
        Member member = Member.builder()
                              .name("사용자1")
                              .id(1L)
                              .address("장덕동")
                              .nickname("호인")
                              .build();
        mockMember = member;
    }

    void createReview() {
        Review review = Review.builder()
                              .content("정말 좋았어요.")
                              .member(mockMember)
                              .doctor(mockDoctor)
                              .hospital(mockHospital)
                              .score(3)
                              .updatedAt(LocalDateTime.now())
                              .id(1L)
                              .build();
        mockDoctor.addReview(review);
        mockHospital.addReview(review);
        mockReview = review;
    }

    @Test
    @DisplayName("병원_검색_위도_경도_테스트")
    public void testSearchByLocation() throws Exception {
        HospitalSearchByLocationResponse mockResponse1 = new HospitalSearchByLocationResponse(
            mockHospital, 3d);
        HospitalSearchByLocationResponse mockResponse2 = new HospitalSearchByLocationResponse(
            mockHospital, 3d);

        when(hospitalService.searchByLocation(any(HospitalLocationRequest.class)))
            .thenReturn(Arrays.asList(mockResponse1, mockResponse2));

        this.mockMvc.perform(
                get("/api/v1/hospital/search/location")
                    .requestAttr(
                        "sLat", 37.5665
                    )
                    .param("sLat", "37.5665")
                    .param("sLng", "126.9780")
                    .param("eLat", "37.5665")
                    .param("eLng", "126.9780")
                    .param("curLat", "37.5665")
                    .param("curLng", "126.9780")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("hospital/searchByLocation",
                        responseFields(
                            fieldWithPath("[].hospitalResponse.hospitalId").description("병원아이디"),
                            fieldWithPath("[].hospitalResponse.name").description("병원이름"),
                            fieldWithPath("[].hospitalResponse.longitude").description("위도"),
                            fieldWithPath("[].hospitalResponse.latitude").description("경도"),
                            fieldWithPath("[].hospitalResponse.hospitalProfileImg").description(
                                "프로필이미지"),
                            fieldWithPath("[].hospitalResponse.bannerProfileImg").description(
                                "배너이미지"),
                            fieldWithPath("[].hospitalResponse.address").description("주소"),
                            fieldWithPath("[].hospitalResponse.tel").description("전화번호"),
                            fieldWithPath("[].hospitalResponse.score").description("리뷰 점수"),
                            fieldWithPath("[].hospitalResponse.email").description("이메일"),
                            fieldWithPath("[].dist").description("거리")
                        )
                    ));
    }

    @Test
    @DisplayName("병원_검색_키워드_테스트")
    public void testSearchByKeyword() throws Exception {

        HospitalResponse mockResponse1 = new HospitalResponse(mockHospital);
        HospitalResponse mockResponse2 = new HospitalResponse(mockHospital);
        when(hospitalService.searchByKeyword("호인병원", "경기도 수원시")).thenReturn(
            Arrays.asList(mockResponse1, mockResponse2));

        this.mockMvc.perform(get("/api/v1/hospital/search/keyword")
                .param("name", "호인병원")
                .param("addr", "경기도 수원시")
                .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("hospital/searchByKeyword",
                        responseFields(
                            fieldWithPath("[].hospitalId").description("병원아이디"),
                            fieldWithPath("[].name").description("병원이름"),
                            fieldWithPath("[].longitude").description("위도"),
                            fieldWithPath("[].latitude").description("경도"),
                            fieldWithPath("[].hospitalProfileImg").description("프로필이미지"),
                            fieldWithPath("[].bannerProfileImg").description("배너이미지"),
                            fieldWithPath("[].address").description("주소"),
                            fieldWithPath("[].tel").description("전화번호"),
                            fieldWithPath("[].score").description("리뷰 점수"),
                            fieldWithPath("[].email").description("이메일")
                        )
                    ));
    }

    @Test
    @DisplayName("병원_상세_조회_테스트")
    public void testGetHospitalDetail() throws Exception {

        HospitalDetailResponse mockResponse = new HospitalDetailResponse(mockHospital);
        when(hospitalService.getHospitalDetail(1L)).thenReturn(mockResponse);

        this.mockMvc.perform(get("/api/v1/hospital/{hospitalId}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(document("hospital/getHospitalDetail",
                        pathParameters(
                            parameterWithName("hospitalId").description("병원 아이디")
                        ),
                        responseFields(
                            fieldWithPath("hospitalResponse.address").description("병원 주소"),
                            fieldWithPath("hospitalResponse.hospitalId").description("병원 아이디"),
                            fieldWithPath("hospitalResponse.name").description("병원 이름"),
                            fieldWithPath("hospitalResponse.longitude").description("병원 위도"),
                            fieldWithPath("hospitalResponse.latitude").description("병원 경도"),
                            fieldWithPath("hospitalResponse.hospitalProfileImg").description(
                                "병원 프로필 이미지"),
                            fieldWithPath("hospitalResponse.bannerProfileImg").description(
                                "병원 배너 이미지"),
                            fieldWithPath("hospitalResponse.address").description("병원 주소"),
                            fieldWithPath("hospitalResponse.tel").description("병원 전화번호"),
                            fieldWithPath("hospitalResponse.score").description("병원 리뷰 평균 점수"),
                            fieldWithPath("hospitalResponse.email").description("병원 이메일"),

                            fieldWithPath("doctorResponses[].doctorId").description("의사 아이디"),
                            fieldWithPath("doctorResponses[].name").description("의사 이름"),
                            fieldWithPath("doctorResponses[].profileImg").description("의사 프로필 이미지"),
                            fieldWithPath("doctorResponses[].licenseNumber").description(
                                "의사 면허 번호"),
                            fieldWithPath("doctorResponses[].licenseImg").description("의사 면허 이미지"),
                            fieldWithPath("doctorResponses[].description").description("의사 약력"),
                            fieldWithPath("doctorResponses[].majorAreas.[]").description(
                                "주요 진료 분야"),
                            fieldWithPath("doctorResponses[].majorAreas.[]").description(
                                "주요 진료 분야"),
                            fieldWithPath("doctorResponses[].educationBackgrounds.[]").description(
                                "학력"),
                            fieldWithPath("doctorResponses[].educationBackgrounds.[]").description(
                                "학력"),

                            fieldWithPath("reviewResponses.[].reviewId").description("리뷰 아이디"),
                            fieldWithPath("reviewResponses.[].score").description("리뷰 점수"),
                            fieldWithPath("reviewResponses.[].content").description("리뷰 내용"),
                            fieldWithPath("reviewResponses.[].memberId").description("작성자 아이디"),
                            fieldWithPath("reviewResponses.[].nickname").description("작성자 닉네임"),
                            fieldWithPath("reviewResponses.[].doctorId").description("의사 아이디"),
                            fieldWithPath("reviewResponses.[].doctorName").description("의사 이름"),
                            fieldWithPath("reviewResponses.[].hospitalId").description("병원 아이디"),
                            fieldWithPath("reviewResponses.[].hospitalName").description("병원 이름"),
                            fieldWithPath("reviewResponses.[].updateDate").description(
                                "리뷰 업데이트 일시"),
                            fieldWithPath("reviewResponses.[].category").description("카테고리")
                        )));
    }

    @Test
    @DisplayName("병원_정보_수정_테스트")
    public void testModifyHospitalInfo() throws Exception {
        String requestBody = "{\"name\":\"Updated Hospital\", \"address\":\"Updated Address\"}";

        Long hospitalId = 1L;
        LoginInfo mockLoginInfo = new LoginInfo("sample_login_id", Role.ROLE_HOSPITAL);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new TestingAuthenticationToken(mockLoginInfo, null));
        SecurityContextHolder.setContext(securityContext);

        HospitalInfoUpdateRequest mockRequest = new HospitalInfoUpdateRequest("호인병원", "광주 광산구",
            "010-1234-1234", "qwer@naver.com", BigDecimal.valueOf(33.232),
            BigDecimal.valueOf(123.123), "43132");
        String requestContent = new ObjectMapper().writeValueAsString(mockRequest);

        MockMultipartFile mockRequestPart = new MockMultipartFile("hospitalInfoUpdateRequest",
            "request.json", "application/json", requestContent.getBytes());
        MockMultipartFile mockProfileImg = new MockMultipartFile("profileImg", "profile.jpg",
            "image/jpeg", "some-image".getBytes());
        MockMultipartFile mockBannerImg = new MockMultipartFile("bannerImg", "banner.jpg",
            "image/jpeg", "some-image".getBytes());
        MockMultipartFile mockRegistrationImg = new MockMultipartFile("registrationImg",
            "registration.jpg", "image/jpeg", "some-image".getBytes());

        // 실행 & 검증
        this.mockMvc.perform(multipart("/api/v1/hospital/{hospitalId}", hospitalId)
                .file(mockProfileImg)
                .file(mockBannerImg)
                .file(mockRegistrationImg)
                .file(mockRequestPart)
                .with(request -> {  // PUT 메서드로 설정
                    request.setMethod("PUT");
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andDo(document("hospital/modifyHospital",  // RestDocs를 사용한 문서화
                        pathParameters(
                            parameterWithName("hospitalId").description("병원 아이디")
                        )
                    ));
    }

}
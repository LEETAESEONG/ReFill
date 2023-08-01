package com.refill.hospital.controller;

import com.refill.hospital.dto.request.HospitalInfoUpdateRequest;
import com.refill.hospital.dto.response.HospitalDetailResponse;
import com.refill.hospital.dto.response.HospitalResponse;
import com.refill.hospital.dto.response.HospitalSearchByLocationResponse;
import com.refill.hospital.entity.Hospital;
import com.refill.hospital.service.HospitalService;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/hospital")
@RestController
@Slf4j
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/")
    public ResponseEntity<String> sayHello() {

        return ResponseEntity.ok()
                             .body("hello");
    }

    /* 현 지도에서 검색, 위도/경도 */
    @GetMapping("/search/location")
    public ResponseEntity<List<HospitalSearchByLocationResponse>> searchByLocation(
        @RequestParam(name = "lat") BigDecimal latitude,
        @RequestParam(name = "lng") BigDecimal longitude,
        @RequestParam(name = "z") Integer zoomLevel) {
        log.info("{}, {}, {}", latitude, longitude, zoomLevel);
        List<HospitalSearchByLocationResponse> searchHospitalResponses = hospitalService.searchByLocation(
            latitude, longitude, zoomLevel);
        log.info("searchHospitalResponses: {}", searchHospitalResponses);
        return ResponseEntity.ok()
                             .body(searchHospitalResponses);
    }

    /* 병원명, 주소 등 키워드로 검색 */
    @GetMapping("/search/keyword")
    public ResponseEntity<List<HospitalResponse>> searchByKeyword(
        @RequestParam(name = "name", required = false) String hospitalName,
        @RequestParam(name = "addr", required = false) String address) {
        log.info(hospitalName);
        log.info(address);
        List<HospitalResponse> hospitalResponses = hospitalService.searchByKeyword(hospitalName,
            address);
        return ResponseEntity.ok()
                             .body(hospitalResponses);
    }

    /* 병원 상세 조회 */
    @GetMapping("/{hospitalId}")
    public ResponseEntity<HospitalDetailResponse> getHospitalDetail(@PathVariable Long hospitalId) {
        HospitalDetailResponse hospitalDetailResponse = hospitalService.getHospitalDetail(
            hospitalId);
        return ResponseEntity.ok()
                             .body(hospitalDetailResponse);
    }

    //todo - @AuthenticationPrincipal -> LoginUser로 만들어서 권한 검증
    @GetMapping("/mypage")
    public ResponseEntity<HospitalDetailResponse> getHospitalDetail(
        @AuthenticationPrincipal String loginId) {
        Hospital hospital = hospitalService.findByLoginId(loginId);
        HospitalDetailResponse hospitalDetailResponse = hospitalService.getHospitalDetail(
            hospital.getId());
        return ResponseEntity.ok()
                             .body(hospitalDetailResponse);
    }

    /* 병원 정보 수정 */
    @PutMapping("/{hospitalId}")
    public ResponseEntity<String> modifyHospitalInfo(
        @AuthenticationPrincipal String loginId,
        @PathVariable Long hospitalId,
        @RequestPart("hospitalInfoUpdateRequest") @Valid final HospitalInfoUpdateRequest hospitalInfoUpdateRequest,
        @RequestPart(required = false) MultipartFile profileImg,
        @RequestPart(required = false) MultipartFile bannerImg,
        @RequestPart @Valid final MultipartFile registrationImg){
        hospitalService.modifyHospitalInfo(hospitalId, loginId, hospitalInfoUpdateRequest, profileImg,
            bannerImg, registrationImg);
        return ResponseEntity.ok()
                             .build();
    }

    /* 의사 정보 수정 */
    // todo
    @PutMapping("/{hospitalId}/doctor/{doctorId}")
    public ResponseEntity<?> modifyHospitalDoctor(
        @AuthenticationPrincipal String loginId,
        @PathVariable Long hospitalId,
        @PathVariable Long doctorId
    ) {
        return ResponseEntity.ok()
                             .build();
    }

    /* 의사 정보 삭제 */
    @DeleteMapping("/{hospitalId}/doctor/{doctorId}")
    public ResponseEntity<String> deleteHospitalDoctor(
        @AuthenticationPrincipal String loginId,
        @PathVariable Long hospitalId,
        @PathVariable Long doctorId) {
        hospitalService.deleteDoctorById(loginId, hospitalId, doctorId);
        return ResponseEntity.ok()
                             .build();
    }
}

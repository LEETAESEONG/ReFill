package com.refill.global.util.runner;

import com.refill.aidiagnosis.entity.AiDiagnosis;
import com.refill.aidiagnosis.entity.HairLossType;
import com.refill.aidiagnosis.repository.AiDiagnosisRepository;
import com.refill.doctor.entity.Doctor;
import com.refill.doctor.entity.EducationBackground;
import com.refill.doctor.entity.MajorArea;
import com.refill.doctor.repository.DoctorRepository;
import com.refill.doctor.repository.EducationBackgroundRepository;
import com.refill.doctor.repository.MajorAreaRepository;
import com.refill.global.entity.Role;
import com.refill.hospital.dto.request.HospitalOperatingHoursRequest;
import com.refill.hospital.entity.Hospital;
import com.refill.hospital.entity.HospitalOperatingHour;
import com.refill.hospital.repository.HospitalOperatingHourRepository;
import com.refill.hospital.repository.HospitalRepository;
import com.refill.member.entity.Member;
import com.refill.member.repository.MemberRepository;
import com.refill.report.entity.Report;
import com.refill.report.entity.TargetType;
import com.refill.report.repository.ReportRepository;
import com.refill.reservation.entity.Reservation;
import com.refill.reservation.repository.ReservationRepository;
import com.refill.review.entity.Review;
import com.refill.review.repository.ReviewRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({"prod"})
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final MajorAreaRepository majorAreaRepository;
    private final EducationBackgroundRepository educationBackgroundRepository;
    private final ReviewRepository reviewRepository;
    private final HospitalOperatingHourRepository hospitalOperatingHourRepository;
    private final ReservationRepository reservationRepository;
    private final AiDiagnosisRepository aiDiagnosisRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String[] si = {"서울특별시", "수원시", "광주광역시", "대전광역시", "부산광역시", "구미시"};
        String[] gu = {"광산구", "종로구", "용산구", "동구", "남구", "수정구", "유성구", "덕진구", "분당구"};
        String[] building = {"타워펠리스 3차", "파키원 타워", "롯데타워", "운체강 타워", "외피드 타워"};

        /* 관리자 생성 */
        Member admin = Member.builder()
                             .name("신호인")
                             .loginId("admin")
                             .nickname("관리자")
                             .birthDay(LocalDate.of(1983, 1, 1))
                             .profileImg("https://picsum.photos/600/600/?random")
                             .address("광주광역시 광산구 윗마을")
                             .email("hoin123@naver.com")
                             .loginPassword(bCryptPasswordEncoder.encode("1234"))
                             .role(Role.ROLE_ADMIN)
                             .tel("010-1234-1234")
                             .createdAt(LocalDateTime.now())
                             .updatedAt(LocalDateTime.now())
                             .build();
        memberRepository.save(admin);



        for(int i=1; i<=20; i++){

            Random random = new Random();
            double randomDouble = random.nextDouble();

            /* 일반 회원 생성 */
            String address = si[i % si.length] + gu[i % gu.length] + building[i % building.length];
            Member member = Member.builder()
                                  .name("member" + i)
                                  .loginId("member" + i)
                                  .nickname("일반유저" + i)
                                  .birthDay(LocalDate.of(2000, 1, 1))
                                  .profileImg("https://picsum.photos/600/600/?random")
                                  .address(address)
                                  .email("member" + i + "@google.com")
                                  .loginPassword(bCryptPasswordEncoder.encode("1234"))
                                  .role(Role.ROLE_MEMBER)
                                  .tel("010-5678-5678")
                                  .createdAt(LocalDateTime.now())
                                  .updatedAt(LocalDateTime.now()).build();
            memberRepository.save(member);

            AiDiagnosis aiDiagnosis = AiDiagnosis.builder()
                                                 .member(member)
                                                 .hairLossScore(40)
                                                 .hairLossType(HairLossType.TYPE3)
                                                 .certainty("60")
                                                 .surveyResult("1010100000")
                                                 .build();

            aiDiagnosis.updateFileAddress("https://picsum.photos/600/600/?random");
            aiDiagnosisRepository.save(aiDiagnosis);


            /* 병원 생성 */
            String[] hospitalName = {
                "드림헤어라인의원", "오라클피부과", "맥스웰피부과",
                "헬스켈병원", "광주더모의원", "젬마모발이식센터",
                "참닥터의원", "더블랙의원 강남", "모텐셜의원",
                "모앤블레스의원"};

            Hospital hospital = Hospital.builder()
                                        .name(hospitalName[i % 10])
                                        .address(address)
                                        .email("hospital_" + i + "@samsung.com")
                                        .loginId("hospital" + i)
                                        .loginPassword(
                                            bCryptPasswordEncoder.encode("1234")) //1234
                                        .role(i % 4 == 0 ? Role.ROLE_GUEST : Role.ROLE_HOSPITAL)
                                        .tel("02-2345-3465")
                                        .hospitalBannerImg("https://picsum.photos/600/600/?random")
                                        .hospitalProfileImg("https://picsum.photos/600/600/?random")
                                        .latitude(BigDecimal.valueOf(33.452613d + 0.0001d * (double)i))
                                        .longitude(BigDecimal.valueOf(126.570888d + 0.0001d * (double)i))
                                        .postalCode(
                                            String.valueOf(random.nextInt(90000)+10000))
                                        .registrationImg("https://picsum.photos/600/600/?random")
                                        .build();
            hospitalRepository.save(hospital);

            // 운영시간 넣기
            LocalTime startTime = LocalTime.of(9, 0);
            LocalTime endTime = LocalTime.of(19, 0);

            DayOfWeek[] weeks = DayOfWeek.values();

            for(int k = 0; k < weeks.length; k++) {
                HospitalOperatingHoursRequest request = new HospitalOperatingHoursRequest(weeks[k], startTime, endTime);
                HospitalOperatingHour hospitalOperatingHour = HospitalOperatingHour.from(request, hospital);
                hospitalOperatingHourRepository.save(hospitalOperatingHour);
            }


            String[] firstName = {"김", "이", "박", "신", "유", "최"};

            /* 의사 생성 */
            for(int j=0; j<4; j++){
                Doctor doctor = Doctor.builder()
                                      .name(firstName[(i + j) % firstName.length] + "의사")
                                      .profileImg("https://picsum.photos/600/600/?random")
                                      .licenseImg("https://picsum.photos/600/600/?random")
                                      .licenseNumber("DOC-LN-2123-" + i)
                                      .description("한국 미용 성형학회 자문의원\nIBCS\n모발이식의 대가")
                                      .hospital(hospital)
                                      .build();
                doctorRepository.save(doctor);
                /* 주요 진료 분야 */
                String[] major = {"탈모 진단 및 진행 추적", "줄기세포 모발 이식 시술", "컨설팅"};
                for(int k=0; k<major.length; k++){
                    MajorArea majorArea = MajorArea.builder()
                                                   .doctor(doctor)
                                                   .content(major[k])
                                                   .build();
                    majorAreaRepository.save(majorArea);
                }

                /* 학력 */
                String[] edu = {"서울대학교 대학원 졸업", "경희대학교 의과대학 졸업"};
                for(int k=0; k<edu.length; k++){
                    EducationBackground educationBackground = EducationBackground.builder()
                                                                                 .doctor(doctor)
                                                                                 .content(edu[k])
                                                                                 .build();
                    educationBackgroundRepository.save(educationBackground);
                }

                /* 리뷰 생성 - 의사 한명당 3개의 리뷰 생성 */
                String[] content = {"이분이 진짜 최고", "모발이식 상담을 너무 잘해요!!", "난 좀 별로인듯..."};
                for(int k=0; k < 4; k++){
                    Review review = Review.builder()
                                          .doctor(doctor)
                                          .content(content[(j + k) % content.length])
                                          .hospital(hospital)
                                          .member(member)
                                          .score(random.nextInt(6))
                                          .isBlocked(false)
                                          .build();
                    reviewRepository.save(review);

                    Report report = new Report(member.getRole(), member.getId(), review.getId(), "내용이 추잡합니다.", TargetType.REVIEW);
                    reportRepository.save(report);

                }

                // 예약
//                LocalDate localDate = LocalDate.now().plusDays(1L);
//                LocalTime localTime = LocalTime.of(10, 0);
//                LocalDateTime startDateTime = LocalDateTime.of(localDate, localTime);
//                LocalDateTime endDateTime = startDateTime.plusMinutes(30);
//                String counselingDemands = "상담 요청합니다.";
//
//                Reservation reservation = Reservation.builder()
//                                                     .member(member)
//                                                     .doctor(doctor)
//                                                     .startDateTime(startDateTime)
//                                                     .endDateTime(endDateTime)
//                                                     .counselingDemands(counselingDemands)
//                                                     .build();
//
//                reservationRepository.save(reservation);

                LocalDate localDate = LocalDate.now();
                LocalTime localTime = LocalTime.of(17, 00);
                LocalDateTime startDateTime = LocalDateTime.of(localDate, localTime);
                LocalDateTime endDateTime = startDateTime.plusMinutes(30);
                String counselingDemands = "상담 요청합니다.";

                Reservation reservation = Reservation.builder()
                                                     .member(member)
                                                     .doctor(doctor)
                                                     .startDateTime(startDateTime)
                                                     .endDateTime(endDateTime)
                                                     .counselingDemands(counselingDemands)
                                                     .build();

                reservationRepository.save(reservation);

//                LocalDate localDate1 = LocalDate.now();
//                LocalTime localTime1 = LocalTime.of(13, 30);
//                LocalDateTime startDateTime1 = LocalDateTime.of(localDate1, localTime1);
//                LocalDateTime endDateTime1 = startDateTime1.plusMinutes(30);
//                String counselingDemands1 = "상담 요청합니다.";
//
//                Reservation reservation1 = Reservation.builder()
//                                                     .member(member)
//                                                     .doctor(doctor)
//                                                     .startDateTime(startDateTime1)
//                                                     .endDateTime(endDateTime1)
//                                                     .counselingDemands(counselingDemands1)
//                                                     .build();
//
//                reservationRepository.save(reservation1);
//
//                LocalDate localDate2 = LocalDate.now();
//                LocalTime localTime2 = LocalTime.of(14, 0);
//                LocalDateTime startDateTime2 = LocalDateTime.of(localDate2, localTime2);
//                LocalDateTime endDateTime2 = startDateTime2.plusMinutes(30);
//                String counselingDemands2 = "상담 요청합니다.";
//
//                Reservation reservation2 = Reservation.builder()
//                                                     .member(member)
//                                                     .doctor(doctor)
//                                                     .startDateTime(startDateTime2)
//                                                     .endDateTime(endDateTime2)
//                                                     .counselingDemands(counselingDemands2)
//                                                     .build();
//
//                reservationRepository.save(reservation2);
//
//                LocalDate localDate3 = LocalDate.now();
//                LocalTime localTime3 = LocalTime.of(14, 30);
//                LocalDateTime startDateTime3 = LocalDateTime.of(localDate3, localTime3);
//                LocalDateTime endDateTime3 = startDateTime3.plusMinutes(30);
//                String counselingDemands3 = "상담 요청합니다.";
//
//                Reservation reservation3 = Reservation.builder()
//                                                     .member(member)
//                                                     .doctor(doctor)
//                                                     .startDateTime(startDateTime3)
//                                                     .endDateTime(endDateTime3)
//                                                     .counselingDemands(counselingDemands3)
//                                                     .build();
//
//                reservationRepository.save(reservation3);
//
//                LocalDate localDate4 = LocalDate.now();
//                LocalTime localTime4 = LocalTime.of(15, 0);
//                LocalDateTime startDateTime4 = LocalDateTime.of(localDate4, localTime4);
//                LocalDateTime endDateTime4 = startDateTime4.plusMinutes(30);
//                String counselingDemands4 = "상담 요청합니다.";
//
//                Reservation reservation4 = Reservation.builder()
//                                                     .member(member)
//                                                     .doctor(doctor)
//                                                     .startDateTime(startDateTime4)
//                                                     .endDateTime(endDateTime4)
//                                                     .counselingDemands(counselingDemands4)
//                                                     .build();
//
//                reservationRepository.save(reservation4);
//
//                LocalDate localDate5 = LocalDate.now();
//                LocalTime localTime5 = LocalTime.of(15, 30);
//                LocalDateTime startDateTime5 = LocalDateTime.of(localDate5, localTime5);
//                LocalDateTime endDateTime5 = startDateTime5.plusMinutes(30);
//                String counselingDemands5 = "상담 요청합니다.";
//
//                Reservation reservation5 = Reservation.builder()
//                                                     .member(member)
//                                                     .doctor(doctor)
//                                                     .startDateTime(startDateTime5)
//                                                     .endDateTime(endDateTime5)
//                                                     .counselingDemands(counselingDemands5)
//                                                     .build();
//
//                reservationRepository.save(reservation5);
//
//                LocalDate localDate6 = LocalDate.now();
//                LocalTime localTime6 = LocalTime.of(16, 00);
//                LocalDateTime startDateTime6 = LocalDateTime.of(localDate6, localTime6);
//                LocalDateTime endDateTime6 = startDateTime6.plusMinutes(30);
//                String counselingDemands6 = "상담 요청합니다.";
//
//                Reservation reservation6 = Reservation.builder()
//                                                      .member(member)
//                                                      .doctor(doctor)
//                                                      .startDateTime(startDateTime6)
//                                                      .endDateTime(endDateTime6)
//                                                      .counselingDemands(counselingDemands6)
//                                                      .build();
//
//                reservationRepository.save(reservation6);
//
//                LocalDate localDate7 = LocalDate.now();
//                LocalTime localTime7 = LocalTime.of(16, 30);
//                LocalDateTime startDateTime7 = LocalDateTime.of(localDate7, localTime7);
//                LocalDateTime endDateTime7 = startDateTime7.plusMinutes(30);
//                String counselingDemands7 = "상담 요청합니다.";
//
//                Reservation reservation7 = Reservation.builder()
//                                                      .member(member)
//                                                      .doctor(doctor)
//                                                      .startDateTime(startDateTime7)
//                                                      .endDateTime(endDateTime7)
//                                                      .counselingDemands(counselingDemands7)
//                                                      .build();
//
//                reservationRepository.save(reservation7);
//
//                LocalDate localDate8 = LocalDate.now();
//                LocalTime localTime8 = LocalTime.of(17, 00);
//                LocalDateTime startDateTime8 = LocalDateTime.of(localDate8, localTime8);
//                LocalDateTime endDateTime8 = startDateTime8.plusMinutes(30);
//                String counselingDemands8 = "상담 요청합니다.";
//
//                Reservation reservation8 = Reservation.builder()
//                                                      .member(member)
//                                                      .doctor(doctor)
//                                                      .startDateTime(startDateTime8)
//                                                      .endDateTime(endDateTime8)
//                                                      .counselingDemands(counselingDemands8)
//                                                      .build();
//
//                reservationRepository.save(reservation8);
//
//                LocalDate localDate9 = LocalDate.now();
//                LocalTime localTime9 = LocalTime.of(17, 30);
//                LocalDateTime startDateTime9 = LocalDateTime.of(localDate9, localTime9);
//                LocalDateTime endDateTime9 = startDateTime9.plusMinutes(30);
//                String counselingDemands9 = "상담 요청합니다.";
//
//                Reservation reservation9 = Reservation.builder()
//                                                      .member(member)
//                                                      .doctor(doctor)
//                                                      .startDateTime(startDateTime9)
//                                                      .endDateTime(endDateTime9)
//                                                      .counselingDemands(counselingDemands9)
//                                                      .build();
//
//                reservationRepository.save(reservation9);
//
//                LocalDate localDate10 = LocalDate.now();
//                LocalTime localTime10 = LocalTime.of(18, 00);
//                LocalDateTime startDateTime10 = LocalDateTime.of(localDate10, localTime10);
//                LocalDateTime endDateTime10 = startDateTime10.plusMinutes(30);
//                String counselingDemands10 = "상담 요청합니다.";
//
//                Reservation reservation10 = Reservation.builder()
//                                                      .member(member)
//                                                      .doctor(doctor)
//                                                      .startDateTime(startDateTime10)
//                                                      .endDateTime(endDateTime10)
//                                                      .counselingDemands(counselingDemands10)
//                                                      .build();
//
//                reservationRepository.save(reservation10);
            }


        }
    }
}

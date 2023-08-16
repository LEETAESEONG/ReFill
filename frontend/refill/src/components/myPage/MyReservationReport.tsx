import React, { useState } from "react";
import axios from "axios";
// 토큰가져오기
import { RootState } from "store/reducers";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

// CSS
import "styles/Reservation.css";

// 하위 컴포넌트
import CautionReservation from "./CautionReservation";
import DeleteModal from "./DeleteModal";
import ReservationCompo from "./ReservationCompo";

type Reservation = {
  doctorName: string;
  hospitalName: string;
  reservationId: number;
  startDateTime: string;
};

interface MyReservationReportProps {
  reservationList: Reservation[] | null;
}

const MyReservationReport: React.FC<MyReservationReportProps> = ({
  reservationList,
}) => {
  const token = useSelector((state: RootState) => state.login.token);
  // 상담 취소
  const [openModal, setOpenModal] = useState(false);
  const [wantDelete, setWantDelete] = useState(false);

  const deleteReservation = (id: number) => {
    setOpenModal(true);
    axios
      .delete(`/api/v1/reservation/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        console.log("ok");
        // 요청이 성공적으로 완료된 경우, 필요한 추가 동작을 여기에 작성합니다.
      })
      .catch((err) => {
        console.log(err);
        // 오류가 발생한 경우, 오류 처리 로직을 여기에 작성합니다.
      });
  };

  // 1. 현재 날짜와 시간을 가져옵니다.
  const now = new Date();

  // 2. 현재 날짜 및 시간에서 31분을 빼서 경계 시간을 계산합니다.
  const thirtyOneMinutesAgo = new Date(now.getTime() - 31 * 60 * 1000);
  console.log(thirtyOneMinutesAgo)
  const sortedList = reservationList
    ?.filter(
      (reservation) =>
        new Date(reservation.startDateTime) >= thirtyOneMinutesAgo
    )
    .slice()
    .sort(
      (a, b) =>
        new Date(a.startDateTime).getTime() -
        new Date(b.startDateTime).getTime()
    );

  const [currentIndex, setCurrentIndex] = useState(0);

  const slidesToShow = 3; // 한 번에 보여줄 슬라이드 수

  const nextSlide = () => {
    if (reservationList) {
      setCurrentIndex((prevIndex) =>
        Math.min(
          prevIndex + slidesToShow,
          reservationList.length - slidesToShow
        )
      );
    }
  };

  const prevSlide = () => {
    setCurrentIndex((prevIndex) => Math.max(prevIndex - 1, 0)); // 한 슬라이드만 이동하도록 수정
  };

  return (
    <div className="grid grid-rows-4 gap-4 items-center">
      {openModal ? (
        <DeleteModal
          setOpenModal={setOpenModal}
          setWantDelete={setWantDelete}
        />
      ) : (
        <></>
      )}

      <div className="row-start-1 row-end-4">
        <div className="flex justify-between">
          <button className="text-lg" onClick={prevSlide}>
            Prev
          </button>
          <button className="text-lg" onClick={nextSlide}>
            Next
          </button>
        </div>
        <div
          style={{
            overflow: "hidden",
            width: "calc(200px * 3)", // 100px는 각 슬라이드의 너비입니다. 적절히 조절해야 합니다.
          }}
        >
          <div
            style={{
              display: "flex",
              transform: `translateX(-${currentIndex * 200}px)`, // 여기도 너비에 따라 적절히 조절이 필요합니다.
              transition: "transform 0.3s",
            }}
          >
            {sortedList?.map((reservation, index) => (
              <div
                className="p-2"
                key={index}
                style={{ minWidth: "200px", padding: "5px" }}
              >
                <ReservationCompo
                  reservation={reservation}
                  deleteReservation={deleteReservation}
                />
              </div>
            ))}
          </div>
        </div>
      </div>
      <div className="px-10">
        <CautionReservation />
      </div>
    </div>
  );
};

export default MyReservationReport;

import React, { useState, useEffect } from "react";
import axios from "axios";
// 토큰가져오기
import { RootState } from "store/reducers";
import { useSelector } from "react-redux";
// 하위 컴포넌트
import DetailConsultingModal from "./DetailConsultingModal";

type consultData = {
  consultingId: number;
  hospitalName: string;
  doctorName: string;
  startDateTime: string;
};

type detailConsulting = {
  content: string;
  doctorName: string;
  startDateTime: string;
  memberAddress: string;
  memberName: string;
  birthDay: string;
  // 상담소견
  detailConsultingInfo: string;
  hospitalName: string;
  hospitalAddress: string;
  tel: string;
};

const DummyData2 = {
  content: "",
  doctorName: "김정필 교수님",
  startDateTime: "2023-06-30",
  memberAddress: "광주광역시 북구 호동로 72-9 리치빌 302호",
  memberName: "김정필",
  birthDay: "900110",
  // 상담소견
  detailConsultingInfo: "현재 탈모 진행이 꽤나 진행됨",
  hospitalName: "젬마 모발 이식 센터",
  hospitalAddress: "서울 강남구 논현로 815",
  tel: "02-582-1312",
};

interface ConsultingProps {
  consListItem: consultData;
}

const ConsultingListItem: React.FC<ConsultingProps> = ({ consListItem }) => {
  // 토큰
  const token = useSelector((state: RootState) => state.login.token);
  // 디테일 가져오기
  const [consDetail, setConsDetail] = useState<detailConsulting | null>(null);

  const [openDetail, setOpenDetail] = useState(false);

  // 상담상세 페이지를 들어갈 수 있어야 함
  const date = new Date(consListItem.startDateTime);

  // 요일을 표시하기 위한 배열
  const daysOfWeek = ["일", "월", "화", "수", "목", "금", "토"];

  // 날짜를 원하는 형식으로 가공
  const formattedDate =
    `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()} ` +
    `(${daysOfWeek[date.getDay()]}) ${date.getHours()}:${date.getMinutes()}`;

  // 상세 상담 가져오는 axios
  const getDetailCons = () => {
    axios
      .get(`/api/v1/consulting/consultingDetail/${consListItem.consultingId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        console.log("ok");
        setConsDetail(res.data);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const handleModal = () => {
    getDetailCons();
    // setConsDetail(DummyData2);
    if (consDetail) {
      setOpenDetail(true);
    } else {
      console.log("error");
    }
  };

  return (
    <>
      {/* {openDetail ? (
        <>
          <DetailConsultingModal consDetail={DummyData2} setOpenDetail={setOpenDetail} />
        </>
      ) : (
        <></>
      )} */}
      {openDetail ? (
        <>
          <DetailConsultingModal
            consDetail={consDetail}
            setOpenDetail={setOpenDetail}
          />
        </>
      ) : (
        <></>
      )}
      <div className="cons-list-item flex justify-between items-center">
        <div className="text-xl">{formattedDate}</div>
        <div className="text-xl font-black">{consListItem.hospitalName}</div>
        <div className="text-xl font-black">{consListItem.doctorName}</div>
        <button onClick={handleModal}>{`상담 기록 보러가기 >`}</button>
      </div>
    </>
  );
};
export default ConsultingListItem;

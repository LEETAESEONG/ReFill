import React, { useState, useEffect } from "react";
import "../App.css";
import styled from "@emotion/styled";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import Button from "../components/elements/Button";
import axios from "axios";
import default_profile from "../assets/default_profile.png";
import { RootState } from "store/reducers";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import ChangePassword from "./user/ChangePassword";
import ModifyMember from "./user/ModifyMember";
import { Container, Grid } from "@mui/material";
// 하위 컴포넌트
import MyReservationReport from "components/myPage/MyReservationReport";
import AiDiagnosisList from "components/myPage/AiDiagnosisList";
import ConsultingList from "components/myPage/ConsultingList";
// CSS
import "styles/MyPage.css";

interface DivProps {
  selected?: boolean;
}

// 예약 타입 설정
type Reservation = {
  reservationId: number;
  hospitalId: number;
  doctorId: number;
  memberId: number;
  doctorName: string;
  hospitalName: string;
  startDateTime: string;
};

const Containerdiv = styled.div`
  border 0;
  min-width: 100%;
  min-height: 50vh;
  align-items: center;
  display:flex;
  justify-content: center;
  flex-direction: column;
  margin : 100px 0px;
`;

const Common = styled.div`
  border-radius: 10px;
  border-color: #ccc;
  border-width: 1px;
  box-shadow: 0px 0px 1px 1px gray;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const ButtonList = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
`;

const Content = styled.div`
  display: flex;
  flex-direction: column;
  margin: 0px 50px;
  display: ${(props: DivProps) => (props.selected ? "block" : "none")};
`;
const Content2 = styled.div`
  display: flex;
  flex-direction: column;
  margin: 0px 50px;
  display: ${(props: DivProps) => (props.selected ? "none" : "block")};
`;
const Profileimg = styled.img`
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 1px;
`;

const Ptag = styled.p`
  font-size: 20px;
  color: black;
`;

const DownContent = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const Mypage: React.FC = () => {
  // 로그인이 안되있으면 메인페이지로 이동

  const [selected, setSelected] = useState(true);
  const [checkimg, setCheckimg] = useState(false);
  const [userData, setuserData] = useState({
    address: "",
    birthDay: "",
    email: "",
    name: "",
    nickname: "",
    profileImg: null,
    tel: "",
  });

  // 예약 정보 받기
  const [reservationList, setReservationList] = useState<Reservation[] | null>(
    null,
  );

  const token: string = useSelector((state: RootState) => state.login.token);
  const memberId: number = useSelector((state: RootState) => state.login.pk);
  const islogin: boolean = useSelector(
    (state: RootState) => state.login.islogin,
  );
  const loginId = useSelector((state: RootState) => state.login.loginId);
  const userPk = useSelector((state: RootState) => state.login.pk);
  // 토큰 분해하기 memberId가져오기
  const navigate = useNavigate();

  useEffect(() => {
    if (islogin === true) {
      axios
        .get("api/v1/member/mypage", {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        })

        .then((response) => {
          console.log("ok");
          setuserData(response.data);
          setReservationList(response.data.reservationList);
          if (userData.profileImg !== null) {
            setCheckimg(true);
          }
        })
        .catch((error) => {
          console.log(error);
        });
    } else {
      navigate("/");
      alert("접근 권한이 없습니다.");
    }
  }, []);

  const buttonclick = () => {
    console.log("계정 삭제");
    axios
      .delete(`/api/v1/account/member/${memberId}`, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        console.log(res.data);
        navigate("/logout");
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const handleToChangeMypage = () => {
    setSelected(true);
  };

  const handleToMyRecord = () => {
    setSelected(false);
  };

  // 비밀번호 변경 모달
  const [changeopen, setChangeOpen] = useState(false);
  const handleCMOpen = () => {
    setChangeOpen(true);
  };
  const handleCMClose = () => {
    setChangeOpen(false);
  };

  const ChangePw = async (data: any) => {
    axios
      .put(`api/v1/member/mypage/password`, data, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      })

      .then((response) => {
        handleCMClose();
      })

      .catch((error) => {
        console.log(error.response.data.message);
        handleCMClose();
      });
  };

  // 회원정보 변경 모달
  const [modifyOpen, setModifyOpen] = useState(false);
  const handleMMOpen = () => {
    setModifyOpen(true);
  };
  const handleMMClose = () => {
    setModifyOpen(false);
  };

  const ModifyDoc = async (formData: any) => {
    axios
      .put(`api/v1/member/mypage`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${token}`,
        },
      })

      .then((response) => {
        console.log("ok");
        handleMMClose();
      })

      .catch((error) => {
        console.log(error);
        handleMMClose();
      });
  };

  return (
    <div>
      <Navbar />
      <Containerdiv>
        <div className="flex">
          <Common style={{ width: "200px", height: "220px" }}>
            <div className="ms-4 mb-4">
              <span className="text-gray-500">내 정보 관리</span>
            </div>
            <ButtonList>
              <Button
                content="계정관리"
                variant={selected ? "menuSelected" : "menuUnselected"}
                width="180px"
                onClick={handleToChangeMypage}
              />
              <br />
              <Button
                content="나의기록"
                variant={selected ? "menuUnselected" : "menuSelected"}
                width="180px"
                onClick={handleToMyRecord}
              />
            </ButtonList>
          </Common>
          <Content selected={selected}>
            <h1 style={{ color: "#2E5077" }} className="text-3xl font-bold">
              계정 관리
            </h1>
            <br />
            <span className="text-xl font-bold">기본 정보</span>
            <Common
              style={{ width: "700px", height: "480px" }}
              className="mt-3 mb-6 "
            >
              <Profileimg
                src={
                  userData.profileImg
                    ? `/${userData.profileImg}`
                    : `${default_profile}`
                }
                className="mb-3"
              />
              <div
                className="flex justify-between py-3"
                style={{ width: "600px" }}
              >
                <Container>
                  <Grid container spacing={2} className="my-10">
                    <Grid item xs={4}>
                      <Ptag>이름</Ptag>
                    </Grid>
                    <Grid item xs={8}>
                      <Ptag>{userData.name}</Ptag>
                    </Grid>
                    <Grid item xs={4}>
                      <Ptag>주소</Ptag>
                    </Grid>
                    <Grid item xs={8}>
                      <Ptag>{userData.address}</Ptag>
                    </Grid>
                    <Grid item xs={4}>
                      <Ptag>생년월일</Ptag>
                    </Grid>
                    <Grid item xs={8}>
                      <Ptag>{userData.birthDay}</Ptag>
                    </Grid>
                    <Grid item xs={4}>
                      <Ptag>전화번호</Ptag>
                    </Grid>
                    <Grid item xs={8}>
                      <Ptag>{userData.tel}</Ptag>
                    </Grid>
                    <Grid item xs={4}>
                      <Ptag>닉네임</Ptag>
                    </Grid>
                    <Grid item xs={6}>
                      <Ptag>{userData.nickname}</Ptag>
                    </Grid>
                    <Grid item xs={2} style={{ paddingLeft: "40px" }}>
                      <ModifyMember
                        open={modifyOpen}
                        handleMOpen={handleMMOpen}
                        handleMClose={handleMMClose}
                        name={userData.name}
                        address={userData.address}
                        birthday={userData.birthDay}
                        tel={userData.tel}
                        nickname={userData.nickname}
                        email={userData.email}
                        userData={userData}
                        setuserData={setuserData}
                        profile={
                          checkimg
                            ? `/${userData.profileImg}`
                            : `${default_profile}`
                        }
                        onModify={(formData) => ModifyDoc(formData)}
                      ></ModifyMember>
                    </Grid>
                  </Grid>
                </Container>
              </div>
            </Common>
            <span className="text-xl font-bold mt-10 mb-3">비밀번호</span>
            <Common
              style={{ width: "700px", height: "150px" }}
              className="mt-3 mb-6"
            >
              <DownContent style={{ width: "600px" }}>
                <h1 className="text-xl text-gray-400 font-bold">
                  비밀번호를 바꾸고 싶거나 잊어버리셨나요?
                </h1>
                <ChangePassword
                  open={changeopen}
                  handleMOpen={handleCMOpen}
                  handleMClose={handleCMClose}
                  onChangemember={(data) => ChangePw(data)}
                ></ChangePassword>
              </DownContent>
            </Common>
            <span className="text-xl font-bold mt-10 mb-3">계정 삭제</span>
            <Common
              style={{ width: "700px", height: "150px" }}
              className="mt-3 mb-6"
            >
              <DownContent style={{ width: "600px" }}>
                <h1 className="text-xl text-gray-400 font-bold">
                  계정 삭제 시 작성했던 댓글, 리뷰는 삭제되지 않습니다.
                </h1>
                <Button
                  content="계정 삭제"
                  variant="danger"
                  width="80px"
                  onClick={buttonclick}
                />
              </DownContent>
            </Common>
          </Content>
          <Content2 selected={selected}>
            <h1 style={{ color: "#2E5077" }} className="text-3xl font-bold">
              나의 기록
            </h1>
            <br />
            <span className="text-xl font-bold mb-3">나의 예약 현황</span>
            <Common
              style={{ width: "700px", height: "350px" }}
              className="mt-3 mb-6"
            >
              <MyReservationReport reservationList={reservationList} />
            </Common>
            <span className="text-xl font-bold mt-10 mb-3">나의 상담 기록</span>
            <Common
              style={{ width: "700px", height: "290px" }}
              className="mt-3 mb-6"
            >
              <ConsultingList userPk={userPk} />
            </Common>
            <span className="text-xl font-bold mt-10 mb-3">
              나의 AI 자가진단 기록
            </span>
            <Common
              style={{ width: "700px", height: "290px" }}
              className="mt-3 mb-6"
            >
              <AiDiagnosisList memberId={memberId} />
            </Common>
          </Content2>
        </div>
      </Containerdiv>
      <Footer />
    </div>
  );
};

export default Mypage;

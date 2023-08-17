import React, { useState } from "react";
import ToggleSwitch from "components/elements/ToggleSwitch";
import FindId from "components/find/FindId";
import FindPassword from "components/find/FindPassword";
import FindSelect from "components/find/FindSelect";
import styled from "@emotion/styled";
import "../../styles/Loginsignup.css";
import { Link } from "react-router-dom";
import app_logo from "../../assets/app_logo.png";

const FindForm = styled.div`
  background-color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  width: 500px;
  height: 450px;
`;

const FindIdPassword: React.FC = () => {
  const [ismember, setisMeber] = useState(true);
  const [isfindid, setisFindId] = useState(true);

  const handleFindCheck = () => {
    setisFindId(!isfindid);
  };

  const handleToggleMember = () => {
    setisMeber(!ismember); // ismember 값을 변경하는 함수
  };

  return (
    <div
      className={`${
        ismember ? "MemberForm" : "HospitalForm"
      } flex justify-center items-center`}
    >
      <div className="flex-col justify-center items-center">
        <div className="flex justify-between items-center">
          <ToggleSwitch
            ismember={ismember}
            onText="일반"
            offText="병원"
            handleToggle={handleToggleMember}
          />
          <span className="text-2xl font-bold text-center text-white">
            {ismember ? "일반회원" : "병원회원"}
          </span>
          <Link to="/" className="flex items-center">
            {/* <img
              src={app_logo}
              alt="nav_log"
              style={{ width: "100px", height: "100px" }}
            /> */}
            <p style={{color:'white'}}>홈으로 돌아가기</p>
          </Link>
        </div>
        <div className="flex-col justify-center items-center">
          <FindSelect
            isfindid={isfindid}
            handdleSelect={handleFindCheck}
          ></FindSelect>
          
          <FindForm className="rounded-b-2xl flex justify-between">
            {isfindid ? (
              <>
                <div style={{textAlign: 'center', marginBottom: '50px'}}>
                  <h1 style={{fontWeight: '700', fontSize:'20px', marginBottom:'5px'}}>아이디 찾기</h1>
                  <p>
                    아이디를 받고 싶은 이메일을 입력해 주세요.
                  </p>
                  <p>
                    입력된 메일로 자세한 안내를 보내드립니다.
                  </p>
                </div>
                <FindId
                  ismember={ismember}
                  toggleMembership={handleToggleMember}
                />
              </>
            ) : (
              <>
                <div style={{textAlign: 'center', marginBottom: '20px'}}>
                  <h1 style={{fontWeight: '700', fontSize:'20px', marginBottom:'5px'}}>비밀번호 재설정</h1>
                  <p>
                    비밀번호를 재설정 할 이메일을 입력해 주세요.
                  </p>
                  <p>
                    입력된 메일로 자세한 안내를 보내드립니다.
                  </p>
                </div>
                <FindPassword ismember={ismember} />
              </>
            )}
          </FindForm>
        </div>
      </div>
    </div>
  );
};

export default FindIdPassword;

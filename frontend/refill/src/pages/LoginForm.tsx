import React, { useState } from "react";
import axios from "axios";
import Logo_img from "../assets/logo2_final.png";
import Kakao from "../assets/Kakao_logo.png";
import Naver from "../assets/Naver_logo.png";
import Google from "../assets/Google_logo.png";
import Button from "../components/elements/Button";
import "../styles/Loginsignup.css";
import UseLoginForm from "hooks/UseLoginForm";
import { useSelector } from "react-redux";
import { RootState } from "store/reducers";

const LoginForm: React.FC = () => {
  const [loginId, setLoginId] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [handleSubmitLogin] = UseLoginForm(loginId, loginPassword);
  const user = useSelector((state: RootState) => state.login.islogin);

  const handleUsernameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setLoginId(event.target.value);
  };

  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setLoginPassword(event.target.value);
  };

  const middle = "flex justify-center items-center";

  return (
    <div className="MemberForm">
      {" "}
      {/* 버튼의 useState값에 따라 배경색 및 페이지 렌더링 진행 */}
      <div className="Common_Left text-3xl" style={{ margin: "200px 0px" }}>
        <div>
          <div className="my-3">
            <span>{user}</span>
            <span className="text-white">머리도 </span>
            <span className="text-whiteblue">Fill </span>
          </div>
          <div className="my-3">
            <span className="text-white">자신감도 </span>
            <span className="text-whiteblue">Fill </span>
          </div>
          <div className="my-3">
            <span className="text-white">전과 다른 느낌을 </span>
            <span className="text-whiteblue">Feel</span>
          </div>
        </div>
        <img src={Logo_img} className="Logo_img" alt="" />
      </div>
      <div className={`Common_Right ${middle}`}>
        <div className="flex justify-between">
          <span className="text-white text-2xl font-bold my-4">
            일반 로그인
          </span>
        </div>
        <div className="flex">
          <div className="select_LoginOrSignUp rounded-t-2xl bg-white">
            <span className="my-2">로그인</span>
          </div>
          <div className="select_LoginOrSignUp rounded-t-2xl bg-red">
            <span className="my-2">회원가입</span>
          </div>
        </div>
        <div className={`${middle} MLogin rounded-b-2xl`}>
          <form
            onSubmit={handleSubmitLogin}
            className=""
            style={{ width: "80%" }}
          >
            <div>
              <div className="flex justify-between">
                <label className="block mb-2 text-sm font-medium text-gray-900 ">
                  ID
                </label>
                <a href="" className="mb-2 text-sm font-medium text-blue-600">
                  아이디 찾기
                </a>
              </div>
              <input
                type="text"
                className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
                placeholder="아이디를 입력해주세요"
                value={loginId}
                onChange={handleUsernameChange}
              ></input>
            </div>
            <br />
            <div>
              <div className="flex justify-between">
                <label className="block mb-2 text-sm font-medium text-gray-900 ">
                  PASSWORD
                </label>
                <a href="" className="mb-2 text-sm font-medium text-blue-600">
                  비밀번호 찾기
                </a>
              </div>
              <input
                type="password"
                className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
                placeholder="비밀번호를 입력해주세요"
                value={loginPassword}
                onChange={handlePasswordChange}
              ></input>
            </div>
            <br />
            <div className="my-3">
              <Button
                content="로그인"
                variant="success"
                width="200px"
                type="submit"
                customStyles={{ width: "100%" }}
              />
            </div>
            <div className="text-sm flex justify-center mt-4">
              <span className="mx-2">일반 회원이 아니십니까?</span>
              <a href="">병원회원으로 로그인 하기</a>
            </div>
            <div className="flex justify-around my-5">
              <div className="flex justify-center flex-col text-center">
                <img
                  src={Google}
                  style={{ width: "50px", height: "50px" }}
                  alt=""
                />
                <span>Google</span>
              </div>
              <div className="flex justify-center flex-col text-center">
                <img
                  src={Kakao}
                  style={{ width: "50px", height: "50px" }}
                  alt=""
                />
                <span>Kakao</span>
              </div>
              <div className="flex justify-center flex-col text-center">
                <img
                  src={Naver}
                  style={{ width: "50px", height: "50px" }}
                  alt=""
                />
                <span>Naver</span>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginForm;

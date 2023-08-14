import React, { ChangeEvent, useState, useEffect } from "react";
import axios from "axios";
import Button from "../elements/Button";
import Social from "components/common/Social";
import "../../styles/Loginsignup.css";
import { useNavigate } from "react-router-dom";
import { useKakaoMapScript } from "hooks/UseKakaoMap";

declare global {
  interface Window {
    daum: any;
  }
}

interface Addr {
  address: string;
}

interface InputImageState {
  profileImg: File | null;
  regImg: File | null;
}

interface TypeChangeForGeocoder {
  address_name: string;
  address_type: string;
  road_address: object;
  address: object;
  x: string;
  y: string;
}

const HospitalSignup: React.FC = () => {
  const navigate = useNavigate();
  // 회원가입 할 때 필요한 데이터
  const [inputData, setInputData] = useState({
    loginId: "",
    loginPassword: "",
    name: "",
    address: "",
    postalCode: "",
    tel: "",
    email: "",
    latitude: "",
    longitude: "",
  });

  // kakaomap 주소 -> 좌표 변환
  const [hospitalAddr, setHospitalAddr] = useState("");
  const scriptLoaded = useKakaoMapScript();

  const [lonlat, setLonlat] = useState(["", ""]);

  const addrToGeocoder = async function (
    result: TypeChangeForGeocoder[],
    status: any,
  ) {
    if (status === window.kakao.maps.services.Status.OK && result.length > 0) {
      console.log(result[0].x, result[0].y);
      await setLonlat([result[0].x, result[0].y]);
    } else {
      alert("올바르지 않은 주소 접근입니다.");
      console.log("err", result);
    }
  };

  useEffect(() => {
    if (scriptLoaded && window.kakao && window.kakao.maps) {
      const geocoder = new window.kakao.maps.services.Geocoder();
      geocoder.addressSearch(hospitalAddr, addrToGeocoder);
    }
  }, [hospitalAddr]); // 의존성 배열에 scriptLoaded 추가

  //

  // 이미지 올리기
  const [inputImage, setInputImage] = useState<InputImageState>({
    profileImg: null,
    regImg: null,
  });

  const handleImgChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null; // 선택한 파일을 가져옵니다. 없으면 null로 설정합니다.

    setInputImage((prevInputImage) => ({
      ...prevInputImage,
      [e.target.name]: file,
    }));
    if (file) {
      if (e.target.name === "profileImg") {
        (document.getElementById("profilename") as HTMLInputElement).value =
          file.name;
      } else if (e.target.name === "regImg") {
        (document.getElementById("regname") as HTMLInputElement).value =
          file.name;
      }
    }
  };

  // 이메일 인증 로직
  const [code, setCode] = useState("");
  const [checkCode, setCheckCode] = useState("");
  const [check, setCheck] = useState(false);

  const handleCodeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCode(event.target.value);
  };

  const checkemail = () => {
    if (checkCode === code) setCheck(true);
    else setCheck(false);

    console.log(code);
    console.log(checkCode);
    console.log(check);
  };

  // 이메일 인증 요청
  const emailCertify = async (event: React.MouseEvent<HTMLButtonElement>) => {
    // event.preventDefault();

    const data = { email: inputData.email };

    axios
      .post("api/v1/account/verify/join", data, {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        console.log(response.data);
        setCheckCode(response.data.code);
      })
      .catch((err) => {
        console.log(err.response.data);
      });
  };

  // 패스워드 확인 체크 로직
  const [checkPassword, setCheckPassword] = useState("");

  const handleCheckPassword = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCheckPassword(event.target.value);
  };

  const passwordError =
    checkPassword.length > 0 && inputData.loginPassword !== checkPassword;

  // 입력값 바뀌는거 확인 로직
  const changeInput = (e: ChangeEvent<HTMLInputElement>) => {
    setInputData({
      ...inputData,
      [e.target.name]: e.target.value,
    });
  };

  // 도로명 주소 API 로직
  const onClickAddr = () => {
    new window.daum.Postcode({
      oncomplete: function (data: Addr) {
        (document.getElementById("addr") as HTMLInputElement).value =
          data.address;
        document.getElementById("addrDetail")?.focus();
        setHospitalAddr(data.address);
      },
    }).open();
  };

  // 병원 회원가입 axios요청 부분
  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const hospitalJoinRequest = {
      loginId: inputData.loginId,
      loginPassword: inputData.loginPassword,
      name: inputData.name,
      postalCode: "55055",
      // address값은 바뀔 예정
      address:
        (document.getElementById("addr") as HTMLInputElement).value +
        ", " +
        inputData.address,
      tel: inputData.tel,
      email: inputData.email,
      latitude: lonlat[1],
      longitude: lonlat[0],
    };

    const json = JSON.stringify(hospitalJoinRequest);
    const jsonBlob = new Blob([json], { type: "application/json" });

    const formData = new FormData();
    formData.append("hospitalJoinRequest", jsonBlob);
    console.log(lonlat);

    if (inputImage.profileImg) {
      formData.append("profileImg", inputImage.profileImg);
    }

    if (inputImage.regImg) {
      formData.append("regImg", inputImage.regImg);
    }
    console.log(inputImage.profileImg);
    axios
      .post("api/v1/account/hospital/join", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        console.log(response.data);
        navigate("/");
      })
      .catch((err) => {
        console.log(err.response.data);
      });
  };

  const middle = "flex justify-center items-center";

  return (
    <div className={`${middle} MSignup rounded-b-2xl mt-6 mb-3`}>
      <form onSubmit={handleSubmit} className="" style={{ width: "100%" }}>
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              병원 이름
            </label>
          </div>
          <input
            type="text"
            className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
            placeholder="이름을 입력해주세요"
            onChange={(e) => {
              changeInput(e);
            }}
            name="name"
            value={inputData.name}
          ></input>
        </div>
        <br />
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              ID
            </label>
          </div>
          <input
            type="text"
            className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
            placeholder="아이디를 입력해주세요"
            name="loginId"
            value={inputData.loginId}
            onChange={(e) => {
              changeInput(e);
            }}
          ></input>
        </div>
        <br />
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              PASSWORD
            </label>
          </div>
          <input
            type="password"
            className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
            placeholder="영문자, 숫자, 특수문자 포함 최소 8~20자"
            onChange={(e) => {
              changeInput(e);
            }}
            name="loginPassword"
            value={inputData.loginPassword}
          ></input>
          <br />
          {/* 비밀번호 입력 확인 Logic구성해서 적용해야함 */}
          <input
            type="password"
            className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
            placeholder="비밀번호를 확인해주세요"
            value={checkPassword}
            onChange={handleCheckPassword}
          ></input>
          {passwordError && (
            <span style={{ color: "red" }}>비밀번호가 일치하지 않습니다.</span>
          )}
        </div>
        <br />
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              병원 프로필 사진 등록 (선택)
            </label>
          </div>
          <div className="flex justify-between">
            <input
              id="profilename"
              readOnly
              type="text"
              className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
              placeholder="업로드를 눌러주세요"
              style={{ width: "75%" }}
            ></input>
            <label
              htmlFor="profileImg"
              className="file-input-btn"
              style={{ backgroundColor: "#F2981F" }}
            >
              업로드
              <input
                type="file"
                accept="image/*"
                id="profileImg"
                name="profileImg"
                className="file-input"
                onChange={handleImgChange}
              />
            </label>
          </div>
        </div>
        <br />
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              병원 등록증 (필수)
            </label>
          </div>
          <div className="flex justify-between">
            <input
              id="regname"
              readOnly
              type="text"
              className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
              placeholder="업로드를 눌러주세요"
              style={{ width: "75%" }}
            ></input>
            <label
              htmlFor="regImg"
              className="file-input-btn"
              style={{ backgroundColor: "#F2981F" }}
            >
              업로드
              <input
                type="file"
                accept="image/*"
                id="regImg"
                name="regImg"
                className="file-input"
                onChange={handleImgChange}
              />
            </label>
          </div>
        </div>
        <br />
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              E-mail
            </label>
          </div>
          <div className="flex justify-between">
            <input
              type="text"
              className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
              placeholder="이메일을 입력해주세요"
              name="email"
              value={inputData.email}
              onChange={(e) => {
                changeInput(e);
              }}
              style={{ width: "65%" }}
            ></input>
            <button
              className="button-style hbutton"
              type="button"
              style={{ height: "41.6px", width: "30%" }}
              onClick={emailCertify}
            >
              인증하기
            </button>
          </div>
        </div>
        <br />
        {checkCode && (
          <div className="flex justify-between">
            <input
              type="text"
              className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
              placeholder="인증코드를 입력해주세요"
              onChange={handleCodeChange}
              name="code"
              value={check ? "인증되었습니다" : code}
              readOnly={check}
              style={{ width: "65%" }}
            />
            <button
              className="button-style hbutton"
              type="button"
              style={{ height: "41.6px", width: "30%" }}
              onClick={checkemail}
            >
              확인하기
            </button>
          </div>
        )}
        <br />
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              Address
            </label>
          </div>
          <div className="flex justify-between mb-2">
            <input
              id="addr"
              readOnly
              type="text"
              className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
              placeholder="조회를 눌러주세요"
              style={{ width: "65%" }}
            ></input>
            <button
              className="button-style hbutton"
              type="button"
              style={{ height: "41.6px", width: "30%" }}
              onClick={onClickAddr}
            >
              조회하기
            </button>
          </div>
          <input
            id="addrDetail"
            type="text"
            className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
            placeholder="상세주소 입력란입니다"
            name="address"
            value={inputData.address}
            onChange={(e) => {
              changeInput(e);
            }}
          ></input>
        </div>
        <br />
        <div>
          <div className="flex justify-start">
            <label className="block mb-2 text-sm font-medium text-gray-900 ">
              Phone-Number
            </label>
          </div>
          <input
            type="text"
            className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
            placeholder="전화번호를 입력해주세요"
            name="tel"
            value={inputData.tel}
            onChange={(e) => {
              changeInput(e);
            }}
          ></input>
        </div>
        <br />
        <div className="my-3">
          <Button
            content="회원가입"
            variant="warning"
            type="submit"
            customStyles={{ width: "100%" }}
          />
        </div>
        <div className="mt-4 text-lg font-bold flex-col text-center">
          <span className="text-xl">회원을 등록하고 싶으신가요?</span>
          <br />
          <span className="mx-2">위에</span>
          <span className="text-red">토글 버튼</span>
          <span className=""> 을 통해</span>
          <br />
          <span className="mx-2">일반 회원가입으로 가세요!</span>
          <div className="mt-3">
            <span className="">다른 계정으로 로그인하기</span>
          </div>
        </div>
        <div className="flex justify-around mb-5">
          <Social />
        </div>
      </form>
    </div>
  );
};

export default HospitalSignup;

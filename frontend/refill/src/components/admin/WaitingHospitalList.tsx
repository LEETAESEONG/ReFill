import * as React from "react";
import axios from "axios";
import { RootState } from "store/reducers";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import HospitalDetailModal from "./HospitalDetailModal";
import { WaitingHospitalResponse } from "./adminTypes";

const WaitingHospitalList: React.FC = () => {
  const token: string = useSelector((state: RootState) => state.login.token);
  const islogin: boolean = useSelector(
    (state: RootState) => state.login.islogin,
  );
  const navigate = useNavigate();

  const [hospitals, setHospitals] = React.useState<WaitingHospitalResponse[]>(
    [],
  );
  const [loading, setLoading] = React.useState(true);

  const fetchHospitals = async () => {
    try {
      if (islogin) {
        const response = await axios.get<WaitingHospitalResponse[]>(
          "api/v1/admin/hospitals",
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );
        console.log("ok");
        setHospitals(response.data);
      } else {
        navigate("/");
        alert("접근 권한이 없습니다.");
      }
    } catch (error) {
      console.error("Error fetching hospitals:", error);
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    fetchHospitals();
  }, []);

  const handleAccept = async (id: number) => {
    if (confirm("정말 승인하시겠습니까?")) {
      try {
        const response = await axios.get<string>(
          `api/v1/admin/hospitals/accept/${id}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );
        alert(response.data);
        // 병원 목록을 다시 가져옵니다.
        fetchHospitals();
      } catch (error) {
        console.error("Error accepting hospital:", error);
      }
    } else {
      alert("취소하셨습니다.");
      return;
    }
  };

  const handleReject = async (id: number) => {
    if (confirm("정말 거절하시겠습니까?")) {
      try {
        const response = await axios.get<string>(
          `api/v1/admin/hospitals/reject/${id}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );
        alert(response.data);
        // 병원 목록을 다시 가져옵니다.
        fetchHospitals();
      } catch (error) {
        console.error("Error rejecting hospital:", error);
      }
    } else {
      alert("취소하셨습니다.");
      return;
    }
  };

  const [selectedHospital, setSelectedHospital] =
    React.useState<WaitingHospitalResponse | null>(null);

  const handleOpenModal = (hospital: WaitingHospitalResponse) => {
    setSelectedHospital(hospital);
  };

  const handleCloseModal = () => {
    setSelectedHospital(null);
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <h2 className="text-2xl font-bold mb-4">병원 가입 대기</h2>

      {hospitals.map((hospital) => (
        <div
          key={hospital.id}
          className="flex items-center justify-between mb-4 p-4 border-t border-b min-w-[400px]"
        >
          <img
            src={`/${hospital.hospitalProfileImg}`}
            alt="Profile"
            width="50"
            onClick={() => handleOpenModal(hospital)}
            className="cursor-pointer"
          />
          <div className="ml-4">
            <div className="font-medium">{hospital.name}</div>
            <div className="text-sm text-gray-600">{hospital.address}</div>
          </div>
          <div className="flex space-x-2">
            {" "}
            {/* 버튼들을 묶는 부모 div */}
            <button
              onClick={() => handleAccept(hospital.id)}
              className="text-green-500 border rounded p-2 hover:bg-green-100"
            >
              승인
            </button>
            <button
              onClick={() => handleReject(hospital.id)}
              style={{ color: "red" }}
              className="border rounded p-2 hover:bg-red-100"
            >
              거절
            </button>{" "}
            {/* 임시로 인라인 스타일 사용 */}
            <button
              onClick={() => handleOpenModal(hospital)}
              className="text-blue-500 border rounded p-2 hover:bg-blue-100"
            >
              상세
            </button>
          </div>
        </div>
      ))}

      {selectedHospital && (
        <HospitalDetailModal
          hospital={selectedHospital}
          onClose={handleCloseModal}
        />
      )}
    </>
  );
};

export default WaitingHospitalList;

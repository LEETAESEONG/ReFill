import React, { useEffect, useState } from "react";
import Navbar from "components/Navbar";
import ImgItem from "components/aidiagnosis/resultItems/ImgItem";
import ContentItem from "components/aidiagnosis/resultItems/ContentItem";
import ChartItem from "components/aidiagnosis/resultItems/ChartItem";
import RightImg from "assets/aidiagnosis/right_img.png";
import ContentChild from "components/aidiagnosis/children/ContentChild";
import { useLocation } from "react-router-dom";
import AlertModal from "components/aidiagnosis/children/AlertModal";

const AIDiagnosisResult: React.FC = () => {
  const colOneStart = "col-start-1";
  const colThreeStart = "col-start-3";
  const colFiveStart = "col-start-5";
  const colSevenStart = "col-start-7";

  const colThreeEnd = "col-end-3";
  const colFiveEnd = "col-end-5";
  const colSevenEnd = "col-end-7";
  const colNineEnd = "col-end-9";

  const location = useLocation();

  const [openAlert, setOpenAlert] = useState(false);

  // 전달된 state에서 jsonDataString을 추출
  const jsonDataString = location.state.jsonDataString;
  const jsonData = JSON.parse(jsonDataString);

  // jsonData의 각 정보를 변수에 저장
  const hairLossScore = jsonData.hairLossScore;
  const certainty = jsonData.certainty;
  const modelConfidence = jsonData.modelConfidence;
  const diagnosisImage = jsonData.diagnosisImage;
  const [newPercent, setNewPercent] = useState(0);

  useEffect(() => {
    if (modelConfidence < 80) {
      setNewPercent(modelConfidence);
    } else {
      setNewPercent(certainty);
    }
  }, [modelConfidence, certainty]);

  const getValidityMessage = (conf: number) => {
    if (conf < 80) {
      return "신뢰도가 낮습니다. 올바른 사진에 가까운 사진을 업로드하여 다시 진단 받으시길 바랍니다.";
    }
    return "신뢰도가 높습니다. 올바른 사진을 업로드 하였으며 AI 정확도도 높을 경우 보다 정확한 데이터라고 할 수 있습니다.";
  };

  useEffect(() => {
    window.scrollTo(0, 106);
    if (Number(modelConfidence) < 80) {
      setOpenAlert(true);
    }
  }, []);

  return (
    <div>
      {openAlert ? (
        <AlertModal
          setOpenAlert={setOpenAlert}
          resultValue={Number(modelConfidence)}
        />
      ) : (
        <></>
      )}
      <Navbar />
      <div className="aid-result">
        <div className="px-32">
          <div className="grid grid-rows-2 lg:mx-30 md:mx-20 sm:mx-10">
            <div className="grid grid-cols-8 gap-5">
              <ImgItem
                src={`/${diagnosisImage}`}
                title="현재 사진"
                colStart={colOneStart}
                colEnd={colThreeEnd}
              />
              <ImgItem
                src={`${RightImg}`}
                title="모범 사진"
                colStart={colThreeStart}
                colEnd={colFiveEnd}
                content="*위 모범사진처럼 촬영시 정확도가 상승하여보다 정확한 AI 자가진단이 가능합니다."
              />
              <ChartItem
                result={newPercent}
                title="AI 정확도"
                colStart={colFiveStart}
                colEnd={colSevenEnd}
                content="*올려주신 사진을 바탕으로 진행한 AI 탈모 진행 예측의 
정확도를 계산한 수치입니다."
              />
              <ChartItem
                result={hairLossScore}
                title="탈모 진행도"
                colStart={colSevenStart}
                colEnd={colNineEnd}
                content="*AI가 진단한 탈모 진행 예측 결과입니다."
              />
            </div>
            <div className="grid grid-cols-8 gap-10">
              <ContentItem
                colStart={`${colOneStart}`}
                colEnd={`${colFiveEnd}`}
                title="서비스"
                content1={
                  <ContentChild
                    service={true}
                    content="AI 진단을 다시 해보고 싶다면 아래 버튼을 클릭해주세요."
                    buttonName="AI 사전진단"
                    connectNextLink="/diagnosis"
                  />
                }
                content2={
                  <ContentChild
                    service={true}
                    content="전문의와 상담하시길 원하신다면 아래 버튼을 클릭해주세요."
                    buttonName="상담예약"
                    connectNextLink="/search"
                  />
                }
              />
              <ContentItem
                colStart={`${colFiveStart}`}
                colEnd={`${colNineEnd}`}
                title="상세 결과"
                content1={
                  <ContentChild
                    service={false}
                    content="AI 정확도"
                    subContent="위 모범사진 처럼 여백이 없고 흐릿함이 없는 사진을 업로드
              하게 되시면 더 좋은 결과를 얻을 수 있습니다."
                  />
                }
                content2={
                  <ContentChild
                    service={false}
                    content="신뢰도"
                    subContent={getValidityMessage(modelConfidence)}
                  />
                }
              />
            </div>
            <div className="h-20 w-full bg-sky-300 mb-5 px-14 flex items-center">
              <ul className="list-disc font-black text-black text-base">
                <li>
                  본 AI 진단 서비스는 전문의가 진단한 결과가 아닙니다. 실재 탈모
                  진단은 병원을 방문하셔서 정확한 진단을 받으시거나{" "}
                  <span className="text-orange">AI 상담 서비스</span>틀 통해
                  전문의와 상담하시는 것을 추천드립니다.{" "}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default AIDiagnosisResult;

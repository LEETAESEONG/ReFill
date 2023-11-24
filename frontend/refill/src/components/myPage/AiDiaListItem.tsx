import React from "react";

// AiDiagnosisListResponse
type aiDiagnosisData = {
  id: number;
  diagnosisDate: string;
  hairLossScore: number;
  certainty: number;
  diagnosisImage: string;
};

interface AiDiaListProps {
  aiListItem: aiDiagnosisData;
}

const AiDiaListItem: React.FC<AiDiaListProps> = ({ aiListItem }) => {
  return (
    <div className="ai-list-item flex">
      <div className="h-full aspect-square">
        <img
          className="rounded-md h-full ml-2"
          src={
            `` +
            `${aiListItem.diagnosisImage}`
          }
          alt=""
        />
      </div>
      <div className="pl-10 h-full w-full">
        <p className="text-xs">{aiListItem.diagnosisDate}</p>
        <p className="font-black">AI 정확도 : {aiListItem.certainty}%</p>
        <p className="text-xs">
          *사진을 최대한 정확하게 올려주실수록 AI 정확도가 증가합니다.
        </p>
        <p className="font-black">
          AI 예측 탈모 진행도 : {aiListItem.hairLossScore}%
        </p>
        <p className="text-xs">
          *위 결과는 전문의의 결과가 아닌 AI 가 분석한 결과이므로 참고 용도로만
          사용할 것을 권장드립니다.
        </p>
      </div>
    </div>
  );
};
export default AiDiaListItem;

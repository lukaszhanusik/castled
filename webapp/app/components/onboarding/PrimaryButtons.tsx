import { primaryButton } from "./data/PrimaryButton";

export default function PrimaryButtons({
  stepsToggle,
  btnType,
  doneCount,
}: {
  stepsToggle: () => void;
  btnType: string;
  doneCount: number;
}) {
  return (
    <>
      {primaryButton.map(
        (item) =>
          item.type === btnType && (
            <div
              className="card mb-3 p-3"
              onClick={stepsToggle}
              style={{ cursor: "pointer" }}
            >
              <div className="row text-center">
                <div className="col-1 align-self-center">
                  <img src={item.icon} className="card-img-top p-0" />
                </div>
                <div className="col-7 align-self-center pt-1">
                  <h3>{item.title}</h3>
                </div>
                <div className="col-3 align-self-center">
                  <span>{`${doneCount} of 4 completed`}</span>
                </div>
                <div className="col-1 align-self-center">
                  {">"}
                </div>
              </div>
            </div>
          )
      )}
    </>
  );
}

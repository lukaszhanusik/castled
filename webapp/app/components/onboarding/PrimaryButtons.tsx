import { IconChevronRight } from "@tabler/icons";
import Link from "next/link";
import { primaryButton } from "./data/PrimaryButton";

export default function PrimaryButtons({
  stepsToggle,
  btnType,
}: {
  stepsToggle: () => void;
  btnType: string;
}) {
  return (
    <>
      {primaryButton.map(
        (item) =>
          item.type === btnType && (
            <div className="card mb-4 p-3" onClick={stepsToggle}>
              <Link href="#">
                <a className="row">
                  <div className="col-3">
                    <img src={item.icon} className="card-img-top w-25 p-0" />
                  </div>
                  <div className="col-8">
                    <h3>{item.title}</h3>
                  </div>
                  <div className="col-1">
                    <IconChevronRight size={24} className="text-muted" />
                  </div>
                </a>
              </Link>
            </div>
          )
      )}
    </>
  );
}

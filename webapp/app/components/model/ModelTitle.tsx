import { ModelResponseDto } from "@/app/common/dtos/ModelResponseDto";

export function ModelTitle({
  warehouse,
  activeSyncsCount,
}: Partial<ModelResponseDto>) {
  return (
    <div className="categories m-0">
      <img src={warehouse?.logoUrl} />
      <span className="col-md-10 ps-0 px-4 font-weight-bold">
        {warehouse?.name}
      </span>
      <span className="col-md-10 ps-0 px-4 text-muted">{warehouse?.type}</span>
      <span className="col-md-10 ps-0 px-4">
        Total Syncs - {activeSyncsCount}
      </span>
    </div>
  );
}

export default ModelTitle;

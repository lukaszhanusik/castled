export default function ErrorMessage({
  errors,
  include,
  classNames,
}: {
  errors: any;
  include: string;
  classNames?: string;
}) {
  return (
    <>
      <ul className={`${classNames ? classNames : "mb-0 px-4"}`}>
        {Object.keys(errors).map(
          (key) =>
            key.includes(include) && (
              <li key={key} className="text-danger">
                {errors[key]}
              </li>
            )
        )}
      </ul>
    </>
  );
}

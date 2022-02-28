export default function ErrorMessage({
  errors,
  include,
}: {
  errors: any;
  include: string;
}) {
  return (
    <>
      <ul>
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

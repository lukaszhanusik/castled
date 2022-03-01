export default function ErrorMessage({
  errors,
  include,
}: {
  errors: any;
  include: string;
}) {
  return (
    <>
      <ul className="ml-4 my-2">
        {Object.keys(errors).map(
          (key) =>
            key.includes(include) && (
              <li key={key} className="text-danger text-sm">
                {errors[key]}
              </li>
            )
        )}
      </ul>
    </>
  );
}

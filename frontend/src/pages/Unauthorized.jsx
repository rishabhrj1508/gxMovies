import { useNavigate } from "react-router-dom";

const Unauthorized = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center h-screen text-center">
    <img src="https://cdn.dribbble.com/users/761395/screenshots/6287961/error_401.jpg" width={400}/>
      <h1 className="text-3xl font-semibold text-red-500">Unauthorized Access</h1>
      <p className="mt-2">
        You do not have permission to view this page.
      </p>

      <div className="mt-4">
        <button
          onClick={() => navigate(-1)}
          className="px-4 py-2 bg-blue-500 text-white rounded-md mr-2"
        >
          Go Back
        </button>
      </div>
    </div>
  );
};

export default Unauthorized;

import { useNavigate } from "react-router-dom";

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center h-screen text-center">
      <img src="/images/pagenotfound.png" width={400}></img>
      <h1 className="text-3xl font-semibold text-red-500">Page Not Found</h1>
      <p className="mt-2">The page you are looking for does not exist.</p>

      <div className="mt-4">
        <button
          onClick={() => navigate(-1)}
          className="px-4 py-2 bg-blue-500 text-white rounded-md"
        >
          Go Back
        </button>
      </div>
    </div>
  );
};

export default NotFound;

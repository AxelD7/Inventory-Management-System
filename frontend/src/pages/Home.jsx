import { useAuth } from "../context/AuthContext";


function Home() {
  const { user, logout } = useAuth();

  return (
    <div className="home">
      <h2>welcome home</h2>
      <div className="bg-slate-100 p-4 rounded-lg border mb-6">
        <h2 className="text-xl font-semibold mb-2">Logged-in User Profile:</h2>
        <p>
          <strong>Email:</strong> {user || "Unknown"}
        </p>
      </div>
      <button
        onClick={logout}
        className="bg-red-600 text-white px-4 py-2 rounded font-medium hover:bg-red-700"
      >
        Log Out
      </button>{" "}
    </div>
  );
}

export default Home;

import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Signin() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!email || !password) {
      setError("Please enter a email and a password.");
      return;
    }
    try {
      await login(email, password);
      console.log("Sign in successful.");
      navigate("/");
    } catch (err) {
      console.error("Log in failed", err);
      if (err.response?.data?.message !== "Bad credentials") {
        setError(err.response.data.message);
      } else {
        setError("Invalid email or password.");
      }
    }
  };

  return (
    <div className="login border-2 max-w-md rounded-xl bg-white border-slate-200 p-6 shadow-sm flex-col">
      <div className="login-title mb-5">
        <h2 className="font-bold text-2xl text-center mb-3">
          Asset Management System
        </h2>
        <hr />
      </div>
      <div className="login-form gap-8 items-center">
        <form>
          <label className="text-xl block">Email:</label>
          <input
            className="emai w-full mb-2 border-2 rounded-md border-slate-600 bg-gray-200"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          ></input>
          <label className="text-xl block">Password:</label>
          <input
            className="password w-full mb-2 border-2 rounded-md border-slate-600 bg-gray-200"
            placeholder="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          ></input>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 rounded mb-4 text-sm">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="bg-blue-600 w-full border-2 rounded-2xl border-gray-500"
            onClick={handleLogin}
          >
            Log in
          </button>
        </form>
      </div>
    </div>
  );
}

export default Signin;

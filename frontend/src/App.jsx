
import Home from "./pages/Home";
import Signin from "./pages/Signin";

import { Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";

function App() {
  return (
    <AuthProvider>
      <div className="main-content w-full min-h-screen flex flex-col items-center justify-center bg-slate-100">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/signin" element={<Signin />} />
        </Routes>
      </div>
    </AuthProvider>
  );
}

export default App;

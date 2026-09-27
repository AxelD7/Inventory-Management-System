import Dashboard from "./pages/Dashboard";
import Signin from "./pages/Signin";
import Navbar from "./components/Navbar";

import { Navigate, Routes, Route } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { NotificationProvider } from "./context/NotificationProvider";

function HomeRoute() {
  const { accessToken } = useAuth();
  return accessToken ? <Dashboard /> : <Navigate to="/signin" replace />;
}

function App() {
  return (
    <AuthProvider>
      <NotificationProvider>
        <div className="main-content w-full min-h-screen flex flex-col bg-slate-100">
          <Navbar />
          <div className="w-full flex-1 flex flex-col items-center">
            <Routes>
              <Route path="/" element={<HomeRoute />} />
              <Route path="/signin" element={<Signin />} />
            </Routes>
          </div>
        </div>
      </NotificationProvider>
    </AuthProvider>
  );
}

export default App;

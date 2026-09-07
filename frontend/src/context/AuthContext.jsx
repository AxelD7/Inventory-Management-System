import {
  createContext,
  useContext,
  useState,
  useEffect,
  useLayoutEffect,
} from "react";
import { axiosClient } from "../api/axiosClient";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [accessToken, setAccessToken] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const response = await axiosClient.post("/auth/refresh");
        setAccessToken(response.data.accessToken);
        setUser(response.data.user);
      } catch (error) {
        setAccessToken(null);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, []);

  const login = async (email, password) => {
    const response = await axiosClient.post("/auth/signin", {
      email,
      password,
    });
    setAccessToken(response.data.accessToken);
    setUser(response.data.user);
    return response.data;
  };

  const logout = async () => {
    try {
      await axiosClient.post("/auth/logout");
    } catch (err) {
      console.error("Logout error on backend:", err);
    } finally {
      setAccessToken(null);
      setUser(null);
      navigate("/signin");
    }
  };

  useLayoutEffect(() => {
    const authInterceptor = axiosClient.interceptors.request.use((config) => {
      if (accessToken && !config.headers.Authorization) {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }
      return config;
    });

    return () => {
      axiosClient.interceptors.request.eject(authInterceptor);
    };
  }, [accessToken]);

  useLayoutEffect(() => {
    const refreshInterceptor = axiosClient.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        if (
          error.response?.status === 401 &&
          !originalRequest?._retry &&
          !originalRequest?.url?.includes("/auth/signin") &&
          !originalRequest?.url?.includes("/auth/refresh") &&
          !originalRequest?.url?.includes("/auth/logout")
        ) {
          originalRequest._retry = true;

          try {
            const response = await axiosClient.post("/auth/refresh");
            const newAccessToken = response.data.accessToken;

            setAccessToken(newAccessToken);
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

            return axiosClient(originalRequest);
          } catch (refreshError) {
            setAccessToken(null);
            setUser(null);
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      },
    );

    return () => {
      axiosClient.interceptors.response.eject(refreshInterceptor);
    };
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 text-slate-600 font-medium text-sm">
        Loading app...
      </div>
    );
  }

  return (
    <AuthContext.Provider
      value={{
        accessToken,
        setAccessToken,
        user,
        login,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an <AuthProvider>");
  }
  return context;
};

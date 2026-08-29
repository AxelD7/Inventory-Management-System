import {
  createContext,
  useContext,
  useState,
  useEffect,
  useLayoutEffect,
} from "react";
import { axiosClient } from "../api/axiosClient";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [accessToken, setAccessToken] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const response = await axiosClient.get("/auth/refresh");
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
    } finally {
      setAccessToken(null);
      setUser(null);
      alert("logout successful")
    }
  };

  useLayoutEffect(() => {
    const authInterceptor = axiosClient.interceptors.request.use((config) => {
      config.headers.Authorization =
        !config._retry && accessToken
          ? `Bearer ${accessToken}`
          : config.headers.Authorization;
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
          !originalRequest._retry &&
          !originalRequest.url.includes("/auth/signin") &&
          !originalRequest.url.includes("/auth/refresh") &&
          !originalRequest.url.includes("/auth/logout")
        ) {
          originalRequest._retry = true;

          try {
            const response = await axiosClient.get("/auth/refresh");
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

  return (
    <AuthContext.Provider
      value={{ accessToken, setAccessToken, user, login, logout, loading }}
    >
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

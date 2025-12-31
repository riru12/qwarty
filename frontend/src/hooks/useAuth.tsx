import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "@contexts/AuthContext";
import { apiService } from "@services/api/ApiService";
import {
    LoginEndpoint,
    RefreshEndpoint,
    SignupEndpoint,
    LogoutEndpoint
} from "@/services/api/endpoints";
import type { EndpointReq } from "@/services/api/endpoints/endpoint";

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    const navigate = useNavigate();

    if (!ctx) {
        throw new Error("useAuth must be used within an AuthProvider");
    }

    /**
     * Use ApiService to make a call to LoginEndpoint, setting the
     * content's AuthStates upon successful login
     */
    const login = async (payload: EndpointReq<typeof LoginEndpoint>) => {
        const response = await apiService.call(LoginEndpoint, payload);
        ctx.setAuthStates(response.accessToken, response.username);
        navigate("/");
    };

    const signup = async (payload: EndpointReq<typeof SignupEndpoint>) => {
        await apiService.call(SignupEndpoint, payload);
    };

    const refresh = async () => {
        const response = await apiService.call(RefreshEndpoint);
        ctx.setAuthStates(response.accessToken, ctx.username);
    };

    const logout = async() => {
        await apiService.call(LogoutEndpoint);
        ctx.setAuthStates(null, null);
        navigate("/");
    }

    return { ...ctx, login, signup, refresh, logout };
};

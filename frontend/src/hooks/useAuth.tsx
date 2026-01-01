import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "@contexts/AuthContext";
import { apiService } from "@services/api/ApiService";
import {
    LoginEndpoint,
    RefreshEndpoint,
    SignupEndpoint,
    LogoutEndpoint,
    GuestEndpoint
} from "@/services/api/endpoints";
import type { EndpointReq } from "@/services/api/endpoints/endpoint";
import { IdentityEndpoint } from "@/services/api/endpoints/IdentityEndpoint";

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
        await apiService.call(LoginEndpoint, payload);
        await me(); // hydrate auth state after logging in
        navigate("/");
    };

    const signup = async (payload: EndpointReq<typeof SignupEndpoint>) => {
        await apiService.call(SignupEndpoint, payload);
    };

    /**
     * Refreshes the user's access token using their refresh token (httpOnly cookie).
     * Deduplicates concurrent requests within the same page instance.
     */
    let refreshing: Promise <void> | null = null;
    const refresh = async () => {
        if (refreshing !== null) {
            return refreshing;
        }

        refreshing = (async () => {
            try {
                await apiService.call(RefreshEndpoint);
            } finally {
                refreshing = null;
            }
        })();
        return refreshing;
    };

    const logout = async () => {
        ctx.setAuthState(null);
        await apiService.call(LogoutEndpoint);
        navigate("/");
    };

    const guest = async () => {
        await apiService.call(GuestEndpoint);
    };

    const me = async() => {
        const response = await apiService.call(IdentityEndpoint);
        ctx.setAuthState(response);
    };

    return { ...ctx, login, signup, refresh, logout, guest, me };
};

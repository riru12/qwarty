import { useContext } from "react";
import { AuthContext } from "@contexts/AuthContext";
import { apiService } from "@services/api/ApiService";
import {
    LoginEndpoint,
    RefreshEndpoint,
    SignupEndpoint
} from "@/services/api/endpoints";
import type { EndpointReq } from "@/services/api/endpoints/endpoint";

export const useAuth = () => {
    const ctx = useContext(AuthContext);
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
    };

    const signup = async (payload: EndpointReq<typeof SignupEndpoint>) => {
        await apiService.call(SignupEndpoint, payload);
    };

    const refresh = async () => {
        const response = await apiService.call(RefreshEndpoint);
        console.log(response.accessToken);
        ctx.setAuthStates(response.accessToken, ctx.username);
    };

    return { ...ctx, login, signup, refresh };
};

import { createContext } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import type { EndpointRes } from "@interfaces/api/endpoint";
import { IdentityEndpoint, type UserType } from "@interfaces/api/endpoints/IdentityEndpoint";
import { apiClient } from "@utils/ApiClient";

interface AuthContextType {
    getAuthState: () => { username: string | null; userType: UserType };
    setAuthState: (identity: EndpointRes<typeof IdentityEndpoint> | null) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

const IDENTITY_QUERY_KEY = ["auth", "identity"];

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const queryClient = useQueryClient();

    const { data: identity } = useQuery({
        queryKey: IDENTITY_QUERY_KEY,
        queryFn: () => apiClient.call(IdentityEndpoint),
        staleTime: 5 * 60 * 1000, // 5 minutes
        gcTime: 10 * 60 * 1000, // 10 minutes
    });

    const setAuthState = (identity: EndpointRes<typeof IdentityEndpoint> | null) => {
        queryClient.setQueryData(IDENTITY_QUERY_KEY, identity);
    };

    const getAuthState = () => ({
        username: identity?.username ?? null,
        userType: identity?.userType ?? "ANON",
    });

    return (
        <AuthContext.Provider
            value={{
                getAuthState,
                setAuthState
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

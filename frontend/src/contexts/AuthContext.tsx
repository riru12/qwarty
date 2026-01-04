import { createContext, useEffect, useState } from "react";
import type { EndpointRes } from "@interfaces/api/endpoint";
import { IdentityEndpoint, type UserType } from "@interfaces/api/endpoints/IdentityEndpoint";
import { apiClient } from "@utils/ApiClient";

interface AuthContextType {
    getAuthState: () => { username: string | null; userType: UserType };
    setAuthState: (identity: EndpointRes<typeof IdentityEndpoint> | null) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [username, setUsername] = useState<string | null>(null);
    const [userType, setUserType] = useState<UserType>("ANON");

    useEffect(() => {
        const initAuthState = async () => {
            setAuthState(await apiClient.call(IdentityEndpoint));
        };
        initAuthState();
    });

    const clearAuthState = () => {
        setUsername(null);
        setUserType("ANON");
    };

    const updateAuthState = (user: string, userType: UserType) => {
        setUsername(user);
        setUserType(userType);
    };

    const setAuthState = (identity: EndpointRes<typeof IdentityEndpoint> | null) => {
        if (identity === null) {
            clearAuthState();
            return;
        }

        updateAuthState(identity.username, identity.userType);
    };

    const getAuthState = () => ({ username, userType });

    return (
        <AuthContext.Provider
            value={{
                getAuthState,
                setAuthState,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

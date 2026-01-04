import { createContext, useState } from "react";
import type { EndpointRes } from "@interfaces/api/endpoint";
import type { IdentityEndpoint } from "@interfaces/api/endpoints/IdentityEndpoint";

interface AuthContextType {
    getAuthState: () => { username: string | null; isGuest: boolean };
    setAuthState: (identity: EndpointRes<typeof IdentityEndpoint> | null) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [username, setUsername] = useState<string | null>(null);
    const [isGuest, setIsGuest] = useState<boolean>(true);

    const clearAuthState = () => {
        setUsername(null);
        setIsGuest(true);
    };

    const updateAuthState = (user: string, guest: boolean) => {
        setUsername(user);
        setIsGuest(guest);
    };

    const setAuthState = (identity: EndpointRes<typeof IdentityEndpoint> | null) => {
        if (identity === null) {
            clearAuthState();
            return;
        }

        updateAuthState(identity.username, identity.isGuest);
    };

    const getAuthState = () => ({ username, isGuest });

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

import { createContext, useRef, useState } from "react";

interface AuthContextType {
    accessToken: React.RefObject<string | null>;
    username: string | null;
    isGuest: boolean;
    setAuthState: (token: string | null) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(
    undefined
);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const accessToken = useRef<string | null>(
        localStorage.getItem("accessToken")
    );
    const [username, setUsername] = useState<string | null>(() =>
        localStorage.getItem("username")
    );
    const [isGuest, setIsGuest] = useState<boolean>(
        () => localStorage.getItem("isGuest") === "true"
    );

    const parseJwt = (token: string) => {
        var base64Url = token.split(".")[1];
        var base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
        var jsonPayload = decodeURIComponent(
            window
                .atob(base64)
                .split("")
                .map(function (c) {
                    return (
                        "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)
                    );
                })
                .join("")
        );

        return JSON.parse(jsonPayload);
    };

    const clearAuthState = () => {
        accessToken.current = null;
        setUsername(null);
        setIsGuest(true);

        localStorage.removeItem("accessToken");
        localStorage.removeItem("username");
        localStorage.removeItem("isGuest");
    };

    const updateAuthState = (token: string, user: string, guest: boolean) => {
        accessToken.current = token;
        setUsername(user);
        setIsGuest(guest);

        localStorage.setItem("accessToken", token);
        localStorage.setItem("username", user);
        localStorage.setItem("isGuest", String(guest));
    };

    const setAuthState = (accessToken: string | null) => {
        if (accessToken === null) {
            clearAuthState();
            return;
        }

        try {
            const parsedJwt = parseJwt(accessToken);
            updateAuthState(
                accessToken,
                parsedJwt.sub,
                Boolean(parsedJwt.guest)
            );
        } catch (error) {
            clearAuthState();
        }
    };

    return (
        <AuthContext.Provider
            value={{
                accessToken,
                username,
                isGuest,
                setAuthState
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

import { createContext } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { IdentityEndpoint, type UserType } from "@interfaces/api/endpoints/IdentityEndpoint";
import { apiClient } from "@utils/ApiClient";

/**
 * Public interface exposed by AuthContext.
 */
interface AuthContextType {
    getAuthState: () => { username: string | null; userType: UserType };
    updateAuthState: (options?: { clear?: boolean }) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

const IDENTITY_QUERY_KEY = ["auth", "identity"];
export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const queryClient = useQueryClient();
    const { data: identity } = useQuery({
        queryKey: IDENTITY_QUERY_KEY,
        queryFn: () => apiClient.call(IdentityEndpoint),
    });

    const getAuthState = () => ({
        username: identity?.username ?? null,
        userType: identity?.userType ?? "ANON",
    });

    /**
     * Performs a call to check the user's identity and updates the cached identity state.
     *
     * @param options optional parameter:
     *  - clear: if true, clears the cached identity instead of fetching it.
     *
     * Usage:
     *  await updateAuthState();        // fetch and update identity
     *  await updateAuthState({ clear: true }); // clear cached identity
     */
    const updateAuthState = async ({ clear = false }: { clear?: boolean } = {}) => {
        if (clear) {
            queryClient.setQueryData(IDENTITY_QUERY_KEY, null);
            return;
        }

        try {
            const identity = await apiClient.call(IdentityEndpoint);
            queryClient.setQueryData(IDENTITY_QUERY_KEY, identity);
        } catch {
            // TODO: Add Toast
            queryClient.setQueryData(IDENTITY_QUERY_KEY, null);
        }
    };

    return (
        <AuthContext.Provider
            value={{
                getAuthState,
                updateAuthState,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

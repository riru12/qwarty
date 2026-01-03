import AppRoutes from "./routes/AppRoutes";
import "./App.css";
import { NavBar } from "@/components";
import { useEffect } from "react";
import { useApi, useAuth } from "@/hooks";
import { IdentityEndpoint } from "./services/api/endpoints";

function App() {
    const api = useApi();
    const auth = useAuth();

    // on render, hydrate the auth state
    useEffect(() => {
        const initAuth = async () => {
            try {
                // avoid falling back to guest token if identity check fails, treat as anonymous and grant token on first meaningful action
                auth.setAuthState(
                    await api.call(IdentityEndpoint, undefined, {
                        fallbackToGuest: false
                    })
                );
            } catch {
                auth.setAuthState(null);
            }
        };
        initAuth();
    }, []);

    return (
        <>
            <NavBar />
            <main className="app-content">
                <AppRoutes />
            </main>
        </>
    );
}

export default App;

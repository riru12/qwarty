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
                auth.setAuthState(
                    await api.call(IdentityEndpoint, undefined, { retry: true })
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

import AppRoutes from "./routes/AppRoutes";
import "./App.css";
import { NavBar } from "@/components";
import { useEffect } from "react";
import { useAuth } from "@/hooks";

function App() {
    const auth = useAuth();

    // on render, hydrate the auth state
    useEffect(() => {
        const initAuth = async () => {
            try {
                await auth.refresh();
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

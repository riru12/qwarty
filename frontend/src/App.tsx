import AppRoutes from "./routes/AppRoutes";
import "./App.css";
import { NavBar } from "@/components";

function App() {
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

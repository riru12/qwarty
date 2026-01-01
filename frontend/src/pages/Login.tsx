import { SignupPane, LoginPane } from "@/components";
import "./Login.css";
import { useEffect } from "react";
import { useApi } from "@/hooks";
import { GameEndpoint } from "@/services/api/endpoints/GameEndpoint";

export function Login() {
    const api = useApi();
    useEffect(() => {
        api.call(GameEndpoint);
    }, [])

    return (
        <div className="page-container">
            <div className="pane-container">
                <SignupPane />
                <LoginPane />
            </div>
        </div>
    );
}

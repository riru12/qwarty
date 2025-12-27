import { SignupPane, LoginPane } from "@/components";
import "./Login.css";

export function Login() {
    return (
        <div className="page-container">
            <div className="pane-container">
                <SignupPane />
                <LoginPane />
            </div>
        </div>
    );
}

import { LoginPane } from "@components/ui/LoginPane";
import { SignupPane } from "@components/ui/SignupPane";
import "@components/styles/Login.css";

export const Login = () => {
    return (
        <div className="login-container">
            <div className="pane-container">
                <SignupPane />
                <LoginPane />
            </div>
        </div>
    );
};

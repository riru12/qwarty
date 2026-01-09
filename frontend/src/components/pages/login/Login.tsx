import { LoginPane } from "@components/pages/login/LoginPane";
import { SignupPane } from "@components/pages/login/SignupPane";
import "./Login.css";

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

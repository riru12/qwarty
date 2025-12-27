import { useApi, useAuth } from "@/hooks";
import { Link, useNavigate } from "react-router-dom";
import { LogoutEndpoint } from "@/services/api/endpoints";
import "./NavBar.css";

export function NavBar() {
    const { username, isGuest, setAuthState } = useAuth();
    const api = useApi();
    const nav = useNavigate();

    const logout = async () => {
        setAuthState(null);
        await api.call(LogoutEndpoint);
        nav("/");
    };

    return (
        <div className="navbar">
            <div className="navbar-inner">
                <div className="navbar-left">
                    <Link className="link" to="/">
                        qwarty
                    </Link>
                </div>
                <div className="navbar-right">
                    {username && <span className="username">{username}</span>}
                    {isGuest ? (
                        <Link className="link" to="/login">
                            log in
                        </Link>
                    ) : (
                        <button className="logout-button" onClick={logout}>
                            Log out
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

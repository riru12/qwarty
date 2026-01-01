import { useAuth } from "@/hooks";
import { Link } from "react-router-dom";
import "./NavBar.css";

export function NavBar() {
    const { username, isGuest, logout } = useAuth();

    return (
        <div className="navbar">
            <div className="navbar-inner">
                <div className="navbar-left">
                    <Link className="link" to="/">
                        qwarty
                    </Link>
                </div>
                <div className="navbar-right">
                    {username && (<span className="username">{username}</span>)}
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

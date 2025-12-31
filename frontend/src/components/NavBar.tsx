import { useAuth } from "@/hooks";
import { Link } from "react-router-dom";
import "./NavBar.css";

export function NavBar() {
    const { username } = useAuth();

    return (
        <div className="navbar">
            <div className="navbar-inner">
                <div className="navbar-left">
                    <Link className="link" to="/">
                        qwarty
                    </Link>
                </div>
                <div className="navbar-right">
                    {username}
                    {!username && (
                        <Link className="link" to="/login">
                            log in
                        </Link>
                    )}
                </div>
            </div>
        </div>
    );
}

import { Link } from "@tanstack/react-router";
import "../styles/404NotFound.css";

export const NotFound = () => {
    return (
        <div className="not-found-container">
            <h1>404</h1>
            <Link className="link" to="/">return to qwarty</Link>
        </div>
    )
}
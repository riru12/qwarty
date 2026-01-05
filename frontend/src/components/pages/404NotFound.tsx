import { Link } from "@tanstack/react-router";
import "@components/styles/404NotFound.css";

export const NotFound = () => {
    return (
        <div className="not-found-container">
            <h1 className="not-found-404">404</h1>
            <Link className="link" to="/">
                return to qwarty
            </Link>
        </div>
    );
};

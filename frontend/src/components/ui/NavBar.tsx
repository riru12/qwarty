import { Link } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import "@config/i18n";
import "../styles/NavBar.css";

export const NavBar = () => {
    const { t } = useTranslation(["global"]);

    return (
        <div className="navbar">
            <div className="navbar-inner">
                <div className="navbar-left">
                    <Link className="link" to="/">
                        qwarty
                    </Link>
                </div>
                <div className="navbar-right">
                    <Link className="link" to="/login">
                        {t("login")}
                    </Link>
                </div>
            </div>
        </div>
    );
};

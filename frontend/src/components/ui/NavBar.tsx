import { Link } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import "@config/i18n";
import "../styles/NavBar.css";
import { useAuth } from "@hooks/useAuth";
import { apiClient } from "@utils/ApiClient";
import { LogoutEndpoint } from "@interfaces/api/endpoints";

export const NavBar = () => {
    const { t } = useTranslation(["global"]);
    const { getAuthState, updateAuthState } = useAuth();

    const auth = getAuthState();

    const handleLogout = async () => {
        updateAuthState({ clear: true });
        await apiClient.call(LogoutEndpoint);
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
                    {auth.userType === "USER" ? (
                        <>
                            <span className="navbar-username">{auth.username}</span>
                            <button className="link logout-button" onClick={handleLogout}>
                                {t("logout")}
                            </button>
                        </>
                    ) : (
                        <Link className="link" to="/login">
                            {t("login")}
                        </Link>
                    )}
                </div>
            </div>
        </div>
    );
};

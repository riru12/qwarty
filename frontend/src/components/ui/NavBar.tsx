import { useTranslation } from "react-i18next";
import { useAuth } from "@hooks/useAuth";
import { Link } from "@tanstack/react-router";
import { apiClient } from "@utils/ApiClient";
import { LogoutEndpoint } from "@interfaces/endpoints";
import "@config/i18n";
import "./NavBar.css";

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

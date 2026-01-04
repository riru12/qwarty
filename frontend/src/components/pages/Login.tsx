import { useTranslation } from "react-i18next";
import "@config/i18n";

export const Login = () => {
    const { t } = useTranslation(["login"]);

    return (
        <div>{t("login")}</div>
    )
}
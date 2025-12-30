import "@config/i18n";
import { useState } from "react";
import { useApi } from "@hooks";
import { useTranslation } from "react-i18next";
import { Button, Input, PasswordInput } from "@/components";
import { LoginEndpoint } from "@services/api/endpoints";
import "./Login.css";

export function Login() {
    const [formUsername, setFormUsername] = useState("");
    const [formPassword, setFormPassword] = useState("");
    const { t } = useTranslation(["login"]);
    const api = useApi();

    async function requestLogin() {
        try {
            await api.call(LoginEndpoint, {
                username: formUsername,
                password: formPassword
            });

            setFormUsername("");
            setFormPassword("");
        } catch (error) {
            alert("Log in failed. Please try again.");
        }
    }

    return (
        <div className="login-container">
            <h1>{t("login")}</h1>
            <Input
                value={formUsername}
                setValue={setFormUsername}
                placeholder={t("username")}
            />
            <PasswordInput
                value={formPassword}
                setValue={setFormPassword}
                placeholder={t("password")}
            />
            <Button label={t("login")} onClick={requestLogin} />
        </div>
    );
}

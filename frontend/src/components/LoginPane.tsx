import "@config/i18n";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useApi, useAuth } from "@/hooks";
import { Button, Input, PasswordInput } from "@components";
import { IdentityEndpoint, LoginEndpoint } from "@/services/api/endpoints";
import "./LoginPane.css";

type LoginFormState = {
    username: string;
    password: string;
};

export function LoginPane() {
    const api = useApi();
    const auth = useAuth();
    const nav = useNavigate();
    const { t } = useTranslation(["login"]);
    const [form, setForm] = useState<LoginFormState>({
        username: "",
        password: ""
    });

    function updateField<K extends keyof LoginFormState>(
        key: K,
        value: LoginFormState[K]
    ) {
        setForm((prev) => ({
            ...prev,
            [key]: value
        }));
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        try {
            await api.call(LoginEndpoint, form);
            auth.setAuthState(await api.call(IdentityEndpoint));
            nav("/");
        } catch (error) {
            alert("Log in failed. Please try again.");
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>{t("login")}</h2>
            <div className="field-container">
                <Input
                    value={form.username}
                    onChange={(e) => updateField("username", e.target.value)}
                    placeholder={t("username")}
                />
                <PasswordInput
                    value={form.password}
                    onChange={(e) => updateField("password", e.target.value)}
                    placeholder={t("password")}
                />
                <Button type="submit" label={t("login")} />
            </div>
        </form>
    );
}

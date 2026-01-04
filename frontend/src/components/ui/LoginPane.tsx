import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useMutation } from "@tanstack/react-query";
import { apiClient } from "@utils/ApiClient";
import { useAuth } from "@hooks/useAuth";
import { IdentityEndpoint, LoginEndpoint } from "@interfaces/api/endpoints";
import { Input, PasswordInput, Button } from "@components/ui";
import "@config/i18n";
import "../styles/LoginPane.css";

type LoginFormState = {
    username: string;
    password: string;
};

export function LoginPane() {
    const { t } = useTranslation(["login"]);
    const { setAuthState } = useAuth();
    const [form, setForm] = useState<LoginFormState>({ username: "", password: "" });

    const loginMutation = useMutation({
        mutationFn: async () => {
            return apiClient.call(LoginEndpoint, form);
        },
        onSuccess: async () => {
            try {
                const user = await apiClient.call(IdentityEndpoint);
                setAuthState(user);
                console.log(user);
            } catch (err) {
                console.error("Failed to fetch user identity:", err);
            }
        },
        onError: (error: any) => {
            console.error("Login failed:", error);
            alert(t("login_failed") || "Log in failed. Please try again.");
        },
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        loginMutation.mutate();
    };

    return (
        <form onSubmit={handleSubmit} className="field-container">
            <h2>{t("login")}</h2>

            <Input name="username" placeholder={t("username")} value={form.username} onChange={handleChange} />

            <PasswordInput name="password" placeholder={t("password")} value={form.password} onChange={handleChange} />

            <Button type="submit" label={t("login")} />
        </form>
    );
}

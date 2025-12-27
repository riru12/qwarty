import "@config/i18n";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useApi } from "@/hooks";
import { Button, Input, PasswordInput } from "@components";
import { SignupEndpoint } from "@/services/api/endpoints";

type SignupFormState = {
    username: string;
    email: string;
    verifyEmail: string;
    password: string;
    verifyPassword: string;
};

export function SignupPane() {
    const api = useApi();
    const { t } = useTranslation(["login"]);
    const [form, setForm] = useState<SignupFormState>({
        username: "",
        email: "",
        verifyEmail: "",
        password: "",
        verifyPassword: ""
    });

    function updateField<K extends keyof SignupFormState>(
        key: K,
        value: SignupFormState[K]
    ) {
        setForm((prev) => ({
            ...prev,
            [key]: value
        }));
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        try {
            await api.call(SignupEndpoint, {
                username: form.username,
                email: form.email,
                password: form.password
            });
        } catch (error) {
            alert("Sign up failed. Please try again.");
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>{t("register")}</h2>
            <div className="field-container">
                <Input
                    value={form.username}
                    onChange={(e) => updateField("username", e.target.value)}
                    placeholder={t("username")}
                    error="test"
                />
                <Input
                    value={form.email}
                    onChange={(e) => updateField("email", e.target.value)}
                    placeholder={t("email")}
                />
                <Input
                    value={form.verifyEmail}
                    onChange={(e) => updateField("verifyEmail", e.target.value)}
                    placeholder={t("verify.email")}
                />
                <PasswordInput
                    value={form.password}
                    onChange={(e) => updateField("password", e.target.value)}
                    placeholder={t("password")}
                />
                <PasswordInput
                    value={form.verifyPassword}
                    onChange={(e) =>
                        updateField("verifyPassword", e.target.value)
                    }
                    placeholder={t("verify.password")}
                />
                <Button type="submit" label={t("signup")} />
            </div>
        </form>
    );
}

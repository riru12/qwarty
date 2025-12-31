import "@config/i18n";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useApi } from "@/hooks";
import { Button, Input, PasswordInput } from "@components";
import { SignupEndpoint } from "@/services/api/endpoints";

type SignupFormState = {
    email: string;
    username: string;
    password: string;
};

export function SignupPane() {
    const { t } = useTranslation(["login"]);
    const api = useApi();
    const [form, setForm] = useState<SignupFormState>({
        email: "",
        username: "",
        password: ""
    });

    function updateField<K extends keyof SignupFormState>(key: K, value: SignupFormState[K]) {
        setForm((prev) => ({
            ...prev,
            [key]: value
        }));
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        try {
            await api.call(SignupEndpoint, form);
        } catch (error) {
            alert("Log in failed. Please try again.");
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>{t("register")}</h2>
            <Input
                value={form.email}
                onChange={(e) => updateField("email", e.target.value)}
                placeholder={t("email")}
            />
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
            <Button type="submit" label={t("signup")}/>
        </form>
    );
}

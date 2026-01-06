import "@config/i18n";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useMutation } from "@tanstack/react-query";
import { apiClient } from "@utils/ApiClient";
import { Input, PasswordInput, Button } from "@components/ui";
import { SignupEndpoint } from "@interfaces/api/endpoints";
import "../styles/SignupPane.css";

type SignupFormState = {
    username: string;
    email: string;
    verifyEmail: string;
    password: string;
    verifyPassword: string;
};

export function SignupPane() {
    const { t } = useTranslation(["login"]);
    const [form, setForm] = useState<SignupFormState>({
        username: "",
        email: "",
        verifyEmail: "",
        password: "",
        verifyPassword: "",
    });
    const [errors, setErrors] = useState<Partial<SignupFormState>>({});

    // TODO: Add Toast
    const signupMutation = useMutation({
        mutationFn: async () => {
            return apiClient.call(SignupEndpoint, {
                payload: {
                    username: form.username,
                    email: form.email,
                    password: form.password,
                },
            });
        },
        onSuccess: () => {
            alert(t("signup_success") || "Sign up successful! You can now log in.");
        },
        onError: (error: any) => {
            const problem = error?.problemDetail;

            if (problem?.errors?.length) {
                const backendErrors: Partial<SignupFormState> = {};
                problem.errors.forEach((err: { pointer: string; detail: string }) => {
                    const field = err.pointer as keyof SignupFormState;
                    backendErrors[field] = err.detail;
                });

                setErrors(backendErrors);
            }
        },
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        // Simple client-side validation
        const newErrors: Partial<SignupFormState> = {};
        if (!form.username) newErrors.username = t("username_required");
        if (!form.email) newErrors.email = t("email_required");
        if (form.email !== form.verifyEmail) newErrors.verifyEmail = t("emails_do_not_match");
        if (!form.password) newErrors.password = t("password_required");
        if (form.password !== form.verifyPassword) newErrors.verifyPassword = t("passwords_do_not_match");

        setErrors(newErrors);

        if (Object.keys(newErrors).length === 0) {
            signupMutation.mutate();
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>{t("register")}</h2>
            <div className="field-container">
                <Input name="username" placeholder={t("username")} value={form.username} onChange={handleChange} error={errors.username} />
                <Input name="email" placeholder={t("email")} value={form.email} onChange={handleChange} error={errors.email} />
                <Input name="verifyEmail" placeholder={t("verify.email")} value={form.verifyEmail} onChange={handleChange} error={errors.verifyEmail} />
                <PasswordInput name="password" placeholder={t("password")} value={form.password} onChange={handleChange} error={errors.password} />
                <PasswordInput name="verifyPassword" placeholder={t("verify.password")} value={form.verifyPassword} onChange={handleChange} error={errors.verifyPassword} />
                <Button type="submit" label={t("signup")} />
            </div>
        </form>
    );
}

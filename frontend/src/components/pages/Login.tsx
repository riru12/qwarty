import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useMutation } from "@tanstack/react-query";
import { apiClient } from "@utils/ApiClient";
import { LoginEndpoint, LogoutEndpoint } from "@interfaces/api/endpoints";
import "@config/i18n";

export const Login = () => {
  const { t } = useTranslation(["login"]);
  const [form, setForm] = useState({ username: "", password: "" });

  const mutation = useMutation({
    mutationFn: async () => {
      // replace this with your actual API call
      return apiClient.call(LoginEndpoint, form);
    },
    onSuccess: (data) => {
      console.log("Logged in successfully:", data);
      // you can also update auth state here
    },
    onError: (error) => {
      console.error("Login failed:", error);
    },
  });

  const logoutMutation = useMutation({
    mutationFn: async () => apiClient.call(LogoutEndpoint),
    onSuccess: () => {
      console.log("Logged out successfully");
      // optionally reset auth state here
    },
    onError: (error) => console.error("Logout failed:", error),
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutation.mutate();
  };

  const handleLogout = () => {
    logoutMutation.mutate();
  };


  return (
    <div>
        <form onSubmit={handleSubmit}>
            <h2>{t("login")}</h2>
            <input
                type="text"
                name="username"
                placeholder={t("username")}
                value={form.username}
                onChange={handleChange}
            />
            <input
                type="password"
                name="password"
                placeholder={t("password")}
                value={form.password}
                onChange={handleChange}
            />
            <button type="submit">{t("login")}</button>
        </form>
        <button onClick={handleLogout} style={{ marginTop: "10px" }}>
            {t("logout")}
        </button>
    </div>
  );
};

import { useState } from "react";
import type { InputHTMLAttributes } from "react";
import { Eye, EyeClosed, X } from "lucide-react";
import "./Input.css";
import "./PasswordInput.css";

type PasswordInputProps = InputHTMLAttributes<HTMLInputElement> & {
    error?: string;
};

export function PasswordInput({ error, ...props }: PasswordInputProps) {
    const [showPassword, setShowPassword] = useState(false);

    function togglePasswordVisibility() {
        setShowPassword((prev) => !prev);
    }

    return (
        <div className="input-container">
            <input {...props} type={showPassword ? "text" : "password"} className="input password-input" />
            <div className="icons">
                {error && (
                    <div className="error-tooltip">
                        <X className="error" />
                        <span className="error-text">{error}</span>
                    </div>
                )}
                <button type="button" onClick={togglePasswordVisibility} className="password-toggle">
                    {showPassword ? <EyeClosed /> : <Eye />}
                </button>
            </div>
        </div>
    );
}

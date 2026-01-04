import type { InputHTMLAttributes } from "react";
import { X } from "lucide-react";
import "../styles/Input.css"

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
    error?: string;
};

export function Input({ error, ...props }: InputProps) {
    return (
        <div className="input-container">
            <input {...props} className="input" />
            <div className="icons">
                {error && (
                    <div className="error-tooltip">
                        <X className="error" />
                        <span className="error-text">{error}</span>
                    </div>
                )}
            </div>
        </div>
    );
}
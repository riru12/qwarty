import { useState } from "react";
import type { ChangeEvent, Dispatch, InputHTMLAttributes, SetStateAction } from "react";
import { Eye, EyeClosed } from "lucide-react";
import "./PasswordInput.css";

type PasswordInputProps = {
    value: string,
    setValue: Dispatch<SetStateAction<string>>;
} & InputHTMLAttributes<HTMLInputElement>;

export function PasswordInput({ value, setValue, ...props }: PasswordInputProps) {
    const [showPassword, setShowPassword] = useState(false);
    
    function updateValue(event: ChangeEvent<HTMLInputElement>) {
        setValue(event.target.value);
    }

    function togglePasswordVisibility() {
        setShowPassword(!showPassword);
    }

    return (
        <div className="password-input-container">
            <input 
                value={value} 
                onChange={updateValue}
                type={showPassword ? "text" : "password"}
                {...props}
                className="password-input"
            />
            <button 
                onClick={togglePasswordVisibility}
                className="password-toggle"
                type="button"
            >
                { showPassword ? <EyeClosed /> : <Eye /> }
            </button>
        </div>
    );
}
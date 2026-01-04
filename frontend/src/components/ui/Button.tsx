import type { ButtonHTMLAttributes } from "react";
import "../styles/Button.css"

type ButtonProps = {
    label: string;
} & ButtonHTMLAttributes<HTMLButtonElement>;

export function Button({ label, ...props }: ButtonProps) {
    return (
        <button className="button" {...props}>
            {label}
        </button>
    );
}
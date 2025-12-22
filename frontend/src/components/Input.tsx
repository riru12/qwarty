import type { ChangeEvent, Dispatch, InputHTMLAttributes, SetStateAction } from "react";
import "./Input.css";

type InputProps = {
    value: any,
    setValue: Dispatch<SetStateAction<string>>;
} & InputHTMLAttributes<HTMLInputElement>;

function Input({ value, setValue, ...props }: InputProps) {
    
    function updateValue(event: ChangeEvent<HTMLInputElement>) {
        setValue(event.target.value);
    }

    return (
        <input 
            value={value} 
            onChange={updateValue}
            {...props}
            className="input"
        />
    );
}

export default Input;
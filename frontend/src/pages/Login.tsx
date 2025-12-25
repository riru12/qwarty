import { useState } from "react";
import { Input, PasswordInput } from "@components";
import { LoginEndpoint } from "@services/api/endpoints";
import { useAuth, useApi } from "@hooks";

export function Login() {
    const [formUsername, setFormUsername] = useState('');
    const [formPassword, setFormPassword] = useState('');
    const api = useApi();
    
    const { username } = useAuth();

    async function requestLogin() {
        try {
            await api.call(LoginEndpoint, {
                username: formUsername,
                password: formPassword
            });

            setFormUsername('');
            setFormPassword('');
        } catch (error) {
            alert("Sign up failed. Please try again.");
        }
    }

    return (
        <>  
            <div>hello {username}</div>
            <Input 
                value={formUsername} 
                setValue={setFormUsername} 
                placeholder="Username"
            />
            <PasswordInput 
                value={formPassword} 
                setValue={setFormPassword} 
                placeholder="Password"
            />
            <button onClick={requestLogin}></button>
        </>
    )
}
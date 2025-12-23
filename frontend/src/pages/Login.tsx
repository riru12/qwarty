import { useState } from "react";
import { Input, PasswordInput } from "../components";
import { LoginEndpoint } from "../services/api/endpoints";
import apiService from "../services/api/ApiService";

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    async function requestLogin() {
        try {
            await apiService.call(LoginEndpoint, {
                username: username,
                password: password
            });

            // clear inputs after successful request
            setUsername('');
            setPassword('');
        } catch (error) {
            alert("Sign up failed. Please try again.");
        }
    }

    return (
        <>
            <Input 
                value={username} 
                setValue={setUsername} 
                placeholder="Username"
            />
            <PasswordInput 
                value={password} 
                setValue={setPassword} 
                placeholder="Password"
            />
            <button onClick={requestLogin}></button>
        </>
    )
}

export default Login
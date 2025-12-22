import { useState } from "react";
import { Input, PasswordInput } from "../components";
import { SignUpEndpoint } from "../services/api/endpoints/SignUpEndpoint";
import apiService from "../services/api/ApiService";

function Signup() {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    async function requestSignUp() {
        try {
            await apiService.request(SignUpEndpoint, {
                email: email,
                username: username,
                password: password
            });

            // clear inputs after successful request
            setEmail('');
            setUsername('');
            setPassword('');
        } catch (error) {
            alert("Sign up failed. Please try again.");
        }
    }

    return (
        <>
            <Input 
                value={email} 
                setValue={setEmail} 
                placeholder="email@example.com"  
            />
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
            <button onClick={requestSignUp}></button>
        </>
    )
}

export default Signup
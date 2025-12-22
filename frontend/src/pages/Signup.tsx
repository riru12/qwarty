import { useState } from "react";
import { Input, PasswordInput } from "../components";

function Signup() {
    const [email, setEmail] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

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
        </>
    )
}

export default Signup
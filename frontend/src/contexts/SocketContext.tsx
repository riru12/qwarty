import { Client } from "@stomp/stompjs";
import { createContext, useEffect, useState } from "react";

export const SocketContext = createContext<Client | null>(null);

export const SocketProvider = ({ url, children }: { url: string; children: React.ReactNode }) => {
    const [stompClient, setStompClient] = useState<Client | null>(null);

    useEffect(() => {
        const client = new Client({
            brokerURL: url,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = () => {
            setStompClient(client);
        };

        client.onDisconnect = () => {
            setStompClient(null);
        };

        client.activate();

        return () => {
            client.deactivate();
        };
    }, [url]);

    return (
        <SocketContext.Provider value={stompClient}>
            {children}
        </SocketContext.Provider>
    );
};
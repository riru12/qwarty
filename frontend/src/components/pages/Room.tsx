import { RoomRoute } from "@routes/routes";
import { useMatch } from "@tanstack/react-router";
import { Client } from '@stomp/stompjs';
import { useEffect, useRef, useState } from 'react';

export const Room = () => {
  const { params } = useMatch({ from: RoomRoute.id }); 
  const clientRef = useRef<Client | null>(null);
  const [connected, setConnected] = useState(false);
  const [messages, setMessages] = useState<any[]>([]);

  useEffect(() => {
    // Create STOMP client
    const client = new Client({
      brokerURL: 'ws://localhost:8081/api/ws',
      
      onConnect: () => {
        console.log('Connected to WebSocket');
        setConnected(true);
        
        // Subscribe to room-specific topic
        client.subscribe(`/topic/room/${params.roomId}`, (message) => {
          const data = JSON.parse(message.body);
          console.log('Received message:', data);
          setMessages(prev => [...prev, data]);
        });
      },
      
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        setConnected(false);
      },

      onDisconnect: () => {
        console.log('Disconnected from WebSocket');
        setConnected(false);
      }
    });

    clientRef.current = client;
    client.activate();

    // Cleanup on unmount
    return () => {
      client.deactivate();
    };
  }, [params.roomId]);

  const sendMessage = (content: string, messageType: string = 'INPUT') => {
    if (clientRef.current && connected) {
        clientRef.current.publish({
        destination: `/app/room/${params.roomId}`,
        body: JSON.stringify({
            content: content,
            messageType: messageType,
            // sender and roomId will be set by the backend
        })
        });
    }
    };

    return (
    <div>
        <h1>Welcome to room {params.roomId}</h1>
        <p>Status: {connected ? '🟢 Connected' : '🔴 Disconnected'}</p>
        
        {/* Example: Display received messages */}
        <div>
        {messages.map((msg, i) => (
            <div key={i}>
            <strong>{msg.sender}:</strong> {msg.content}
            </div>
        ))}
        </div>

        {/* Example: Send a message */}
        <button onClick={() => sendMessage('Hello from browser!', 'INPUT')}>
        Send Test Message
        </button>
    </div>
    );
};
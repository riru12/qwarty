import { useEffect, useState } from "react";
import "../styles/Racer.css";
import { useSocket } from "@hooks/useSocket";
import type { Client, StompSubscription } from "@stomp/stompjs";

export const Racer = ({ roomId, hasJoinedRoom } : {roomId: string; hasJoinedRoom: boolean}) => {
    const { client } = useSocket();
    const [targetText, setTargetText] = useState("The quick brown fox jumps over the lazy dog");
    const [typed, setTyped] = useState("");

    const messageHandler = (message: any) => {
        const event = JSON.parse(message.body);
        if (event.messageType === "COUNTDOWN") {
            // setCountdown(event.payload);
        } 

        console.log(message.body);
    }

    /**
     * Subscribe to user-specific game updates
     */
    const subscribeToUserUpdates = (stompClient: Client): StompSubscription => {
        return stompClient.subscribe(`/user/queue/game/${roomId}`, (message) => {
            messageHandler(message);
        });
    }

    /**
     * Subscribe to global game updates
     */
    const subscribeToGameUpdates = (stompClient: Client): StompSubscription => {
        return stompClient.subscribe(`/topic/room/${roomId}/game`, (message) => {
            messageHandler(message);
        });
    };


    useEffect(() => {
        if (!client || !client.connected || !hasJoinedRoom) return;

        const subUser = subscribeToUserUpdates(client);
        const subGame = subscribeToGameUpdates(client);

        client.publish({
            destination: `/app/game.join/${roomId}`,
        });
        client.publish({
            destination: `/app/game.ready/${roomId}`,
        });

        return () => {
            subUser.unsubscribe();
            subGame.unsubscribe();
        };
    }, [client, client?.connected, hasJoinedRoom]);

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            // Always allow backspace
            if (e.key === "Backspace") {
                setTyped((prev) => {
                    if (prev.length === 0) return prev;

                    const lastIndex = prev.length - 1;
                    const lastTypedChar = prev[lastIndex];
                    const correctChar = targetText[lastIndex];

                    // Block backspace if last char is correct
                    if (lastTypedChar === correctChar) {
                        return prev;
                    }

                    // Allow backspace only if last char is wrong
                    return prev.slice(0, -1);
                });
                return;
            }

            // Ignore non-character keys
            if (e.key.length !== 1) return;

            setTyped((prev) => {
                const index = prev.length;

                // If last character was wrong, block input
                if (index > 0 && prev[index - 1] !== targetText[index - 1]) {
                    return prev;
                }

                // Stop at end of text
                if (index >= targetText.length) {
                    return prev;
                }

                return prev + e.key;
            });
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, []);

    return (
        <>
            <div>racer</div>
            <div>
                {targetText.split("").map((char, i) => {
                    let className = ""; // not typed yet

                    if (i < typed.length) {
                        className = typed[i] === char ? "correct" : "wrong";
                    }

                    return (
                        <span key={i} className={className}>
                            {char}
                        </span>
                    );
                })}
            </div>
            {/* <div>{typed}</div> */}
        </>
    );
};

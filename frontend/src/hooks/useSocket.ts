import { SocketContext } from "@contexts/SocketContext";
import { useContext } from "react";

export const useSocket = () => {
    const ctx = useContext(SocketContext);

    if (!ctx) {
        throw new Error("useSocket must be used within an SocketProvider");
    }
    return ctx;
};

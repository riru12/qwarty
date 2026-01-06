export type GameSocket = {
    send: (destination: string, body?: unknown) => void;
    subscribe: (destination: string, cb: (event: any) => void) => () => void;
};

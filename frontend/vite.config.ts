import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from 'path';

// https://vite.dev/config/
export default defineConfig({
    envDir: "../",
    plugins: [react()],
    resolve: {
        alias: {
            "@config": path.resolve(__dirname, "./src/config"),
            "@components": path.resolve(__dirname, "./src/components"),
            "@routes": path.resolve(__dirname, "./src/routes"),
        },
    }
});

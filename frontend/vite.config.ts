import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";

// https://vite.dev/config/
export default defineConfig({
    envDir: "../",
    plugins: [
        react({
            babel: {
                plugins: [["babel-plugin-react-compiler"]]
            }
        })
    ],
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
            "@components": path.resolve(__dirname, "./src/components"),
            "@config": path.resolve(__dirname, "./src/config"),
            "@contexts": path.resolve(__dirname, "./src/contexts"),
            "@hooks": path.resolve(__dirname, "./src/hooks"),
            "@pages": path.resolve(__dirname, "./src/pages"),
            "@services": path.resolve(__dirname, "./src/services")
        }
    }
});

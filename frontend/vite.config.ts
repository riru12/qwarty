import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";

// https://vite.dev/config/
export default defineConfig({
    envDir: "../",
    plugins: [react()],
    resolve: {
        alias: {
            src: path.resolve(__dirname, "src"),
            "@assets": path.resolve(__dirname, "src/assets"),
            "@config": path.resolve(__dirname, "src/config"),
            "@contexts": path.resolve(__dirname, "src/contexts"),
            "@components": path.resolve(__dirname, "src/components"),
            "@hooks": path.resolve(__dirname, "src/hooks"),
            "@interfaces": path.resolve(__dirname, "src/interfaces"),
            "@routes": path.resolve(__dirname, "src/routes"),
            "@utils": path.resolve(__dirname, "src/utils"),
        },
    },
});

import { createRoute, Outlet } from "@tanstack/react-router";
import { NavBar } from "@components/ui";
import { root } from "../root";

const MainLayout = () => {
    return (
        <>
            <NavBar />
            <Outlet />
        </>
    );
};

export const MainLayoutRoute = createRoute({
    getParentRoute: () => root,
    id: "qwarty-layout",
    component: MainLayout,
});

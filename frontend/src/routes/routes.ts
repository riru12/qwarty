import { createRoute } from "@tanstack/react-router";
import { MainLayoutRoute } from "@routes/layouts";
import { Home, Login } from "@components/pages";
import { root } from "./root";

const mainRoutes = [
    createRoute({
        getParentRoute: () => MainLayoutRoute,
        path: "/",
        component: Home,
    }),
    createRoute({
        getParentRoute: () => MainLayoutRoute,
        path: "/login",
        component: Login,
    })
]

export const routeTree = root.addChildren([
  MainLayoutRoute.addChildren([...mainRoutes])
]);
import { createRoute } from "@tanstack/react-router";
import { MainLayoutRoute } from "@routes/layouts";
import { Home, Login, Room } from "@components/pages";
import { root } from "./root";

export const HomeRoute = createRoute({
    getParentRoute: () => MainLayoutRoute,
    path: "/",
    component: Home,
});
export const LoginRoute = createRoute({
    getParentRoute: () => MainLayoutRoute,
    path: "/login",
    component: Login,
});
export const RoomRoute = createRoute({
    getParentRoute: () => MainLayoutRoute,
    path: "/room/$roomId",
    component: Room,
});

const mainRoutes = [HomeRoute, LoginRoute, RoomRoute];

export const routeTree = root.addChildren([MainLayoutRoute.addChildren([...mainRoutes])]);

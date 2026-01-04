import { NotFound } from "@components/pages";
import { createRootRoute, Outlet } from "@tanstack/react-router";

const RootLayout = () => {
  return (
    <main>
        <Outlet />
    </main>
  );
};

export const root = createRootRoute({
    component: RootLayout,
    notFoundComponent: NotFound,
});

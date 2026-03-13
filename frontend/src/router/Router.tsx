import App from "@/App";
import NotFound from "./NotFound";
import {createBrowserRouter} from "react-router-dom";
import RouteError from "./RouteError";
import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Signup from "@/pages/Signup";
import OrganizerDashboard from "@/pages/OrganizerDashboard";
import CreateEventPage from "@/pages/CreateEventPage";
import EventsPage from "@/pages/EventsPage";

const router = createBrowserRouter([
    {
        path:"/",
        element:<App/>,
        errorElement:<RouteError/>,
        children:[
            {
                index:true,
                element:<Home/>
            },
            {
                path:"/login",
                element:<Login/>
            },
            {
                path:"/signup",
                element:<Signup/>
            },
            {
                path:"/404",
                element:<NotFound/>
            },
            {
                path: "/organizer/dashboard",
                element: <OrganizerDashboard />,
            },
            {
                path: "/organizer/events/create",
                element: <CreateEventPage />,
            },
            {
                path: "/events",
                element: <EventsPage />,
             },
        ]
    },
]);
export default router;


import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
} from "@/components/ui/navigation-menu"
import { Button } from "../ui/button";
import { Link, useNavigate } from "react-router-dom";

import favicon from "/appicon.svg"

export default () => {
    const navigate = useNavigate();

    return (
        <div className="flex justify-between w-full border-2 rounded-lg sticky top-0">
            <div className="flex justify-between gap-x-5">
                <div className="flex flex-row justify-between align-bottom">
                    <Link to={"/"}>
                        <img src={favicon} width="40" alt="SUMMS Logo"/>
                    </Link>
                    <h1 className="text-foreground">Ticket App</h1> 
                </div>
            </div>
            <NavigationMenu>
                <NavigationMenuList>
                    <Button className="bg-background text-foreground rounded-lg" onClick={()=>navigate("/login")}>
                        Login
                    </Button>
                </NavigationMenuList>
            </NavigationMenu>
        </div>
    )
}
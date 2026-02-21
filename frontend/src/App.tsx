import Header from "@/components/navigation/Header.tsx";
import { Outlet } from "react-router-dom";

export default function App() {
  return (
      <div className="min-h-screen bg-blue-50">
        <Header />
        <Outlet />
      </div>
  );
}

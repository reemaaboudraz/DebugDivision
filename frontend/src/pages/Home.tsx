import { Link } from "react-router";
import { Ticket, Film, Music, Plane, Trophy } from "lucide-react";
import { Spinner } from "@/components/ui/spinner";
import { useAuth } from "@/AuthContext";

export default function Home() {

    const {uid, loading} = useAuth();
    
    const categoryCards = [
        {
          label: "Movies",
          category: "movie",
          icon: <Film className="w-8 h-8 text-[#EC4899]" />,
          bg: "bg-[#FDF2F8]",
        },
        {
          label: "Concerts",
          category: "concert",
          icon: <Music className="w-8 h-8 text-[#3B82F6]" />,
          bg: "bg-[#EFF6FF]",
        },
        {
          label: "Sports",
          category: "sports",
          icon: <Trophy className="w-8 h-8 text-[#EC4899]" />,
          bg: "bg-[#FDF2F8]",
        },
        {
          label: "Travel",
          category: "travel",
          icon: <Plane className="w-8 h-8 text-[#3B82F6]" />,
          bg: "bg-[#EFF6FF]",
        },
      ];

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
            <div className="text-center mb-16">
                <div className="mb-8 flex justify-center">
                    <div className="bg-[#3B82F6] p-6 rounded-3xl shadow-lg">
                        <Ticket className="w-20 h-20 text-white" />
                    </div>
                </div>
                <h1 className="text-5xl md:text-6xl mb-6 text-[#1F2937]">
                    Welcome to Tixy
                </h1>
                <p className="text-xl md:text-2xl text-[#6B7280] mb-12 max-w-2xl mx-auto">
                    Book tickets for movies, concerts, sports, and travel easily.
                </p>
                {loading && <Spinner/>}
                {!uid && 
                <div className="flex gap-4 justify-center flex-wrap">
                    <Link
                        to="/login"
                        className="px-8 py-4 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] hover:shadow-lg hover:scale-105 transition-all text-lg"
                    >
                        Login
                    </Link>
                    <Link
                        to="/signup"
                        className="px-8 py-4 bg-[#EC4899] text-white rounded-full hover:bg-[#DB2777] hover:shadow-lg hover:scale-105 transition-all text-lg"
                    >
                        Sign Up
                    </Link>
                </div>
                }
            </div>
            {/* Types of Tickets Available */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mt-20">
                    {categoryCards.map((card) => (
                      <Link
                        key={card.category}
                        to={`/events?category=${card.category}`}
                        className="bg-white rounded-2xl p-6 text-center hover:shadow-lg transition-all hover:scale-105 border border-gray-100"
                      >
                        <div className={`${card.bg} w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4`}>
                          {card.icon}
                        </div>
                        <h3 className="text-[#1F2937]">{card.label}</h3>
                      </Link>
                    ))}
                  </div>
                </div>
              );
   }
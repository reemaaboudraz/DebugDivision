import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

//these are public values, no secrets are being shared
const firebaseConfig = {
  apiKey: "AIzaSyAtXF_qL9rOznLXoYR93_71xHF0u8IbRqk",
  authDomain: "ticket-reservation-523a4.firebaseapp.com",
  projectId: "ticket-reservation-523a4",
  storageBucket: "ticket-reservation-523a4.firebasestorage.app",
  messagingSenderId: "542495230122",
  appId: "1:542495230122:web:868ad0de082196407de8d3",
  measurementId: "G-VXK35GV2SX"
};


const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
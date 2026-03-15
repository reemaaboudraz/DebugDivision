import { 
  signInWithCustomToken, 
  signInWithEmailAndPassword, 
  signOut,
  RecaptchaVerifier,
  signInWithPhoneNumber,
  type ConfirmationResult,
} from "firebase/auth";
import { auth } from "../lib/firebaseConfig";

async function loginCustomToken(token: string):Promise<void>{

  try {
    await signInWithCustomToken(auth, token);
  } 
  catch(error: any) {
    throw new Error("Login failed, firebase authentication failure");
  }
}

async function login(email: string, password: string){
  try {
    await signInWithEmailAndPassword(auth, email, password);
  } 
  catch(error: any) {
    throw new Error("Login failed, firebase authentication failure");
  }
}

async function logout(): Promise<void> {
  try {
    await signOut(auth);
  } catch (error: any) {
    throw new Error("Logout failed, firebase authentication failure");
  }
}

let confirmationResultRef: ConfirmationResult | null = null;
let recaptchaVerifierRef: RecaptchaVerifier | null = null;

function setupPhoneRecaptcha(containerId: string) {
  if (recaptchaVerifierRef) return recaptchaVerifierRef;

  recaptchaVerifierRef = new RecaptchaVerifier(auth, containerId, {
    size: "normal",
  });

  return recaptchaVerifierRef;
}

async function sendPhoneVerificationCode(phoneNumber: string): Promise<void> {
  const verifier = recaptchaVerifierRef;
  if (!verifier) {
    throw new Error("reCAPTCHA is not initialized");
  }

  confirmationResultRef = await signInWithPhoneNumber(auth, phoneNumber, verifier);
}

async function confirmPhoneVerificationCode(code: string) {
  if (!confirmationResultRef) {
    throw new Error("No verification request in progress");
  }

  const result = await confirmationResultRef.confirm(code);
  return result.user;
}

export {
  login, 
  loginCustomToken, 
  logout, 
  setupPhoneRecaptcha, 
  sendPhoneVerificationCode, 
  confirmPhoneVerificationCode
};
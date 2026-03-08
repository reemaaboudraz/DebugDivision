import { auth } from "./firebaseConfig";

//helper fetch functions that automatically include the bearer token

export async function authenticatedPost(url:string, data: any){

    const token = await auth.currentUser?.getIdToken();
    return fetch(url, {
          method: "POST",
          headers: { "Content-Type": "application/json", "Authorization": `Bearer ${token}`},
          body: data,
    });
}
export async function authenticatedGet(url:string){
    const token = await auth.currentUser?.getIdToken();
    return fetch(url, {
          method: "GET",
          headers: { "Content-Type": "application/json", "Authorization": `Bearer ${token}`},
    });
}
import appLogo from '/appicon.svg'
import Header from './components/navigation/Header';
import { Outlet } from "react-router-dom";

function App() {

  return (
    <div className='flex flex-col items-center py-5'>
    <Header/>
      <div className='bg-background'>
      </div>
      <Outlet/>
    </div>
  )
}

export default App

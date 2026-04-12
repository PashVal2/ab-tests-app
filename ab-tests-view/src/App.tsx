import { Route, Routes } from "react-router-dom";
import Footer from "./pages/footer/Footer";
import Header from "./pages/header/Header";
import Home from "./pages/home/Home";
import Auth from "./pages/auth/AuthLayout";
import './styles/app.scss';
import ABRoutes from "./pages/admin/ABRoutes";
import ABRouteWrapper from "./context/ABRouteWrapper";

function App() {
  return (
    <div className="appWrapper">
      <Header />
      <div className="appWrapperContent">
        <Routes>
          <Route path='/' element={<Home />} />
          <Route path='/auth/*' element={<Auth />} />
          <Route path='/ab-testing/*'
            element={
              <ABRouteWrapper>
                <ABRoutes />
              </ABRouteWrapper>
            }
          />
        </Routes>
      </div>
      <Footer />
    </div>
  );
}

export default App;

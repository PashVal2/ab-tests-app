import { Navigate, Route, Routes } from 'react-router-dom';
import './pages/LoginForm';
import LoginForm from './pages/LoginForm';
import RegisterForm from './pages/RegisterForm';
import EmailVerify from './pages/EmailVerify';
import EmailSent from './pages/EmailSent';

function Auth() {
  return (
    <div>
      <Routes>
        <Route path='' element={<Navigate to="login" replace />} />
        <Route path='login' element={<LoginForm />} />
        <Route path='register' element={<RegisterForm />} />
        <Route path='confirm-email' element={<EmailVerify />} />
        <Route path='email-sent' element={<EmailSent />} />
      </Routes>
    </div>
  );
}

export default Auth;

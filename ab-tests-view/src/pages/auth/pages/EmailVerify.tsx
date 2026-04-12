import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { verifyEmail } from "../../../api/AuthApi";
import c from './AuthForms.module.scss';

export function EmailVerify() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const navigate = useNavigate();

  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    if (!token) {
      setTimeout(() => {
        setStatusMessage("Ссылка недействительна или просрочена");
        setIsError(true);
      }, 0);
      return;
    }
    const checkEmail = async () => {
      const success = await verifyEmail(token);
      if (success) {
        setStatusMessage("E-mail успешно подтвержден");
        setIsError(false);
        setTimeout(() => navigate("../login"), 3000);
      } else {
        setStatusMessage("Ссылка недействительна или просрочена");
        setIsError(true);
      }
    }
    checkEmail();
  }, [token, navigate]);

  return (
    <div className={c['emailStatus']}>
      {statusMessage ? (
        <div className={isError ? c.error : c.succes}>
          {isError ? "Ошибка: " : ""} {statusMessage}
        </div>
      ) : (
        <div>Проверка токена...</div>
      )}
    </div>
  );
}

export default EmailVerify;

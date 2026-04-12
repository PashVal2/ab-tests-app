import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { resendConfirmation } from "../../../api/AuthApi";
import c from "./EmailSent.module.scss";

const RESEND_TIMEOUT_SECONDS = 60;

function EmailSent() {
  const location = useLocation();
  const state = location.state as { email?: string; mode?: string } | null;
  const email = state?.email ?? "";

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [cooldown, setCooldown] = useState(RESEND_TIMEOUT_SECONDS);

  useEffect(() => {
    if (cooldown <= 0) {
      return;
    }

    const timer = window.setInterval(() => {
      setCooldown((prev) => {
        if (prev <= 1) {
          window.clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => window.clearInterval(timer);
  }, [cooldown]);

  const handleResend = async () => {
    if (!email || cooldown > 0) return;

    try {
      setLoading(true);
      setMessage("");

      await resendConfirmation(email);

      setMessage("Письмо отправлено повторно");
      setCooldown(RESEND_TIMEOUT_SECONDS);
    } catch {
      setMessage("Не удалось отправить письмо повторно");
    } finally {
      setLoading(false);
    }
  };

  const isError = message.includes("Не удалось");

  return (
    <div className={c.page}>
      <div className={c.card}>
        <h1 className={c.title}>Проверьте вашу почту</h1>

        <p className={c.description}>
          Мы отправили письмо для подтверждения аккаунта.
          Перейдите по ссылке в письме, чтобы завершить регистрацию.
        </p>

        {email && (
          <div className={c.emailBox}>
            <span className={c.emailLabel}>Адрес электронной почты</span>
            <b className={c.emailValue}>{email}</b>
          </div>
        )}

        {email ? (
          <div className={c.resendBlock}>
            <span className={c.resendText}>Письмо не пришло?</span>

            <button
              type="button"
              className={c.resendLink}
              onClick={handleResend}
              disabled={loading || cooldown > 0}
            >
              {loading
                ? "Отправка..."
                : cooldown > 0
                  ? `Отправить ещё раз через ${cooldown} сек`
                  : "Отправить ещё раз"}
            </button>
          </div>
        ) : (
          <div className={`${c.message} ${c.error}`}>
            Не удалось определить адрес электронной почты
          </div>
        )}

        {message && (
          <div className={`${c.message} ${isError ? c.error : c.success}`}>
            {message}
          </div>
        )}
      </div>
    </div>
  );
}

export default EmailSent;

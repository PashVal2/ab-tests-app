import { type ChangeEvent, type SyntheticEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { register } from "../../../api/AuthApi";
import type { RegisterRequest } from "../../../types/AuthTypes";
import c from "./AuthForms.module.scss";

type RegisterFormState = {
  name: string;
  username: string;
  password: string;
  passwordConfirm: string;
};

type RegisterFormErrors = Partial<Record<keyof RegisterFormState, string>>;

function getRegisterErrorText(code?: string): string {
  switch (code) {
    case "USER_ALREADY_EXISTS":
      return "Эта почта уже занята";
    case "EMAIL_NOT_CONFIRMED":
      return "Почта не подтверждена";
    case "PASSWORDS_MUST_MATCH":
      return "Пароли не совпадают";
    case "INVALID_ARGUMENTS":
      return "Проверьте введённые данные";
    default:
      return "Не удалось зарегистрироваться";
  }
}

function RegisterForm() {
  const [form, setForm] = useState<RegisterFormState>({
    name: "",
    username: "",
    password: "",
    passwordConfirm: "",
  });

  const [errors, setErrors] = useState<RegisterFormErrors>({});

  const navigate = useNavigate();

  const changeHandler = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));

    setErrors((prev) => ({
      ...prev,
      [name]: undefined,
    }));
  };

  const resetPasswords = () => {
    setForm((prev) => ({
      ...prev,
      password: "",
      passwordConfirm: "",
    }));
  };

  const validate = (): boolean => {
    const newErrors: RegisterFormErrors = {};
    const regex = /.+@.+\..+/i;

    if (!form.username || form.username.length > 100 || !regex.test(form.username)) {
      newErrors.username = !form.username
        ? "E-mail обязателен"
        : form.username.length > 100
          ? "E-mail слишком длинный"
          : "Некорректный e-mail";
    }

    if (!form.name || form.name.length > 50) {
      newErrors.name = !form.name
        ? "Никнейм обязателен"
        : "Никнейм слишком длинный";
    }

    if (!form.password || form.password.length < 8 || form.password.length > 71) {
      newErrors.password = !form.password
        ? "Пароль обязателен"
        : form.password.length < 8
          ? "Пароль слишком короткий"
          : "Пароль слишком длинный";
    }

    if (!form.passwordConfirm) {
      newErrors.passwordConfirm = "Подтверждение пароля обязательно";
    } else if (form.password !== form.passwordConfirm) {
      newErrors.passwordConfirm = "Пароли не совпадают";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const submitHandler = async (e: SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!validate()) {
      return;
    }

    const request: RegisterRequest = {
      name: form.name,
      username: form.username,
      password: form.password,
      passwordConfirm: form.passwordConfirm,
    };

    try {
      await register(request);
      resetPasswords();

      navigate("../email-sent", {
        state: {
          email: form.username,
          mode: "registered",
        },
      });
    } catch (err) {
      const apiError = err as { code?: string; message?: string };

      if (apiError.code === "EMAIL_NOT_CONFIRMED") {
        resetPasswords();

        navigate("../email-sent", {
          state: {
            email: form.username,
            mode: "unconfirmed",
          },
        });
        return;
      }

      if (apiError.code === "PASSWORDS_MUST_MATCH") {
        setErrors((prev) => ({
          ...prev,
          passwordConfirm: getRegisterErrorText(apiError.code),
        }));
        return;
      }

      setErrors((prev) => ({
        ...prev,
        username: getRegisterErrorText(apiError.code),
      }));
    }
  };

  return (
    <div className={c.formWrapper}>
      <div className={c.formContainer}>
        <h2>Регистрация</h2>

        <form onSubmit={submitHandler}>
          <div>
            <label>E-mail</label>
            <input
              name="username"
              type="email"
              value={form.username}
              onChange={changeHandler}
              required
            />
            <div className={c.error}>{errors.username || "\u00A0"}</div>
          </div>

          <div>
            <label>Никнейм</label>
            <input
              name="name"
              type="text"
              value={form.name}
              onChange={changeHandler}
              required
            />
            <div className={c.error}>{errors.name || "\u00A0"}</div>
          </div>

          <div>
            <label>Пароль</label>
            <input
              name="password"
              type="password"
              value={form.password}
              onChange={changeHandler}
              required
            />
            <div className={c.error}>{errors.password || "\u00A0"}</div>
          </div>

          <div>
            <label>Пароль ещё раз</label>
            <input
              name="passwordConfirm"
              type="password"
              value={form.passwordConfirm}
              onChange={changeHandler}
              required
            />
            <div className={c.error}>{errors.passwordConfirm || "\u00A0"}</div>
          </div>

          <button type="submit">Регистрация</button>
        </form>

        <div className={c.redirect}>
          Уже зарегистрированы? <Link to="../login">Войдите</Link>
        </div>
      </div>
    </div>
  );
}

export default RegisterForm;

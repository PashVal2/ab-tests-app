import { useContext, useState, type ChangeEvent, type SyntheticEvent } from "react";
import type { LoginFormErrors, LoginFormState, LoginRequest } from "../../../types/AuthTypes";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../../../api/AuthApi";
import c from './AuthForms.module.scss';
import { me } from "../../../api/UserApi";
import { AuthContext } from "../../../context/AuthContext";

function LoginForm() {
  const [form, setForm] = useState<LoginFormState>({
    username: "",
    password: "",
  });
  const [errors, setErrors] = useState<LoginFormErrors>({});
  const auth = useContext(AuthContext);
  const navigate = useNavigate();

  if (!auth) throw new Error("LoginForm must be used inside AuthProvider");
  const { setUser } = auth;

  const changeHandler = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }

  const validate = (): boolean => {
    const newErrors: LoginFormErrors = {};
    const regex = /.+@.+\..+/i;

    if (!form.username) {
      newErrors.username = "Почта обязательна";
    } else if (!regex.test(form.username)) {
      newErrors.username = "Некорректный e-mail";
    }

    if (!form.password) newErrors.password = "Пароль обязателен";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  const resetPasswords = () => {
    setForm(prev => ({ ...prev, password: "" }));
  }

  const submitHandler = async(e: SyntheticEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!validate()) {
      return;
    }

    const request: LoginRequest = { username: form.username, password: form.password }
    resetPasswords();

    try {
      await login(request);
      const userData = await me();
      setUser(userData);
      navigate("/");
    } catch (err) {
      setErrors(prev => ({
        ...prev,
        username: "Неверный логин или пароль"
      }));
      console.log(err);
    }
  }

  return (
    <div className={c['formWrapper']}>
      <div className={c['formContainer']}>
        <h2>Вход</h2>
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
            <div className={c.error}>
              {errors.username || "\u00A0"}
            </div>
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
            <div className={c.error}>
              {errors.password || "\u00A0"}
            </div>
          </div>
          <button type="submit">Войти</button>
        </form>
        <div className={c.redirect}>
          Ещё нет аккаунта? <Link to={"../register"}>Зарегистрируйтесь</Link>
        </div>
      </div>
    </div>
  );
}

export default LoginForm;

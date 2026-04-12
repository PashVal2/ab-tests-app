import { Link, useNavigate } from "react-router-dom";
import c from "./Header.module.scss";
import { useContext } from "react";
import { logout } from "../../api/AuthApi";
import { AuthContext } from "../../context/AuthContext";

function Header() {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();

  if (!auth) throw new Error("Header must be used inside AuthProvider");
  const { user, setUser } = auth;

  const logoutHandler = async () => {
    try {
      await logout();
      setUser(null);
      navigate("/auth");
    } catch (err) {
      console.log(err);
    }
  }

  return (
    <header className={c.header}>
      <div className={c.logo}>
        <Link to={"/"}>A/B Hub</Link>
      </div>
      <nav className={c.nav}>
        <div className={c.items}>
          <div className={c.item}>
            <Link to={"/"}>Главная</Link>
          </div>
          <div className={c.item}>
            <Link to={"/ab-testing"}>A/B тестирование</Link>
          </div>
        </div>
        <div className={c.sign}>
          <span className={c.line}></span>
          {user ? (
            <div className={c.userBlock}>
              <span>{user.name}</span>
              <button onClick={logoutHandler} className={c.logoutBtn}>
                Выйти
              </button>
            </div>
          ) : (
            <Link to={"/auth"}>Вход</Link>
          )}
        </div>
      </nav>
    </header>
  );
}

export default Header;

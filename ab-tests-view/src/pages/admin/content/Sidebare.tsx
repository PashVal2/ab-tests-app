import { Link } from "react-router-dom";
import c from "./Sidebare.module.scss";
import useActiveProject from "../../../hooks/useActiveProject";

function Sidebar() {
  const { project } = useActiveProject();

  if (!project) {
    return null;
  }

  const base = `/ab-testing/project/${project.code}`;

  return (
    <div className={c.sidebar}>
      <div className={c.currentProject}>
        <span className={c.currentProjectName}>{project.name}</span>
        <span className={c.currentProjectCode}>{project.code}</span>
      </div>

      <div className={c.item}>
        <Link to={`${base}/overview`}>Обзор</Link>
      </div>
      <div className={c.item}>
        <Link to={`${base}/features`}>Признаки</Link>
      </div>
      <div className={c.item}>
        <Link to={`${base}/experiments/create`}>Конструктор</Link>
      </div>
      <div className={c.item}>
        <Link to={`${base}/experiments`}>Эксперименты</Link>
      </div>
      <div className={c.item}>
        <Link to={`${base}/api-keys`}>API-ключи</Link>
      </div>
      <div className={c.item}>
        <Link to={`${base}/events`}>События</Link>
      </div>
      <div className={c.item}>
        <Link to={`${base}/integration`}>Интеграция</Link>
      </div>
    </div>
  );
}

export default Sidebar;

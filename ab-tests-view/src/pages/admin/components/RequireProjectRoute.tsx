import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import useActiveProject from "../../../hooks/useActiveProject";
import c from "./RequireProjectRoute.module.scss";

type Props = {
  children: ReactNode;
};

function RequireProjectRoute({ children }: Props) {
  const { projectId, project, loading } = useActiveProject();

  if (loading) {
    return null;
  }

  if (!projectId) {
    return (
      <div className={c.emptyState}>
        <h3>Проект не выбран</h3>
        <p>Откройте список проектов и выберите нужный проект.</p>
        <Link to="/ab-testing/projects" className={c.action}>
          К проектам
        </Link>
      </div>
    );
  }

  if (!project) {
    return (
      <div className={c.emptyState}>
        <h3>Проект не найден</h3>
        <p>Возможно, проект был удалён или у вас нет к нему доступа.</p>
        <Link to="/ab-testing/projects" className={c.action}>
          Назад к проектам
        </Link>
      </div>
    );
  }

  return <>{children}</>;
}

export default RequireProjectRoute;

import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { createProject, getMyProjects } from "../../../../api/ProjectApi";
import type {
  CreateProjectRequest,
  ProjectResponse,
} from "../../../../types/ProjectTypes";
import c from "./Projects.module.scss";

const LAST_PROJECT_CODE_STORAGE_KEY = "lastProjectCode";

function Projects() {
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const [name, setName] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [createError, setCreateError] = useState("");

  const lastProjectCode = useMemo(() => {
    return localStorage.getItem(LAST_PROJECT_CODE_STORAGE_KEY);
  }, []);

  useEffect(() => {
    const loadProjects = async () => {
      try {
        const data = await getMyProjects();
        setProjects(data);
      } finally {
        setLoading(false);
      }
    };
    loadProjects();
  }, []);

  const handleCreate = async () => {
    try {
      setSubmitting(true);
      setCreateError("");

      const payload: CreateProjectRequest = { name };
      const created = await createProject(payload);

      setProjects((prev) => [created, ...prev]);
      setName("");
    } catch (e) {
      setCreateError(e instanceof Error ? e.message : "Не удалось создать проект");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className={c.wrapper}>
      <div className={c.grid}>
        <section className={c.listCard}>
          <div className={c.sectionHeader}>
            <h3>Список проектов</h3>
          </div>

          { projects.length === 0 && !loading ? (
            <div className={c.empty}>
              У вас пока нет проектов. Создайте первый проект справа.
            </div>
          ) : projects.length > 0 ? (
            <div className={c.projectList}>
              {projects.map((project) => {
                const isLast = project.code === lastProjectCode;

                return (
                  <div key={project.id} className={c.projectItem}>
                    <div className={c.projectTop}>
                      <div>
                        <div className={c.projectName}>{project.name}</div>
                        <div className={c.projectCode}>{project.code}</div>
                      </div>
                      {isLast && <span className={c.badge}>Последний</span>}
                    </div>

                    <div className={c.projectActions}>
                      <Link
                        to={`/ab-testing/project/${project.code}/overview`}
                        className={c.primaryAction}
                      >
                        Открыть проект
                      </Link>

                      <Link
                        to={`/ab-testing/project/${project.code}/experiments`}
                        className={c.secondaryAction}
                      >
                        К экспериментам
                      </Link>
                    </div>
                  </div>
                );
              })}
            </div>
          ) : null}
        </section>

        <aside className={c.createCard}>
          <div className={c.sectionHeader}>
            <h3>Создать проект</h3>
          </div>

          <div className={c.form}>
            <input
              type="text"
              placeholder="Название проекта"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />

            <button onClick={handleCreate} disabled={!name || submitting}>
              {submitting ? "Создание..." : "Создать проект"}
            </button>
          </div>

          {createError && <div className={c.error}>{createError}</div>}
        </aside>
      </div>
    </div>
  );
}

export default Projects;

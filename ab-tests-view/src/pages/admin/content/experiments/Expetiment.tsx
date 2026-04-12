import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getExperiments } from "../../../../api/ExperimentApi";
import type {
  ExperimentResponse,
  ExperimentStatus,
  MetricType,
} from "../../../../types/ExperimentTypes";
import c from "./Experiment.module.scss";
import useActiveProject from "../../../../hooks/useActiveProject";

const metricOptions: MetricType[] = [
  "CTR",
  "VIEW_RATE",
  "LIKE_RATE",
  "WATCH_START_RATE",
  "WATCH_FINISH_RATE",
  "WATCH_THROUGH_RATE",
];

const statusOptions: ExperimentStatus[] = ["DRAFT", "RUNNING", "FINISHED"];

function Experiments() {
  const { project } = useActiveProject();

  const [experiments, setExperiments] = useState<ExperimentResponse[]>([]);
  const [loadingPage, setLoadingPage] = useState(true);

  const [name, setName] = useState("");
  const [status, setStatus] = useState("");
  const [primaryMetric, setPrimaryMetric] = useState("");
  const [createdFrom, setCreatedFrom] = useState("");
  const [createdTo, setCreatedTo] = useState("");

  const [debouncedName, setDebouncedName] = useState("");

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedName(name);
    }, 400);

    return () => clearTimeout(timer);
  }, [name]);

  useEffect(() => {
    const loadData = async () => {
      if (!project) {
        setLoadingPage(false);
        return;
      }

      setLoadingPage(true);
      try {
        const data = await getExperiments(project.id, {
          name: debouncedName || undefined,
          status: status ? (status as ExperimentStatus) : undefined,
          primaryMetric: primaryMetric ? (primaryMetric as MetricType) : undefined,
          createdFrom: createdFrom ? new Date(createdFrom).toISOString() : undefined,
          createdTo: createdTo ? new Date(createdTo).toISOString() : undefined,
        });

        setExperiments(data);
      } finally {
        setLoadingPage(false);
      }
    };

    loadData();
  }, [project, debouncedName, status, primaryMetric, createdFrom, createdTo]);

  const handleRefresh = async () => {
    if (!project) return;

    setLoadingPage(true);
    try {
      const data = await getExperiments(project.id, {
        name: debouncedName || undefined,
        status: status ? (status as ExperimentStatus) : undefined,
        primaryMetric: primaryMetric ? (primaryMetric as MetricType) : undefined,
        createdFrom: createdFrom ? new Date(createdFrom).toISOString() : undefined,
        createdTo: createdTo ? new Date(createdTo).toISOString() : undefined,
      });

      setExperiments(data);
    } finally {
      setLoadingPage(false);
    }
  };

  const handleReset = () => {
    setName("");
    setStatus("");
    setPrimaryMetric("");
    setCreatedFrom("");
    setCreatedTo("");
  };

  if (!project) {
    return null;
  }

  return (
    <div className={c.wrapper}>
      <div className={c.listSection}>
        <div className={c.header}>
          <div>
            <h2 className={c.title}>Эксперименты</h2>
            <p className={c.subtitle}>
              Список экспериментов, переход к деталям и результатам
            </p>
          </div>

          <div className={c.headerActions}>
            <button className={c.refreshBtn} onClick={handleRefresh}>
              Обновить
            </button>
          </div>
        </div>

        {experiments.length === 0 && !loadingPage ? (
          <div className={c.empty}>Эксперименты не найдены</div>
        ) : experiments.length > 0 ? (
          <div className={c.list}>
            {experiments.map((experiment) => (
              <div key={experiment.id} className={c.item}>
                <div className={c.itemTop}>
                  <div className={c.itemTitle}>{experiment.name}</div>
                  <span className={`${c.statusBadge} ${c[experiment.status]}`}>
                    {experiment.status}
                  </span>
                </div>

                <div className={c.itemInfo}>
                  <div>
                    <b>Метрика:</b> {experiment.primaryMetric}
                  </div>
                  <div>
                    <b>Id:</b> {experiment.externalKey}
                  </div>
                </div>

                <div className={c.itemActions}>
                  <Link
                    to={`/ab-testing/project/${project.code}/experiments/${experiment.externalKey}`}
                    className={c.link}
                  >
                    Подробнее
                  </Link>

                  <Link
                    to={`/ab-testing/project/${project.code}/experiments/${experiment.externalKey}/results`}
                    className={c.secondaryLink}
                  >
                    Результаты
                  </Link>
                </div>
              </div>
            ))}
          </div>
        ) : null}
      </div>

      <aside className={c.filtersSection}>
        <div className={c.filters}>
          <label>
            <span>Название</span>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Поиск по названию"
            />
          </label>

          <label className={c.selectField}>
            <span>Статус</span>
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="">Все</option>
              {statusOptions.map((item) => (
                <option key={item} value={item}>
                  {item}
                </option>
              ))}
            </select>
          </label>

          <label className={c.selectField}>
            <span>Основная метрика</span>
            <select
              value={primaryMetric}
              onChange={(e) => setPrimaryMetric(e.target.value)}
            >
              <option value="">Все</option>
              {metricOptions.map((item) => (
                <option key={item} value={item}>
                  {item}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Дата от</span>
            <input
              type="datetime-local"
              value={createdFrom}
              onChange={(e) => setCreatedFrom(e.target.value)}
            />
          </label>

          <label>
            <span>Дата до</span>
            <input
              type="datetime-local"
              value={createdTo}
              onChange={(e) => setCreatedTo(e.target.value)}
            />
          </label>

          <div className={c.actions}>
            <button className={c.secondaryBtn} onClick={handleReset}>
              Сбросить
            </button>
          </div>
        </div>
      </aside>
    </div>
  );
}

export default Experiments;

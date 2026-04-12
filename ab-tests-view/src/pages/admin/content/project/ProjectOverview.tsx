import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getApiKeys } from "../../../../api/ApiKeysApi";
import { getExperiments } from "../../../../api/ExperimentApi";
import { getFeatures } from "../../../../api/FeatureApi";
import useActiveProject from "../../../../hooks/useActiveProject";
import c from "./ProjectOverview.module.scss";

function ProjectOverview() {
  const { project } = useActiveProject();

  const [featuresCount, setFeaturesCount] = useState(0);
  const [experimentsCount, setExperimentsCount] = useState(0);
  const [apiKeysCount, setApiKeysCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadStats = async () => {
      if (!project) return;

      setLoading(true);
      try {
        const [features, experiments, apiKeys] = await Promise.all([
          getFeatures(project.id),
          getExperiments(project.id),
          getApiKeys(project.id),
        ]);

        setFeaturesCount(features.length);
        setExperimentsCount(experiments.length);
        setApiKeysCount(apiKeys.length);
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, [project]);

  if (!project) {
    return null;
  }

  const base = `/ab-testing/project/${project.code}`;

  return (
    <div className={c.wrapper}>
      <div className={c.hero}>
        <div>
          <h2 className={c.title}>{project.name}</h2>
          <p className={c.subtitle}>
            Основная информация и быстрые переходы по сущностям проекта
          </p>
        </div>

        <div className={c.projectBadge}>
          <span className={c.projectCodeLabel}>Код проекта</span>
          <span className={c.projectCode}>{project.code}</span>
        </div>
      </div>

      <div className={c.sectionHeader}>
        <h3>Объекты проекта</h3>
        <span className={c.sectionHint}>
          {loading ? null : "Текущий активный проект"}
        </span>
      </div>

      <div className={c.stats}>
        <div className={c.statCard}>
          <div className={c.statTop}>
            <div className={c.statValue}>{featuresCount}</div>
            <div className={c.statLabel}>Признаков</div>
          </div>
          <div className={c.statDescription}>
            Управление параметрами, которые участвуют в сегментации и логике экспериментов
          </div>
          <Link to={`${base}/features`} className={c.cardAction}>
            Открыть признаки
          </Link>
        </div>

        <div className={c.statCard}>
          <div className={c.statTop}>
            <div className={c.statValue}>{experimentsCount}</div>
            <div className={c.statLabel}>Экспериментов</div>
          </div>
          <div className={c.statDescription}>
            Просмотр, создание и контроль A/B-экспериментов внутри проекта
          </div>
          <Link to={`${base}/experiments`} className={c.cardAction}>
            Открыть эксперименты
          </Link>
        </div>

        <div className={c.statCard}>
          <div className={c.statTop}>
            <div className={c.statValue}>{apiKeysCount}</div>
            <div className={c.statLabel}>API-ключей</div>
          </div>
          <div className={c.statDescription}>
            Доступы для интеграций, подключения клиента и внешних сервисов
          </div>
          <Link to={`${base}/api-keys`} className={c.cardAction}>
            Открыть API-ключи
          </Link>
        </div>
      </div>

      <div className={c.createPanel}>
        <div>
          <div className={c.createTitle}>Быстрые действия</div>
          <div className={c.createText}>
            Сразу перейдите к созданию нужной сущности в проекте
          </div>
        </div>

        <div className={c.createActions}>
          <Link to={`${base}/features`} className={c.secondaryBtn}>
            Создать признак
          </Link>
          <Link to={`${base}/experiments/create`} className={c.secondaryBtn}>
            Создать эксперимент
          </Link>
          <Link to={`${base}/api-keys`} className={c.secondaryBtn}>
            Создать API-ключ
          </Link>
        </div>
      </div>
    </div>
  );
}

export default ProjectOverview;

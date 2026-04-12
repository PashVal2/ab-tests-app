import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  finishExperiment,
  getExperimentByKey,
  startExperiment,
} from "../../../../api/ExperimentApi";
import type { ExperimentDetailsResponse } from "../../../../types/ExperimentTypes";
import useActiveProject from "../../../../hooks/useActiveProject";
import c from "./ExperimentDetails.module.scss";

function ExperimentDetails() {
  const { project } = useActiveProject();
  const { externalKey } = useParams();

  const [experiment, setExperiment] = useState<ExperimentDetailsResponse | null>(null);
  const [loadingAction, setLoadingAction] = useState(false);
  const [error, setError] = useState("");

  const load = async () => {
    if (!project || !externalKey) return;
    const data = await getExperimentByKey(project.id, externalKey);
    setExperiment(data);
  };

  useEffect(() => {
    const fetchExperiment = async () => {
      if (!project || !externalKey) return;
      const data = await getExperimentByKey(project.id, externalKey);
      setExperiment(data);
    };

    fetchExperiment();
  }, [project, externalKey]);

  const handleStart = async () => {
    if (!project || !experiment) return;

    try {
      setLoadingAction(true);
      setError("");
      await startExperiment(project.id, experiment.id);
      await load();
    } catch {
      setError("Не удалось запустить эксперимент");
    } finally {
      setLoadingAction(false);
    }
  };

  const handleFinish = async () => {
    if (!project || !experiment) return;

    try {
      setLoadingAction(true);
      setError("");
      await finishExperiment(project.id, experiment.id);
      await load();
    } catch {
      setError("Не удалось остановить эксперимент");
    } finally {
      setLoadingAction(false);
    }
  };

  if (!project || !experiment) {
    return null;
  }

  return (
    <div className={c.wrapper}>
      <div className={c.header}>
        <div className={c.headerInfo}>
          <h2 className={c.title}>{experiment.name}</h2>
          <div className={`${c.statusBadge} ${c[experiment.status]}`}>
            {experiment.status}
          </div>
        </div>

        <div className={c.actions}>
          {experiment.status === "DRAFT" && (
            <button
              onClick={handleStart}
              disabled={loadingAction}
              className={c.primaryBtn}
            >
              {loadingAction ? "Запуск..." : "Запустить"}
            </button>
          )}

          {experiment.status === "RUNNING" && (
            <button
              onClick={handleFinish}
              disabled={loadingAction}
              className={c.dangerBtn}
            >
              {loadingAction ? "Остановка..." : "Остановить"}
            </button>
          )}

          <Link
            to={`/ab-testing/project/${project.code}/experiments/${experiment.externalKey}/results`}
            className={c.secondaryBtn}
          >
            Результаты
          </Link>
        </div>
      </div>

      {error && <div className={c.error}>{error}</div>}

      <div className={c.card}>
        <div className={c.row}>
          <b>Статус:</b>
          <span>{experiment.status}</span>
        </div>
        <div className={c.row}>
          <b>Основная метрика:</b>
          <span>{experiment.primaryMetric}</span>
        </div>
        <div className={c.row}>
          <b>External key:</b>
          <span>{experiment.externalKey}</span>
        </div>
        <div className={c.row}>
          <b>H0:</b>
          <span>{experiment.nullHypothesis}</span>
        </div>
        <div className={c.row}>
          <b>H1:</b>
          <span>{experiment.alternativeHypothesis}</span>
        </div>
      </div>

      <div className={c.section}>
        <h3 className={c.subtitle}>Варианты</h3>

        <div className={c.variants}>
          {experiment.variants.map((variant) => (
            <div key={variant.id} className={c.variantCard}>
              <div className={c.variantHeader}>
                <span className={c.variantTitle}>{variant.name}</span>
                <span>{variant.control ? "Контрольный" : "Тестовый"}</span>
              </div>

              <div className={c.variantInfo}>
                <div>
                  <b>Трафик:</b> {variant.trafficPercent}%
                </div>
                <div>
                  <b>Код:</b> {variant.externalCode}
                </div>
              </div>

              <div className={c.features}>
                {variant.features.map((feature) => (
                  <div key={feature.featureId} className={c.featureItem}>
                    <span>
                      {feature.featureName} ({feature.featureCode})
                    </span>
                    <span>вес {feature.weight}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default ExperimentDetails;

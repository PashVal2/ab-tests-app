import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  Cell,
} from "recharts";
import {
  getExperimentByKey,
  getExperimentResults,
} from "../../../../api/ExperimentApi";
import type { ExperimentResultsResponse } from "../../../../types/ExperimentTypes";
import useActiveProject from "../../../../hooks/useActiveProject";
import c from "./ExperimentResults.module.scss";

function formatPercent(value: number) {
  return `${(value * 100).toFixed(2)}%`;
}

function formatNullable(value: number | null) {
  if (value === null || Number.isNaN(value)) return "—";
  return value.toFixed(4);
}

function formatUplift(value: number | null | undefined) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return "—";
  }
  return `${value > 0 ? "+" : ""}${value.toFixed(2)}%`;
}

function formatDiff(value: number | null | undefined) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return "—";
  }
  return `${value > 0 ? "+" : ""}${(value * 100).toFixed(2)} п.п.`;
}

function ExperimentResults() {
  const { project } = useActiveProject();
  const { externalKey } = useParams();

  const [results, setResults] = useState<ExperimentResultsResponse | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        if (!project || !externalKey) return;

        const experiment = await getExperimentByKey(project.id, externalKey);
        const data = await getExperimentResults(project.id, experiment.id);

        setResults(data);
      } catch {
        setError("Не удалось загрузить результаты");
      }
    };

    load();
  }, [project, externalKey]);

  const metricChartData = useMemo(() => {
    if (!results) return [];

    return results.variants.map((variant) => ({
      name: variant.variantName,
      metricValue: Number((variant.metricValue * 100).toFixed(2)),
      control: variant.control,
    }));
  }, [results]);

  const eventsChartData = useMemo(() => {
    if (!results) return [];

    return results.variants.map((variant) => ({
      name: variant.variantName,
      impressions: variant.impressions,
      clicks: variant.clicks,
      likes: variant.likes,
    }));
  }, [results]);

  if (!project) {
    return null;
  }

  if (error) {
    return <div className={c.wrapper}>{error}</div>;
  }

  if (!results) {
    return <div className={c.wrapper}>Загрузка...</div>;
  }

  return (
    <div className={c.wrapper}>
      <div className={c.top}>
        <div>
          <h2 className={c.title}>Результаты эксперимента</h2>
          <div className={c.subtitle}>{results.experimentName}</div>
        </div>

        <Link
          to={`/ab-testing/project/${project.code}/experiments/${results.externalKey}`}
          className={c.backLink}
        >
          Назад
        </Link>
      </div>

      <div className={c.summary}>
        <div className={c.summaryCard}>
          <span>Основная метрика</span>
          <b>{results.primaryMetric}</b>
        </div>
        <div className={c.summaryCard}>
          <span>Статус</span>
          <b>{results.status}</b>
        </div>
        <div className={c.summaryCard}>
          <span>Победитель</span>
          <b>{results.winnerVariantName ?? "Не определён"}</b>
        </div>
        <div className={c.summaryCard}>
          <span>Стат. значимость</span>
          <b>{results.statisticallySignificant ? "Есть" : "Нет"}</b>
        </div>
      </div>

      <div className={c.chartCard}>
        <div className={c.chartHeader}>
          <h3>Сравнение основной метрики</h3>
          <p>Метрика считается на уровне пользователей, а не количества всех событий.</p>
        </div>

        <div className={c.chartWrap}>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={metricChartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip formatter={(value) => [`${value}%`, results.primaryMetric]} />
              <Legend />
              <Bar dataKey="metricValue" name={results.primaryMetric} radius={[8, 8, 0, 0]}>
                {metricChartData.map((entry, index) => (
                  <Cell
                    key={`metric-cell-${index}`}
                    fill={entry.control ? "#94a3b8" : "#2563eb"}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className={c.chartCard}>
        <div className={c.chartHeader}>
          <h3>Сырые события по вариантам</h3>
          <p>Эти числа нужны для контекста, но решение о победителе принимается по user-level метрике.</p>
        </div>

        <div className={c.chartWrap}>
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={eventsChartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar
                dataKey="impressions"
                name="Impressions"
                fill="#94a3b8"
                radius={[6, 6, 0, 0]}
              />
              <Bar
                dataKey="clicks"
                name="Clicks"
                fill="#2563eb"
                radius={[6, 6, 0, 0]}
              />
              <Bar
                dataKey="likes"
                name="Likes"
                fill="#db2777"
                radius={[6, 6, 0, 0]}
              />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className={c.cards}>
        {results.variants.map((variant) => (
          <div key={variant.variantId} className={c.card}>
            <div className={c.cardHeader}>
              <div>
                <div className={c.variantTitle}>
                  {variant.variantName}
                  {variant.control && <span className={c.badge}>Контроль</span>}
                  {results.winnerVariantId === variant.variantId && (
                    <span className={c.winner}>Победитель</span>
                  )}
                </div>
                <div className={c.variantMeta}>Код: {variant.externalCode}</div>
              </div>

              <div className={c.metricBox}>
                <span>{variant.metricName}</span>
                <b>{formatPercent(variant.metricValue)}</b>
              </div>
            </div>

            <div className={c.grid}>
              <div>
                <span>Назначено пользователей</span>
                <b>{variant.assignedUsers}</b>
              </div>
              <div>
                <span>Пользователи в базе метрики</span>
                <b>{variant.denominatorUsers}</b>
              </div>
              <div>
                <span>Пользователи с целевым действием</span>
                <b>{variant.numeratorUsers}</b>
              </div>
              <div>
                <span>Uplift</span>
                <b>{formatUplift(variant.upliftPercent)}</b>
              </div>
              <div>
                <span>Разница с контролем</span>
                <b>{formatDiff(variant.diffFromControl)}</b>
              </div>
              <div>
                <span>p-value</span>
                <b>{formatNullable(variant.pValue)}</b>
              </div>
              <div>
                <span>95% CI</span>
                <b>
                  {variant.ci95Lower == null || variant.ci95Upper == null
                    ? "—"
                    : `[${(variant.ci95Lower * 100).toFixed(2)}; ${(variant.ci95Upper * 100).toFixed(2)}] п.п.`}
                </b>
              </div>
              <div>
                <span>Стат. значимость</span>
                <b>{variant.statisticallySignificant ? "Есть" : "Нет"}</b>
              </div>
            </div>

            <div className={c.events}>
              <div>
                Impression: <b>{variant.impressions}</b>
              </div>
              <div>
                Click: <b>{variant.clicks}</b>
              </div>
              <div>
                View: <b>{variant.views}</b>
              </div>
              <div>
                Watch start: <b>{variant.watchStarts}</b>
              </div>
              <div>
                Watch finish: <b>{variant.watchFinishes}</b>
              </div>
              <div>
                Like: <b>{variant.likes}</b>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ExperimentResults;

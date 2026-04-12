import { useEffect, useMemo, useState } from "react";
import c from "./Constructor.module.scss";
import VariantBlock from "../../components/VariantBlock";
import { createExperiment } from "../../../../api/ExperimentApi";
import { getActiveFeatures } from "../../../../api/FeatureApi";
import type {
  ExperimentRequest,
  MetricType,
  VariantRequest,
  VariantState
} from "../../../../types/ExperimentTypes";
import type { Feature } from "../../../../types/FeatureTypes";
import useActiveProject from "../../../../hooks/useActiveProject";

type Notification = {
  text: string;
  type: "success" | "error";
};

const metricOptions: { value: MetricType; label: string }[] = [
  { value: "CTR", label: "CTR" },
  { value: "VIEW_RATE", label: "View rate" },
  { value: "LIKE_RATE", label: "Like rate" },
  { value: "WATCH_START_RATE", label: "Watch start rate" },
  { value: "WATCH_FINISH_RATE", label: "Watch finish rate" },
  { value: "WATCH_THROUGH_RATE", label: "Watch through rate" },
];

function Constructor() {
  const { project } = useActiveProject();

  const [experimentName, setExperimentName] = useState("");
  const [nullHypothesis, setNullHypothesis] = useState("");
  const [alternativeHypothesis, setAlternativeHypothesis] = useState("");
  const [primaryMetric, setPrimaryMetric] = useState<MetricType>("CTR");
  const [features, setFeatures] = useState<Feature[]>([]);
  const [variants, setVariants] = useState<Record<"A" | "B", VariantState>>({
    A: { selected: {}, weights: {}, trafficPercent: "50", externalCode: "A" },
    B: { selected: {}, weights: {}, trafficPercent: "50", externalCode: "B" },
  });
  const [notification, setNotification] = useState<Notification | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const load = async () => {
      if (!project) return;

      try {
        const data = await getActiveFeatures(project.id);
        setFeatures(data);

        const selected = Object.fromEntries(data.map((f) => [f.id, true]));
        const weights = Object.fromEntries(data.map((f) => [f.id, "0"]));

        setVariants({
          A: { selected, weights, trafficPercent: "50", externalCode: "A" },
          B: {
            selected: { ...selected },
            weights: { ...weights },
            trafficPercent: "50",
            externalCode: "B",
          },
        });
      } catch {
        setNotification({ text: "Не удалось загрузить признаки", type: "error" });
      }
    };

    load();
  }, [project]);

  const toggleFeature = (variant: "A" | "B", id: number) => {
    setVariants((prev) => ({
      ...prev,
      [variant]: {
        ...prev[variant],
        selected: {
          ...prev[variant].selected,
          [id]: !prev[variant].selected[id],
        },
      },
    }));
  };

  const changeWeight = (variant: "A" | "B", id: number, value: string) => {
    setVariants((prev) => ({
      ...prev,
      [variant]: {
        ...prev[variant],
        weights: {
          ...prev[variant].weights,
          [id]: value,
        },
      },
    }));
  };

  const changeTrafficPercent = (variant: "A" | "B", value: string) => {
    setVariants((prev) => ({
      ...prev,
      [variant]: {
        ...prev[variant],
        trafficPercent: value,
      },
    }));
  };

  const changeExternalCode = (variant: "A" | "B", value: string) => {
    setVariants((prev) => ({
      ...prev,
      [variant]: {
        ...prev[variant],
        externalCode: value,
      },
    }));
  };

  const buildVariantRequest = useMemo(() => {
    return (variantName: "A" | "B", control: boolean): VariantRequest => {
      const state = variants[variantName];

      return {
        name: variantName,
        control,
        trafficPercent: Number(state.trafficPercent),
        externalCode: state.externalCode,
        features: features
          .filter((feature) => state.selected[feature.id])
          .map((feature) => ({
            featureId: feature.id,
            weight: Number(state.weights[feature.id] || "0"),
          })),
      };
    };
  }, [features, variants]);

  const handleCreateExperiment = async () => {
    if (!project) return;

    try {
      setLoading(true);
      setNotification(null);

      const payload: ExperimentRequest = {
        name: experimentName,
        nullHypothesis,
        alternativeHypothesis,
        primaryMetric,
        variants: [
          buildVariantRequest("A", true),
          buildVariantRequest("B", false),
        ],
      };

      await createExperiment(project.id, payload);

      setNotification({ text: "Эксперимент создан", type: "success" });
      setExperimentName("");
      setNullHypothesis("");
      setAlternativeHypothesis("");
    } catch {
      setNotification({ text: "Не удалось создать эксперимент", type: "error" });
    } finally {
      setLoading(false);
    }
  };

  if (!project) {
    return null;
  }

  return (
    <div className={c.panelWrapper}>
      <h2>Конструктор эксперимента</h2>

      <input
        type="text"
        placeholder="Название эксперимента"
        value={experimentName}
        onChange={(e) => setExperimentName(e.target.value)}
      />

      <input
        type="text"
        placeholder="H0: нулевая гипотеза"
        value={nullHypothesis}
        onChange={(e) => setNullHypothesis(e.target.value)}
      />

      <input
        type="text"
        placeholder="H1: альтернативная гипотеза"
        value={alternativeHypothesis}
        onChange={(e) => setAlternativeHypothesis(e.target.value)}
      />

      <div className={c.selectWrapper}>
        <select
          value={primaryMetric}
          onChange={(e) => setPrimaryMetric(e.target.value as MetricType)}
          className={c.select}
        >
          {metricOptions.map((metric) => (
            <option key={metric.value} value={metric.value}>
              {metric.label}
            </option>
          ))}
        </select>
      </div>

      <VariantBlock
        variantName="A"
        trafficPercent={variants.A.trafficPercent}
        externalCode={variants.A.externalCode}
        selected={variants.A.selected}
        weights={variants.A.weights}
        features={features}
        changeTrafficPercent={(value) => changeTrafficPercent("A", value)}
        changeExternalCode={(value) => changeExternalCode("A", value)}
        toggleFeature={(id) => toggleFeature("A", id)}
        changeWeight={(id, value) => changeWeight("A", id, value)}
      />

      <VariantBlock
        variantName="B"
        trafficPercent={variants.B.trafficPercent}
        externalCode={variants.B.externalCode}
        selected={variants.B.selected}
        weights={variants.B.weights}
        features={features}
        changeTrafficPercent={(value) => changeTrafficPercent("B", value)}
        changeExternalCode={(value) => changeExternalCode("B", value)}
        toggleFeature={(id) => toggleFeature("B", id)}
        changeWeight={(id, value) => changeWeight("B", id, value)}
      />

      <button
        onClick={handleCreateExperiment}
        disabled={
          loading ||
          !experimentName ||
          !nullHypothesis ||
          !alternativeHypothesis
        }
        className={loading ? c.loadingBtn : ""}
      >
        Создать эксперимент
      </button>

      {notification && (
        <div className={`${c.toast} ${c[notification.type]}`}>
          {notification.text}
        </div>
      )}
    </div>
  );
}

export default Constructor;

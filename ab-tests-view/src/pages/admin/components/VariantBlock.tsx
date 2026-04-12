import type { Feature } from "../../../types/FeatureTypes";
import c from "./VariantBlock.module.scss";

interface VariantBlockProps {
  variantName: string;
  trafficPercent: string;
  externalCode: string;
  selected: Record<number, boolean>;
  weights: Record<number, string>;
  changeTrafficPercent: (value: string) => void;
  changeExternalCode: (value: string) => void;
  toggleFeature: (id: number) => void;
  changeWeight: (id: number, value: string) => void;
  features: Feature[];
}

function VariantBlock({
  variantName,
  trafficPercent,
  externalCode,
  selected,
  weights,
  changeTrafficPercent,
  changeExternalCode,
  toggleFeature,
  changeWeight,
  features,
}: VariantBlockProps) {
  return (
    <div className={c.variantBlock}>
      <h3>Вариант {variantName}</h3>

      <div className={c.rowsGroup}>
        <div className={`${c.row} ${c.topRow}`}>
          <span className={c.row__label}>Название варианта</span>
          <input
            type="text"
            placeholder="Например, control_A"
            value={externalCode}
            onChange={(e) => changeExternalCode(e.target.value)}
            className={c.textInput}
          />
        </div>

        <div className={`${c.row} ${c.topRow}`}>
          <span className={c.rowLabel}>Распределение пользователей</span>
          <div className={c.percentInputWrap}>
            <input
              type="number"
              placeholder="0"
              min={0}
              max={100}
              step={0.1}
              value={trafficPercent}
              onChange={(e) => changeTrafficPercent(e.target.value)}
              className={c.numberInput}
            />
            <span className={c.inputSuffix}>%</span>
          </div>
        </div>

        {features.map((f) => (
          <div
            key={f.id}
            className={`${c.row} ${selected[f.id] ? c.selected : c.unselected}`}
          >
            <input
              type="checkbox"
              checked={selected[f.id]}
              onChange={() => toggleFeature(f.id)}
            />
            <span className={c.featureName}>{f.name}</span>
            <input
              type="number"
              placeholder="0.0"
              step={0.1}
              value={weights[f.id]}
              onChange={(e) => changeWeight(f.id, e.target.value)}
              className={c.numberInput}
              disabled={!selected[f.id]}
            />
          </div>
        ))}
      </div>
    </div>
  );
}

export default VariantBlock;

import { useEffect, useState, useCallback } from "react";
import {
  activateFeature,
  createFeature,
  deactivateFeature,
  getFeatures,
  updateFeature,
} from "../../../../api/FeatureApi";
import type { CreateFeatureRequest, Feature } from "../../../../types/FeatureTypes";
import useActiveProject from "../../../../hooks/useActiveProject";
import c from "./Feature.module.scss";

function Features() {
  const { project } = useActiveProject();

  const [features, setFeatures] = useState<Feature[]>([]);
  const [code, setCode] = useState("");
  const [name, setName] = useState("");
  const [loadingActionId, setLoadingActionId] = useState<number | null>(null);

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editCode, setEditCode] = useState("");
  const [editName, setEditName] = useState("");

  const loadFeatures = useCallback(async () => {
    if (!project) return;
    const data = await getFeatures(project.id);
    setFeatures(data);
  }, [project]);

  useEffect(() => {
    loadFeatures();
  }, [loadFeatures]);

  const handleCreate = async () => {
    if (!project) return;

    const payload: CreateFeatureRequest = { code, name };
    const created = await createFeature(project.id, payload);

    setFeatures((prev) => [...prev, created]);
    setCode("");
    setName("");
  };

  const handleToggle = async (feature: Feature) => {
    if (!project) return;

    try {
      setLoadingActionId(feature.id);

      const updatedFeature = feature.active
        ? await deactivateFeature(project.id, feature.id)
        : await activateFeature(project.id, feature.id);

      setFeatures((prev) =>
        prev.map((item) => (item.id === updatedFeature.id ? updatedFeature : item))
      );
    } finally {
      setLoadingActionId(null);
    }
  };

  const startEdit = (feature: Feature) => {
    setEditingId(feature.id);
    setEditCode(feature.code);
    setEditName(feature.name);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditCode("");
    setEditName("");
  };

  const handleSaveEdit = async (featureId: number) => {
    if (!project) return;

    try {
      setLoadingActionId(featureId);

      const updated = await updateFeature(project.id, featureId, {
        code: editCode,
        name: editName,
      });

      setFeatures((prev) =>
        prev.map((item) => (item.id === updated.id ? updated : item))
      );

      cancelEdit();
    } finally {
      setLoadingActionId(null);
    }
  };

  if (!project) {
    return null;
  }

  return (
    <div className={c.wrapper}>
      <h2 className={c.title}>Признаки</h2>

      <div className={c.form}>
        <input
          type="text"
          placeholder="Название признака"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <input
          type="text"
          placeholder="Код признака"
          value={code}
          onChange={(e) => setCode(e.target.value)}
        />
        <button onClick={handleCreate} disabled={!code || !name}>
          Добавить
        </button>
      </div>

      <div className={c.list}>
        {features.length === 0 ? (
          <p className={c.empty}>Признаков пока нет</p>
        ) : (
          features.map((feature) => {
            const isEditing = editingId === feature.id;

            return (
              <div key={feature.id} className={c.item}>
                {isEditing ? (
                  <div className={c.editBlock}>
                    <input
                      type="text"
                      value={editName}
                      onChange={(e) => setEditName(e.target.value)}
                      placeholder="Название"
                    />
                    <input
                      type="text"
                      value={editCode}
                      onChange={(e) => setEditCode(e.target.value)}
                      placeholder="Код"
                    />
                    <div className={c.actionsRow}>
                      <button
                        className={c.saveBtn}
                        onClick={() => handleSaveEdit(feature.id)}
                        disabled={!editName || !editCode || loadingActionId === feature.id}
                      >
                        {loadingActionId === feature.id ? "Сохранение..." : "Сохранить"}
                      </button>
                      <button className={c.cancelBtn} onClick={cancelEdit}>
                        Отмена
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className={c.itemTop}>
                    <div>
                      <div className={c.itemTitle}>{feature.name}</div>
                      <div className={c.itemMeta}>
                        <span>{feature.code}</span>
                        <span>{feature.active ? "Активен" : "Неактивен"}</span>
                      </div>
                    </div>

                    <div className={c.actionsRow}>
                      <button
                        className={c.editBtn}
                        onClick={() => startEdit(feature)}
                      >
                        Изменить
                      </button>

                      <button
                        className={feature.active ? c.deactivateBtn : c.activateBtn}
                        onClick={() => handleToggle(feature)}
                        disabled={loadingActionId === feature.id}
                      >
                        {loadingActionId === feature.id
                          ? "Сохранение..."
                          : feature.active
                          ? "Отключить"
                          : "Включить"}
                      </button>
                    </div>
                  </div>
                )}
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}

export default Features;

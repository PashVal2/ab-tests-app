import { useEffect, useState, useCallback } from "react";
import {
  createApiKey,
  getApiKeys,
  revokeApiKey,
} from "../../../../api/ApiKeysApi";
import type {
  ApiKeyResponse,
  ApiKeyStatus,
  CreateApiKeyResponse,
} from "../../../../types/ApiKeysTypes";
import useActiveProject from "../../../../hooks/useActiveProject";
import c from "./ApiKeys.module.scss";

const statusOptions: ApiKeyStatus[] = ["ACTIVE", "REVOKED"];

function ApiKeys() {
  const { project } = useActiveProject();

  const [apiKeys, setApiKeys] = useState<ApiKeyResponse[]>([]);
  const [name, setName] = useState("");
  const [createdKey, setCreatedKey] = useState<CreateApiKeyResponse | null>(null);
  const [loadingPage, setLoadingPage] = useState(true);
  const [loadingActionId, setLoadingActionId] = useState<number | null>(null);

  const [filterName, setFilterName] = useState("");
  const [keyPrefix, setKeyPrefix] = useState("");
  const [status, setStatus] = useState("");
  const [createdFrom, setCreatedFrom] = useState("");
  const [createdTo, setCreatedTo] = useState("");

  const [debouncedFilterName, setDebouncedFilterName] = useState("");
  const [debouncedKeyPrefix, setDebouncedKeyPrefix] = useState("");

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedFilterName(filterName);
    }, 400);

    return () => clearTimeout(timer);
  }, [filterName]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedKeyPrefix(keyPrefix);
    }, 400);

    return () => clearTimeout(timer);
  }, [keyPrefix]);

  const loadApiKeys = useCallback(async () => {
    if (!project) {
      setLoadingPage(false);
      return;
    }

    setLoadingPage(true);
    try {
      const data = await getApiKeys(project.id, {
        name: debouncedFilterName || undefined,
        keyPrefix: debouncedKeyPrefix || undefined,
        status: status ? (status as ApiKeyStatus) : undefined,
        createdFrom: createdFrom ? new Date(createdFrom).toISOString() : undefined,
        createdTo: createdTo ? new Date(createdTo).toISOString() : undefined,
      });

      setApiKeys(data);
    } finally {
      setLoadingPage(false);
    }
  }, [project, debouncedFilterName, debouncedKeyPrefix, status, createdFrom, createdTo]);

  useEffect(() => {
    loadApiKeys();
  }, [loadApiKeys]);

  const handleRefresh = async () => {
    await loadApiKeys();
  };

  const handleCreate = async () => {
    if (!project) return;

    const data = await createApiKey(project.id, { name });
    setCreatedKey(data);
    setName("");

    await loadApiKeys();
  };

  const handleRevoke = async (apiKeyId: number) => {
    if (!project) return;

    try {
      setLoadingActionId(apiKeyId);

      const updated = await revokeApiKey(project.id, apiKeyId);

      setApiKeys((prev) =>
        prev.map((item) => (item.id === updated.id ? updated : item))
      );
    } finally {
      setLoadingActionId(null);
    }
  };

  const handleReset = () => {
    setFilterName("");
    setKeyPrefix("");
    setStatus("");
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
          <h2 className={c.title}>API-ключи</h2>
          <button className={c.refreshBtn} onClick={handleRefresh}>
            Обновить
          </button>
        </div>

        <div className={c.form}>
          <input
            type="text"
            placeholder="Название ключа"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <button onClick={handleCreate} disabled={!name.trim()}>
            Создать ключ
          </button>
        </div>

        {createdKey && (
          <div className={c.createdKey}>
            <div className={c.createdKeyTitle}>Скопировать ключ:</div>
            <div className={c.createdKeyValue}>{createdKey.rawKey}</div>
          </div>
        )}

        {loadingPage ? (
          <div className={c.empty}>Загрузка...</div>
        ) : apiKeys.length === 0 ? (
          <div className={c.empty}>Ключей не найдено</div>
        ) : (
          <div className={c.list}>
            {apiKeys.map((key) => (
              <div key={key.id} className={c.item}>
                <div className={c.itemTop}>
                  <div className={c.itemTitle}>{key.name}</div>

                  {key.status === "ACTIVE" ? (
                    <button
                      className={c.revokeBtn}
                      onClick={() => handleRevoke(key.id)}
                      disabled={loadingActionId === key.id}
                    >
                      {loadingActionId === key.id ? "Отзыв..." : "Отозвать"}
                    </button>
                  ) : (
                    <span className={c.revokedBadge}>REVOKED</span>
                  )}
                </div>

                <div className={c.itemMeta}>
                  <span>prefix: {key.keyPrefix}</span>
                  <span>status: {key.status}</span>
                  <span>created: {new Date(key.createdAt).toLocaleString()}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <aside className={c.filtersSection}>
        <div className={c.filters}>
          <label>
            <span>Название</span>
            <input
              type="text"
              value={filterName}
              onChange={(e) => setFilterName(e.target.value)}
              placeholder="Поиск по названию"
            />
          </label>

          <label>
            <span>Prefix</span>
            <input
              type="text"
              value={keyPrefix}
              onChange={(e) => setKeyPrefix(e.target.value)}
              placeholder="Например abcd1234"
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

export default ApiKeys;

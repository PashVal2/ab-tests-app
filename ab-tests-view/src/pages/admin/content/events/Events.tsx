import { useEffect, useState } from "react";
import { getEvents, getEventsSummary } from "../../../../api/EventApi";
import { getExperiments } from "../../../../api/ExperimentApi";
import type { ExperimentResponse } from "../../../../types/ExperimentTypes";
import type {
  EventPageResponse,
  EventType,
  EventSummaryResponse,
} from "../../../../types/EventTypes";
import c from "./Events.module.scss";
import useActiveProject from "../../../../hooks/useActiveProject";

const PAGE_SIZE = 50;

const eventTypeOptions: EventType[] = [
  "IMPRESSION",
  "CLICK",
  "VIEW",
  "WATCH_START",
  "WATCH_FINISH",
  "LIKE",
];

function Events() {
  const { project } = useActiveProject();

  const [eventsPage, setEventsPage] = useState<EventPageResponse | null>(null);
  const [summary, setSummary] = useState<EventSummaryResponse | null>(null);
  const [experiments, setExperiments] = useState<ExperimentResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const [externalKey, setExternalKey] = useState("");
  const [eventType, setEventType] = useState("");
  const [externalUserId, setExternalUserId] = useState("");
  const [createdFrom, setCreatedFrom] = useState("");
  const [createdTo, setCreatedTo] = useState("");

  const [debouncedExternalUserId, setDebouncedExternalUserId] = useState("");
  const [page, setPage] = useState(0);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedExternalUserId(externalUserId);
    }, 400);

    return () => clearTimeout(timer);
  }, [externalUserId]);

  useEffect(() => {
    const loadExperiments = async () => {
      if (!project) return;
      const experimentsData = await getExperiments(project.id);
      setExperiments(experimentsData);
    };

    loadExperiments();
  }, [project]);

  useEffect(() => {
    setPage(0);
  }, [externalKey, eventType, debouncedExternalUserId, createdFrom, createdTo]);

  useEffect(() => {
    const loadData = async () => {
      if (!project) {
        setLoading(false);
        return;
      }

      setLoading(true);
      try {
        const params = {
          externalKey: externalKey || undefined,
          eventType: eventType ? (eventType as EventType) : undefined,
          externalUserId: debouncedExternalUserId || undefined,
          createdFrom: createdFrom ? new Date(createdFrom).toISOString() : undefined,
          createdTo: createdTo ? new Date(createdTo).toISOString() : undefined,
        };

        const [eventsData, summaryData] = await Promise.all([
          getEvents(project.id, {
            ...params,
            page,
            size: PAGE_SIZE,
          }),
          getEventsSummary(project.id, params),
        ]);

        setEventsPage(eventsData);
        setSummary(summaryData);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [project, externalKey, eventType, debouncedExternalUserId, createdFrom, createdTo, page]);

  const handleRefresh = async () => {
    if (!project) return;

    setLoading(true);
    try {
      const params = {
        externalKey: externalKey || undefined,
        eventType: eventType ? (eventType as EventType) : undefined,
        externalUserId: debouncedExternalUserId || undefined,
        createdFrom: createdFrom ? new Date(createdFrom).toISOString() : undefined,
        createdTo: createdTo ? new Date(createdTo).toISOString() : undefined,
      };

      const [eventsData, summaryData] = await Promise.all([
        getEvents(project.id, {
          ...params,
          page,
          size: PAGE_SIZE,
        }),
        getEventsSummary(project.id, params),
      ]);

      setEventsPage(eventsData);
      setSummary(summaryData);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setExternalKey("");
    setEventType("");
    setExternalUserId("");
    setCreatedFrom("");
    setCreatedTo("");
    setPage(0);
  };

  const events = eventsPage?.content ?? [];
  const totalPages = Math.max(eventsPage?.totalPages ?? 1, 1);
  const currentPage = (eventsPage?.page ?? 0) + 1;

  const getVisiblePages = () => {
    const pages: (number | "...")[] = [];

    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i += 1) {
        pages.push(i);
      }
      return pages;
    }

    pages.push(1);

    if (currentPage > 3) {
      pages.push("...");
    }

    const start = Math.max(2, currentPage - 1);
    const end = Math.min(totalPages - 1, currentPage + 1);

    for (let i = start; i <= end; i += 1) {
      pages.push(i);
    }

    if (currentPage < totalPages - 2) {
      pages.push("...");
    }

    pages.push(totalPages);

    return pages;
  };

  const visiblePages = getVisiblePages();

  if (!project) {
    return null;
  }

  return (
    <div className={c.wrapper}>
      <div className={c.listSection}>
        <div className={c.header}>
          <h2 className={c.title}>События пользователей</h2>
          <button className={c.refreshBtn} onClick={handleRefresh}>
            Обновить
          </button>
        </div>

        {summary && (
          <div className={c.summaryGrid}>
            <div className={c.summaryCard}>
              <span>Всего</span>
              <b>{summary.total}</b>
            </div>
            <div className={c.summaryCard}>
              <span>Impression</span>
              <b>{summary.impressions}</b>
            </div>
            <div className={c.summaryCard}>
              <span>Click</span>
              <b>{summary.clicks}</b>
            </div>
            <div className={c.summaryCard}>
              <span>View</span>
              <b>{summary.views}</b>
            </div>
            <div className={c.summaryCard}>
              <span>Watch start</span>
              <b>{summary.watchStarts}</b>
            </div>
            <div className={c.summaryCard}>
              <span>Watch finish</span>
              <b>{summary.watchFinishes}</b>
            </div>
            <div className={c.summaryCard}>
              <span>Like</span>
              <b>{summary.likes}</b>
            </div>
          </div>
        )}

        {events.length === 0 && !loading ? (
          <div className={c.empty}>Событий не найдено</div>
        ) : events.length > 0 ? (
          <>
            <div className={c.tableWrap}>
              <table className={c.table}>
                <thead>
                  <tr>
                    <th>Эксперимент</th>
                    <th>id</th>
                    <th>Вариант</th>
                    <th>Пользователь</th>
                    <th>Контент</th>
                    <th>Тип</th>
                    <th>Дата</th>
                  </tr>
                </thead>
                <tbody>
                  {events.map((event) => (
                    <tr key={event.id}>
                      <td>{event.experimentName}</td>
                      <td>{event.experimentExternalKey}</td>
                      <td>{event.variantName}</td>
                      <td>{event.externalUserId}</td>
                      <td>{event.contentId ?? "—"}</td>
                      <td>
                        <span className={`${c.eventBadge} ${c[event.eventType]}`}>
                          {event.eventType}
                        </span>
                      </td>
                      <td>{new Date(event.createdAt).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className={c.pagination}>
              <button
                className={c.pageArrow}
                onClick={() => setPage(0)}
                disabled={!eventsPage || eventsPage.first}
                aria-label="Первая страница"
              >
                «
              </button>

              <button
                className={c.pageArrow}
                onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                disabled={!eventsPage || eventsPage.first}
                aria-label="Предыдущая страница"
              >
                ‹
              </button>

              <div className={c.pageNumbers}>
                {visiblePages.map((item, index) =>
                  item === "..." ? (
                    <span key={`dots-${index}`} className={c.pageDots}>
                      ...
                    </span>
                  ) : (
                    <button
                      key={item}
                      className={`${c.pageBtn} ${item === currentPage ? c.activePage : ""}`}
                      onClick={() => setPage(item - 1)}
                      aria-label={`Страница ${item}`}
                      aria-current={item === currentPage ? "page" : undefined}
                    >
                      {item}
                    </button>
                  )
                )}
              </div>

              <button
                className={c.pageArrow}
                onClick={() => {
                  if (!eventsPage || eventsPage.last) return;
                  setPage((prev) => prev + 1);
                }}
                disabled={!eventsPage || eventsPage.last}
                aria-label="Следующая страница"
              >
                ›
              </button>

              <button
                className={c.pageArrow}
                onClick={() => setPage(totalPages - 1)}
                disabled={!eventsPage || eventsPage.last}
                aria-label="Последняя страница"
              >
                »
              </button>
            </div>
          </>
        ) : null}
      </div>

      <aside className={c.filtersSection}>
        <div className={c.filters}>
          <label className={c.selectField}>
            <span>Эксперимент</span>
            <select value={externalKey} onChange={(e) => setExternalKey(e.target.value)}>
              <option value="">Все</option>
              {experiments.map((experiment) => (
                <option key={experiment.id} value={experiment.externalKey}>
                  {experiment.name}
                </option>
              ))}
            </select>
          </label>

          <label className={c.selectField}>
            <span>Тип события</span>
            <select value={eventType} onChange={(e) => setEventType(e.target.value)}>
              <option value="">Все</option>
              {eventTypeOptions.map((item) => (
                <option key={item} value={item}>
                  {item}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>externalUserId</span>
            <input
              type="text"
              value={externalUserId}
              onChange={(e) => setExternalUserId(e.target.value)}
              placeholder="Например user-42"
            />
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

export default Events;

import useActiveProject from "../../../../hooks/useActiveProject";
import c from "./IntegrationDocs.module.scss";

function IntegrationDocs() {
  const { project } = useActiveProject();

  if (!project) {
    return null;
  }

  return (
    <div className={c.wrapper}>
      <div className={c.hero}>
        <div>
          <h2 className={c.title}>Интеграция</h2>
          <p className={c.subtitle}>
            Инструкция по подключению внешнего клиента к API A/B-тестирования
          </p>
        </div>

        <div className={c.projectBadge}>
          <span className={c.projectCodeLabel}>Проект</span>
          <span className={c.projectCode}>{project.name}</span>
        </div>
      </div>

      <div className={c.card}>
        <h3>Авторизация</h3>
        <p>Для всех integration-запросов передавайте API-ключ в заголовке:</p>
        <pre>{`x-api-key: <YOUR_API_KEY>`}</pre>
      </div>

      <div className={c.card}>
        <h3>1. Получить конфигурацию варианта</h3>
        <p>Клиент получает конфигурацию для пользователя и конкретного эксперимента.</p>
        <pre>{`POST /api/v1/integration/config
Content-Type: application/json
x-api-key: <YOUR_API_KEY>

{
  "experimentExternalKey": "home-feed-ctr-test",
  "externalUserId": "user-42"
}`}</pre>
      </div>

      <div className={c.card}>
        <h3>2. Явно назначить пользователя на вариант</h3>
        <pre>{`POST /api/v1/integration/assign
Content-Type: application/json
x-api-key: <YOUR_API_KEY>

{
  "experimentExternalKey": "home-feed-ctr-test",
  "externalUserId": "user-42"
}`}</pre>
      </div>

      <div className={c.card}>
        <h3>3. Отправить событие</h3>
        <pre>{`POST /api/v1/integration/events
Content-Type: application/json
x-api-key: <YOUR_API_KEY>

{
  "experimentExternalKey": "home-feed-ctr-test",
  "externalUserId": "user-42",
  "eventType": "CLICK",
  "contentId": "movie-1001"
}`}</pre>
      </div>

      <div className={c.card}>
        <h3>Поддерживаемые eventType</h3>
        <ul className={c.eventList}>
          <li>IMPRESSION</li>
          <li>CLICK</li>
          <li>VIEW</li>
          <li>WATCH_START</li>
          <li>WATCH_FINISH</li>
          <li>LIKE</li>
        </ul>
      </div>

      <div className={c.card}>
        <h3>Рекомендуемый порядок интеграции</h3>
        <ol className={c.orderList}>
          <li>Создать API-ключ в проекте.</li>
          <li>На клиенте при заходе пользователя запросить config.</li>
          <li>Применить признаки варианта к интерфейсу.</li>
          <li>При действиях пользователя отправлять события.</li>
          <li>Анализировать результаты в интерфейсе платформы.</li>
        </ol>
      </div>
    </div>
  );
}

export default IntegrationDocs;

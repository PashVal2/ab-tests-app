import { Link } from "react-router-dom";
import c from "./Home.module.scss";

function Home() {
  return (
    <div className={c.home}>
      <section className={c.hero}>
        <div className={c.heroContent}>
          <span className={c.badge}>Платформа A/B тестирования</span>

          <h1 className={c.title}>
            Управляйте экспериментами и
            признаками в одном месте
          </h1>

          <p className={c.subtitle}>
            Создавайте проекты, настраивайте признаки, распределяйте пользователей
            между вариантами и отслеживайте структуру экспериментов через удобный интерфейс.
          </p>

          <div className={c.heroActions}>
            <Link to="/ab-testing/projects" className={c.primaryBtn}>
              Перейти к проекту
            </Link>
          </div>
        </div>
      </section>

      <section className={c.grid}>
        <div className={c.infoCard}>
          <div className={c.infoTop}>
            <span className={c.infoTag}>Основа</span>
          </div>
          <h3>Проекты</h3>
          <p>
            Каждый проект изолирует признаки, эксперименты, API-ключи и события
            внутри собственного рабочего пространства.
          </p>
        </div>

        <div className={c.infoCard}>
          <div className={c.infoTop}>
            <span className={c.infoTag}>Управление</span>
          </div>
          <h3>Признаки</h3>
          <p>
            Создавайте, редактируйте и включайте или отключайте признаки,
            которые участвуют в конфигурации вариантов.
          </p>
        </div>

        <div className={c.infoCard}>
          <div className={c.infoTop}>
            <span className={c.infoTag}>Эксперименты</span>
          </div>
          <h3>A/B тесты</h3>
          <p>
            Настраивайте варианты, задавайте веса признаков, запускайте и
            завершайте эксперименты, отслеживая метрики и статистическую значимость.
          </p>
        </div>

        <div className={c.infoCard}>
          <div className={c.infoTop}>
            <span className={c.infoTag}>Интеграция</span>
          </div>
          <h3>API и события</h3>
          <p>
            Используйте API-ключи, получайте конфигурацию эксперимента,
            назначайте пользователей на варианты и отправляйте события клиента.
          </p>
        </div>
      </section>

      <section className={c.workflow}>
        <div className={c.workflowHeader}>
          <h2>Как это работает</h2>
        </div>

        <div className={c.steps}>
          <div className={c.step}>
            <div className={c.stepNumber}>01</div>
            <div>
              <h3>Создайте проект</h3>
              <p>Проект становится контейнером для признаков, экспериментов и интеграции.</p>
            </div>
          </div>

          <div className={c.step}>
            <div className={c.stepNumber}>02</div>
            <div>
              <h3>Настройте признаки</h3>
              <p>Создайте признаки и включите только те, которые должны участвовать в новых экспериментах.</p>
            </div>
          </div>

          <div className={c.step}>
            <div className={c.stepNumber}>03</div>
            <div>
              <h3>Соберите эксперимент</h3>
              <p>Распределите трафик по вариантам и задайте веса признаков для каждой конфигурации.</p>
            </div>
          </div>

          <div className={c.step}>
            <div className={c.stepNumber}>04</div>
            <div>
              <h3>Подключите клиент</h3>
              <p>Используйте API-ключ, получайте конфигурацию и отправляйте пользовательские события.</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}

export default Home;

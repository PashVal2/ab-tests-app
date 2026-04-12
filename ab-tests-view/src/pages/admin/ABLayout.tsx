import { Outlet } from "react-router-dom";
import c from "./ABLayout.module.scss";
import Sidebar from "./content/Sidebare";
import RequireProjectRoute from "./components/RequireProjectRoute";

function ProjectWorkspaceLayout() {
  return (
    <RequireProjectRoute>
      <div className={c.layout}>
        <Sidebar />
        <div className={c.content}>
          <Outlet />
        </div>
      </div>
    </RequireProjectRoute>
  );
}

export default ProjectWorkspaceLayout;

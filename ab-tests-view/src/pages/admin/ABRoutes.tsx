import { Navigate, Route, Routes } from "react-router-dom";
import Projects from "./content/project/Projects";
import ProjectWorkspaceLayout from "./ABLayout";
import ProjectOverview from "./content/project/ProjectOverview";
import Features from "./content/feature/Feature";
import Constructor from "./content/constructer/Constructor";
import Experiments from "./content/experiments/Expetiment";
import ExperimentDetails from "./content/experiments/ExpetimentDetails";
import ExperimentResults from "./content/experiments/ExperimentResults";
import ApiKeys from "./content/api-key/ApiKeys";
import Events from "./content/events/Events";
import IntegrationDocs from "./content/integration-docs/IntegrationDocs";

function ABRoutes() {
  return (
    <Routes>
      <Route path="" element={<Navigate to="projects" replace />} />
      <Route path="projects" element={<Projects />} />

      <Route path="project/:projectCode" element={<ProjectWorkspaceLayout />}>
        <Route index element={<Navigate to="overview" replace />} />
        <Route path="overview" element={<ProjectOverview />} />

        <Route path="features" element={<Features />} />

        <Route path="experiments" element={<Experiments />} />
        <Route path="experiments/create" element={<Constructor />} />
        <Route path="experiments/:externalKey" element={<ExperimentDetails />} />
        <Route path="experiments/:externalKey/results" element={<ExperimentResults />} />

        <Route path="api-keys" element={<ApiKeys />} />
        <Route path="events" element={<Events />} />
        <Route path="integration" element={<IntegrationDocs />} />
      </Route>
    </Routes>
  );
}

export default ABRoutes;

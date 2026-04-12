import { useEffect, useState } from "react";
import { getProjectByCode } from "../api/ProjectApi";
import type { ProjectResponse } from "../types/ProjectTypes";
import useProjectCodeParam from "./useProjectCodeParam";

const LAST_PROJECT_CODE_STORAGE_KEY = "lastProjectCode";

function useActiveProject() {
  const projectCode = useProjectCodeParam();
  const [project, setProject] = useState<ProjectResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadProject = async () => {
      if (!projectCode) {
        setProject(null);
        setLoading(false);
        return;
      }

      setLoading(true);
      try {
        const data = await getProjectByCode(projectCode);
        setProject(data);
        localStorage.setItem(LAST_PROJECT_CODE_STORAGE_KEY, projectCode);
      } catch {
        setProject(null);
      } finally {
        setLoading(false);
      }
    };

    loadProject();
  }, [projectCode]);

  return {
    projectCode,
    project,
    projectId: project?.id ?? null,
    loading,
    hasProject: !!project,
  };
}

export default useActiveProject;

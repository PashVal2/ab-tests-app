import { useParams } from "react-router-dom";

function useProjectCodeParam(): string | null {
  const { projectCode } = useParams();

  if (!projectCode || projectCode.trim() === "") {
    return null;
  }

  return projectCode;
}

export default useProjectCodeParam;

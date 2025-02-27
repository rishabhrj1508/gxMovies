import { handleApiRequest } from "./ApiService";

const ADMIN_API_URL = "/admin";

// Get Admin Summary
export const getSummary = async () =>
  handleApiRequest("get", `${ADMIN_API_URL}/summary`);

// Get Chart Data by Type
export const getChartData = async (type) =>
  handleApiRequest("get", `${ADMIN_API_URL}/chart`, null, { type });

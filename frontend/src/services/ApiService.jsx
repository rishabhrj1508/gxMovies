import api from "./Api";

export const handleApiRequest = async (
  method,
  url,
  data = null,
  params = null
) => {
  try {
    const response = await api({
      method,
      url,
      data,
      params,
    });

    if (response.data.success) {
      return response.data.data;
    } else {
      throw new Error(response.data.message || "An error occurred");
    }
  } catch (error) {
    console.error(`API Error: ${error.message}`);
    throw error;
  }
};

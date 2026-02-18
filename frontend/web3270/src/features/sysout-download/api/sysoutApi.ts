import { apiClient } from '../../../shared/api/axiosClient';

interface SysoutRequest {
  evt: string;
  user: string;
  password?: string;
  option: string;
  jobId: string;
}

export const requestSysout = async (data: SysoutRequest) => {
  const response = await apiClient.post('/api/sysout/download', data);
  // The response might be a URL to download or the file content itself.
  return response.data;
};

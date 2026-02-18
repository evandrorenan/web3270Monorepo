import React, { useState } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { Button } from '../../../shared/ui/Button';
import { Input } from '../../../shared/ui/Input';
import { Card, CardContent, CardHeader, CardTitle } from '../../../shared/ui/Card';
import { requestSysout } from '../api/sysoutApi';

interface SysoutFormValues {
  evt: string;
  user: string;
  password?: string;
  option: string;
  jobId: string; // Comma separated
}

export const SysoutDownloadForm: React.FC = () => {
  const { register, handleSubmit, formState: { errors } } = useForm<SysoutFormValues>();
  const [downloading, setDownloading] = useState(false);
  const [results, setResults] = useState<string[]>([]);

  const onSubmit: SubmitHandler<SysoutFormValues> = async (data) => {
    setDownloading(true);
    setResults([]);

    const jobIds = data.jobId.split(',').map(s => s.trim()).filter(s => s);
    const promises = jobIds.map(async (jobId) => {
      try {
        const result = await requestSysout({
          ...data,
          jobId
        });
        // Assuming result is a URL or message
        return `Job ${jobId}: Success - ${result}`;
      } catch (e: unknown) {
        return `Job ${jobId}: Failed - ${(e as Error).message || 'Unknown error'}`;
      }
    });

    const responses = await Promise.all(promises);
    setResults(responses);
    setDownloading(false);
  };

  return (
    <Card className="w-full max-w-lg mx-auto mt-8">
      <CardHeader>
        <CardTitle>Sysout Download</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input
            label="EVT (evt04, evts05...)"
            {...register('evt', { required: 'EVT is required', maxLength: 6 })}
            error={errors.evt?.message}
          />
          <Input
            label="User"
            {...register('user', { required: 'User is required', maxLength: 8 })}
            error={errors.user?.message}
          />
          <Input
            label="Password"
            type="password"
            {...register('password', { maxLength: 8 })}
            error={errors.password?.message}
          />
          <Input
            label="Option (Z or C)"
            {...register('option', { required: 'Option is required', maxLength: 1 })}
            error={errors.option?.message}
          />
          <Input
            label="Job IDs (comma separated)"
            {...register('jobId', { required: 'Job ID is required' })}
            error={errors.jobId?.message}
          />

          <Button type="submit" disabled={downloading} className="w-full">
            {downloading ? 'Downloading...' : 'Request Download'}
          </Button>

          {results.length > 0 && (
             <div className="mt-4 p-4 bg-gray-50 rounded text-sm space-y-1">
                {results.map((res, i) => <div key={i}>{res}</div>)}
             </div>
          )}
        </form>
      </CardContent>
    </Card>
  );
};

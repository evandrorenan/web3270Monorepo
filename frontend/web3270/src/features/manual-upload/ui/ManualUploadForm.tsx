import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Button } from '../../../shared/ui/Button';
import { Card, CardContent, CardHeader, CardTitle } from '../../../shared/ui/Card';
import { uploadFile } from '../api/uploadApi';

interface UploadFormData {
  file: FileList;
}

export const ManualUploadForm: React.FC = () => {
  const { register, handleSubmit, formState: { errors }, reset } = useForm<UploadFormData>();
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (data: UploadFormData) => {
    if (!data.file || data.file.length === 0) return;

    setUploading(true);
    setMessage(null);
    setError(null);

    try {
      const file = data.file[0];
      await uploadFile(file);
      setMessage(`Successfully uploaded ${file.name}`);
      reset();
    } catch (err) {
      setError('Failed to upload file. Please try again.');
      console.error(err);
    } finally {
      setUploading(false);
    }
  };

  return (
    <Card className="w-full max-w-md mx-auto mt-8">
      <CardHeader>
        <CardTitle>Manual File Upload</CardTitle>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="flex flex-col gap-2">
            <label htmlFor="file-upload" className="text-sm font-medium text-gray-700">
              Select File
            </label>
            <input
              id="file-upload"
              type="file"
              {...register('file', { required: 'Please select a file' })}
              className="block w-full text-sm text-gray-500
                file:mr-4 file:py-2 file:px-4
                file:rounded-full file:border-0
                file:text-sm file:font-semibold
                file:bg-blue-50 file:text-blue-700
                hover:file:bg-blue-100"
              disabled={uploading}
            />
            {errors.file && <span className="text-red-500 text-sm">{errors.file.message}</span>}
          </div>

          <Button type="submit" disabled={uploading} className="w-full">
            {uploading ? 'Uploading...' : 'Upload'}
          </Button>

          {message && <div className="p-2 bg-green-100 text-green-700 rounded text-sm">{message}</div>}
          {error && <div className="p-2 bg-red-100 text-red-700 rounded text-sm">{error}</div>}
        </form>
      </CardContent>
    </Card>
  );
};

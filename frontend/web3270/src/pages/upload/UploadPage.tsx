import React from 'react';
import { ManualUploadForm } from '../../features/manual-upload/ui/ManualUploadForm';
import { SysoutDownloadForm } from '../../features/sysout-download/ui/SysoutDownloadForm';

const UploadPage: React.FC = () => {
  return (
    <div className="space-y-8">
      <div className="text-center">
        <h2 className="text-3xl font-bold tracking-tight text-gray-900">Upload & Download</h2>
        <p className="mt-2 text-sm text-gray-600">
          Upload files to the mainframe or download Sysout reports.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-start">
        <ManualUploadForm />
        <SysoutDownloadForm />
      </div>
    </div>
  );
};

export default UploadPage;

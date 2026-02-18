import React from 'react';
import { DataDivisionMap } from '../../entities/report/ui/DataDivisionMap';

const ReportsPage: React.FC = () => {
  // Placeholder data
  const data = [
    { name: 'ITEM-ID', type: 'PIC X(10)', length: 10, offset: 0 },
    { name: 'ITEM-DESC', type: 'PIC X(40)', length: 40, offset: 10 },
    { name: 'ITEM-PRICE', type: 'PIC 9(6)V99', length: 8, offset: 50 },
  ];

  return (
    <div className="space-y-8">
      <div className="text-center">
        <h2 className="text-3xl font-bold tracking-tight text-gray-900">System Reports</h2>
        <p className="mt-2 text-sm text-gray-600">
          View Program Reports and Data Division Maps.
        </p>
      </div>

      <div className="flex justify-center">
        <DataDivisionMap data={data} />
      </div>
    </div>
  );
};

export default ReportsPage;

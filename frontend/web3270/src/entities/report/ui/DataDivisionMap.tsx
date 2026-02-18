import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../../shared/ui/Card';

interface DataItem {
  name: string;
  type: string;
  length: number;
  offset: number;
}

interface DataDivisionMapProps {
  data: DataItem[];
}

export const DataDivisionMap: React.FC<DataDivisionMapProps> = ({ data }) => {
  if (!data || data.length === 0) {
    return <div className="text-gray-500">No data available.</div>;
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle>Data Division Map</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left text-gray-500">
            <thead className="text-xs text-gray-700 uppercase bg-gray-50">
              <tr>
                <th className="px-6 py-3">Name</th>
                <th className="px-6 py-3">Type</th>
                <th className="px-6 py-3">Length</th>
                <th className="px-6 py-3">Offset</th>
              </tr>
            </thead>
            <tbody>
              {data.map((item, index) => (
                <tr key={index} className="bg-white border-b hover:bg-gray-50">
                  <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap">
                    {item.name}
                  </td>
                  <td className="px-6 py-4">{item.type}</td>
                  <td className="px-6 py-4">{item.length}</td>
                  <td className="px-6 py-4">{item.offset}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
};

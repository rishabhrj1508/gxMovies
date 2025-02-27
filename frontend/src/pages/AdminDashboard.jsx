/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import Chart from "react-apexcharts";
import { getSummary, getChartData } from "../services/AdminService";

const AdminDashboard = () => {
  const [summaryData, setSummaryData] = useState({});
  const [selectedChart, setSelectedChart] = useState("moviesByGenre");
  const [chartData, setChartData] = useState({ options: {}, series: [] });
  const labelConfig = window.labelConfig;

  useEffect(() => {
    fetchSummary();
  }, []);

  useEffect(() => {
    fetchChartData(selectedChart);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedChart]);

  const fetchSummary = async () => {
    try {
      const data = await getSummary();
      setSummaryData(data);
    } catch (error) {
      console.error("Error fetching summary data", error);
    }
  };

  const fetchChartData = async (chartType) => {
    try {
      const response = await getChartData(chartType);
      console.log(response);

      // Prepare the data
      const categories = response.series.map((item) => item[0]);
      const values = response.series.map((item) => Number(item[1].toFixed(2)));

      setChartData({
        options: {
          chart: {
            type: "bar",
          },
          xaxis: {
            categories: categories,
          },
          title: {
            text: `${labelConfig.adminDashboard.charts[chartType]}`,
          },
          fill: {
            colors: labelConfig.adminDashboard.chartColors,
          },
        },
        series: [
          {
            name: chartType,
            data: values,
          },
        ],
      });
    } catch (error) {
      console.error("Error fetching chart data", error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6 space-y-6">
      {/* Top Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-4 shadow-lg rounded-lg text-center">
          <h3 className="text-xl font-semibold">
            {labelConfig.adminDashboard.summary.totalUsers}
          </h3>
          <p className="text-2xl font-bold">{summaryData.numberOfUsers}</p>
        </div>
        <div className="bg-white p-4 shadow-lg rounded-lg text-center">
          <h3 className="text-xl font-semibold">
            {labelConfig.adminDashboard.summary.totalMovies}
          </h3>
          <p className="text-2xl font-bold">{summaryData.numberOfMovies}</p>
        </div>
        <div className="bg-white p-4 shadow-lg rounded-lg text-center">
          <h3 className="text-xl font-semibold">
            {labelConfig.adminDashboard.summary.totalRevenue}
          </h3>
          <p className="text-2xl font-bold">
            Rs.{summaryData.totalRevenue?.toFixed(2)}
          </p>
        </div>
      </div>

      {/* Chart Dropdown Selector */}
      <div className="flex justify-center">
        <select
          className="p-2 border rounded"
          value={selectedChart}
          onChange={(e) => setSelectedChart(e.target.value)}
        >
          <option value="moviesByGenre">
            {labelConfig.adminDashboard.charts.moviesByGenre}
          </option>
          <option value="revenueByGenre">
            {labelConfig.adminDashboard.charts.revenueByGenre}
          </option>
          <option value="topUsers">
            {labelConfig.adminDashboard.charts.topUsers}
          </option>
        </select>
      </div>

      {/* Chart Display */}
      <div className="bg-white p-4 rounded-lg shadow-lg">
        <Chart
          options={chartData.options}
          series={chartData.series}
          type="bar"
          height={350}
        />
      </div>
    </div>
  );
};

export default AdminDashboard;

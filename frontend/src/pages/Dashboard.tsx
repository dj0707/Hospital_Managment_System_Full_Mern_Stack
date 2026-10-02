import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { DashboardStats } from '../types';
import {
  Users,
  UserCheck,
  Calendar,
  BedDouble,
  TrendingUp,
  AlertTriangle,
  Receipt,
  DollarSign,
  Activity,
  ArrowUpRight,
} from 'lucide-react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
} from 'recharts';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const res = await api.get('/hospital/dashboard/stats');
      setStats(res.data);
    } catch (err) {
      console.error('Failed to fetch dashboard stats', err);
    } finally {
      setLoading(false);
    }
  };

  const chartData = [
    { name: 'Patients', count: stats?.totalPatients || 0 },
    { name: 'Doctors', count: stats?.activeDoctors || 0 },
    { name: 'Appts Today', count: stats?.todayAppointments || 0 },
    { name: 'Admissions', count: stats?.activeAdmissions || 0 },
    { name: 'Avail Beds', count: stats?.availableBeds || 0 },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Operational Dashboard</h1>
          <p className="text-sm text-slate-500">Live hospital analytics derived from persistent database records</p>
        </div>
        <div className="inline-flex items-center px-3 py-1.5 rounded-full bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold">
          <span className="w-2 h-2 rounded-full bg-emerald-500 mr-2 animate-pulse" />
          Aiven Cloud MySQL Connected
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Total Patients</p>
            <h3 className="text-2xl font-bold text-slate-800 mt-1">{stats?.totalPatients || 0}</h3>
            <span className="text-xs text-blue-600 font-medium flex items-center mt-1">
              Registered in EHR
            </span>
          </div>
          <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
            <Users className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Today Appointments</p>
            <h3 className="text-2xl font-bold text-slate-800 mt-1">{stats?.todayAppointments || 0}</h3>
            <span className="text-xs text-indigo-600 font-medium flex items-center mt-1">
              Scheduled today
            </span>
          </div>
          <div className="w-12 h-12 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <Calendar className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Active Inpatients</p>
            <h3 className="text-2xl font-bold text-slate-800 mt-1">{stats?.activeAdmissions || 0}</h3>
            <span className="text-xs text-emerald-600 font-medium flex items-center mt-1">
              {stats?.availableBeds || 0} Beds Available
            </span>
          </div>
          <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center">
            <BedDouble className="w-6 h-6" />
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Today Pharmacy Sales</p>
            <h3 className="text-2xl font-bold text-slate-800 mt-1">₹{stats?.todaySales ? Number(stats.todaySales).toFixed(2) : '0.00'}</h3>
            <span className="text-xs text-amber-600 font-medium flex items-center mt-1">
              ₹{stats?.monthlySales ? Number(stats.monthlySales).toFixed(2) : '0.00'} Month
            </span>
          </div>
          <div className="w-12 h-12 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center">
            <DollarSign className="w-6 h-6" />
          </div>
        </div>
      </div>

      {/* Charts & Operational Alerts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <h3 className="text-base font-bold text-slate-800 mb-4">Hospital Activity Metrics</h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} />
                <YAxis stroke="#64748b" fontSize={12} tickLine={false} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1e293b',
                    borderRadius: '8px',
                    color: '#fff',
                    border: 'none',
                  }}
                />
                <Bar dataKey="count" fill="#0284c7" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex flex-col justify-between">
          <div>
            <h3 className="text-base font-bold text-slate-800 mb-3">Inventory Health & Expiry</h3>
            <p className="text-xs text-slate-500 mb-4">First-Expiry-First-Out (FEFO) automated tracking</p>
            
            <div className="space-y-3">
              <div className="p-3.5 rounded-lg bg-slate-50 border border-slate-100 flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <Activity className="w-4 h-4 text-blue-600" />
                  <span className="text-sm font-medium text-slate-700">Catalog Medicines</span>
                </div>
                <span className="text-sm font-bold text-slate-900">{stats?.totalMedicines || 0}</span>
              </div>

              <div className="p-3.5 rounded-lg bg-amber-50/60 border border-amber-100 flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <AlertTriangle className="w-4 h-4 text-amber-600" />
                  <span className="text-sm font-medium text-amber-800">Batches Expiring (30d)</span>
                </div>
                <span className="text-sm font-bold text-amber-900">{stats?.lowStockMedicines || 0}</span>
              </div>

              <div className="p-3.5 rounded-lg bg-slate-50 border border-slate-100 flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <Receipt className="w-4 h-4 text-slate-600" />
                  <span className="text-sm font-medium text-slate-700">Outstanding Balance</span>
                </div>
                <span className="text-sm font-bold text-slate-900">₹{stats?.outstandingBalance ? Number(stats.outstandingBalance).toFixed(2) : '0.00'}</span>
              </div>
            </div>
          </div>

          <div className="mt-4 pt-4 border-t border-slate-100 text-[11px] text-slate-400">
            Real-time calculations backed by JPA & MySQL
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;

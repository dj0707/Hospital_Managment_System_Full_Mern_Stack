import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  Calendar,
  Pill,
  ShoppingCart,
  Receipt,
  BedDouble,
  Bot,
  Activity,
  FileText,
} from 'lucide-react';

const Sidebar: React.FC = () => {
  const { hasAnyRole, hasRole } = useAuth();

  const navItems = [
    {
      to: '/dashboard',
      label: 'Dashboard',
      icon: LayoutDashboard,
      roles: ['ROLE_ADMIN'],
    },
    {
      to: '/patients',
      label: 'Patient Records (EHR)',
      icon: Users,
      roles: ['ROLE_ADMIN'], // Strictly Admin only for full management
    },
    {
      to: '/doctors',
      label: 'Doctors & Staff',
      icon: UserCheck,
      roles: ['ROLE_ADMIN'],
    },
    {
      to: '/appointments',
      label: 'Appointments',
      icon: Calendar,
      roles: ['ROLE_ADMIN', 'ROLE_DOCTOR', 'ROLE_RECEPTIONIST', 'ROLE_PATIENT'],
    },
    {
      to: '/pharmacy/pos',
      label: 'Pharmacy POS Billing',
      icon: ShoppingCart,
      roles: ['ROLE_ADMIN', 'ROLE_PHARMACIST', 'ROLE_RECEPTIONIST'],
      badge: 'Billing',
    },
    {
      to: '/pharmacy/medicines',
      label: 'Medicine Catalog',
      icon: Pill,
      roles: ['ROLE_ADMIN', 'ROLE_PHARMACIST'],
    },
    {
      to: '/invoices',
      label: 'Invoices & Reports',
      icon: Receipt,
      roles: ['ROLE_ADMIN', 'ROLE_PHARMACIST', 'ROLE_ACCOUNTANT'],
    },
    {
      to: '/inpatient',
      label: 'Wards & Beds',
      icon: BedDouble,
      roles: ['ROLE_ADMIN', 'ROLE_DOCTOR'],
    },
    {
      to: '/ai-assistant',
      label: 'AI Operations',
      icon: Bot,
      roles: ['ROLE_ADMIN', 'ROLE_DOCTOR', 'ROLE_PHARMACIST'],
      badge: 'AI',
    },
  ];

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col shrink-0 border-r border-slate-800">
      <div className="h-16 flex items-center px-6 border-b border-slate-800 bg-slate-950/60">
        <div className="flex items-center space-x-3">
          <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center text-white shadow-md shadow-blue-500/30">
            <Activity className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-base font-bold text-white tracking-wide">MediCore HMS</h1>
            <p className="text-[10px] text-blue-400 font-medium uppercase tracking-wider">Smart Hospital & POS</p>
          </div>
        </div>
      </div>

      <div className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
        {navItems.map((item) => {
          if (!hasAnyRole(item.roles)) return null;

          return (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `flex items-center justify-between px-3 py-2.5 rounded-lg text-xs font-semibold transition-all ${
                  isActive
                    ? 'bg-blue-600 text-white shadow-sm'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800'
                }`
              }
            >
              <div className="flex items-center space-x-3">
                <item.icon className="w-4 h-4" />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span className="text-[9px] uppercase font-bold px-1.5 py-0.5 rounded bg-blue-500/20 text-blue-300">
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}
      </div>

      <div className="p-3 border-t border-slate-800 bg-slate-950/40 text-[11px] text-slate-500 text-center">
        Role-Governed Access Control Active
      </div>
    </aside>
  );
};

export default Sidebar;

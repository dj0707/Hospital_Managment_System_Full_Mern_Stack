import React from 'react';
import { useAuth } from '../context/AuthContext';
import { User, LogOut, Bell, Shield } from 'lucide-react';

const Navbar: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-6 z-10">
      <div className="flex items-center space-x-3">
        <span className="text-sm font-medium text-slate-500 hidden sm:inline-block">
          Hospital Operations Portal
        </span>
      </div>

      <div className="flex items-center space-x-4">
        <div className="flex items-center space-x-3 border-r border-slate-200 pr-4">
          <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-semibold text-sm">
            {user?.fullName?.charAt(0) || 'U'}
          </div>
          <div className="text-left hidden md:block">
            <p className="text-sm font-semibold text-slate-800 leading-none">{user?.fullName}</p>
            <div className="flex items-center space-x-1 mt-1">
              <Shield className="w-3 h-3 text-blue-600" />
              <p className="text-xs text-slate-500 font-medium">{user?.roles[0]?.replace('ROLE_', '')}</p>
            </div>
          </div>
        </div>

        <button
          onClick={logout}
          className="flex items-center space-x-1 px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50 rounded-lg transition-colors"
          title="Sign Out"
        >
          <LogOut className="w-4 h-4" />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </header>
  );
};

export default Navbar;

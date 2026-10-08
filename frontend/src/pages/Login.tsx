import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import {
  Activity,
  Lock,
  User,
  AlertCircle,
  ShieldCheck,
  Stethoscope,
  UserCheck,
  Pill,
  CheckCircle,
  Eye,
  EyeOff,
  UserPlus,
  LogIn,
} from 'lucide-react';

const Login: React.FC = () => {
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  const [selectedPortal, setSelectedPortal] = useState<'ADMIN' | 'DOCTOR' | 'PATIENT' | 'PHARMACIST'>('ADMIN');

  // Login inputs
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // Registration inputs
  const [regFullName, setRegFullName] = useState('');
  const [regUsername, setRegUsername] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regPhone, setRegPhone] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regConfirmPassword, setRegConfirmPassword] = useState('');

  // UI state
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  // Password validation helper
  const validatePasswordStrength = (pwd: string) => {
    if (pwd.length < 6) return 'Password must be at least 6 characters long';
    return null;
  };

  const handlePortalSwitch = (portal: 'ADMIN' | 'DOCTOR' | 'PATIENT' | 'PHARMACIST') => {
    setSelectedPortal(portal);
    setError('');
    setSuccess('');
    if (portal === 'ADMIN') {
      setUsername('admin');
    } else if (portal === 'DOCTOR') {
      setUsername('doctor');
    } else if (portal === 'PHARMACIST') {
      setUsername('pharmacist');
    } else {
      setUsername('');
    }
    setPassword('');
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!username.trim() || !password) {
      setError('Please enter both username/email and password');
      return;
    }

    setLoading(true);
    try {
      const response = await api.post('/auth/login', { username: username.trim(), password });
      
      const userData = {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email,
        fullName: response.data.fullName,
        roles: response.data.roles,
      };

      // Server role check for the chosen portal window
      const role = userData.roles[0] || '';
      if (selectedPortal === 'ADMIN' && !role.includes('ADMIN')) {
        setError('Access denied: This portal window requires an Administrator account.');
        setLoading(false);
        return;
      }
      if (selectedPortal === 'DOCTOR' && !role.includes('DOCTOR') && !role.includes('ADMIN')) {
        setError('Access denied: This portal window is restricted to registered Medical Doctors.');
        setLoading(false);
        return;
      }
      if (selectedPortal === 'PHARMACIST' && !role.includes('PHARMACIST') && !role.includes('ADMIN')) {
        setError('Access denied: This portal window is restricted to authorized Pharmacists.');
        setLoading(false);
        return;
      }

      login(response.data.token, userData);

      if (role.includes('PHARMACIST')) {
        navigate('/pharmacy/pos');
      } else if (role.includes('DOCTOR')) {
        navigate('/appointments');
      } else {
        navigate('/dashboard');
      }
    } catch (err: any) {
      if (!err.response) {
        const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
        setError(`Backend Connection Failed: Unable to reach API at ${apiUrl}. Please ensure Spring Boot backend is running or VITE_API_BASE_URL is configured.`);
      } else {
        setError(err.response?.data?.message || 'Invalid username or password. Check credentials.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Field validations
    if (!regFullName.trim() || !regUsername.trim() || !regEmail.trim()) {
      setError('All profile fields are required.');
      return;
    }
    const pwdErr = validatePasswordStrength(regPassword);
    if (pwdErr) {
      setError(pwdErr);
      return;
    }
    if (regPassword !== regConfirmPassword) {
      setError('Password and Confirm Password do not match.');
      return;
    }

    setLoading(true);
    try {
      await api.post('/auth/register', {
        fullName: regFullName.trim(),
        username: regUsername.trim(),
        email: regEmail.trim(),
        phone: regPhone.trim(),
        password: regPassword,
      });

      setSuccess('Account created successfully! Please sign in with your credentials.');
      setIsRegisterMode(false);
      setSelectedPortal('PATIENT');
      setUsername(regUsername);
      setPassword('');
    } catch (err: any) {
      if (!err.response) {
        const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
        setError(`Backend Connection Failed: Unable to reach API at ${apiUrl}. Please ensure Spring Boot backend is running.`);
      } else {
        setError(err.response?.data?.message || 'Registration failed. Username or email may already exist.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center py-8 sm:px-6 lg:px-8 relative overflow-hidden font-sans">
      {/* Ambient background glows */}
      <div className="absolute top-[-15%] left-[-10%] w-[50%] h-[50%] bg-blue-600/15 blur-[140px] rounded-full pointer-events-none" />
      <div className="absolute bottom-[-15%] right-[-10%] w-[50%] h-[50%] bg-cyan-600/15 blur-[140px] rounded-full pointer-events-none" />

      {/* Header */}
      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10 text-center">
        <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-blue-600 text-white shadow-xl shadow-blue-500/25 mb-3">
          <Activity className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-black tracking-tight text-white">MediCore HMS</h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Role-Enforced Hospital Administration & Pharmacy POS System
        </p>
      </div>

      <div className="mt-6 sm:mx-auto sm:w-full sm:max-w-md relative z-10">
        <div className="bg-slate-900/90 backdrop-blur-xl border border-slate-800 py-6 px-4 shadow-2xl sm:rounded-2xl sm:px-8">
          
          {/* Portal Switcher Tabs */}
          {!isRegisterMode && (
            <div className="mb-6">
              <label className="block text-[10px] font-bold text-slate-400 uppercase tracking-wider mb-2 text-center">
                Select Dedicated Portal Window
              </label>
              <div className="grid grid-cols-4 gap-1.5 p-1 bg-slate-950 rounded-xl border border-slate-800">
                <button
                  type="button"
                  onClick={() => handlePortalSwitch('ADMIN')}
                  className={`py-2 px-1 rounded-lg text-xs font-bold flex flex-col items-center justify-center space-y-1 transition-all ${
                    selectedPortal === 'ADMIN'
                      ? 'bg-blue-600 text-white shadow-md'
                      : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                  }`}
                >
                  <ShieldCheck className="w-4 h-4" />
                  <span className="text-[10px]">Admin</span>
                </button>

                <button
                  type="button"
                  onClick={() => handlePortalSwitch('DOCTOR')}
                  className={`py-2 px-1 rounded-lg text-xs font-bold flex flex-col items-center justify-center space-y-1 transition-all ${
                    selectedPortal === 'DOCTOR'
                      ? 'bg-blue-600 text-white shadow-md'
                      : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                  }`}
                >
                  <Stethoscope className="w-4 h-4" />
                  <span className="text-[10px]">Doctor</span>
                </button>

                <button
                  type="button"
                  onClick={() => handlePortalSwitch('PHARMACIST')}
                  className={`py-2 px-1 rounded-lg text-xs font-bold flex flex-col items-center justify-center space-y-1 transition-all ${
                    selectedPortal === 'PHARMACIST'
                      ? 'bg-blue-600 text-white shadow-md'
                      : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                  }`}
                >
                  <Pill className="w-4 h-4" />
                  <span className="text-[10px]">Pharmacy</span>
                </button>

                <button
                  type="button"
                  onClick={() => handlePortalSwitch('PATIENT')}
                  className={`py-2 px-1 rounded-lg text-xs font-bold flex flex-col items-center justify-center space-y-1 transition-all ${
                    selectedPortal === 'PATIENT'
                      ? 'bg-blue-600 text-white shadow-md'
                      : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                  }`}
                >
                  <UserCheck className="w-4 h-4" />
                  <span className="text-[10px]">Patient</span>
                </button>
              </div>
            </div>
          )}

          {/* Feedback messages */}
          {error && (
            <div className="mb-4 bg-red-500/10 border border-red-500/30 text-red-400 px-3.5 py-2.5 rounded-xl text-xs flex items-center space-x-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {success && (
            <div className="mb-4 bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 px-3.5 py-2.5 rounded-xl text-xs flex items-center space-x-2">
              <CheckCircle className="w-4 h-4 shrink-0" />
              <span>{success}</span>
            </div>
          )}

          {/* Login Form */}
          {!isRegisterMode ? (
            <form className="space-y-4" onSubmit={handleLogin}>
              <div>
                <label className="block text-xs font-bold text-slate-300 mb-1.5">
                  {selectedPortal === 'ADMIN' && 'Administrator Username / Email'}
                  {selectedPortal === 'DOCTOR' && 'Doctor Portal Username / ID'}
                  {selectedPortal === 'PHARMACIST' && 'Pharmacist Staff Username'}
                  {selectedPortal === 'PATIENT' && 'Patient Registered Username / Email'}
                </label>
                <div className="relative rounded-lg shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                    <User className="h-4 w-4" />
                  </div>
                  <input
                    type="text"
                    required
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    className="block w-full pl-10 pr-3 py-2.5 bg-slate-950 border border-slate-700 rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 text-xs font-medium"
                    placeholder="Enter your username"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-300 mb-1.5">
                  Password
                </label>
                <div className="relative rounded-lg shadow-sm">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                    <Lock className="h-4 w-4" />
                  </div>
                  <input
                    type={showPassword ? 'text' : 'password'}
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="block w-full pl-10 pr-10 py-2.5 bg-slate-950 border border-slate-700 rounded-lg text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500 text-xs font-medium"
                    placeholder="Enter password"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-200"
                  >
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full mt-2 py-2.5 px-4 bg-blue-600 hover:bg-blue-500 disabled:bg-slate-700 text-white rounded-lg text-xs font-bold shadow-lg shadow-blue-500/25 transition-all flex items-center justify-center space-x-2"
              >
                <LogIn className="w-4 h-4" />
                <span>{loading ? 'Verifying & Authenticating...' : `Sign in to ${selectedPortal} Portal`}</span>
              </button>

              <div className="pt-4 border-t border-slate-800 text-center">
                <button
                  type="button"
                  onClick={() => {
                    setIsRegisterMode(true);
                    setError('');
                    setSuccess('');
                  }}
                  className="text-xs font-semibold text-blue-400 hover:text-blue-300 inline-flex items-center space-x-1"
                >
                  <UserPlus className="w-3.5 h-3.5" />
                  <span>New Patient? Register your account here</span>
                </button>
              </div>
            </form>
          ) : (
            /* Patient Registration Form */
            <form className="space-y-3" onSubmit={handleRegister}>
              <div className="border-b border-slate-800 pb-2 mb-2">
                <h3 className="text-sm font-bold text-white">Create New Patient Account</h3>
                <p className="text-[11px] text-slate-400">Register to view appointments, prescriptions, and medical history</p>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-300 mb-1">Full Legal Name</label>
                <input
                  type="text"
                  required
                  value={regFullName}
                  onChange={(e) => setRegFullName(e.target.value)}
                  className="block w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-lg text-white text-xs"
                  placeholder="e.g. Siddharth Verma"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-[11px] font-bold text-slate-300 mb-1">Username</label>
                  <input
                    type="text"
                    required
                    value={regUsername}
                    onChange={(e) => setRegUsername(e.target.value)}
                    className="block w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-lg text-white text-xs"
                    placeholder="siddharth26"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-bold text-slate-300 mb-1">Phone Number</label>
                  <input
                    type="tel"
                    required
                    value={regPhone}
                    onChange={(e) => setRegPhone(e.target.value)}
                    className="block w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-lg text-white text-xs"
                    placeholder="9876543210"
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-300 mb-1">Email Address</label>
                <input
                  type="email"
                  required
                  value={regEmail}
                  onChange={(e) => setRegEmail(e.target.value)}
                  className="block w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-lg text-white text-xs"
                  placeholder="patient@example.com"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-[11px] font-bold text-slate-300 mb-1">Password</label>
                  <input
                    type="password"
                    required
                    value={regPassword}
                    onChange={(e) => setRegPassword(e.target.value)}
                    className="block w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-lg text-white text-xs"
                    placeholder="Min 6 chars"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-bold text-slate-300 mb-1">Confirm Password</label>
                  <input
                    type="password"
                    required
                    value={regConfirmPassword}
                    onChange={(e) => setRegConfirmPassword(e.target.value)}
                    className="block w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-lg text-white text-xs"
                    placeholder="Re-enter password"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full mt-2 py-2.5 px-4 bg-emerald-600 hover:bg-emerald-500 disabled:bg-slate-700 text-white rounded-lg text-xs font-bold shadow-lg shadow-emerald-500/25 transition-all flex items-center justify-center space-x-2"
              >
                <UserPlus className="w-4 h-4" />
                <span>{loading ? 'Creating Patient Account...' : 'Complete Patient Registration'}</span>
              </button>

              <div className="pt-3 border-t border-slate-800 text-center">
                <button
                  type="button"
                  onClick={() => {
                    setIsRegisterMode(false);
                    setError('');
                  }}
                  className="text-xs font-semibold text-slate-400 hover:text-white"
                >
                  Already registered? Back to Portal Login
                </button>
              </div>
            </form>
          )}

        </div>
      </div>
    </div>
  );
};

export default Login;

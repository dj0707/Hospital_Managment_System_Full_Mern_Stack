import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Patients from './pages/Patients';
import Doctors from './pages/Doctors';
import Appointments from './pages/Appointments';
import PharmacyPOS from './pages/PharmacyPOS';
import MedicineCatalog from './pages/MedicineCatalog';
import Invoices from './pages/Invoices';
import Inpatient from './pages/Inpatient';
import AiAssistant from './pages/AiAssistant';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route element={<Layout />}>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/patients" element={<Patients />} />
            <Route path="/doctors" element={<Doctors />} />
            <Route path="/appointments" element={<Appointments />} />
            <Route path="/pharmacy/pos" element={<PharmacyPOS />} />
            <Route path="/pharmacy/medicines" element={<MedicineCatalog />} />
            <Route path="/invoices" element={<Invoices />} />
            <Route path="/inpatient" element={<Inpatient />} />
            <Route path="/ai-assistant" element={<AiAssistant />} />
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;

import React, { useState, useEffect } from 'react';
import api from '../api/client';
import { Ward, Bed, Admission, Patient, Doctor } from '../types';
import { BedDouble, Plus, CheckCircle, AlertCircle, UserCheck } from 'lucide-react';

const Inpatient: React.FC = () => {
  const [wards, setWards] = useState<Ward[]>([]);
  const [admissions, setAdmissions] = useState<Admission[]>([]);
  const [availableBeds, setAvailableBeds] = useState<Bed[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAdmitModal, setShowAdmitModal] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const [formData, setFormData] = useState({
    patientId: '',
    bedId: '',
    doctorId: '',
    diagnosis: '',
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [wardRes, admRes, bedRes, patRes, docRes] = await Promise.all([
        api.get('/hospital/wards'),
        api.get('/hospital/admissions/active'),
        api.get('/hospital/beds/available'),
        api.get('/clinic/patients/search?size=50'),
        api.get('/clinic/doctors/search?size=50'),
      ]);
      setWards(wardRes.data || []);
      setAdmissions(admRes.data || []);
      setAvailableBeds(bedRes.data || []);
      setPatients(patRes.data.content || []);
      setDoctors(docRes.data.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAdmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');
    try {
      await api.post('/hospital/admissions', {
        patientId: Number(formData.patientId),
        bedId: Number(formData.bedId),
        doctorId: Number(formData.doctorId),
        diagnosis: formData.diagnosis,
      });
      setSuccessMsg('Patient admitted to bed successfully!');
      setShowAdmitModal(false);
      fetchData();
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Failed to admit patient');
    }
  };

  const handleDischarge = async (id: number) => {
    try {
      const res = await api.post(`/hospital/admissions/${id}/discharge`);
      setSuccessMsg(`Patient discharged. Calculated Bed Charge: ₹${res.data.totalBedCharge}`);
      fetchData();
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Failed to discharge patient');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Wards, Beds & Inpatient Admissions</h1>
          <p className="text-sm text-slate-500">Inpatient stay tracking, daily ward rates, and discharge billing</p>
        </div>
        <button
          onClick={() => setShowAdmitModal(true)}
          className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-semibold flex items-center space-x-2 transition-colors shadow-sm"
        >
          <Plus className="w-4 h-4" />
          <span>Admit Patient</span>
        </button>
      </div>

      {successMsg && (
        <div className="bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 rounded-xl text-sm flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <CheckCircle className="w-5 h-5 shrink-0" />
            <span>{successMsg}</span>
          </div>
          <button onClick={() => setSuccessMsg('')} className="text-xs font-bold">Dismiss</button>
        </div>
      )}

      {errorMsg && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <AlertCircle className="w-5 h-5 shrink-0" />
            <span>{errorMsg}</span>
          </div>
          <button onClick={() => setErrorMsg('')} className="text-xs font-bold">Dismiss</button>
        </div>
      )}

      {/* Active Admissions Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-200 flex items-center justify-between">
          <h3 className="text-base font-bold text-slate-800">Currently Admitted Inpatients</h3>
          <span className="text-xs font-bold text-emerald-600">{availableBeds.length} Beds Available</span>
        </div>

        {loading ? (
          <div className="p-8 text-center text-slate-400">Loading inpatient records...</div>
        ) : admissions.length === 0 ? (
          <div className="p-12 text-center text-slate-400 space-y-2">
            <BedDouble className="w-10 h-10 text-slate-300 mx-auto" />
            <p className="text-sm font-semibold">No active inpatient admissions</p>
            <p className="text-xs">Click "Admit Patient" to assign beds.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200 text-xs text-left">
              <thead className="bg-slate-50 text-slate-700 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">Code</th>
                  <th className="py-3 px-4">Patient Name</th>
                  <th className="py-3 px-4">Ward & Bed #</th>
                  <th className="py-3 px-4">Attending Doctor</th>
                  <th className="py-3 px-4">Admission Date</th>
                  <th className="py-3 px-4">Diagnosis</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {admissions.map((adm) => (
                  <tr key={adm.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3 px-4 font-bold text-blue-600">{adm.admissionCode}</td>
                    <td className="py-3 px-4 font-semibold text-slate-900">{adm.patientName}</td>
                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 rounded bg-indigo-50 text-indigo-700 font-bold">
                        {adm.wardName} — {adm.bedNumber}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-slate-700">{adm.doctorName}</td>
                    <td className="py-3 px-4 text-slate-600">
                      {new Date(adm.admissionDate!).toLocaleDateString()}
                    </td>
                    <td className="py-3 px-4 text-slate-500">{adm.diagnosis || 'Under evaluation'}</td>
                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => handleDischarge(adm.id!)}
                        className="px-2.5 py-1 bg-amber-50 text-amber-700 hover:bg-amber-100 rounded font-bold text-[11px]"
                      >
                        Discharge & Bill
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Admit Patient Modal */}
      {showAdmitModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <h3 className="text-base font-bold text-slate-900 border-b border-slate-200 pb-3">
              Inpatient Bed Admission
            </h3>
            <form onSubmit={handleAdmit} className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Select Patient</label>
                <select
                  required
                  value={formData.patientId}
                  onChange={(e) => setFormData({ ...formData, patientId: e.target.value })}
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg bg-white"
                >
                  <option value="">Select Patient</option>
                  {patients.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.firstName} {p.lastName} ({p.patientCode})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Select Available Bed</label>
                <select
                  required
                  value={formData.bedId}
                  onChange={(e) => setFormData({ ...formData, bedId: e.target.value })}
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg bg-white"
                >
                  <option value="">Select Bed</option>
                  {availableBeds.map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.wardName} - Bed {b.bedNumber} (₹{b.dailyRate}/day)
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Attending Doctor</label>
                <select
                  required
                  value={formData.doctorId}
                  onChange={(e) => setFormData({ ...formData, doctorId: e.target.value })}
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg bg-white"
                >
                  <option value="">Select Doctor</option>
                  {doctors.map((d) => (
                    <option key={d.id} value={d.id}>
                      {d.fullName} ({d.specialization})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Admission Diagnosis / Reason</label>
                <textarea
                  rows={2}
                  value={formData.diagnosis}
                  onChange={(e) => setFormData({ ...formData, diagnosis: e.target.value })}
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                  placeholder="Post-op recovery, acute fever observation..."
                />
              </div>

              <div className="flex justify-end space-x-3 pt-4 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setShowAdmitModal(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-semibold"
                >
                  Confirm Admission
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Inpatient;

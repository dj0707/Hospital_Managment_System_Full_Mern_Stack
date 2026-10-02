import React, { useState, useEffect } from 'react';
import api from '../api/client';
import { Medicine, MedicineCategory, Supplier, MedicineBatch } from '../types';
import { Pill, Plus, Search, Layers, Calendar, PackagePlus, AlertCircle, CheckCircle } from 'lucide-react';

const MedicineCatalog: React.FC = () => {
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [categories, setCategories] = useState<MedicineCategory[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  // Modals
  const [showAddMed, setShowAddMed] = useState(false);
  const [showAddBatch, setShowAddBatch] = useState(false);
  const [selectedMedForBatch, setSelectedMedForBatch] = useState<Medicine | null>(null);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  // New Medicine Form State
  const [newMed, setNewMed] = useState({
    sku: '',
    brandName: '',
    genericName: '',
    categoryId: '',
    dosageForm: 'Tablet',
    strength: '',
    manufacturer: '',
    unitsPerStrip: 10,
    baseUnit: 'Tablet',
    costPerStrip: '',
    pricePerStrip: '',
    taxPercent: '5.0',
    reorderLevelStrips: 10,
  });

  // New Batch Form State
  const [newBatch, setNewBatch] = useState({
    batchNumber: '',
    expiryDate: '',
    quantityInStrips: '',
    purchaseCostPerStrip: '',
    mrpPerStrip: '',
    supplierId: '',
  });

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    setLoading(true);
    try {
      const [medsRes, catsRes, supsRes] = await Promise.all([
        api.get('/pharmacy/medicines?size=50'),
        api.get('/pharmacy/categories'),
        api.get('/pharmacy/suppliers'),
      ]);
      setMedicines(medsRes.data.content || []);
      setCategories(catsRes.data || []);
      setSuppliers(supsRes.data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateMedicine = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');
    try {
      await api.post('/pharmacy/medicines', {
        ...newMed,
        categoryId: Number(newMed.categoryId),
        unitsPerStrip: Number(newMed.unitsPerStrip),
        costPerStrip: Number(newMed.costPerStrip),
        pricePerStrip: Number(newMed.pricePerStrip),
        taxPercent: Number(newMed.taxPercent),
      });
      setSuccessMsg('Medicine registered successfully in catalog!');
      setShowAddMed(false);
      fetchInitialData();
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Failed to create medicine');
    }
  };

  const handleAddBatch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedMedForBatch) return;
    setErrorMsg('');
    try {
      await api.post('/pharmacy/batches', {
        medicineId: selectedMedForBatch.id,
        batchNumber: newBatch.batchNumber,
        expiryDate: newBatch.expiryDate,
        quantityInStrips: Number(newBatch.quantityInStrips),
        purchaseCostPerStrip: newBatch.purchaseCostPerStrip ? Number(newBatch.purchaseCostPerStrip) : undefined,
        mrpPerStrip: newBatch.mrpPerStrip ? Number(newBatch.mrpPerStrip) : undefined,
        supplierId: newBatch.supplierId ? Number(newBatch.supplierId) : undefined,
      });
      setSuccessMsg('Stock batch successfully inwarded!');
      setShowAddBatch(false);
      fetchInitialData();
    } catch (err: any) {
      setErrorMsg(err.response?.data?.message || 'Failed to add batch');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Medicine Catalog & Inventory</h1>
          <p className="text-sm text-slate-500">
            Catalog specifications, strip configurations, and batch-level stock management
          </p>
        </div>
        <div className="flex space-x-3">
          <button
            onClick={() => setShowAddMed(true)}
            className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-semibold flex items-center space-x-2 transition-colors shadow-sm"
          >
            <Plus className="w-4 h-4" />
            <span>Add Medicine</span>
          </button>
        </div>
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

      {/* Catalog Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-200 flex items-center justify-between">
          <h3 className="text-base font-bold text-slate-800">Catalog Inventory List</h3>
          <span className="text-xs text-slate-500">{medicines.length} Medicines Registered</span>
        </div>

        {loading ? (
          <div className="p-8 text-center text-slate-400">Loading catalog...</div>
        ) : medicines.length === 0 ? (
          <div className="p-12 text-center text-slate-400 space-y-2">
            <Pill className="w-10 h-10 text-slate-300 mx-auto" />
            <p className="text-sm font-semibold">No medicines in the database</p>
            <p className="text-xs">Click "Add Medicine" to create the first catalog entry.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200 text-xs text-left">
              <thead className="bg-slate-50 text-slate-700 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">SKU / Code</th>
                  <th className="py-3 px-4">Brand & Generic Name</th>
                  <th className="py-3 px-4">Category</th>
                  <th className="py-3 px-4">Dosage & Strength</th>
                  <th className="py-3 px-4">Pack Size</th>
                  <th className="py-3 px-4 text-right">Price / Strip</th>
                  <th className="py-3 px-4 text-center">Available Stock</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {medicines.map((med) => (
                  <tr key={med.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3 px-4 font-semibold text-blue-600">{med.sku}</td>
                    <td className="py-3 px-4">
                      <p className="font-bold text-slate-900">{med.brandName}</p>
                      <p className="text-slate-500">{med.genericName}</p>
                    </td>
                    <td className="py-3 px-4">{med.categoryName || 'General'}</td>
                    <td className="py-3 px-4">
                      <span className="font-medium text-slate-800">{med.strength}</span> • {med.dosageForm}
                    </td>
                    <td className="py-3 px-4">
                      {med.unitsPerStrip} {med.baseUnit}s/strip
                    </td>
                    <td className="py-3 px-4 text-right font-bold text-slate-900">
                      ₹{Number(med.pricePerStrip).toFixed(2)}
                    </td>
                    <td className="py-3 px-4 text-center">
                      <span
                        className={`inline-flex items-center px-2 py-0.5 rounded-full font-bold ${
                          (med.availableStockStrips || 0) > 5
                            ? 'bg-emerald-100 text-emerald-800'
                            : (med.availableStockStrips || 0) > 0
                            ? 'bg-amber-100 text-amber-800'
                            : 'bg-red-100 text-red-800'
                        }`}
                      >
                        {med.availableStockStrips || 0} strips ({med.availableBaseUnits || 0} {med.baseUnit}s)
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => {
                          setSelectedMedForBatch(med);
                          setShowAddBatch(true);
                        }}
                        className="px-2.5 py-1.5 bg-slate-100 hover:bg-blue-50 text-blue-600 hover:text-blue-700 rounded font-semibold text-xs transition-colors"
                      >
                        + Inward Batch
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Add Medicine Modal */}
      {showAddMed && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-xl w-full p-6 shadow-2xl space-y-4 max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-bold text-slate-900 border-b border-slate-200 pb-3">
              Add New Medicine to Catalog
            </h3>
            <form onSubmit={handleCreateMedicine} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">SKU / Barcode</label>
                  <input
                    type="text"
                    required
                    value={newMed.sku}
                    onChange={(e) => setNewMed({ ...newMed, sku: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="MED-PARA-650"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Brand Name</label>
                  <input
                    type="text"
                    required
                    value={newMed.brandName}
                    onChange={(e) => setNewMed({ ...newMed, brandName: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="Dolo 650"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Generic Name</label>
                  <input
                    type="text"
                    required
                    value={newMed.genericName}
                    onChange={(e) => setNewMed({ ...newMed, genericName: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="Paracetamol"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Category</label>
                  <select
                    required
                    value={newMed.categoryId}
                    onChange={(e) => setNewMed({ ...newMed, categoryId: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg bg-white"
                  >
                    <option value="">Select Category</option>
                    {categories.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Dosage Form</label>
                  <select
                    value={newMed.dosageForm}
                    onChange={(e) => setNewMed({ ...newMed, dosageForm: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg bg-white"
                  >
                    <option value="Tablet">Tablet</option>
                    <option value="Capsule">Capsule</option>
                    <option value="Syrup">Syrup</option>
                    <option value="Injection">Injection</option>
                    <option value="Ointment">Ointment</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Strength</label>
                  <input
                    type="text"
                    required
                    value={newMed.strength}
                    onChange={(e) => setNewMed({ ...newMed, strength: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="650mg"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Units / Strip</label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={newMed.unitsPerStrip}
                    onChange={(e) => setNewMed({ ...newMed, unitsPerStrip: parseInt(e.target.value) || 10 })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Manufacturer</label>
                  <input
                    type="text"
                    required
                    value={newMed.manufacturer}
                    onChange={(e) => setNewMed({ ...newMed, manufacturer: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="Micro Labs"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Cost / Strip (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={newMed.costPerStrip}
                    onChange={(e) => setNewMed({ ...newMed, costPerStrip: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="20.00"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Price / Strip (₹)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={newMed.pricePerStrip}
                    onChange={(e) => setNewMed({ ...newMed, pricePerStrip: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="35.00"
                  />
                </div>
              </div>

              <div className="flex justify-end space-x-3 pt-4 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setShowAddMed(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-xs font-semibold"
                >
                  Save Medicine
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Inward Batch Modal */}
      {showAddBatch && selectedMedForBatch && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4">
            <h3 className="text-base font-bold text-slate-900 border-b border-slate-200 pb-3">
              Inward Batch Stock: {selectedMedForBatch.brandName}
            </h3>
            <form onSubmit={handleAddBatch} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Batch Number</label>
                <input
                  type="text"
                  required
                  value={newBatch.batchNumber}
                  onChange={(e) => setNewBatch({ ...newBatch, batchNumber: e.target.value })}
                  className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                  placeholder="BATCH-2026-X1"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Expiry Date</label>
                  <input
                    type="date"
                    required
                    value={newBatch.expiryDate}
                    onChange={(e) => setNewBatch({ ...newBatch, expiryDate: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Quantity in Strips</label>
                  <input
                    type="number"
                    min="1"
                    required
                    value={newBatch.quantityInStrips}
                    onChange={(e) => setNewBatch({ ...newBatch, quantityInStrips: e.target.value })}
                    className="w-full px-3 py-2 text-xs border border-slate-300 rounded-lg"
                    placeholder="50"
                  />
                </div>
              </div>

              <div className="flex justify-end space-x-3 pt-4 border-t border-slate-200">
                <button
                  type="button"
                  onClick={() => setShowAddBatch(false)}
                  className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-semibold"
                >
                  Confirm Inward
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default MedicineCatalog;

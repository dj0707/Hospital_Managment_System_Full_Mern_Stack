import React, { useState, useEffect } from 'react';
import api from '../api/client';
import { Invoice } from '../types';
import { Receipt, Search, Eye, Download, Printer, DollarSign } from 'lucide-react';

const Invoices: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);

  useEffect(() => {
    fetchInvoices();
  }, []);

  const fetchInvoices = async (query = '') => {
    setLoading(true);
    try {
      const res = await api.get(`/pharmacy/invoices?query=${encodeURIComponent(query)}&size=30`);
      setInvoices(res.data.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchInvoices(searchQuery);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Invoices & Billing Records</h1>
          <p className="text-sm text-slate-500">Immutable financial invoices, line item snapshots, and payment history</p>
        </div>
      </div>

      {/* Search Filter */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
        <form onSubmit={handleSearch} className="flex-1 max-w-md relative">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
            <Search className="h-4 w-4" />
          </div>
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by invoice #, customer name, or phone..."
            className="w-full pl-9 pr-4 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </form>
        <span className="text-xs font-semibold text-slate-500">{invoices.length} Invoices</span>
      </div>

      {/* Invoices Table */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-slate-400">Loading billing records...</div>
        ) : invoices.length === 0 ? (
          <div className="p-12 text-center text-slate-400 space-y-2">
            <Receipt className="w-10 h-10 text-slate-300 mx-auto" />
            <p className="text-sm font-semibold">No invoices found</p>
            <p className="text-xs">Complete sales in the Pharmacy POS to record invoices.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200 text-xs text-left">
              <thead className="bg-slate-50 text-slate-700 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">Invoice #</th>
                  <th className="py-3 px-4">Type</th>
                  <th className="py-3 px-4">Customer</th>
                  <th className="py-3 px-4">Date & Time</th>
                  <th className="py-3 px-4 text-right">Subtotal</th>
                  <th className="py-3 px-4 text-right">Tax (GST)</th>
                  <th className="py-3 px-4 text-right">Total Amount</th>
                  <th className="py-3 px-4 text-center">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {invoices.map((inv) => (
                  <tr key={inv.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3 px-4 font-bold text-blue-600">{inv.invoiceNumber}</td>
                    <td className="py-3 px-4">
                      <span className="px-2 py-0.5 rounded bg-blue-50 text-blue-700 font-semibold text-[10px]">
                        {inv.invoiceType}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      <p className="font-semibold text-slate-900">{inv.customerName}</p>
                      <p className="text-[11px] text-slate-500">{inv.customerPhone || '—'}</p>
                    </td>
                    <td className="py-3 px-4 text-slate-600">
                      {new Date(inv.createdAt).toLocaleString()}
                    </td>
                    <td className="py-3 px-4 text-right">₹{Number(inv.subtotal).toFixed(2)}</td>
                    <td className="py-3 px-4 text-right">₹{Number(inv.taxAmount).toFixed(2)}</td>
                    <td className="py-3 px-4 text-right font-bold text-slate-900">
                      ₹{Number(inv.totalAmount).toFixed(2)}
                    </td>
                    <td className="py-3 px-4 text-center">
                      <span
                        className={`px-2 py-0.5 rounded-full font-bold text-[10px] ${
                          inv.paymentStatus === 'PAID'
                            ? 'bg-emerald-100 text-emerald-800'
                            : 'bg-amber-100 text-amber-800'
                        }`}
                      >
                        {inv.paymentStatus}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <button
                        onClick={() => setSelectedInvoice(inv)}
                        className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded font-semibold text-[11px] flex items-center space-x-1 ml-auto"
                      >
                        <Eye className="w-3 h-3" />
                        <span>View</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Invoice Detail Modal */}
      {selectedInvoice && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-slate-200 pb-3">
              <div>
                <h3 className="text-lg font-bold text-slate-900">MediCore Hospital Invoice</h3>
                <p className="text-xs text-slate-500">Invoice: {selectedInvoice.invoiceNumber}</p>
              </div>
              <button
                onClick={() => setSelectedInvoice(null)}
                className="text-slate-400 hover:text-slate-600 text-sm font-bold"
              >
                ✕
              </button>
            </div>

            <div className="grid grid-cols-2 text-xs text-slate-600 gap-2 bg-slate-50 p-3 rounded-lg">
              <div><span className="font-semibold text-slate-800">Customer: </span>{selectedInvoice.customerName}</div>
              <div><span className="font-semibold text-slate-800">Cashier: </span>{selectedInvoice.cashierName}</div>
              <div><span className="font-semibold text-slate-800">Date: </span>{new Date(selectedInvoice.createdAt).toLocaleString()}</div>
              <div><span className="font-semibold text-slate-800">Status: </span>{selectedInvoice.paymentStatus}</div>
            </div>

            <div className="border border-slate-200 rounded-lg overflow-hidden">
              <table className="min-w-full text-xs text-left">
                <thead className="bg-slate-100 text-slate-700 font-semibold">
                  <tr>
                    <th className="py-2 px-3">Item</th>
                    <th className="py-2 px-3 text-center">Strips</th>
                    <th className="py-2 px-3 text-right">Price</th>
                    <th className="py-2 px-3 text-right">Total</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-200">
                  {selectedInvoice.items?.map((i) => (
                    <tr key={i.id}>
                      <td className="py-2 px-3">
                        <p className="font-medium text-slate-900">{i.itemDescription}</p>
                        <p className="text-[10px] text-slate-500">
                          {i.totalBaseUnits} units • Batch: {i.batchNumbers?.join(', ')}
                        </p>
                      </td>
                      <td className="py-2 px-3 text-center">{i.quantityStrips}</td>
                      <td className="py-2 px-3 text-right">₹{Number(i.unitPricePerStrip).toFixed(2)}</td>
                      <td className="py-2 px-3 text-right font-semibold">₹{Number(i.lineTotal).toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div className="space-y-1.5 text-xs text-slate-600 text-right pt-2 border-t border-slate-200">
              <div>Subtotal: ₹{Number(selectedInvoice.subtotal).toFixed(2)}</div>
              <div>Tax (GST): ₹{Number(selectedInvoice.taxAmount).toFixed(2)}</div>
              <div className="text-base font-bold text-slate-900">
                Total Amount: ₹{Number(selectedInvoice.totalAmount).toFixed(2)}
              </div>
            </div>

            <div className="flex space-x-3 pt-4 border-t border-slate-200">
              <button
                onClick={() => window.print()}
                className="flex-1 py-2 px-4 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-bold flex items-center justify-center space-x-2"
              >
                <Printer className="w-3.5 h-3.5" />
                <span>Print Bill</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Invoices;

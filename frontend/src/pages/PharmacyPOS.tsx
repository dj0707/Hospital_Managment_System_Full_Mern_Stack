import React, { useState, useEffect } from 'react';
import api from '../api/client';
import { Medicine, CartItem, Invoice } from '../types';
import {
  Search,
  Plus,
  Minus,
  Trash2,
  ShoppingCart,
  Receipt,
  User,
  Phone,
  CreditCard,
  CheckCircle,
  AlertCircle,
  Printer,
  Sparkles,
} from 'lucide-react';

const PharmacyPOS: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Medicine[]>([]);
  const [searching, setSearching] = useState(false);
  const [cart, setCart] = useState<CartItem[]>([]);

  // Checkout inputs
  const [customerName, setCustomerName] = useState('Walk-in Customer');
  const [customerPhone, setCustomerPhone] = useState('');
  const [paymentMethod, setPaymentMethod] = useState('CASH');
  const [invoiceDiscount, setInvoiceDiscount] = useState<number>(0);
  const [loadingCheckout, setLoadingCheckout] = useState(false);
  const [lastInvoice, setLastInvoice] = useState<Invoice | null>(null);
  const [errorMessage, setErrorMessage] = useState('');

  // Debounced medicine search
  useEffect(() => {
    const handler = setTimeout(() => {
      if (searchQuery.trim().length >= 2) {
        executeSearch(searchQuery);
      } else {
        setSearchResults([]);
      }
    }, 300);
    return () => clearTimeout(handler);
  }, [searchQuery]);

  const executeSearch = async (query: string) => {
    setSearching(true);
    try {
      const res = await api.get(`/pharmacy/medicines/search?query=${encodeURIComponent(query)}&size=10`);
      setSearchResults(res.data.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setSearching(false);
    }
  };

  const addToCart = (med: Medicine) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.medicine.id === med.id);
      if (existing) {
        return prev.map((item) =>
          item.medicine.id === med.id
            ? { ...item, quantityStrips: item.quantityStrips + 1 }
            : item
        );
      }
      return [...prev, { medicine: med, quantityStrips: 1, discountAmount: 0 }];
    });
  };

  const updateQuantity = (medicineId: number, qty: number) => {
    if (qty <= 0) {
      removeFromCart(medicineId);
      return;
    }
    setCart((prev) =>
      prev.map((item) =>
        item.medicine.id === medicineId ? { ...item, quantityStrips: qty } : item
      )
    );
  };

  const removeFromCart = (medicineId: number) => {
    setCart((prev) => prev.filter((item) => item.medicine.id !== medicineId));
  };

  // Live preview totals calculation
  const subtotal = cart.reduce(
    (sum, item) => sum + item.medicine.pricePerStrip * item.quantityStrips,
    0
  );
  const totalItemTax = cart.reduce((sum, item) => {
    const linePrice = item.medicine.pricePerStrip * item.quantityStrips;
    return sum + (linePrice * (item.medicine.taxPercent || 0)) / 100;
  }, 0);
  const grandTotal = Math.max(0, subtotal - invoiceDiscount + totalItemTax);

  const handleCheckout = async () => {
    if (cart.length === 0) return;
    setErrorMessage('');
    setLoadingCheckout(true);

    try {
      const payload = {
        customerName: customerName.trim() || 'Walk-in Customer',
        customerPhone: customerPhone.trim() || undefined,
        paymentMethod,
        invoiceDiscount: Number(invoiceDiscount) || 0,
        paidAmount: grandTotal,
        items: cart.map((item) => ({
          medicineId: item.medicine.id,
          quantityStrips: item.quantityStrips,
          discountAmount: item.discountAmount || 0,
        })),
      };

      const res = await api.post('/pharmacy/pos/checkout', payload);
      setLastInvoice(res.data);
      setCart([]);
      setSearchQuery('');
      setSearchResults([]);
    } catch (err: any) {
      setErrorMessage(
        err.response?.data?.message || 'Failed to complete checkout. Verify stock availability.'
      );
    } finally {
      setLoadingCheckout(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Pharmacy Point of Sale (POS)</h1>
          <p className="text-sm text-slate-500">
            Real-time strip inventory dispensing with automatic FEFO batch allocation
          </p>
        </div>
      </div>

      {errorMessage && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm flex items-center space-x-2">
          <AlertCircle className="w-5 h-5 shrink-0" />
          <span>{errorMessage}</span>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column: Search & Medicine Catalog Selection */}
        <div className="lg:col-span-7 space-y-4">
          <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-4">
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                <Search className="h-5 w-5" />
              </div>
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search medicines by Brand Name, Generic, or SKU (e.g. Paracetamol, Amoxil)..."
                className="w-full pl-10 pr-4 py-3 rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
              />
            </div>

            {/* Results Box */}
            <div className="min-h-[300px]">
              {searching ? (
                <div className="flex items-center justify-center py-12 text-slate-400 text-sm">
                  Searching database catalog...
                </div>
              ) : searchResults.length > 0 ? (
                <div className="space-y-2.5 max-h-[420px] overflow-y-auto pr-1">
                  {searchResults.map((med) => (
                    <div
                      key={med.id}
                      className="p-3.5 rounded-lg border border-slate-200 hover:border-blue-300 hover:bg-blue-50/40 transition-all flex items-center justify-between"
                    >
                      <div>
                        <div className="flex items-center space-x-2">
                          <h4 className="text-sm font-bold text-slate-900">{med.brandName}</h4>
                          <span className="text-xs px-2 py-0.5 rounded bg-slate-100 text-slate-600 font-medium">
                            {med.strength}
                          </span>
                          <span className="text-xs px-2 py-0.5 rounded bg-blue-50 text-blue-700 font-semibold">
                            {med.dosageForm}
                          </span>
                        </div>
                        <p className="text-xs text-slate-500 mt-0.5">
                          Generic: <span className="text-slate-700">{med.genericName}</span> • SKU: {med.sku}
                        </p>
                        <p className="text-xs text-slate-500 mt-0.5">
                          Pack: {med.unitsPerStrip} {med.baseUnit}s/strip • In Stock:{' '}
                          <span className="font-bold text-slate-800">
                            {med.availableStockStrips || 0} strips ({med.availableBaseUnits || 0} {med.baseUnit}s)
                          </span>
                        </p>
                      </div>

                      <div className="flex items-center space-x-3 text-right">
                        <div>
                          <p className="text-sm font-bold text-slate-900">₹{Number(med.pricePerStrip).toFixed(2)}</p>
                          <p className="text-[11px] text-slate-400">per strip ({med.taxPercent}% Tax)</p>
                        </div>
                        <button
                          onClick={() => addToCart(med)}
                          disabled={(med.availableStockStrips || 0) <= 0}
                          className="px-3 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-200 disabled:text-slate-400 text-white rounded-lg text-xs font-semibold flex items-center space-x-1 transition-colors"
                        >
                          <Plus className="w-3.5 h-3.5" />
                          <span>Add</span>
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : searchQuery.trim().length >= 2 ? (
                <div className="flex flex-col items-center justify-center py-16 text-slate-400">
                  <AlertCircle className="w-8 h-8 text-slate-300 mb-2" />
                  <p className="text-sm font-medium">No medicines found in MySQL catalog for "{searchQuery}"</p>
                  <p className="text-xs text-slate-400 mt-1">Add items via the Medicine Catalog module</p>
                </div>
              ) : (
                <div className="flex flex-col items-center justify-center py-16 text-slate-400">
                  <ShoppingCart className="w-10 h-10 text-slate-300 mb-2" />
                  <p className="text-sm font-medium">Start typing to search available medicines</p>
                  <p className="text-xs text-slate-400 mt-1">Search by Brand, Generic compound, or Barcode/SKU</p>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Right Column: Billing Cart & Checkout Summary */}
        <div className="lg:col-span-5 space-y-4">
          <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-col justify-between h-full">
            <div>
              <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-4">
                <h3 className="text-base font-bold text-slate-800 flex items-center space-x-2">
                  <ShoppingCart className="w-4 h-4 text-blue-600" />
                  <span>Dispensing Cart ({cart.length})</span>
                </h3>
                {cart.length > 0 && (
                  <button
                    onClick={() => setCart([])}
                    className="text-xs text-red-600 hover:text-red-700 font-medium"
                  >
                    Clear Cart
                  </button>
                )}
              </div>

              {/* Cart Items List */}
              <div className="space-y-3 max-h-[260px] overflow-y-auto pr-1">
                {cart.length === 0 ? (
                  <p className="text-center py-8 text-xs text-slate-400">Cart is empty. Select medicines to bill.</p>
                ) : (
                  cart.map((item) => (
                    <div
                      key={item.medicine.id}
                      className="p-3 rounded-lg bg-slate-50 border border-slate-200/80 flex items-center justify-between"
                    >
                      <div className="min-w-0 flex-1 pr-2">
                        <h4 className="text-xs font-bold text-slate-800 truncate">{item.medicine.brandName}</h4>
                        <p className="text-[11px] text-slate-500">
                          ₹{Number(item.medicine.pricePerStrip).toFixed(2)}/strip • {item.medicine.unitsPerStrip}{' '}
                          {item.medicine.baseUnit}s
                        </p>
                      </div>

                      {/* Quantity Controller */}
                      <div className="flex items-center space-x-2">
                        <div className="flex items-center border border-slate-300 rounded bg-white">
                          <button
                            onClick={() => updateQuantity(item.medicine.id!, item.quantityStrips - 1)}
                            className="p-1 text-slate-500 hover:text-slate-800 hover:bg-slate-100"
                          >
                            <Minus className="w-3 h-3" />
                          </button>
                          <input
                            type="number"
                            min="1"
                            value={item.quantityStrips}
                            onChange={(e) =>
                              updateQuantity(item.medicine.id!, parseInt(e.target.value) || 1)
                            }
                            className="w-10 text-center text-xs font-bold text-slate-800 focus:outline-none"
                          />
                          <button
                            onClick={() => updateQuantity(item.medicine.id!, item.quantityStrips + 1)}
                            className="p-1 text-slate-500 hover:text-slate-800 hover:bg-slate-100"
                          >
                            <Plus className="w-3 h-3" />
                          </button>
                        </div>

                        <div className="w-16 text-right">
                          <p className="text-xs font-bold text-slate-900">
                            ₹{(item.medicine.pricePerStrip * item.quantityStrips).toFixed(2)}
                          </p>
                        </div>

                        <button
                          onClick={() => removeFromCart(item.medicine.id!)}
                          className="text-slate-400 hover:text-red-600 p-1"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </div>
                  ))
                )}
              </div>

              {/* Customer and Payment Details */}
              <div className="mt-4 pt-4 border-t border-slate-100 space-y-3">
                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 uppercase mb-1">
                      Customer Name
                    </label>
                    <input
                      type="text"
                      value={customerName}
                      onChange={(e) => setCustomerName(e.target.value)}
                      className="w-full px-2.5 py-1.5 text-xs rounded border border-slate-300 focus:outline-none focus:ring-1 focus:ring-blue-500"
                      placeholder="Walk-in Customer"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 uppercase mb-1">
                      Phone Number
                    </label>
                    <input
                      type="text"
                      value={customerPhone}
                      onChange={(e) => setCustomerPhone(e.target.value)}
                      className="w-full px-2.5 py-1.5 text-xs rounded border border-slate-300 focus:outline-none focus:ring-1 focus:ring-blue-500"
                      placeholder="9876543210"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 uppercase mb-1">
                      Payment Mode
                    </label>
                    <select
                      value={paymentMethod}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                      className="w-full px-2.5 py-1.5 text-xs rounded border border-slate-300 focus:outline-none focus:ring-1 focus:ring-blue-500 bg-white"
                    >
                      <option value="CASH">Cash</option>
                      <option value="UPI">UPI / QR Code</option>
                      <option value="CARD">Credit / Debit Card</option>
                      <option value="NET_BANKING">Net Banking</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-[11px] font-semibold text-slate-600 uppercase mb-1">
                      Discount (₹)
                    </label>
                    <input
                      type="number"
                      min="0"
                      value={invoiceDiscount}
                      onChange={(e) => setInvoiceDiscount(parseFloat(e.target.value) || 0)}
                      className="w-full px-2.5 py-1.5 text-xs rounded border border-slate-300 focus:outline-none focus:ring-1 focus:ring-blue-500"
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Bill Summary Calculation */}
            <div className="mt-4 pt-4 border-t border-slate-200 space-y-2">
              <div className="flex justify-between text-xs text-slate-600">
                <span>Subtotal</span>
                <span>₹{subtotal.toFixed(2)}</span>
              </div>
              {invoiceDiscount > 0 && (
                <div className="flex justify-between text-xs text-emerald-600 font-medium">
                  <span>Discount</span>
                  <span>-₹{Number(invoiceDiscount).toFixed(2)}</span>
                </div>
              )}
              <div className="flex justify-between text-xs text-slate-600">
                <span>Estimated Tax (GST)</span>
                <span>₹{totalItemTax.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-base font-bold text-slate-900 pt-2 border-t border-slate-200">
                <span>Net Payable</span>
                <span className="text-blue-600">₹{grandTotal.toFixed(2)}</span>
              </div>

              <button
                onClick={handleCheckout}
                disabled={cart.length === 0 || loadingCheckout}
                className="w-full mt-3 py-2.5 px-4 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-300 text-white rounded-lg font-bold text-sm shadow-md shadow-emerald-500/20 transition-colors flex items-center justify-center space-x-2"
              >
                <CheckCircle className="w-4 h-4" />
                <span>{loadingCheckout ? 'Processing Transaction...' : 'Complete POS Sale & Print Bill'}</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Invoice Modal / Print Preview */}
      {lastInvoice && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-slate-200 pb-3">
              <div>
                <h3 className="text-lg font-bold text-slate-900">MediCore Hospital Pharmacy</h3>
                <p className="text-xs text-slate-500">Official Tax Invoice & Medicine Receipt</p>
              </div>
              <span className="px-2.5 py-1 bg-emerald-100 text-emerald-800 text-xs font-bold rounded-full">
                {lastInvoice.paymentStatus}
              </span>
            </div>

            <div className="grid grid-cols-2 text-xs text-slate-600 gap-2 bg-slate-50 p-3 rounded-lg">
              <div>
                <span className="font-semibold text-slate-800">Invoice #: </span>
                {lastInvoice.invoiceNumber}
              </div>
              <div>
                <span className="font-semibold text-slate-800">Date: </span>
                {new Date(lastInvoice.createdAt).toLocaleString()}
              </div>
              <div>
                <span className="font-semibold text-slate-800">Customer: </span>
                {lastInvoice.customerName}
              </div>
              <div>
                <span className="font-semibold text-slate-800">Cashier: </span>
                {lastInvoice.cashierName}
              </div>
            </div>

            {/* Items Table */}
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
                  {lastInvoice.items.map((i) => (
                    <tr key={i.id}>
                      <td className="py-2 px-3">
                        <p className="font-medium text-slate-900">{i.itemDescription}</p>
                        <p className="text-[10px] text-slate-500">
                          {i.totalBaseUnits} units • Batch: {i.batchNumbers?.join(', ')}
                        </p>
                      </td>
                      <td className="py-2 px-3 text-center font-medium">{i.quantityStrips}</td>
                      <td className="py-2 px-3 text-right">₹{Number(i.unitPricePerStrip).toFixed(2)}</td>
                      <td className="py-2 px-3 text-right font-semibold">₹{Number(i.lineTotal).toFixed(2)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Totals */}
            <div className="space-y-1.5 text-xs text-slate-600 text-right pt-2 border-t border-slate-200">
              <div>Subtotal: ₹{Number(lastInvoice.subtotal).toFixed(2)}</div>
              <div>Tax (GST): ₹{Number(lastInvoice.taxAmount).toFixed(2)}</div>
              {Number(lastInvoice.discountAmount) > 0 && (
                <div>Discount: -₹{Number(lastInvoice.discountAmount).toFixed(2)}</div>
              )}
              <div className="text-base font-bold text-slate-900">
                Grand Total: ₹{Number(lastInvoice.totalAmount).toFixed(2)}
              </div>
            </div>

            <div className="flex space-x-3 pt-4 border-t border-slate-200">
              <button
                onClick={() => window.print()}
                className="flex-1 py-2 px-4 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-bold flex items-center justify-center space-x-2"
              >
                <Printer className="w-3.5 h-3.5" />
                <span>Print Receipt</span>
              </button>
              <button
                onClick={() => setLastInvoice(null)}
                className="py-2 px-4 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold"
              >
                Close & Next Sale
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PharmacyPOS;

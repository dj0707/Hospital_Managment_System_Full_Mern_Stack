export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
}

export interface Patient {
  id?: number;
  patientCode?: string;
  firstName: string;
  lastName: string;
  dob: string;
  gender: string;
  bloodGroup?: string;
  phone: string;
  email?: string;
  address?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  allergies?: string;
  medicalHistory?: string;
}

export interface Department {
  id: number;
  name: string;
  code: string;
  description?: string;
  headOfDept?: string;
  isActive: boolean;
}

export interface Doctor {
  id?: number;
  departmentId: number;
  departmentName?: string;
  doctorCode?: string;
  fullName: string;
  specialization: string;
  qualification: string;
  experienceYears?: number;
  consultationFee: number;
  phone: string;
  email: string;
  availableDays?: string;
  shiftStart?: string;
  shiftEnd?: string;
  isActive?: boolean;
}

export interface Appointment {
  id?: number;
  appointmentCode?: string;
  patientId: number;
  patientName?: string;
  patientPhone?: string;
  doctorId: number;
  doctorName?: string;
  departmentName?: string;
  appointmentDate: string;
  appointmentTime: string;
  status: string;
  reason?: string;
  consultationFee?: number;
}

export interface MedicineCategory {
  id: number;
  name: string;
  description?: string;
}

export interface Supplier {
  id?: number;
  supplierCode?: string;
  name: string;
  contactPerson?: string;
  phone: string;
  email?: string;
  address?: string;
  gstNumber?: string;
  isActive?: boolean;
}

export interface Medicine {
  id?: number;
  sku: string;
  brandName: string;
  genericName: string;
  categoryId: number;
  categoryName?: string;
  dosageForm: string;
  strength: string;
  manufacturer: string;
  unitsPerStrip: number;
  baseUnit: string;
  costPerStrip: number;
  pricePerStrip: number;
  taxPercent: number;
  reorderLevelStrips?: number;
  prescriptionRequired?: boolean;
  isActive?: boolean;
  availableStockStrips?: number;
  availableBaseUnits?: number;
}

export interface MedicineBatch {
  id?: number;
  medicineId: number;
  medicineBrandName?: string;
  supplierId?: number;
  supplierName?: string;
  batchNumber: string;
  expiryDate: string;
  manufacturingDate?: string;
  quantityInStrips?: number;
  quantityInBaseUnits?: number;
  purchaseCostPerStrip?: number;
  mrpPerStrip?: number;
}

export interface CartItem {
  medicine: Medicine;
  quantityStrips: number;
  discountAmount: number;
}

export interface Invoice {
  id: number;
  invoiceNumber: string;
  invoiceType: string;
  patientId?: number;
  customerName: string;
  customerPhone: string;
  cashierName: string;
  subtotal: number;
  discountAmount: number;
  taxAmount: number;
  totalAmount: number;
  paidAmount: number;
  balanceAmount: number;
  paymentStatus: string;
  status: string;
  notes?: string;
  createdAt: string;
  items: {
    id: number;
    medicineId: number;
    itemDescription: string;
    quantityStrips: number;
    unitsPerStrip: number;
    totalBaseUnits: number;
    unitPricePerStrip: number;
    discountAmount: number;
    taxPercent: number;
    taxAmount: number;
    lineTotal: number;
    batchNumbers: string[];
  }[];
}

export interface Ward {
  id?: number;
  name: string;
  wardType: string;
  dailyRate: number;
  isActive?: boolean;
}

export interface Bed {
  id?: number;
  bedNumber: string;
  wardId: number;
  wardName?: string;
  wardType?: string;
  dailyRate?: number;
  status: string;
}

export interface Admission {
  id?: number;
  admissionCode?: string;
  patientId: number;
  patientName?: string;
  bedId: number;
  bedNumber?: string;
  wardName?: string;
  doctorId: number;
  doctorName?: string;
  admissionDate?: string;
  dischargeDate?: string;
  diagnosis?: string;
  status: string;
  totalBedCharge?: number;
}

export interface DashboardStats {
  totalPatients: number;
  activeDoctors: number;
  todayAppointments: number;
  activeAdmissions: number;
  availableBeds: number;
  totalMedicines: number;
  lowStockMedicines: number;
  todaySales: number;
  monthlySales: number;
  outstandingBalance: number;
}

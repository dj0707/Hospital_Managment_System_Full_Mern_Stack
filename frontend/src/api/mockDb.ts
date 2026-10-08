export interface MockUser {
  id: number;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
  password: string;
}

const DEFAULT_USERS: MockUser[] = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@medicore.com',
    fullName: 'System Administrator',
    roles: ['ROLE_ADMIN'],
    password: 'Admin@Medicore2026!',
  },
  {
    id: 2,
    username: 'doctor',
    email: 'doctor@medicore.com',
    fullName: 'Dr. Sarah Jenkins, MD',
    roles: ['ROLE_DOCTOR'],
    password: 'Doctor@Medicore2026!',
  },
  {
    id: 3,
    username: 'pharmacist',
    email: 'pharmacy@medicore.com',
    fullName: 'David Vance, RPh',
    roles: ['ROLE_PHARMACIST'],
    password: 'Pharmacy@Medicore2026!',
  },
];

const DEFAULT_PATIENTS = [
  {
    id: 1,
    patientCode: 'PAT-1001',
    fullName: 'Eleanor Vance',
    email: 'eleanor.vance@example.com',
    phone: '+1 (555) 234-5678',
    dateOfBirth: '1988-04-12',
    gender: 'FEMALE',
    bloodGroup: 'O_POSITIVE',
    address: '742 Evergreen Terrace, Springfield',
    emergencyContact: 'John Vance (+1 555-987-6543)',
    createdAt: '2026-03-01T09:00:00Z',
  },
  {
    id: 2,
    patientCode: 'PAT-1002',
    fullName: 'Marcus Sterling',
    email: 'marcus.s@example.com',
    phone: '+1 (555) 876-5432',
    dateOfBirth: '1975-11-23',
    gender: 'MALE',
    bloodGroup: 'A_POSITIVE',
    address: '1204 Elmhurst Lane, Chicago',
    emergencyContact: 'Clara Sterling (+1 555-321-7654)',
    createdAt: '2026-03-05T14:30:00Z',
  },
  {
    id: 3,
    patientCode: 'PAT-1003',
    fullName: 'Sophia Chen',
    email: 'sophia.chen@example.com',
    phone: '+1 (555) 345-6789',
    dateOfBirth: '1995-08-19',
    gender: 'FEMALE',
    bloodGroup: 'B_NEGATIVE',
    address: '88 Beacon Hill, Boston',
    emergencyContact: 'Wei Chen (+1 555-654-9870)',
    createdAt: '2026-03-10T11:15:00Z',
  },
];

const DEFAULT_DOCTORS = [
  {
    id: 1,
    doctorCode: 'DOC-101',
    fullName: 'Dr. Sarah Jenkins',
    email: 's.jenkins@medicore.com',
    phone: '+1 (555) 789-0123',
    specialization: 'Cardiology',
    department: 'Cardiovascular Care',
    qualification: 'MD, FACC - Harvard Medical',
    consultationFee: 150.0,
    availableDays: ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'],
    status: 'ACTIVE',
  },
  {
    id: 2,
    doctorCode: 'DOC-102',
    fullName: 'Dr. Robert Alistair',
    email: 'r.alistair@medicore.com',
    phone: '+1 (555) 890-1234',
    specialization: 'Neurology',
    department: 'Neurosciences Institute',
    qualification: 'MD, PhD - Johns Hopkins',
    consultationFee: 180.0,
    availableDays: ['MONDAY', 'WEDNESDAY', 'FRIDAY'],
    status: 'ACTIVE',
  },
  {
    id: 3,
    doctorCode: 'DOC-103',
    fullName: 'Dr. Emily Watson',
    email: 'e.watson@medicore.com',
    phone: '+1 (555) 901-2345',
    specialization: 'Pediatrics',
    department: 'Children Care Center',
    qualification: 'MD, FAAP - Stanford Medicine',
    consultationFee: 120.0,
    availableDays: ['TUESDAY', 'THURSDAY', 'SATURDAY'],
    status: 'ACTIVE',
  },
];

const DEFAULT_APPOINTMENTS = [
  {
    id: 1,
    appointmentNumber: 'APT-2026-001',
    patientId: 1,
    patientName: 'Eleanor Vance',
    doctorId: 1,
    doctorName: 'Dr. Sarah Jenkins',
    appointmentDate: new Date().toISOString().split('T')[0],
    timeSlot: '09:30 AM',
    status: 'CONFIRMED',
    reason: 'Routine Cardiac Checkup & BP review',
    notes: 'Patient reported minor palpitations after workouts.',
  },
  {
    id: 2,
    appointmentNumber: 'APT-2026-002',
    patientId: 2,
    patientName: 'Marcus Sterling',
    doctorId: 2,
    doctorName: 'Dr. Robert Alistair',
    appointmentDate: new Date().toISOString().split('T')[0],
    timeSlot: '11:00 AM',
    status: 'COMPLETED',
    reason: 'Follow-up on MRI Neuro Scan',
    notes: 'No abnormal lesions found. Prescribed preventive migraine regimen.',
  },
];

const DEFAULT_MEDICINES = [
  {
    id: 1,
    medicineCode: 'MED-001',
    name: 'Amoxicillin 500mg',
    genericName: 'Amoxicillin Trihydrate',
    category: 'Antibiotics',
    manufacturer: 'Pfizer Ltd',
    unitPrice: 12.5,
    stockQuantity: 450,
    reorderLevel: 50,
    batches: [
      { id: 101, batchNumber: 'AMX-2026A', expiryDate: '2027-06-30', quantity: 250, unitCost: 7.0 },
      { id: 102, batchNumber: 'AMX-2026B', expiryDate: '2027-11-15', quantity: 200, unitCost: 7.2 },
    ],
  },
  {
    id: 2,
    medicineCode: 'MED-002',
    name: 'Atorvastatin 20mg',
    genericName: 'Atorvastatin Calcium',
    category: 'Cardiovascular',
    manufacturer: 'Novartis',
    unitPrice: 22.0,
    stockQuantity: 320,
    reorderLevel: 40,
    batches: [
      { id: 103, batchNumber: 'ATV-2026', expiryDate: '2028-01-20', quantity: 320, unitCost: 14.0 },
    ],
  },
  {
    id: 3,
    medicineCode: 'MED-003',
    name: 'Paracetamol 650mg',
    genericName: 'Acetaminophen',
    category: 'Analgesics',
    manufacturer: 'GSK Pharma',
    unitPrice: 4.0,
    stockQuantity: 1200,
    reorderLevel: 100,
    batches: [
      { id: 104, batchNumber: 'PCM-2026X', expiryDate: '2027-12-31', quantity: 1200, unitCost: 1.8 },
    ],
  },
  {
    id: 4,
    medicineCode: 'MED-004',
    name: 'Metformin 500mg XR',
    genericName: 'Metformin Hydrochloride',
    category: 'Antidiabetic',
    manufacturer: 'Merck Healthcare',
    unitPrice: 8.5,
    stockQuantity: 600,
    reorderLevel: 80,
    batches: [
      { id: 105, batchNumber: 'MET-2026', expiryDate: '2027-09-15', quantity: 600, unitCost: 4.2 },
    ],
  },
];

const DEFAULT_INVOICES = [
  {
    id: 1,
    invoiceNumber: 'INV-2026-0089',
    patientName: 'Eleanor Vance',
    customerName: 'Eleanor Vance',
    customerPhone: '+1 (555) 234-5678',
    totalAmount: 162.5,
    discountAmount: 10.0,
    taxAmount: 12.2,
    netAmount: 164.7,
    paymentMode: 'CARD',
    status: 'PAID',
    createdAt: new Date().toISOString(),
    items: [
      { id: 1, medicineName: 'Amoxicillin 500mg', batchNumber: 'AMX-2026A', quantity: 10, unitPrice: 12.5, total: 125.0 },
      { id: 2, medicineName: 'Paracetamol 650mg', batchNumber: 'PCM-2026X', quantity: 10, unitPrice: 4.0, total: 40.0 },
    ],
  },
];

const DEFAULT_ROOMS = [
  { id: 1, roomNumber: 'ICU-101', wardType: 'Intensive Care Unit', totalBeds: 4, occupiedBeds: 2, dailyRate: 500.0, status: 'AVAILABLE' },
  { id: 2, roomNumber: 'GEN-201', wardType: 'General Ward', totalBeds: 12, occupiedBeds: 7, dailyRate: 80.0, status: 'AVAILABLE' },
  { id: 3, roomNumber: 'PVT-301', wardType: 'Deluxe Private Suite', totalBeds: 1, occupiedBeds: 0, dailyRate: 350.0, status: 'AVAILABLE' },
  { id: 4, roomNumber: 'PVT-302', wardType: 'Single Private Room', totalBeds: 1, occupiedBeds: 1, dailyRate: 250.0, status: 'OCCUPIED' },
];

const DEFAULT_ADMISSIONS = [
  {
    id: 1,
    admissionCode: 'ADM-2026-014',
    patientId: 2,
    patientName: 'Marcus Sterling',
    doctorId: 1,
    doctorName: 'Dr. Sarah Jenkins',
    roomNumber: 'ICU-101',
    bedNumber: 'Bed-A',
    admissionDate: new Date(Date.now() - 86400000 * 2).toISOString(),
    diagnosis: 'Acute coronary syndrome monitoring',
    status: 'ADMITTED',
  },
];

function getStorage<T>(key: string, initial: T): T {
  try {
    const data = localStorage.getItem(`medicore_${key}`);
    if (data) return JSON.parse(data);
  } catch (e) {
    console.error('Mock storage read failed', e);
  }
  localStorage.setItem(`medicore_${key}`, JSON.stringify(initial));
  return initial;
}

function setStorage<T>(key: string, value: T): void {
  localStorage.setItem(`medicore_${key}`, JSON.stringify(value));
}

export const mockDb = {
  getUsers: () => getStorage<MockUser[]>('users', DEFAULT_USERS),
  setUsers: (users: MockUser[]) => setStorage('users', users),
  getPatients: () => getStorage('patients', DEFAULT_PATIENTS),
  setPatients: (data: any[]) => setStorage('patients', data),
  getDoctors: () => getStorage('doctors', DEFAULT_DOCTORS),
  setDoctors: (data: any[]) => setStorage('doctors', data),
  getAppointments: () => getStorage('appointments', DEFAULT_APPOINTMENTS),
  setAppointments: (data: any[]) => setStorage('appointments', data),
  getMedicines: () => getStorage('medicines', DEFAULT_MEDICINES),
  setMedicines: (data: any[]) => setStorage('medicines', data),
  getInvoices: () => getStorage('invoices', DEFAULT_INVOICES),
  setInvoices: (data: any[]) => setStorage('invoices', data),
  getRooms: () => getStorage('rooms', DEFAULT_ROOMS),
  setRooms: (data: any[]) => setStorage('rooms', data),
  getAdmissions: () => getStorage('admissions', DEFAULT_ADMISSIONS),
  setAdmissions: (data: any[]) => setStorage('admissions', data),
};

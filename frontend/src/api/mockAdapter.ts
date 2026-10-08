import { AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { mockDb, MockUser } from './mockDb';

export function handleMockRequest(config: InternalAxiosRequestConfig | AxiosRequestConfig): Promise<AxiosResponse> {
  const url = (config.url || '').replace(/^https?:\/\/[^/]+/, '').replace(/^\/api\/v1/, '');
  const method = (config.method || 'get').toLowerCase();
  let data: any = {};
  if (typeof config.data === 'string') {
    try {
      data = JSON.parse(config.data);
    } catch {
      data = config.data;
    }
  } else if (config.data) {
    data = config.data;
  }

  const createRes = (status: number, responseData: any): AxiosResponse => ({
    data: responseData,
    status,
    statusText: status >= 200 && status < 300 ? 'OK' : 'Error',
    headers: {},
    config: config as InternalAxiosRequestConfig,
  });

  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        // AUTH / LOGIN
        if (url.startsWith('/auth/login') && method === 'post') {
          const users = mockDb.getUsers();
          const user = users.find(
            (u) =>
              (u.username.toLowerCase() === data.username?.toLowerCase() ||
                u.email.toLowerCase() === data.username?.toLowerCase()) &&
              u.password === data.password
          );

          if (user) {
            return resolve(
              createRes(200, {
                token: 'mock-jwt-token-' + Date.now(),
                type: 'Bearer',
                id: user.id,
                username: user.username,
                email: user.email,
                fullName: user.fullName,
                roles: user.roles,
              })
            );
          } else {
            return reject({
              response: createRes(401, { message: 'Invalid username or password. Check credentials.' }),
            });
          }
        }

        // AUTH / REGISTER
        if (url.startsWith('/auth/register') && method === 'post') {
          const users = mockDb.getUsers();
          if (users.some((u) => u.username.toLowerCase() === data.username?.toLowerCase())) {
            return reject({
              response: createRes(400, { message: 'Username already taken. Please choose another.' }),
            });
          }
          const newUser: MockUser = {
            id: users.length + 1,
            username: data.username,
            email: data.email,
            fullName: data.fullName,
            roles: ['ROLE_PATIENT'],
            password: data.password,
          };
          users.push(newUser);
          mockDb.setUsers(users);

          // Add to patients list as well
          const patients = mockDb.getPatients();
          patients.push({
            id: patients.length + 1,
            patientCode: `PAT-${1000 + patients.length + 1}`,
            fullName: data.fullName,
            email: data.email,
            phone: data.phone || '',
            dateOfBirth: data.dateOfBirth || '',
            gender: data.gender || 'OTHER',
            bloodGroup: data.bloodGroup || 'O_POSITIVE',
            address: data.address || '',
            emergencyContact: '',
            createdAt: new Date().toISOString(),
          });
          mockDb.setPatients(patients);

          return resolve(createRes(200, { message: 'Registration successful!' }));
        }

        // DASHBOARD STATS
        if (url.includes('/dashboard/stats') && method === 'get') {
          const patients = mockDb.getPatients();
          const doctors = mockDb.getDoctors();
          const appointments = mockDb.getAppointments();
          const rooms = mockDb.getRooms();
          const admissions = mockDb.getAdmissions();
          const invoices = mockDb.getInvoices();

          const totalRevenue = invoices.reduce((acc: number, inv: any) => acc + (inv.netAmount || 0), 0);
          const totalBeds = rooms.reduce((acc: number, r: any) => acc + (r.totalBeds || 0), 0);
          const occupiedBeds = rooms.reduce((acc: number, r: any) => acc + (r.occupiedBeds || 0), 0);

          return resolve(
            createRes(200, {
              totalPatients: patients.length,
              activeDoctors: doctors.length,
              todayAppointments: appointments.length,
              activeAdmissions: admissions.length,
              availableBeds: Math.max(0, totalBeds - occupiedBeds),
              todayRevenue: totalRevenue,
              lowStockCount: 0,
              expiredCount: 0,
            })
          );
        }

        // PATIENTS
        if (url.startsWith('/patients')) {
          const patients = mockDb.getPatients();
          if (method === 'get') {
            return resolve(createRes(200, patients));
          }
          if (method === 'post') {
            const newPatient = {
              id: patients.length + 1,
              patientCode: `PAT-${1000 + patients.length + 1}`,
              ...data,
              createdAt: new Date().toISOString(),
            };
            patients.push(newPatient);
            mockDb.setPatients(patients);
            return resolve(createRes(201, newPatient));
          }
        }

        // DOCTORS
        if (url.startsWith('/doctors')) {
          const doctors = mockDb.getDoctors();
          if (method === 'get') {
            return resolve(createRes(200, doctors));
          }
          if (method === 'post') {
            const newDoc = {
              id: doctors.length + 1,
              doctorCode: `DOC-${100 + doctors.length + 1}`,
              status: 'ACTIVE',
              ...data,
            };
            doctors.push(newDoc);
            mockDb.setDoctors(doctors);
            return resolve(createRes(201, newDoc));
          }
        }

        // APPOINTMENTS
        if (url.startsWith('/appointments')) {
          const appointments = mockDb.getAppointments();
          if (method === 'get') {
            return resolve(createRes(200, appointments));
          }
          if (method === 'post') {
            const newApt = {
              id: appointments.length + 1,
              appointmentNumber: `APT-2026-${String(appointments.length + 1).padStart(3, '0')}`,
              status: 'CONFIRMED',
              ...data,
            };
            appointments.push(newApt);
            mockDb.setAppointments(appointments);
            return resolve(createRes(201, newApt));
          }
        }

        // MEDICINES / PHARMACY
        if (url.startsWith('/medicines') || url.startsWith('/pharmacy/medicines')) {
          const medicines = mockDb.getMedicines();
          if (method === 'get') {
            return resolve(createRes(200, medicines));
          }
          if (method === 'post') {
            const newMed = {
              id: medicines.length + 1,
              medicineCode: `MED-${String(medicines.length + 1).padStart(3, '0')}`,
              batches: data.batches || [{ id: Date.now(), batchNumber: 'BAT-2026', expiryDate: '2027-12-31', quantity: data.stockQuantity || 100, unitCost: (data.unitPrice || 10) * 0.7 }],
              ...data,
            };
            medicines.push(newMed);
            mockDb.setMedicines(medicines);
            return resolve(createRes(201, newMed));
          }
        }

        // INVOICES / BILLING
        if (url.startsWith('/invoices') || url.startsWith('/pharmacy/invoices') || url.startsWith('/pharmacy/billing')) {
          const invoices = mockDb.getInvoices();
          if (method === 'get') {
            return resolve(createRes(200, invoices));
          }
          if (method === 'post') {
            const newInv = {
              id: invoices.length + 1,
              invoiceNumber: `INV-2026-${String(invoices.length + 1).padStart(4, '0')}`,
              status: 'PAID',
              createdAt: new Date().toISOString(),
              ...data,
            };
            invoices.unshift(newInv);
            mockDb.setInvoices(invoices);
            return resolve(createRes(201, newInv));
          }
        }

        // ROOMS & INPATIENTS
        if (url.startsWith('/rooms')) {
          const rooms = mockDb.getRooms();
          return resolve(createRes(200, rooms));
        }
        if (url.startsWith('/admissions')) {
          const admissions = mockDb.getAdmissions();
          if (method === 'get') {
            return resolve(createRes(200, admissions));
          }
          if (method === 'post') {
            const newAdm = {
              id: admissions.length + 1,
              admissionCode: `ADM-2026-${String(admissions.length + 1).padStart(3, '0')}`,
              status: 'ADMITTED',
              admissionDate: new Date().toISOString(),
              ...data,
            };
            admissions.push(newAdm);
            mockDb.setAdmissions(admissions);
            return resolve(createRes(201, newAdm));
          }
        }

        // AI ASSISTANT CLINICAL SUMMARY
        if (url.includes('/ai/')) {
          return resolve(
            createRes(200, {
              response:
                "MediCore Clinical AI Note: Patient metrics are stabilized. Recommend standard vitals monitoring and maintaining prescribed dosages.",
            })
          );
        }

        // Generic fallback for any other get/post
        return resolve(createRes(200, []));
      } catch (err) {
        return reject(err);
      }
    }, 200);
  });
}

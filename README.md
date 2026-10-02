# 🏥 MediCore HMS — Smart Hospital Management and Pharmacy Billing System

> **College Major Project**  
> GitHub Repository: **[https://github.com/dj0707/Hospital_Managment_System_Full_Mern_Stack](https://github.com/dj0707/Hospital_Managment_System_Full_Mern_Stack)**  
> Architecture: **Spring Boot 3 (Java 21 LTS) + React TypeScript + Aiven MySQL 8 + Tailwind CSS**

---

## ⚡ 1-Click Run Everything (No copy-pasting commands!)

In `C:\Users\admin\Downloads\medicore-hms`:
👉 **Double-click `start-all.bat`**

This will automatically open and launch:
1. **Spring Boot Backend**: `http://localhost:8080` (API & Swagger at `/swagger-ui.html`)
2. **React Frontend**: `http://localhost:5173`

---

## 🔑 Login Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **System Administrator** | `admin` | `Admin@Medicore2026!` |

---

## 🚀 How to Push this to your GitHub Repository

Run these exact commands in PowerShell or Terminal inside `C:\Users\admin\Downloads\medicore-hms`:

```powershell
# 1. Navigate to project
cd C:\Users\admin\Downloads\medicore-hms

# 2. Link your remote GitHub repo
git remote add origin https://github.com/dj0707/Hospital_Managment_System_Full_Mern_Stack.git

# 3. Rename branch to main and push
git branch -M main
git push -u origin main --force
```

---

## 🌐 How to Deploy Frontend to Vercel (Step-by-Step)

1. Go to **[vercel.com](https://vercel.com)** and log in with your GitHub account.
2. Click **"Add New..."** ➔ **"Project"**.
3. Select your repository: `dj0707/Hospital_Managment_System_Full_Mern_Stack`.
4. Configure the project settings:
   - **Root Directory**: Select `frontend` (or click *Edit* and choose `frontend`).
   - **Framework Preset**: `Vite`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
5. **Environment Variables**:
   - Add `VITE_API_BASE_URL` = `https://your-backend-url.com/api/v1` (or your cloud deployed backend).
6. Click **Deploy**!
   - *A `vercel.json` file is already included inside `frontend/` so all page routes (`/dashboard`, `/pharmacy/pos`, etc.) will work without 404 errors.*

---

## ☁️ How to Deploy Backend (Render / Railway / Aiven)

1. **Database**: Create a free MySQL instance on **[Aiven.io](https://aiven.io)** or **Railway**.
2. **Backend**:
   - Link repository on **Render** or **Railway**.
   - Set **Root Directory**: `backend`
   - Add Environment Variables:
     - `SPRING_DATASOURCE_URL` = `jdbc:mysql://<aiven-host>:<port>/<dbname>?sslMode=PREFERRED`
     - `SPRING_DATASOURCE_USERNAME` = `<username>`
     - `SPRING_DATASOURCE_PASSWORD` = `<password>`
     - `JWT_SECRET` = `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970`
     - `ADMIN_BOOTSTRAP_ENABLED` = `true`

---

## 📦 What is Included in this Project

- **Pharmacy POS (Top Priority)**: Real-time debounced medicine search, strip/pack quantity input, FEFO (First-Expiry-First-Out) batch stock deduction with pessimistic locking, and instant printable receipts.
- **Patient Management (EHR)**: Patient profiles, demographics, blood group, emergency contacts, and history.
- **Doctors & Staff**: Department allocation, consultation fees, and schedule tracking.
- **Appointments**: Conflict-free booking with concurrency check.
- **Inpatient Wards & Beds**: Ward types (General, ICU, Private), bed status, admission tracking, and discharge stay billing.
- **Billing & Invoices**: Immutable line items, tax (GST), discounts, and partial/full payment tracking.
- **AI Operations Assistant**: Advisory chatbot for hospital policies and inventory guidance.

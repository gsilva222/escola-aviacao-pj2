import { Navigate, Route, Routes, useParams } from 'react-router-dom';
import { AppShell } from './components/AppShell';
import { RequireAuth } from './components/RequireAuth';
import { BoCrudPage } from './components/BoCrudPage';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { BoDashboardPage, BoProfilesPage, BoReportsPage } from './pages/bo/BoDashboardPage';
import { BoStudentDocumentsPage, BoUsersPage } from './pages/bo/BoAdminPages';
import {
  aircraftConfig, coursesConfig, evaluationsConfig, flightsConfig,
  instructorsConfig, maintenanceConfig, paymentsConfig, studentsConfig,
} from './pages/bo/boEntityConfigs';
import {
  FoDashboardPage, FoDocumentsPage, FoEvaluationsPage, FoFlightsPage,
  FoHoursPage, FoPaymentsPage, FoProfilePage, FoSchedulePage,
} from './pages/fo/FoPages';

function HomeRedirect() {
  const { auth } = useAuth();
  if (!auth) return <Navigate to="/login" replace />;
  return <Navigate to={auth.role === 'ADMIN' ? '/bo' : '/fo'} replace />;
}

function StudentDocsRoute() {
  const { studentId } = useParams();
  if (!studentId) return <Navigate to="/bo/students" replace />;
  return <BoStudentDocumentsPage studentId={studentId} />;
}

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<HomeRedirect />} />

        <Route element={<RequireAuth role="ADMIN" />}>
          <Route element={<AppShell variant="bo" />}>
            <Route path="/bo" element={<BoDashboardPage />} />
            <Route path="/bo/students" element={<BoCrudPage config={studentsConfig} />} />
            <Route path="/bo/students/:studentId/documents" element={<StudentDocsRoute />} />
            <Route path="/bo/courses" element={<BoCrudPage config={coursesConfig} />} />
            <Route path="/bo/flights" element={<BoCrudPage config={flightsConfig} />} />
            <Route path="/bo/aircraft" element={<BoCrudPage config={aircraftConfig} />} />
            <Route path="/bo/instructors" element={<BoCrudPage config={instructorsConfig} />} />
            <Route path="/bo/maintenance" element={<BoCrudPage config={maintenanceConfig} />} />
            <Route path="/bo/evaluations" element={<BoCrudPage config={evaluationsConfig} />} />
            <Route path="/bo/payments" element={<BoCrudPage config={paymentsConfig} />} />
            <Route path="/bo/reports" element={<BoReportsPage />} />
            <Route path="/bo/profiles" element={<BoProfilesPage />} />
            <Route path="/bo/users" element={<BoUsersPage />} />
          </Route>
        </Route>

        <Route element={<RequireAuth role="STUDENT" />}>
          <Route element={<AppShell variant="fo" />}>
            <Route path="/fo" element={<FoDashboardPage />} />
            <Route path="/fo/schedule" element={<FoSchedulePage />} />
            <Route path="/fo/flights" element={<FoFlightsPage />} />
            <Route path="/fo/hours" element={<FoHoursPage />} />
            <Route path="/fo/evaluations" element={<FoEvaluationsPage />} />
            <Route path="/fo/documents" element={<FoDocumentsPage />} />
            <Route path="/fo/payments" element={<FoPaymentsPage />} />
            <Route path="/fo/profile" element={<FoProfilePage />} />
          </Route>
        </Route>

        <Route path="*" element={<HomeRedirect />} />
      </Routes>
    </AuthProvider>
  );
}

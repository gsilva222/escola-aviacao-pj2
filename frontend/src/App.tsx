import { Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from './components/AppShell';
import { RequireAuth } from './components/RequireAuth';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { BoDashboardPage, BoProfilesPage, BoReportsPage } from './pages/bo/BoDashboardPage';
import {
  BoAircraftPage,
  BoCoursesPage,
  BoEvaluationsPage,
  BoFlightsPage,
  BoInstructorsPage,
  BoMaintenancePage,
  BoPaymentsPage,
  BoStudentsPage,
} from './pages/bo/BoListPages';
import {
  FoDashboardPage,
  FoDocumentsPage,
  FoEvaluationsPage,
  FoFlightsPage,
  FoHoursPage,
  FoPaymentsPage,
  FoProfilePage,
  FoSchedulePage,
} from './pages/fo/FoPages';

function HomeRedirect() {
  const { auth } = useAuth();
  if (!auth) return <Navigate to="/login" replace />;
  return <Navigate to={auth.role === 'ADMIN' ? '/bo' : '/fo'} replace />;
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
            <Route path="/bo/students" element={<BoStudentsPage />} />
            <Route path="/bo/courses" element={<BoCoursesPage />} />
            <Route path="/bo/flights" element={<BoFlightsPage />} />
            <Route path="/bo/aircraft" element={<BoAircraftPage />} />
            <Route path="/bo/instructors" element={<BoInstructorsPage />} />
            <Route path="/bo/maintenance" element={<BoMaintenancePage />} />
            <Route path="/bo/evaluations" element={<BoEvaluationsPage />} />
            <Route path="/bo/payments" element={<BoPaymentsPage />} />
            <Route path="/bo/reports" element={<BoReportsPage />} />
            <Route path="/bo/profiles" element={<BoProfilesPage />} />
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

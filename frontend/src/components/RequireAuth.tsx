import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function RequireAuth({ role }: { role?: 'ADMIN' | 'STUDENT' }) {
  const { auth } = useAuth();
  const location = useLocation();

  if (!auth) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  if (role && auth.role !== role) {
    return <Navigate to={auth.role === 'ADMIN' ? '/bo' : '/fo'} replace />;
  }
  return <Outlet />;
}

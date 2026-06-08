import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { BoPageKey, canAccessBoPage } from '../security/rolePolicy';

const boLinks: { to: string; label: string; end?: boolean; key: BoPageKey }[] = [
  { to: '/bo', label: 'Dashboard', end: true, key: 'dashboard' },
  { to: '/bo/students', label: 'Alunos', key: 'students' },
  { to: '/bo/courses', label: 'Cursos', key: 'courses' },
  { to: '/bo/flights', label: 'Voos', key: 'flights' },
  { to: '/bo/aircraft', label: 'Aeronaves', key: 'aircraft' },
  { to: '/bo/instructors', label: 'Instrutores', key: 'instructors' },
  { to: '/bo/maintenance', label: 'Manutenção', key: 'maintenance' },
  { to: '/bo/evaluations', label: 'Avaliações', key: 'evaluations' },
  { to: '/bo/payments', label: 'Pagamentos', key: 'payments' },
  { to: '/bo/reports', label: 'Relatórios', key: 'reports' },
  { to: '/bo/profiles', label: 'Perfis', key: 'profiles' },
  { to: '/bo/users', label: 'Utilizadores', key: 'users' },
];

const foLinks = [
  { to: '/fo', label: 'Dashboard', end: true },
  { to: '/fo/schedule', label: 'Horário' },
  { to: '/fo/flights', label: 'Voos' },
  { to: '/fo/hours', label: 'Horas' },
  { to: '/fo/evaluations', label: 'Avaliações' },
  { to: '/fo/documents', label: 'Documentos' },
  { to: '/fo/payments', label: 'Pagamentos' },
  { to: '/fo/profile', label: 'Perfil' },
];

export function AppShell({ variant }: { variant: 'bo' | 'fo' }) {
  const { auth, logout } = useAuth();
  const title = variant === 'bo' ? 'BackOffice' : 'Área do Aluno';
  const staffProfile = auth?.staffProfile ?? 'Administrador';

  const visibleBoLinks = variant === 'bo'
    ? boLinks.filter((l) => canAccessBoPage(staffProfile, l.key))
    : [];

  return (
    <div className={`shell shell-${variant}`}>
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-icon">✈</span>
          <div>
            <strong>AeroSchool</strong>
            <small>{title}</small>
          </div>
        </div>
        <nav>
          {(variant === 'bo' ? visibleBoLinks : foLinks).map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end} className="nav-link">
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="user-pill">{auth?.username}{staffProfile && variant === 'bo' ? ` · ${staffProfile}` : ''}</span>
          <button type="button" className="btn btn-ghost btn-sm" onClick={logout}>Sair</button>
        </div>
      </aside>
      <main className="main"><Outlet /></main>
    </div>
  );
}

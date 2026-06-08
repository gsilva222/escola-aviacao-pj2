import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const boLinks = [
  { to: '/bo', label: 'Dashboard', end: true },
  { to: '/bo/students', label: 'Alunos' },
  { to: '/bo/courses', label: 'Cursos' },
  { to: '/bo/flights', label: 'Voos' },
  { to: '/bo/aircraft', label: 'Aeronaves' },
  { to: '/bo/instructors', label: 'Instrutores' },
  { to: '/bo/maintenance', label: 'Manutenção' },
  { to: '/bo/evaluations', label: 'Avaliações' },
  { to: '/bo/payments', label: 'Pagamentos' },
  { to: '/bo/reports', label: 'Relatórios' },
  { to: '/bo/profiles', label: 'Perfis' },
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
  const links = variant === 'bo' ? boLinks : foLinks;
  const title = variant === 'bo' ? 'BackOffice' : 'Área do Aluno';

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
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} end={link.end} className="nav-link">
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="user-pill">{auth?.username}</span>
          <button type="button" className="btn btn-ghost btn-sm" onClick={logout}>
            Sair
          </button>
        </div>
      </aside>
      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}

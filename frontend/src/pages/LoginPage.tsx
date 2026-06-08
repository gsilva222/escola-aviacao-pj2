import { FormEvent, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ErrorAlert } from '../components/Ui';

export function LoginPage() {
  const { auth, login } = useAuth();
  const location = useLocation();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (auth) {
    const from = (location.state as { from?: { pathname: string } })?.from?.pathname;
    if (from) return <Navigate to={from} replace />;
    return <Navigate to={auth.role === 'ADMIN' ? '/bo' : '/fo'} replace />;
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await login(username, password);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Falha no login');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <form className="login-card" onSubmit={handleSubmit}>
        <h1>AeroSchool</h1>
        <p>Inicie sessão para aceder ao BackOffice ou à área do aluno.</p>
        {error && <ErrorAlert message={error} />}
        <div className="field">
          <label htmlFor="username">Utilizador</label>
          <input id="username" value={username} onChange={(e) => setUsername(e.target.value)} required />
        </div>
        <div className="field">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button className="btn" type="submit" disabled={loading} style={{ width: '100%' }}>
          {loading ? 'A entrar...' : 'Entrar'}
        </button>
        <p className="muted" style={{ marginTop: 16, fontSize: 12 }}>
          Demo: admin / admin123 · aluno: joao.silva@email.com / aluno123
        </p>
      </form>
    </div>
  );
}

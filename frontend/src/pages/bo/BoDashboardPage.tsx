import { api } from '../../api/client';
import type { BoDashboard } from '../../api/types';
import { ErrorAlert, KpiCard, Loading, PageHeader } from '../../components/Ui';
import { useFetch } from '../../hooks/useFetch';
import { BoCrudPage } from '../../components/BoCrudPage';
import { profilesConfig } from './boEntityConfigs';

function BarChart({ title, data }: { title: string; data: Record<string, number> }) {
  const entries = Object.entries(data);
  const max = Math.max(1, ...entries.map(([, v]) => v));
  return (
    <div className="card">
      <h3>{title}</h3>
      <div className="bar-chart">
        {entries.length === 0 && <p className="muted">Sem dados</p>}
        {entries.map(([label, value]) => (
          <div className="bar-row" key={label}>
            <span className="bar-label">{label}</span>
            <div className="bar-track"><div className="bar-fill" style={{ width: `${(value / max) * 100}%` }} /></div>
            <span className="bar-value">{value}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export function BoDashboardPage() {
  const { data, loading, error } = useFetch(() => api<BoDashboard>('/bo/dashboard'), []);

  if (loading) return <Loading />;
  if (error || !data) return <ErrorAlert message={error ?? 'Sem dados'} />;

  const r = data.reports;
  const aircraftData = {
    Operacionais: r.operationalAircraft,
    Manutenção: r.maintenanceAircraft,
    Imobilizadas: r.groundedAircraft,
  };

  return (
    <>
      <PageHeader title="Dashboard" subtitle={`${data.flightsToday} voos hoje`} />
      <div className="banner card" style={{ marginBottom: 16 }}>
        <div>
          <h2 style={{ margin: 0 }}>Resumo do mês</h2>
          <p style={{ margin: '6px 0 0', opacity: 0.85 }}>
            {data.completedFlightsToday} completados · {data.scheduledFlightsToday} agendados hoje
          </p>
        </div>
        <div style={{ display: 'flex', gap: 24 }}>
          <div className="banner-stat"><small>Horas de voo</small><strong>{data.flightHoursThisMonth.toFixed(1)}h</strong></div>
          <div className="banner-stat"><small>Receita</small><strong>€ {data.revenueThisMonth.toFixed(0)}</strong></div>
        </div>
      </div>
      <div className="grid grid-4" style={{ marginBottom: 16 }}>
        <KpiCard label="Alunos activos" value={r.activeStudents} hint={`de ${r.totalStudents} total`} />
        <KpiCard label="Voos" value={r.totalFlights} hint={`${r.scheduledFlights} agendados`} />
        <KpiCard label="Aeronaves operacionais" value={r.operationalAircraft} hint={`de ${r.totalAircraft}`} />
        <KpiCard label="Pagamentos pendentes" value={r.pendingPayments + r.overduePayments} hint={`€ ${r.totalPendingAmount.toFixed(0)}`} />
      </div>
      <div className="grid grid-2">
        <BarChart title="Alunos por curso" data={r.studentsByCourse} />
        <BarChart title="Estado da frota" data={aircraftData} />
      </div>
      <div className="card" style={{ marginTop: 16 }}>
        <h3>Actividade recente</h3>
        <ul className="activity-list">
          {data.recentActivity.map((item, i) => (
            <li key={i}><strong>{item.title}</strong><br /><small>{item.subtitle}</small></li>
          ))}
        </ul>
      </div>
    </>
  );
}

export function BoReportsPage() {
  const { data, loading, error } = useFetch(() => api<BoDashboard['reports']>('/bo/reports/summary'), []);
  if (loading) return <Loading />;
  if (error || !data) return <ErrorAlert message={error ?? 'Sem dados'} />;
  return (
    <>
      <PageHeader title="Relatórios" subtitle="Indicadores globais da escola" />
      <div className="grid grid-3" style={{ marginBottom: 16 }}>
        <KpiCard label="Alunos activos" value={data.activeStudents} />
        <KpiCard label="Alunos suspensos" value={data.suspendedStudents} />
        <KpiCard label="Alunos concluídos" value={data.completedStudents} />
        <KpiCard label="Voos completados" value={data.completedFlights} />
        <KpiCard label="Manutenções activas" value={data.activeMaintenances} />
        <KpiCard label="Valor pendente" value={`€ ${data.totalPendingAmount.toFixed(0)}`} />
      </div>
      <BarChart title="Distribuição por curso" data={data.studentsByCourse} />
    </>
  );
}

export function BoProfilesPage() {
  return <BoCrudPage config={profilesConfig} />;
}

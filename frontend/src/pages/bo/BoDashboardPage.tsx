import { api, apiList } from '../../api/client';
import type { BoDashboard } from '../../api/types';
import { ErrorAlert, KpiCard, Loading, PageHeader } from '../../components/Ui';
import { useFetch } from '../../hooks/useFetch';

export function BoDashboardPage() {
  const { data, loading, error } = useFetch(() => api<BoDashboard>('/bo/dashboard'), []);

  if (loading) return <Loading />;
  if (error || !data) return <ErrorAlert message={error ?? 'Sem dados'} />;

  const r = data.reports;
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
          <div className="banner-stat">
            <small>Horas de voo</small>
            <strong>{data.flightHoursThisMonth.toFixed(1)}h</strong>
          </div>
          <div className="banner-stat">
            <small>Receita</small>
            <strong>€ {data.revenueThisMonth.toFixed(0)}</strong>
          </div>
        </div>
      </div>
      <div className="grid grid-4" style={{ marginBottom: 16 }}>
        <KpiCard label="Alunos activos" value={r.activeStudents} hint={`de ${r.totalStudents} total`} />
        <KpiCard label="Voos" value={r.totalFlights} hint={`${r.scheduledFlights} agendados`} />
        <KpiCard label="Aeronaves operacionais" value={r.operationalAircraft} hint={`de ${r.totalAircraft}`} />
        <KpiCard label="Pagamentos pendentes" value={r.pendingPayments + r.overduePayments} hint={`€ ${r.totalPendingAmount.toFixed(0)}`} />
      </div>
      <div className="grid grid-2">
        <div className="card">
          <h3>Alunos por curso</h3>
          <ul className="activity-list">
            {Object.entries(r.studentsByCourse).map(([course, count]) => (
              <li key={course}>
                <strong>{course}</strong>
                <small> · {String(count)} alunos</small>
              </li>
            ))}
          </ul>
        </div>
        <div className="card">
          <h3>Actividade recente</h3>
          <ul className="activity-list">
            {data.recentActivity.map((item: BoDashboard['recentActivity'][number], i: number) => (
              <li key={i}>
                <strong>{item.title}</strong>
                <br />
                <small>{item.subtitle}</small>
              </li>
            ))}
          </ul>
        </div>
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
      <div className="grid grid-3">
        <KpiCard label="Alunos activos" value={data.activeStudents} />
        <KpiCard label="Alunos suspensos" value={data.suspendedStudents} />
        <KpiCard label="Alunos concluídos" value={data.completedStudents} />
        <KpiCard label="Voos completados" value={data.completedFlights} />
        <KpiCard label="Manutenções activas" value={data.activeMaintenances} />
        <KpiCard label="Valor pendente" value={`€ ${data.totalPendingAmount.toFixed(0)}`} />
      </div>
    </>
  );
}

export function BoProfilesPage() {
  const { data, loading, error, reload } = useFetch(() => apiList<{ id: number; nome: string; descricao?: string }>('/bo/profiles'), []);
  if (loading) return <Loading />;
  if (error) return <ErrorAlert message={error} />;
  return (
    <>
      <PageHeader title="Perfis" subtitle="Perfis de utilizador do sistema" actions={
        <button className="btn btn-sm" type="button" onClick={() => reload()}>Actualizar</button>
      } />
      <DataTableSimple
        columns={[
          { key: 'nome', label: 'Nome' },
          { key: 'descricao', label: 'Descrição' },
        ]}
        rows={data ?? []}
      />
    </>
  );
}

function DataTableSimple({ columns, rows }: {
  columns: { key: string; label: string }[];
  rows: Record<string, unknown>[];
}) {
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>{columns.map((c) => <th key={c.key}>{c.label}</th>)}</tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={String(row.id)}>
              {columns.map((c) => <td key={c.key}>{String(row[c.key] ?? '—')}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

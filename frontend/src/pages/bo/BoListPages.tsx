import { apiList } from '../../api/client';
import { DataTable, ErrorAlert, Loading, PageHeader, StatusBadge } from '../../components/Ui';
import { useFetch } from '../../hooks/useFetch';

type Row = Record<string, unknown> & { id: number };

function makeListPage(
  title: string,
  path: string,
  columns: { key: string; label: string; render?: (row: Row) => React.ReactNode }[],
) {
  return function ListPage() {
    const { data, loading, error, reload } = useFetch(() => apiList<Row>(`${path}?size=500`), []);
    if (loading) return <Loading />;
    if (error) return <ErrorAlert message={error} />;
    return (
      <>
        <PageHeader title={title} actions={
          <button className="btn btn-sm btn-ghost" type="button" onClick={() => reload()}>Actualizar</button>
        } />
        <DataTable
          columns={columns.map((c) => ({
            key: c.key,
            label: c.label,
            render: c.render ? (row) => c.render!(row as Row) : undefined,
          }))}
          rows={(data ?? []) as Record<string, unknown>[]}
        />
      </>
    );
  };
}

export const BoStudentsPage = makeListPage('Alunos', '/bo/students', [
  { key: 'name', label: 'Nome' },
  { key: 'email', label: 'Email' },
  { key: 'courseName', label: 'Curso' },
  { key: 'progress', label: 'Progresso', render: (r) => `${r.progress ?? 0}%` },
  { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
]);

export const BoCoursesPage = makeListPage('Cursos', '/bo/courses', [
  { key: 'name', label: 'Nome' },
  { key: 'duration', label: 'Duração' },
  { key: 'flightHours', label: 'Horas voo' },
  { key: 'price', label: 'Preço', render: (r) => `€ ${r.price ?? 0}` },
]);

export const BoFlightsPage = makeListPage('Voos', '/bo/flights', [
  { key: 'flightDate', label: 'Data' },
  { key: 'studentName', label: 'Aluno' },
  { key: 'instructorName', label: 'Instrutor' },
  { key: 'aircraftRegistration', label: 'Aeronave' },
  { key: 'duration', label: 'Horas' },
  { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
]);

export const BoAircraftPage = makeListPage('Aeronaves', '/bo/aircraft', [
  { key: 'registration', label: 'Matrícula' },
  { key: 'model', label: 'Modelo' },
  { key: 'type', label: 'Tipo' },
  { key: 'location', label: 'Local' },
  { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
]);

export const BoInstructorsPage = makeListPage('Instrutores', '/bo/instructors', [
  { key: 'name', label: 'Nome' },
  { key: 'license', label: 'Licença' },
  { key: 'specialization', label: 'Especialização' },
  { key: 'email', label: 'Email' },
  { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
]);

export const BoMaintenancePage = makeListPage('Manutenção', '/bo/maintenance', [
  { key: 'aircraftRegistration', label: 'Aeronave' },
  { key: 'maintenanceType', label: 'Tipo' },
  { key: 'description', label: 'Descrição' },
  { key: 'technician', label: 'Técnico' },
  { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
]);

export const BoEvaluationsPage = makeListPage('Avaliações', '/bo/evaluations', [
  { key: 'examName', label: 'Exame' },
  { key: 'studentName', label: 'Aluno' },
  { key: 'evaluationDate', label: 'Data' },
  {
    key: 'score',
    label: 'Nota',
    render: (r) => (r.score != null ? `${r.score}/${r.maxScore ?? 100}` : '—'),
  },
  { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
]);

export const BoPaymentsPage = makeListPage('Pagamentos', '/bo/payments', [
  { key: 'studentName', label: 'Aluno' },
  { key: 'description', label: 'Descrição' },
  { key: 'amount', label: 'Valor', render: (r) => `€ ${r.amount}` },
  { key: 'dueDate', label: 'Vencimento' },
  { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
]);

export function PageHeader({ title, subtitle, actions }: {
  title: string;
  subtitle?: string;
  actions?: React.ReactNode;
}) {
  return (
    <header className="page-header">
      <div>
        <h1>{title}</h1>
        {subtitle && <p className="muted">{subtitle}</p>}
      </div>
      {actions && <div className="page-actions">{actions}</div>}
    </header>
  );
}

export function Loading() {
  return <div className="loading">A carregar...</div>;
}

export function ErrorAlert({ message }: { message: string }) {
  return <div className="alert alert-error">{message}</div>;
}

export function StatusBadge({ status }: { status: string }) {
  const cls = ['badge'];
  if (['active', 'operational', 'paid', 'passed', 'completed', 'up_to_date'].includes(status)) {
    cls.push('badge-success');
  } else if (['scheduled', 'pending', 'in_progress'].includes(status)) {
    cls.push('badge-warning');
  } else if (['overdue', 'failed', 'cancelled', 'grounded', 'suspended'].includes(status)) {
    cls.push('badge-danger');
  } else {
    cls.push('badge-neutral');
  }
  return <span className={cls.join(' ')}>{status}</span>;
}

export function DataTable({ columns, rows }: {
  columns: { key: string; label: string; render?: (row: Record<string, unknown>) => React.ReactNode }[];
  rows: Record<string, unknown>[];
}) {
  if (rows.length === 0) {
    return <div className="empty-state">Sem registos.</div>;
  }
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.key}>{col.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, idx) => (
            <tr key={String(row.id ?? idx)}>
              {columns.map((col) => (
                <td key={col.key}>
                  {col.render ? col.render(row) : String(row[col.key] ?? '—')}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function KpiCard({ label, value, hint }: { label: string; value: string | number; hint?: string }) {
  return (
    <div className="kpi-card">
      <span className="kpi-label">{label}</span>
      <strong className="kpi-value">{value}</strong>
      {hint && <span className="kpi-hint">{hint}</span>}
    </div>
  );
}

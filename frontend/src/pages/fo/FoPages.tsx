import { FormEvent, useEffect, useState } from 'react';
import { api, apiList, downloadFile, uploadFile } from '../../api/client';
import type { FoDashboard, FoHoursSummary, PaymentSummary, Student, StudentDocument } from '../../api/types';
import { DataTable, ErrorAlert, KpiCard, Loading, PageHeader, StatusBadge } from '../../components/Ui';
import { useFetch } from '../../hooks/useFetch';

export function FoDashboardPage() {
  const { data, loading, error } = useFetch(() => api<FoDashboard>('/fo/dashboard'), []);
  if (loading) return <Loading />;
  if (error || !data) return <ErrorAlert message={error ?? 'Sem dados'} />;
  return (
    <>
      <PageHeader title={`Olá, ${data.studentName}`} subtitle={data.courseName ?? 'Curso'} />
      <div className="grid grid-4" style={{ marginBottom: 16 }}>
        <KpiCard label="Progresso" value={`${data.progress ?? 0}%`} />
        <KpiCard label="Horas voo" value={`${data.completedFlightHours.toFixed(1)}/${data.requiredFlightHours}h`} />
        <KpiCard label="Próximos voos" value={data.upcomingFlights} />
        <KpiCard label="Pagamentos pendentes" value={data.pendingPayments} hint={`€ ${data.pendingAmount.toFixed(0)}`} />
      </div>
      <div className="grid grid-2">
        <div className="card">
          <h3>Voos recentes</h3>
          <DataTable
            columns={[
              { key: 'flightDate', label: 'Data' },
              { key: 'aircraftRegistration', label: 'Aeronave' },
              { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status)} /> },
            ]}
            rows={data.recentFlights as unknown as Record<string, unknown>[]}
          />
        </div>
        <div className="card">
          <h3>Avaliações recentes</h3>
          <DataTable
            columns={[
              { key: 'examName', label: 'Exame' },
              { key: 'evaluationDate', label: 'Data' },
              { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status)} /> },
            ]}
            rows={data.recentEvaluations as unknown as Record<string, unknown>[]}
          />
        </div>
      </div>
    </>
  );
}

export function FoFlightsPage() {
  const { data, loading, error } = useFetch(() => apiList('/fo/flights?size=500'), []);
  if (loading) return <Loading />;
  if (error) return <ErrorAlert message={error} />;
  return (
    <>
      <PageHeader title="Os meus voos" />
      <DataTable
        columns={[
          { key: 'flightDate', label: 'Data' },
          { key: 'instructorName', label: 'Instrutor' },
          { key: 'aircraftRegistration', label: 'Aeronave' },
          { key: 'duration', label: 'Horas' },
          { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status)} /> },
        ]}
        rows={(data ?? []) as Record<string, unknown>[]}
      />
    </>
  );
}

export function FoSchedulePage() {
  const { data, loading, error } = useFetch(() => apiList('/fo/schedule'), []);
  if (loading) return <Loading />;
  if (error) return <ErrorAlert message={error} />;
  return (
    <>
      <PageHeader title="Horário" subtitle="Voos agendados" />
      <DataTable
        columns={[
          { key: 'flightDate', label: 'Data' },
          { key: 'flightTime', label: 'Hora' },
          { key: 'instructorName', label: 'Instrutor' },
          { key: 'aircraftRegistration', label: 'Aeronave' },
        ]}
        rows={(data ?? []) as Record<string, unknown>[]}
      />
    </>
  );
}

export function FoHoursPage() {
  const { data, loading, error } = useFetch(() => api<FoHoursSummary>('/fo/hours'), []);
  if (loading) return <Loading />;
  if (error || !data) return <ErrorAlert message={error ?? 'Sem dados'} />;
  return (
    <>
      <PageHeader title="Horas de voo" />
      <div className="grid grid-3">
        <KpiCard label="Completadas" value={`${data.completedHours.toFixed(1)}h`} />
        <KpiCard label="Locais" value={`${data.localHours.toFixed(1)}h`} />
        <KpiCard label="Navegação" value={`${data.navigationHours.toFixed(1)}h`} />
        <KpiCard label="Total voos" value={data.totalFlights} />
        <KpiCard label="Em falta" value={`${data.remainingHours.toFixed(1)}h`} />
        <KpiCard label="Progresso" value={`${data.progressPercent}%`} />
      </div>
    </>
  );
}

export function FoEvaluationsPage() {
  const { data, loading, error } = useFetch(() => apiList('/fo/evaluations'), []);
  if (loading) return <Loading />;
  if (error) return <ErrorAlert message={error} />;
  return (
    <>
      <PageHeader title="Avaliações" />
      <DataTable
        columns={[
          { key: 'examName', label: 'Exame' },
          { key: 'evaluationDate', label: 'Data' },
          { key: 'score', label: 'Nota' },
          { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status)} /> },
        ]}
        rows={(data ?? []) as Record<string, unknown>[]}
      />
    </>
  );
}

export function FoPaymentsPage() {
  const summary = useFetch(() => api<PaymentSummary>('/fo/payments/summary'), []);
  const list = useFetch(() => apiList('/fo/payments?size=500'), []);
  if (summary.loading || list.loading) return <Loading />;
  if (summary.error || list.error) return <ErrorAlert message={summary.error ?? list.error ?? 'Erro'} />;
  return (
    <>
      <PageHeader title="Pagamentos" />
      {summary.data && (
        <div className="grid grid-3" style={{ marginBottom: 16 }}>
          <KpiCard label="Pendentes" value={`€ ${summary.data.totalPending.toFixed(0)}`} hint={`${summary.data.pendingCount} itens`} />
          <KpiCard label="Em atraso" value={`€ ${summary.data.totalOverdue.toFixed(0)}`} />
          <KpiCard label="Pagos" value={`€ ${summary.data.totalPaid.toFixed(0)}`} />
        </div>
      )}
      <DataTable
        columns={[
          { key: 'description', label: 'Descrição' },
          { key: 'amount', label: 'Valor', render: (r) => `€ ${r.amount}` },
          { key: 'dueDate', label: 'Vencimento' },
          { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status)} /> },
        ]}
        rows={(list.data ?? []) as Record<string, unknown>[]}
      />
    </>
  );
}

export function FoDocumentsPage() {
  const { data, loading, error, reload } = useFetch(() => apiList<StudentDocument>('/fo/documents'), []);
  const [uploading, setUploading] = useState(false);

  async function handleUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      await uploadFile('/fo/documents', file, 'geral');
      await reload();
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  }

  if (loading) return <Loading />;
  if (error) return <ErrorAlert message={error} />;
  return (
    <>
      <PageHeader
        title="Documentos"
        actions={
          <label className="btn btn-sm">
            {uploading ? 'A enviar...' : 'Carregar ficheiro'}
            <input type="file" hidden onChange={handleUpload} disabled={uploading} />
          </label>
        }
      />
      <DataTable
        columns={[
          { key: 'fileName', label: 'Ficheiro' },
          { key: 'category', label: 'Categoria' },
          { key: 'uploadedAt', label: 'Data' },
          {
            key: 'actions',
            label: '',
            render: (r) => (
              <button
                type="button"
                className="btn btn-sm btn-ghost"
                onClick={() => downloadFile(`/fo/documents/${r.id}/download`, String(r.fileName))}
              >
                Download
              </button>
            ),
          },
        ]}
        rows={(data ?? []) as unknown as Record<string, unknown>[]}
      />
    </>
  );
}

export function FoProfilePage() {
  const { data, loading, error, reload } = useFetch(() => api<Student>('/fo/me'), []);
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [nationality, setNationality] = useState('');
  const [saved, setSaved] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [pwdMsg, setPwdMsg] = useState<string | null>(null);

  useEffect(() => {
    if (data) {
      setPhone(data.phone ?? '');
      setAddress(data.address ?? '');
      setNationality(data.nationality ?? '');
    }
  }, [data]);

  if (loading) return <Loading />;
  if (error || !data) return <ErrorAlert message={error ?? 'Sem perfil'} />;

  async function saveProfile(e: FormEvent) {
    e.preventDefault();
    await api('/fo/me', { method: 'PUT', body: JSON.stringify({ phone, address, nationality }) });
    setSaved(true);
    reload();
  }

  async function changePassword(e: FormEvent) {
    e.preventDefault();
    setPwdMsg(null);
    try {
      await api('/auth/change-password', {
        method: 'POST',
        body: JSON.stringify({ currentPassword, newPassword }),
      });
      setPwdMsg('Password alterada com sucesso');
      setCurrentPassword('');
      setNewPassword('');
    } catch (err) {
      setPwdMsg(err instanceof Error ? err.message : 'Erro');
    }
  }

  return (
    <>
      <PageHeader title="Perfil" subtitle={data.email} />
      <div className="grid grid-2">
        <form className="card" onSubmit={saveProfile}>
          <h3>Dados pessoais</h3>
          <div className="field"><label>Nome</label><input value={data.name} disabled /></div>
          <div className="field"><label>Telefone</label><input value={phone} onChange={(e) => setPhone(e.target.value)} /></div>
          <div className="field"><label>Morada</label><input value={address} onChange={(e) => setAddress(e.target.value)} /></div>
          <div className="field"><label>Nacionalidade</label><input value={nationality} onChange={(e) => setNationality(e.target.value)} /></div>
          <button className="btn" type="submit">Guardar</button>
          {saved && <p className="muted">Perfil actualizado.</p>}
        </form>
        <form className="card" onSubmit={changePassword}>
          <h3>Alterar password</h3>
          <div className="field"><label>Password actual</label><input type="password" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} required /></div>
          <div className="field"><label>Nova password</label><input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required minLength={6} /></div>
          <button className="btn" type="submit">Alterar</button>
          {pwdMsg && <p className="muted">{pwdMsg}</p>}
        </form>
      </div>
    </>
  );
}

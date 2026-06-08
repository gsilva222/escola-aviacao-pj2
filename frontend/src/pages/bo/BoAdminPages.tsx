import { FormEvent, useState } from 'react';
import { api, apiList, uploadFile, downloadFile } from '../../api/client';
import { Modal } from '../../components/Modal';
import { DataTable, ErrorAlert, Loading, PageHeader, StatusBadge } from '../../components/Ui';
import { useFetch } from '../../hooks/useFetch';

interface UserAccount {
  id: number;
  username: string;
  role: string;
  active: boolean;
  studentId?: number;
  studentName?: string;
  staffProfile?: string;
}

const STAFF_PROFILES = [
  'Administrador', 'Secretaria', 'Gestor Operacional', 'Instrutor', 'Técnico de Manutenção',
];

export function BoUsersPage() {
  const { data, loading, error, reload } = useFetch(() => apiList<UserAccount>('/bo/users'), []);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({ username: '', password: '', role: 'ADMIN', studentId: '', staffProfile: 'Secretaria' });
  const [msg, setMsg] = useState<string | null>(null);

  async function createUser(e: FormEvent) {
    e.preventDefault();
    setMsg(null);
    try {
      await api('/bo/users', {
        method: 'POST',
        body: JSON.stringify({
          username: form.username,
          password: form.password,
          role: form.role,
          studentId: form.role === 'STUDENT' ? Number(form.studentId) : null,
          staffProfile: form.role === 'ADMIN' ? form.staffProfile : null,
        }),
      });
      setOpen(false);
      reload();
    } catch (err) {
      setMsg(err instanceof Error ? err.message : 'Erro');
    }
  }

  async function toggleActive(user: UserAccount) {
    await api(`/bo/users/${user.id}/active`, {
      method: 'PUT',
      body: JSON.stringify({ active: !user.active }),
    });
    reload();
  }

  async function changeProfile(user: UserAccount, staffProfile: string) {
    await api(`/bo/users/${user.id}/staff-profile`, {
      method: 'PUT',
      body: JSON.stringify({ staffProfile }),
    });
    reload();
  }

  if (loading) return <Loading />;
  return (
    <>
      <PageHeader title="Utilizadores" actions={<button type="button" className="btn" onClick={() => setOpen(true)}>Nova conta</button>} />
      {error && <ErrorAlert message={error} />}
      <DataTable
        columns={[
          { key: 'username', label: 'Utilizador' },
          { key: 'role', label: 'Role' },
          { key: 'staffProfile', label: 'Perfil staff' },
          { key: 'studentName', label: 'Aluno' },
          { key: 'active', label: 'Activo', render: (r) => <StatusBadge status={r.active ? 'active' : 'suspended'} /> },
          {
            key: 'actions', label: 'Acções',
            render: (r) => (
              <div className="row-actions">
                <button type="button" className="btn btn-sm btn-ghost" onClick={() => toggleActive(r as unknown as UserAccount)}>
                  {(r.active as boolean) ? 'Desactivar' : 'Activar'}
                </button>
                {r.role === 'ADMIN' && (
                  <select
                    className="inline-select"
                    value={String(r.staffProfile ?? 'Administrador')}
                    onChange={(e) => changeProfile(r as unknown as UserAccount, e.target.value)}
                  >
                    {STAFF_PROFILES.map((p) => <option key={p} value={p}>{p}</option>)}
                  </select>
                )}
              </div>
            ),
          },
        ]}
        rows={(data ?? []) as unknown as Record<string, unknown>[]}
      />
      <Modal open={open} title="Nova conta" onClose={() => setOpen(false)}>
        <form onSubmit={createUser}>
          {msg && <ErrorAlert message={msg} />}
          <div className="field"><label>Utilizador</label><input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} required /></div>
          <div className="field"><label>Password</label><input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required minLength={6} /></div>
          <div className="field">
            <label>Role</label>
            <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
              <option value="ADMIN">Admin (staff)</option>
              <option value="STUDENT">Aluno</option>
            </select>
          </div>
          {form.role === 'ADMIN' ? (
            <div className="field">
              <label>Perfil staff</label>
              <select value={form.staffProfile} onChange={(e) => setForm({ ...form, staffProfile: e.target.value })}>
                {STAFF_PROFILES.map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
            </div>
          ) : (
            <div className="field"><label>ID Aluno</label><input type="number" value={form.studentId} onChange={(e) => setForm({ ...form, studentId: e.target.value })} required /></div>
          )}
          <div className="modal-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setOpen(false)}>Cancelar</button>
            <button type="submit" className="btn">Criar</button>
          </div>
        </form>
      </Modal>
    </>
  );
}

export function BoStudentDocumentsPage({ studentId }: { studentId: string }) {
  const { data, loading, error, reload } = useFetch(
    () => apiList<{ id: number; fileName: string; category?: string; uploadedAt: string }>(`/bo/student-documents/${studentId}`),
    [studentId],
  );
  const [uploading, setUploading] = useState(false);

  async function onUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      await uploadFile(`/bo/student-documents/${studentId}`, file, 'geral');
      reload();
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  }

  if (loading) return <Loading />;
  return (
    <>
      <PageHeader
        title={`Documentos — Aluno #${studentId}`}
        actions={
          <label className="btn btn-sm">
            {uploading ? 'A enviar...' : 'Carregar'}
            <input type="file" hidden onChange={onUpload} disabled={uploading} />
          </label>
        }
      />
      {error && <ErrorAlert message={error} />}
      <DataTable
        columns={[
          { key: 'fileName', label: 'Ficheiro' },
          { key: 'category', label: 'Categoria' },
          { key: 'uploadedAt', label: 'Data' },
          {
            key: 'dl', label: '',
            render: (r) => (
              <button type="button" className="btn btn-sm btn-ghost" onClick={() => downloadFile(`/bo/student-documents/${studentId}/${r.id}/download`, String(r.fileName))}>
                Download
              </button>
            ),
          },
        ]}
        rows={(data ?? []) as Record<string, unknown>[]}
      />
    </>
  );
}

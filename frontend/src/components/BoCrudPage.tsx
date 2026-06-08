import { FormEvent, ReactNode, useCallback, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, apiList, type Page } from '../api/client';
import { Modal } from './Modal';
import { DataTable, ErrorAlert, Loading, PageHeader, StatusBadge } from './Ui';

export type FieldType = 'text' | 'number' | 'date' | 'time' | 'select' | 'textarea';

export interface FieldDef {
  name: string;
  label: string;
  type: FieldType;
  required?: boolean;
  options?: { value: string | number; label: string }[];
  step?: string;
}

export interface ColumnDef {
  key: string;
  label: string;
  render?: (row: Record<string, unknown>) => ReactNode;
}

export interface EntityConfig {
  title: string;
  path: string;
  columns: ColumnDef[];
  fields: FieldDef[] | ((lookups: LookupState) => FieldDef[]);
  defaultValues: () => Record<string, unknown>;
  toForm: (row: Record<string, unknown>) => Record<string, unknown>;
  toPayload: (form: Record<string, unknown>) => Record<string, unknown>;
  wide?: boolean;
  docLink?: (row: Record<string, unknown>) => string;
}

export interface LookupState {
  courses: { value: number; label: string }[];
  students: { value: number; label: string }[];
  instructors: { value: number; label: string }[];
  aircraft: { value: number; label: string }[];
}

async function loadLookup(path: string, labelKey: string, idKey = 'id'): Promise<{ value: number; label: string }[]> {
  const rows = await apiList<Record<string, unknown>>(`${path}?size=500`);
  return rows.map((r) => ({
    value: Number(r[idKey]),
    label: String(r[labelKey] ?? r[idKey]),
  }));
}

export function BoCrudPage({ config }: { config: EntityConfig }) {
  const [rows, setRows] = useState<Record<string, unknown>[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Record<string, unknown> | null>(null);
  const [form, setForm] = useState<Record<string, unknown>>({});
  const [saving, setSaving] = useState(false);
  const [lookups, setLookups] = useState<LookupState>({
    courses: [], students: [], instructors: [], aircraft: [],
  });

  const pageSize = 15;

  const fields = useMemo(
    () => (typeof config.fields === 'function' ? config.fields(lookups) : config.fields),
    [config, lookups],
  );

  const reload = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api<Page<Record<string, unknown>> | Record<string, unknown>[]>(
        `${config.path}?page=${page}&size=${pageSize}&sort=id,desc`,
      );
      if (Array.isArray(data)) {
        setRows(data);
        setTotal(data.length);
      } else {
        setRows(data.content);
        setTotal(data.totalElements);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao carregar');
    } finally {
      setLoading(false);
    }
  }, [config.path, page]);

  useEffect(() => {
    reload();
  }, [reload]);

  useEffect(() => {
    Promise.all([
      loadLookup('/bo/courses', 'name'),
      loadLookup('/bo/students', 'name'),
      loadLookup('/bo/instructors', 'name'),
      loadLookup('/bo/aircraft', 'registration'),
    ]).then(([courses, students, instructors, aircraft]) => {
      setLookups({ courses, students, instructors, aircraft });
    }).catch(() => undefined);
  }, []);

  function openCreate() {
    setEditing(null);
    setForm(config.defaultValues());
    setModalOpen(true);
  }

  function openEdit(row: Record<string, unknown>) {
    setEditing(row);
    setForm(config.toForm(row));
    setModalOpen(true);
  }

  async function save(e: FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const payload = config.toPayload(form);
      if (editing?.id != null) {
        await api(`${config.path}/${editing.id}`, { method: 'PUT', body: JSON.stringify(payload) });
      } else {
        await api(config.path, { method: 'POST', body: JSON.stringify(payload) });
      }
      setModalOpen(false);
      await reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao guardar');
    } finally {
      setSaving(false);
    }
  }

  async function remove(row: Record<string, unknown>) {
    if (!window.confirm('Eliminar este registo?')) return;
    setError(null);
    try {
      await api(`${config.path}/${row.id}`, { method: 'DELETE' });
      await reload();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao eliminar');
    }
  }

  function setField(name: string, value: unknown) {
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  return (
    <>
      <PageHeader
        title={config.title}
        subtitle={`${total} registos`}
        actions={<button type="button" className="btn" onClick={openCreate}>Novo</button>}
      />
      {error && <ErrorAlert message={error} />}
      {loading ? <Loading /> : (
        <>
          <DataTable
            columns={[
              ...config.columns,
              {
                key: '_actions',
                label: 'Acções',
                render: (row) => (
                  <div className="row-actions">
                    <button type="button" className="btn btn-sm btn-ghost" onClick={() => openEdit(row)}>Editar</button>
                    <button type="button" className="btn btn-sm btn-danger" onClick={() => remove(row)}>Eliminar</button>
                    {config.docLink && (
                      <Link className="btn btn-sm btn-ghost" to={config.docLink(row)}>Documentos</Link>
                    )}
                  </div>
                ),
              },
            ]}
            rows={rows}
          />
          <div className="pagination">
            <button type="button" className="btn btn-sm btn-ghost" disabled={page <= 0} onClick={() => setPage((p) => p - 1)}>Anterior</button>
            <span>Página {page + 1} / {totalPages}</span>
            <button type="button" className="btn btn-sm btn-ghost" disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)}>Seguinte</button>
          </div>
        </>
      )}
      <Modal open={modalOpen} title={editing ? `Editar ${config.title}` : `Novo ${config.title}`} onClose={() => setModalOpen(false)} wide={config.wide}>
        <form onSubmit={save}>
          <div className="form-grid">
            {fields.map((field) => (
              <div className="field" key={field.name}>
                <label htmlFor={field.name}>{field.label}{field.required ? ' *' : ''}</label>
                {field.type === 'select' ? (
                  <select
                    id={field.name}
                    value={form[field.name] != null ? String(form[field.name]) : ''}
                    onChange={(e) => setField(field.name, e.target.value ? Number(e.target.value) || e.target.value : null)}
                    required={field.required}
                  >
                    <option value="">—</option>
                    {field.options?.map((o) => (
                      <option key={String(o.value)} value={String(o.value)}>{o.label}</option>
                    ))}
                  </select>
                ) : field.type === 'textarea' ? (
                  <textarea
                    id={field.name}
                    rows={3}
                    value={String(form[field.name] ?? '')}
                    onChange={(e) => setField(field.name, e.target.value)}
                    required={field.required}
                  />
                ) : (
                  <input
                    id={field.name}
                    type={field.type}
                    step={field.step}
                    value={form[field.name] != null ? String(form[field.name]) : ''}
                    onChange={(e) => setField(field.name, field.type === 'number' ? Number(e.target.value) : e.target.value)}
                    required={field.required}
                  />
                )}
              </div>
            ))}
          </div>
          <div className="modal-actions">
            <button type="button" className="btn btn-ghost" onClick={() => setModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn" disabled={saving}>{saving ? 'A guardar...' : 'Guardar'}</button>
          </div>
        </form>
      </Modal>
    </>
  );
}

export { StatusBadge };

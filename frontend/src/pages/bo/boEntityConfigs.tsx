import { EntityConfig, LookupState, StatusBadge } from '../../components/BoCrudPage';

const num = (v: unknown, fallback = 0) => (v == null || v === '' ? fallback : Number(v));
const str = (v: unknown) => (v == null || v === '' ? null : String(v));
const optStr = (v: unknown) => {
  const s = String(v ?? '').trim();
  return s || null;
};

export const studentsConfig: EntityConfig = {
  title: 'Alunos',
  path: '/bo/students',
  wide: true,
  docLink: (row) => `/bo/students/${row.id}/documents`,
  columns: [
    { key: 'name', label: 'Nome' },
    { key: 'email', label: 'Email' },
    { key: 'courseName', label: 'Curso' },
    { key: 'progress', label: 'Progresso', render: (r) => `${r.progress ?? 0}%` },
    { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
  ],
  fields: (l: LookupState) => [
    { name: 'name', label: 'Nome', type: 'text', required: true },
    { name: 'email', label: 'Email', type: 'text', required: true },
    { name: 'phone', label: 'Telefone', type: 'text' },
    { name: 'nif', label: 'NIF', type: 'text' },
    { name: 'courseId', label: 'Curso', type: 'select', required: true, options: l.courses },
    { name: 'instructorId', label: 'Instrutor', type: 'select', options: l.instructors },
    { name: 'status', label: 'Estado', type: 'select', required: true, options: [
      { value: 'active', label: 'Activo' }, { value: 'suspended', label: 'Suspenso' }, { value: 'completed', label: 'Concluído' },
    ]},
    { name: 'progress', label: 'Progresso (%)', type: 'number' },
    { name: 'flightHours', label: 'Horas voo', type: 'number', step: '0.1' },
    { name: 'theoreticalHours', label: 'Horas teóricas', type: 'number', step: '0.1' },
    { name: 'paymentStatus', label: 'Pagamentos', type: 'select', options: [
      { value: 'up_to_date', label: 'Em dia' }, { value: 'pending', label: 'Pendente' }, { value: 'overdue', label: 'Em atraso' },
    ]},
    { name: 'address', label: 'Morada', type: 'text' },
    { name: 'nationality', label: 'Nacionalidade', type: 'text' },
  ],
  defaultValues: () => ({
    name: '', email: '', phone: '', nif: '', courseId: null, instructorId: null,
    status: 'active', progress: 0, flightHours: 0, theoreticalHours: 0,
    paymentStatus: 'up_to_date', address: '', nationality: 'Portugal',
  }),
  toForm: (r) => ({
    name: r.name, email: r.email, phone: r.phone ?? '', nif: r.nif ?? '',
    courseId: r.courseId, instructorId: r.instructorId ?? null,
    status: r.status, progress: r.progress ?? 0, flightHours: r.flightHours ?? 0,
    theoreticalHours: r.theoreticalHours ?? 0, paymentStatus: r.paymentStatus ?? 'up_to_date',
    address: r.address ?? '', nationality: r.nationality ?? '',
  }),
  toPayload: (f) => ({
    name: f.name, email: f.email, phone: optStr(f.phone), nif: optStr(f.nif),
    birthdate: null, address: optStr(f.address), nationality: optStr(f.nationality),
    courseId: num(f.courseId), instructorId: f.instructorId ? num(f.instructorId) : null,
    status: f.status, enrollmentDate: null, progress: num(f.progress),
    flightHours: num(f.flightHours), theoreticalHours: num(f.theoreticalHours),
    paymentStatus: f.paymentStatus,
  }),
};

export const coursesConfig: EntityConfig = {
  title: 'Cursos',
  path: '/bo/courses',
  columns: [
    { key: 'name', label: 'Nome' },
    { key: 'duration', label: 'Duração' },
    { key: 'flightHours', label: 'Horas voo' },
    { key: 'price', label: 'Preço', render: (r) => `€ ${r.price ?? 0}` },
  ],
  fields: [
    { name: 'name', label: 'Nome', type: 'text', required: true },
    { name: 'duration', label: 'Duração', type: 'text' },
    { name: 'flightHours', label: 'Horas voo', type: 'number', required: true },
    { name: 'theoreticalHours', label: 'Horas teóricas', type: 'number', required: true },
    { name: 'price', label: 'Preço (€)', type: 'number', step: '0.01' },
    { name: 'description', label: 'Descrição', type: 'textarea' },
  ],
  defaultValues: () => ({ name: '', duration: '12 meses', flightHours: 45, theoreticalHours: 100, price: 0, description: '' }),
  toForm: (r) => ({ name: r.name, duration: r.duration, flightHours: r.flightHours, theoreticalHours: r.theoreticalHours, price: r.price, description: r.description ?? '' }),
  toPayload: (f) => ({ name: f.name, duration: str(f.duration), flightHours: num(f.flightHours, 1), theoreticalHours: num(f.theoreticalHours, 1), price: num(f.price), description: str(f.description) }),
};

export const flightsConfig: EntityConfig = {
  title: 'Voos',
  path: '/bo/flights',
  wide: true,
  columns: [
    { key: 'flightDate', label: 'Data' },
    { key: 'studentName', label: 'Aluno' },
    { key: 'instructorName', label: 'Instrutor' },
    { key: 'aircraftRegistration', label: 'Aeronave' },
    { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
  ],
  fields: (l) => [
    { name: 'flightDate', label: 'Data', type: 'date', required: true },
    { name: 'flightTime', label: 'Hora', type: 'time' },
    { name: 'duration', label: 'Duração (h)', type: 'number', step: '0.1', required: true },
    { name: 'studentId', label: 'Aluno', type: 'select', required: true, options: l.students },
    { name: 'instructorId', label: 'Instrutor', type: 'select', required: true, options: l.instructors },
    { name: 'aircraftId', label: 'Aeronave', type: 'select', required: true, options: l.aircraft },
    { name: 'origin', label: 'Origem (ICAO)', type: 'text' },
    { name: 'destination', label: 'Destino (ICAO)', type: 'text' },
    { name: 'flightType', label: 'Tipo', type: 'text' },
    { name: 'status', label: 'Estado', type: 'select', required: true, options: [
      { value: 'scheduled', label: 'Agendado' }, { value: 'completed', label: 'Concluído' }, { value: 'cancelled', label: 'Cancelado' },
    ]},
    { name: 'objectives', label: 'Objectivos', type: 'textarea' },
    { name: 'grade', label: 'Nota', type: 'text' },
  ],
  defaultValues: () => ({
    flightDate: new Date().toISOString().slice(0, 10), flightTime: '10:00', duration: 1.5,
    studentId: null, instructorId: null, aircraftId: null,
    origin: 'LPPT', destination: 'LPPT', flightType: 'training', status: 'scheduled', objectives: '', grade: '',
  }),
  toForm: (r) => ({
    flightDate: r.flightDate, flightTime: r.flightTime ?? '10:00', duration: r.duration,
    studentId: r.studentId, instructorId: r.instructorId, aircraftId: r.aircraftId,
    origin: r.origin ?? '', destination: r.destination ?? '', flightType: r.flightType ?? '',
    status: r.status, objectives: r.objectives ?? '', grade: r.grade ?? '',
  }),
  toPayload: (f) => ({
    flightDate: f.flightDate, flightTime: f.flightTime || null, duration: num(f.duration, 1),
    studentId: num(f.studentId), instructorId: num(f.instructorId), aircraftId: num(f.aircraftId),
    origin: optStr(f.origin)?.toUpperCase() ?? '', destination: optStr(f.destination)?.toUpperCase() ?? '',
    flightType: str(f.flightType), status: f.status, objectives: str(f.objectives), notes: null, grade: str(f.grade),
  }),
};

export const aircraftConfig: EntityConfig = {
  title: 'Aeronaves',
  path: '/bo/aircraft',
  columns: [
    { key: 'registration', label: 'Matrícula' },
    { key: 'model', label: 'Modelo' },
    { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
  ],
  fields: [
    { name: 'registration', label: 'Matrícula', type: 'text', required: true },
    { name: 'model', label: 'Modelo', type: 'text', required: true },
    { name: 'type', label: 'Tipo', type: 'text', required: true },
    { name: 'manufYear', label: 'Ano', type: 'number' },
    { name: 'status', label: 'Estado', type: 'select', options: [
      { value: 'operational', label: 'Operacional' }, { value: 'maintenance', label: 'Manutenção' }, { value: 'grounded', label: 'Imobilizada' },
    ]},
    { name: 'location', label: 'Local', type: 'text' },
    { name: 'fuelLevel', label: 'Combustível (%)', type: 'number' },
  ],
  defaultValues: () => ({ registration: '', model: '', type: 'Single Engine', manufYear: 2020, status: 'operational', flightHours: 0, location: 'Hangar', fuelLevel: 80, notes: '' }),
  toForm: (r) => ({ registration: r.registration, model: r.model, type: r.type, manufYear: r.manufYear, status: r.status, flightHours: r.flightHours ?? 0, location: r.location ?? '', fuelLevel: r.fuelLevel ?? 0, notes: r.notes ?? '' }),
  toPayload: (f) => ({ registration: f.registration, model: f.model, type: f.type, manufYear: num(f.manufYear), status: f.status, flightHours: num(f.flightHours), lastMaintenance: null, nextMaintenance: null, location: str(f.location), fuelLevel: num(f.fuelLevel), notes: str(f.notes) }),
};

export const instructorsConfig: EntityConfig = {
  title: 'Instrutores',
  path: '/bo/instructors',
  columns: [
    { key: 'name', label: 'Nome' },
    { key: 'license', label: 'Licença' },
    { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
  ],
  fields: [
    { name: 'name', label: 'Nome', type: 'text', required: true },
    { name: 'license', label: 'Licença', type: 'text', required: true },
    { name: 'specialization', label: 'Especialização', type: 'text' },
    { name: 'email', label: 'Email', type: 'text' },
    { name: 'phone', label: 'Telefone', type: 'text' },
    { name: 'status', label: 'Estado', type: 'select', options: [{ value: 'active', label: 'Activo' }, { value: 'inactive', label: 'Inactivo' }] },
  ],
  defaultValues: () => ({ name: '', license: 'CPL/FI', specialization: 'PPL', flightHours: 0, status: 'active', email: '', phone: '' }),
  toForm: (r) => ({ name: r.name, license: r.license, specialization: r.specialization ?? '', flightHours: r.flightHours ?? 0, status: r.status, email: r.email ?? '', phone: r.phone ?? '' }),
  toPayload: (f) => ({ name: f.name, license: f.license, specialization: str(f.specialization), flightHours: num(f.flightHours), status: f.status, email: str(f.email), phone: str(f.phone) }),
};

export const maintenanceConfig: EntityConfig = {
  title: 'Manutenção',
  path: '/bo/maintenance',
  wide: true,
  columns: [
    { key: 'aircraftRegistration', label: 'Aeronave' },
    { key: 'maintenanceType', label: 'Tipo' },
    { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
  ],
  fields: (l) => [
    { name: 'aircraftId', label: 'Aeronave', type: 'select', required: true, options: l.aircraft },
    { name: 'maintenanceType', label: 'Tipo', type: 'text', required: true },
    { name: 'description', label: 'Descrição', type: 'textarea', required: true },
    { name: 'technician', label: 'Técnico', type: 'text' },
    { name: 'startDate', label: 'Início', type: 'date' },
    { name: 'estimatedEndDate', label: 'Fim estimado', type: 'date' },
    { name: 'status', label: 'Estado', type: 'select', options: [
      { value: 'scheduled', label: 'Agendada' }, { value: 'in_progress', label: 'Em curso' },
      { value: 'waiting_parts', label: 'Aguarda peças' }, { value: 'completed', label: 'Concluída' },
    ]},
    { name: 'priority', label: 'Prioridade', type: 'select', options: [{ value: 'low', label: 'Baixa' }, { value: 'medium', label: 'Média' }, { value: 'high', label: 'Alta' }] },
    { name: 'cost', label: 'Custo (€)', type: 'number', step: '0.01' },
  ],
  defaultValues: () => ({ aircraftId: null, maintenanceType: 'Inspection', description: '', technician: '', startDate: new Date().toISOString().slice(0, 10), estimatedEndDate: '', status: 'scheduled', priority: 'medium', cost: 0, notes: '' }),
  toForm: (r) => ({ aircraftId: r.aircraftId, maintenanceType: r.maintenanceType, description: r.description, technician: r.technician ?? '', startDate: r.startDate ?? '', estimatedEndDate: r.estimatedEndDate ?? '', actualEndDate: r.actualEndDate ?? '', status: r.status, priority: r.priority ?? 'medium', cost: r.cost ?? 0, notes: r.notes ?? '' }),
  toPayload: (f) => ({ aircraftId: num(f.aircraftId), maintenanceType: f.maintenanceType, description: f.description, technician: str(f.technician), startDate: str(f.startDate), estimatedEndDate: str(f.estimatedEndDate), actualEndDate: str(f.actualEndDate), status: f.status, priority: f.priority, cost: num(f.cost), notes: str(f.notes) }),
};

export const evaluationsConfig: EntityConfig = {
  title: 'Avaliações',
  path: '/bo/evaluations',
  wide: true,
  columns: [
    { key: 'examName', label: 'Exame' },
    { key: 'studentName', label: 'Aluno' },
    { key: 'evaluationDate', label: 'Data' },
    { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
  ],
  fields: (l) => [
    { name: 'studentId', label: 'Aluno', type: 'select', required: true, options: l.students },
    { name: 'courseId', label: 'Curso', type: 'select', required: true, options: l.courses },
    { name: 'examName', label: 'Exame', type: 'text', required: true },
    { name: 'evaluationDate', label: 'Data', type: 'date' },
    { name: 'score', label: 'Nota', type: 'number' },
    { name: 'maxScore', label: 'Nota máx.', type: 'number' },
    { name: 'status', label: 'Estado', type: 'select', options: [{ value: 'scheduled', label: 'Agendado' }, { value: 'passed', label: 'Aprovado' }, { value: 'failed', label: 'Reprovado' }] },
    { name: 'evaluationType', label: 'Tipo', type: 'select', options: [{ value: 'theoretical', label: 'Teórico' }, { value: 'practical', label: 'Prático' }, { value: 'simulator', label: 'Simulador' }] },
    { name: 'notes', label: 'Notas', type: 'textarea' },
  ],
  defaultValues: () => ({ studentId: null, courseId: null, examName: '', evaluationDate: new Date().toISOString().slice(0, 10), score: null, maxScore: 100, status: 'scheduled', evaluationType: 'theoretical', notes: '' }),
  toForm: (r) => ({ studentId: r.studentId, courseId: r.courseId, examName: r.examName, evaluationDate: r.evaluationDate ?? '', score: r.score, maxScore: r.maxScore ?? 100, status: r.status, evaluationType: r.evaluationType ?? 'theoretical', notes: r.notes ?? '' }),
  toPayload: (f) => ({ studentId: num(f.studentId), courseId: num(f.courseId), examName: f.examName, evaluationDate: str(f.evaluationDate), score: f.score != null && f.score !== '' ? num(f.score) : null, maxScore: num(f.maxScore, 100), status: f.status, evaluationType: f.evaluationType, notes: str(f.notes) }),
};

export const paymentsConfig: EntityConfig = {
  title: 'Pagamentos',
  path: '/bo/payments',
  columns: [
    { key: 'studentName', label: 'Aluno' },
    { key: 'description', label: 'Descrição' },
    { key: 'amount', label: 'Valor', render: (r) => `€ ${r.amount}` },
    { key: 'status', label: 'Estado', render: (r) => <StatusBadge status={String(r.status ?? '')} /> },
  ],
  fields: (l) => [
    { name: 'studentId', label: 'Aluno', type: 'select', required: true, options: l.students },
    { name: 'description', label: 'Descrição', type: 'text', required: true },
    { name: 'amount', label: 'Valor (€)', type: 'number', step: '0.01', required: true },
    { name: 'dueDate', label: 'Vencimento', type: 'date' },
    { name: 'paidDate', label: 'Data pagamento', type: 'date' },
    { name: 'status', label: 'Estado', type: 'select', options: [{ value: 'pending', label: 'Pendente' }, { value: 'paid', label: 'Pago' }, { value: 'overdue', label: 'Em atraso' }] },
    { name: 'paymentMethod', label: 'Método', type: 'text' },
  ],
  defaultValues: () => ({ studentId: null, description: '', amount: 100, dueDate: new Date().toISOString().slice(0, 10), paidDate: '', status: 'pending', paymentMethod: 'Transferência', notes: '' }),
  toForm: (r) => ({ studentId: r.studentId, description: r.description, amount: r.amount, dueDate: r.dueDate ?? '', paidDate: r.paidDate ?? '', status: r.status, paymentMethod: r.paymentMethod ?? '', notes: r.notes ?? '' }),
  toPayload: (f) => ({ studentId: num(f.studentId), description: f.description, amount: num(f.amount, 1), dueDate: str(f.dueDate), paidDate: str(f.paidDate), status: f.status, paymentMethod: str(f.paymentMethod), notes: str(f.notes) }),
};

export const profilesConfig: EntityConfig = {
  title: 'Perfis',
  path: '/bo/profiles',
  columns: [
    { key: 'nome', label: 'Nome' },
    { key: 'descricao', label: 'Descrição' },
  ],
  fields: [
    { name: 'nome', label: 'Nome', type: 'text', required: true },
    { name: 'descricao', label: 'Descrição', type: 'textarea' },
  ],
  defaultValues: () => ({ nome: '', descricao: '' }),
  toForm: (r) => ({ nome: r.nome, descricao: r.descricao ?? '' }),
  toPayload: (f) => ({ nome: f.nome, descricao: str(f.descricao) }),
};

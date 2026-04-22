// ── Mock Data for AeroSchool Application ──────────────────────────────────────

export const mockStudents = [
  { id: "1", name: "João Silva", email: "joao.silva@email.com", phone: "+351 912 345 678", nif: "123456789", birthdate: "1998-05-15", address: "Rua das Flores, 123, Lisboa", course: "PPL – Piloto Privado", courseId: "ppl", status: "active", enrollmentDate: "2024-01-15", progress: 68, flightHours: 35.5, theoreticalHours: 82, instructor: "Capt. António Ferreira", payments: "up_to_date", avatar: "JS", nationality: "Portuguesa" },
  { id: "2", name: "Ana Rodrigues", email: "ana.rodrigues@email.com", phone: "+351 963 456 789", nif: "234567890", birthdate: "2000-08-22", address: "Av. da República, 45, Porto", course: "CPL – Piloto Comercial", courseId: "cpl", status: "active", enrollmentDate: "2023-09-01", progress: 42, flightHours: 98.0, theoreticalHours: 156, instructor: "Capt. Margarida Lopes", payments: "pending", avatar: "AR", nationality: "Portuguesa" },
  { id: "3", name: "Carlos Mendes", email: "carlos.mendes@email.com", phone: "+351 934 567 890", nif: "345678901", birthdate: "1995-03-10", address: "Rua do Comércio, 78, Faro", course: "IR – Habilitação por Instrumentos", courseId: "ir", status: "suspended", enrollmentDate: "2023-06-01", progress: 85, flightHours: 52.0, theoreticalHours: 110, instructor: "Capt. António Ferreira", payments: "overdue", avatar: "CM", nationality: "Portuguesa" },
  { id: "4", name: "Marta Costa", email: "marta.costa@email.com", phone: "+351 911 678 901", nif: "456789012", birthdate: "2001-11-30", address: "Praça do Município, 12, Braga", course: "PPL – Piloto Privado", courseId: "ppl", status: "active", enrollmentDate: "2024-02-10", progress: 25, flightHours: 12.5, theoreticalHours: 35, instructor: "Capt. Margarida Lopes", payments: "up_to_date", avatar: "MC", nationality: "Portuguesa" },
  { id: "5", name: "Ricardo Almeida", email: "ricardo.almeida@email.com", phone: "+351 962 789 012", nif: "567890123", birthdate: "1992-07-14", address: "Rua Nova, 56, Coimbra", course: "ATPL – Transporte Aéreo", courseId: "atpl", status: "active", enrollmentDate: "2022-10-15", progress: 91, flightHours: 198.5, theoreticalHours: 650, instructor: "Capt. José Pereira", payments: "up_to_date", avatar: "RA", nationality: "Portuguesa" },
  { id: "6", name: "Sofia Ferreira", email: "sofia.ferreira@email.com", phone: "+351 913 890 123", nif: "678901234", birthdate: "1999-02-28", address: "Av. do Mar, 89, Setúbal", course: "CPL – Piloto Comercial", courseId: "cpl", status: "active", enrollmentDate: "2023-11-05", progress: 55, flightHours: 76.0, theoreticalHours: 120, instructor: "Capt. José Pereira", payments: "up_to_date", avatar: "SF", nationality: "Portuguesa" },
  { id: "7", name: "Miguel Oliveira", email: "miguel.oliveira@email.com", phone: "+351 964 901 234", nif: "789012345", birthdate: "1997-09-18", address: "Rua Central, 34, Évora", course: "PPL – Piloto Privado", courseId: "ppl", status: "completed", enrollmentDate: "2023-01-10", progress: 100, flightHours: 48.0, theoreticalHours: 95, instructor: "Capt. António Ferreira", payments: "up_to_date", avatar: "MO", nationality: "Portuguesa" },
  { id: "8", name: "Beatriz Santos", email: "beatriz.santos@email.com", phone: "+351 915 012 345", nif: "890123456", birthdate: "2002-04-05", address: "Praça da Sé, 7, Viseu", course: "PPL – Piloto Privado", courseId: "ppl", status: "active", enrollmentDate: "2024-03-01", progress: 10, flightHours: 5.0, theoreticalHours: 18, instructor: "Capt. Margarida Lopes", payments: "up_to_date", avatar: "BS", nationality: "Portuguesa" },
];

export const mockInstructors = [
  { id: "i1", name: "Capt. António Ferreira", license: "CPL/IR/FI", specialization: "PPL, IR", flightHours: 4500, students: 12, status: "active" },
  { id: "i2", name: "Capt. Margarida Lopes", license: "CPL/IR/FI/IRI", specialization: "PPL, CPL", flightHours: 3200, students: 8, status: "active" },
  { id: "i3", name: "Capt. José Pereira", license: "ATPL/FI/TRI", specialization: "CPL, ATPL", flightHours: 8700, students: 6, status: "active" },
];

export const mockAircraft = [
  { id: "a1", registration: "CS-AER", model: "Cessna 172 Skyhawk", type: "Single Engine", year: 2018, status: "operational", flightHours: 1248, lastMaintenance: "2025-02-10", nextMaintenance: "2025-04-10", location: "Hangar A", fuel: 85, notes: "Em excelentes condições" },
  { id: "a2", registration: "CS-FLY", model: "Piper PA-28 Cherokee", type: "Single Engine", year: 2016, status: "operational", flightHours: 2105, lastMaintenance: "2025-01-20", nextMaintenance: "2025-03-20", location: "Hangar A", fuel: 60, notes: "" },
  { id: "a3", registration: "CS-NAV", model: "Cessna 172 Skyhawk", type: "Single Engine", year: 2020, status: "maintenance", flightHours: 845, lastMaintenance: "2025-03-10", nextMaintenance: "2025-05-10", location: "Oficina", fuel: 0, notes: "Substituição motor – em manutenção" },
  { id: "a4", registration: "CS-IFR", model: "Diamond DA42 Twin Star", type: "Multi Engine", year: 2019, status: "operational", flightHours: 650, lastMaintenance: "2025-02-28", nextMaintenance: "2025-04-28", location: "Hangar B", fuel: 90, notes: "Avião multi-motor para treino IFR" },
  { id: "a5", registration: "CS-SIM", model: "Beechcraft Bonanza G36", type: "Single Engine", year: 2017, status: "grounded", flightHours: 3420, lastMaintenance: "2025-03-01", nextMaintenance: "2025-03-30", location: "Hangar B", fuel: 40, notes: "Aguarda peças de substituição do trem de aterragem" },
];

export const mockFlights = [
  { id: "f1", date: "2025-03-15", time: "09:00", duration: "1h30", student: "João Silva", studentId: "1", instructor: "Capt. António Ferreira", aircraft: "CS-AER", origin: "LPPT", destination: "LPPT", type: "Local", status: "completed", objectives: "Exercícios de coordenação, viragens", notes: "" },
  { id: "f2", date: "2025-03-15", time: "11:00", duration: "2h00", student: "Ana Rodrigues", studentId: "2", instructor: "Capt. Margarida Lopes", aircraft: "CS-FLY", origin: "LPPT", destination: "LPFR", type: "Navigation", status: "completed", objectives: "Navegação VFR cross-country", notes: "" },
  { id: "f3", date: "2025-03-16", time: "08:30", duration: "1h00", student: "Marta Costa", studentId: "4", instructor: "Capt. Margarida Lopes", aircraft: "CS-AER", origin: "LPPT", destination: "LPPT", type: "Local", status: "scheduled", objectives: "Descolagem e aterragem – circuito de tráfego", notes: "" },
  { id: "f4", date: "2025-03-16", time: "10:30", duration: "2h30", student: "Ricardo Almeida", studentId: "5", instructor: "Capt. José Pereira", aircraft: "CS-IFR", origin: "LPPT", destination: "LPPR", type: "IFR", status: "scheduled", objectives: "Procedimentos de aproximação ILS", notes: "" },
  { id: "f5", date: "2025-03-16", time: "14:00", duration: "1h30", student: "Sofia Ferreira", studentId: "6", instructor: "Capt. José Pereira", aircraft: "CS-FLY", origin: "LPPT", destination: "LPPT", type: "Local", status: "scheduled", objectives: "Manobras lentas e procedimentos de emergência", notes: "" },
  { id: "f6", date: "2025-03-17", time: "09:00", duration: "2h00", student: "João Silva", studentId: "1", instructor: "Capt. António Ferreira", aircraft: "CS-AER", origin: "LPPT", destination: "LPBJ", type: "Navigation", status: "scheduled", objectives: "Navegação VFR – rota LPPT-LPBJ", notes: "" },
  { id: "f7", date: "2025-03-17", time: "13:00", duration: "1h30", student: "Beatriz Santos", studentId: "8", instructor: "Capt. Margarida Lopes", aircraft: "CS-FLY", origin: "LPPT", destination: "LPPT", type: "Local", status: "scheduled", objectives: "Primeira aula de voo – familiarização", notes: "" },
  { id: "f8", date: "2025-03-14", time: "10:00", duration: "1h30", student: "João Silva", studentId: "1", instructor: "Capt. António Ferreira", aircraft: "CS-AER", origin: "LPPT", destination: "LPPT", type: "Local", status: "completed", objectives: "Controlo em baixa velocidade", notes: "Excelente desempenho" },
  { id: "f9", date: "2025-03-13", time: "09:30", duration: "2h00", student: "João Silva", studentId: "1", instructor: "Capt. António Ferreira", aircraft: "CS-AER", origin: "LPPT", destination: "LPCS", type: "Navigation", status: "completed", objectives: "Navegação VFR LPPT-LPCS", notes: "Navegação correta, pequenos desvios na rota" },
  { id: "f10", date: "2025-03-18", time: "09:00", duration: "1h30", student: "João Silva", studentId: "1", instructor: "Capt. António Ferreira", aircraft: "CS-AER", origin: "LPPT", destination: "LPPT", type: "Local", status: "scheduled", objectives: "Revisão procedimentos de emergência", notes: "" },
];

export const mockMaintenance = [
  { id: "m1", aircraft: "CS-NAV", type: "Major", description: "Substituição do motor Continental O-360 – verificação completa após TBO", technician: "Eng. Pedro Santos", startDate: "2025-03-10", estimatedEnd: "2025-03-25", status: "in_progress", priority: "high", cost: 18500 },
  { id: "m2", aircraft: "CS-SIM", type: "Unscheduled", description: "Substituição do trem de aterragem – avaria detetada em inspeção pré-voo", technician: "Eng. Pedro Santos", startDate: "2025-03-12", estimatedEnd: "2025-03-30", status: "waiting_parts", priority: "high", cost: 6800 },
  { id: "m3", aircraft: "CS-AER", type: "Scheduled 50h", description: "Inspeção de 50 horas – lubrificação, verificação filtros, baterias", technician: "Eng. Luísa Marques", startDate: "2025-02-10", estimatedEnd: "2025-02-12", status: "completed", priority: "medium", cost: 850 },
  { id: "m4", aircraft: "CS-FLY", type: "Scheduled 100h", description: "Inspeção de 100 horas – magnetos, carburador, parafusos do motor", technician: "Eng. Luísa Marques", startDate: "2025-01-20", estimatedEnd: "2025-01-23", status: "completed", priority: "medium", cost: 1650 },
  { id: "m5", aircraft: "CS-IFR", type: "Avionics", description: "Atualização base de dados Garmin G1000 – ciclo AIRAC", technician: "Eng. Pedro Santos", startDate: "2025-03-28", estimatedEnd: "2025-03-28", status: "scheduled", priority: "low", cost: 320 },
];

export const mockCourses = [
  {
    id: "ppl", name: "PPL – Piloto Privado", duration: "12 meses", flightHours: 45, theoreticalHours: 100, price: 8500, enrolled: 22, completed: 58,
    modules: [
      { id: "ppl-1", name: "Teoria do Voo", hours: 12, type: "theoretical", status: "active" },
      { id: "ppl-2", name: "Meteorologia", hours: 10, type: "theoretical", status: "active" },
      { id: "ppl-3", name: "Navegação", hours: 15, type: "theoretical", status: "active" },
      { id: "ppl-4", name: "Regulamentos Aéreos", hours: 8, type: "theoretical", status: "active" },
      { id: "ppl-5", name: "Comunicações Rádio", hours: 6, type: "theoretical", status: "active" },
      { id: "ppl-6", name: "Voo Básico – Controlo e Manobras", hours: 15, type: "practical", status: "active" },
      { id: "ppl-7", name: "Navegação VFR", hours: 20, type: "practical", status: "active" },
      { id: "ppl-8", name: "Voo Solo e Consolidação", hours: 10, type: "practical", status: "active" },
    ]
  },
  {
    id: "cpl", name: "CPL – Piloto Comercial", duration: "18 meses", flightHours: 200, theoreticalHours: 250, price: 28000, enrolled: 9, completed: 21,
    modules: [
      { id: "cpl-1", name: "Performance e Planeamento de Voo", hours: 20, type: "theoretical", status: "active" },
      { id: "cpl-2", name: "Sistemas da Aeronave", hours: 18, type: "theoretical", status: "active" },
      { id: "cpl-3", name: "Operações de Voo Avançadas", hours: 80, type: "practical", status: "active" },
      { id: "cpl-4", name: "CRM – Gestão de Recursos da Cabine", hours: 12, type: "practical", status: "active" },
    ]
  },
  {
    id: "ir", name: "IR – Habilitação por Instrumentos", duration: "6 meses", flightHours: 50, theoreticalHours: 150, price: 12000, enrolled: 5, completed: 12,
    modules: [
      { id: "ir-1", name: "Voo por Instrumentos – Fundamentos", hours: 30, type: "theoretical", status: "active" },
      { id: "ir-2", name: "Procedimentos de Aproximação ILS/VOR/RNAV", hours: 40, type: "practical", status: "active" },
      { id: "ir-3", name: "Simulador de Voo por Instrumentos", hours: 20, type: "simulator", status: "active" },
    ]
  },
  {
    id: "atpl", name: "ATPL – Transporte Aéreo", duration: "36 meses", flightHours: 500, theoreticalHours: 650, price: 75000, enrolled: 4, completed: 8,
    modules: [
      { id: "atpl-1", name: "Princípios de Voo Avançados", hours: 40, type: "theoretical", status: "active" },
      { id: "atpl-2", name: "Fatores Humanos na Aviação", hours: 18, type: "theoretical", status: "active" },
      { id: "atpl-3", name: "Multi-Crew Cooperation (MCC)", hours: 30, type: "simulator", status: "active" },
      { id: "atpl-4", name: "Type Rating Preparation", hours: 250, type: "practical", status: "active" },
    ]
  },
];

export const mockEvaluations = [
  { id: "e1", student: "João Silva", studentId: "1", course: "PPL", exam: "Meteorologia – Exame Teórico", date: "2025-02-20", score: 82, maxScore: 100, status: "passed", type: "theoretical" },
  { id: "e2", student: "João Silva", studentId: "1", course: "PPL", exam: "Teoria do Voo – Exame Teórico", date: "2025-01-15", score: 91, maxScore: 100, status: "passed", type: "theoretical" },
  { id: "e3", student: "João Silva", studentId: "1", course: "PPL", exam: "Avaliação Prática – Circuito de Tráfego", date: "2025-03-05", score: 78, maxScore: 100, status: "passed", type: "practical" },
  { id: "e4", student: "Ana Rodrigues", studentId: "2", course: "CPL", exam: "Performance e Planeamento – Exame Teórico", date: "2025-02-10", score: 88, maxScore: 100, status: "passed", type: "theoretical" },
  { id: "e5", student: "Carlos Mendes", studentId: "3", course: "IR", exam: "Voo por Instrumentos – Exame Final", date: "2025-03-01", score: 58, maxScore: 100, status: "failed", type: "practical" },
  { id: "e6", student: "Ricardo Almeida", studentId: "5", course: "ATPL", exam: "MCC – Avaliação Final Simulador", date: "2025-03-10", score: 95, maxScore: 100, status: "passed", type: "simulator" },
  { id: "e7", student: "Marta Costa", studentId: "4", course: "PPL", exam: "Regulamentos Aéreos – Exame Teórico", date: "2025-03-12", score: 74, maxScore: 100, status: "passed", type: "theoretical" },
  { id: "e8", student: "Sofia Ferreira", studentId: "6", course: "CPL", exam: "CRM – Avaliação", date: "2025-02-28", score: 85, maxScore: 100, status: "passed", type: "practical" },
];

export const mockPayments = [
  { id: "p1", student: "João Silva", studentId: "1", description: "Propina mensal – Fevereiro 2025", amount: 580, dueDate: "2025-02-28", paidDate: "2025-02-25", status: "paid", method: "Transferência" },
  { id: "p2", student: "João Silva", studentId: "1", description: "Propina mensal – Março 2025", amount: 580, dueDate: "2025-03-31", paidDate: null, status: "pending", method: null },
  { id: "p3", student: "Ana Rodrigues", studentId: "2", description: "Propina mensal – Março 2025", amount: 1200, dueDate: "2025-03-15", paidDate: null, status: "overdue", method: null },
  { id: "p4", student: "Carlos Mendes", studentId: "3", description: "Propina mensal – Janeiro 2025", amount: 800, dueDate: "2025-01-31", paidDate: null, status: "overdue", method: null },
  { id: "p5", student: "Marta Costa", studentId: "4", description: "Matrícula – PPL", amount: 500, dueDate: "2025-02-10", paidDate: "2025-02-10", status: "paid", method: "MB Way" },
  { id: "p6", student: "Ricardo Almeida", studentId: "5", description: "Propina mensal – Março 2025", amount: 2200, dueDate: "2025-03-31", paidDate: "2025-03-10", status: "paid", method: "Transferência" },
  { id: "p7", student: "Sofia Ferreira", studentId: "6", description: "Propina mensal – Março 2025", amount: 1200, dueDate: "2025-03-31", paidDate: "2025-03-14", status: "paid", method: "Cartão" },
  { id: "p8", student: "Beatriz Santos", studentId: "8", description: "Matrícula – PPL", amount: 500, dueDate: "2025-03-01", paidDate: "2025-03-01", status: "paid", method: "Transferência" },
];

export const mockChartData = {
  enrollments: [
    { month: "Set", alunos: 4 }, { month: "Out", alunos: 6 }, { month: "Nov", alunos: 5 },
    { month: "Dez", alunos: 3 }, { month: "Jan", alunos: 7 }, { month: "Fev", alunos: 8 },
    { month: "Mar", alunos: 5 },
  ],
  flightHoursPerMonth: [
    { month: "Set", horas: 120 }, { month: "Out", horas: 145 }, { month: "Nov", horas: 98 },
    { month: "Dez", horas: 72 }, { month: "Jan", horas: 155 }, { month: "Fev", horas: 178 },
    { month: "Mar", horas: 132 },
  ],
  aircraftStatus: [
    { name: "Operacional", value: 3, color: "#22c55e" },
    { name: "Manutenção", value: 1, color: "#f59e0b" },
    { name: "Imobilizado", value: 1, color: "#ef4444" },
  ],
  revenueData: [
    { month: "Set", receita: 18400, despesa: 9200 }, { month: "Out", receita: 22100, despesa: 11300 },
    { month: "Nov", receita: 19800, despesa: 10100 }, { month: "Dez", receita: 15200, despesa: 8900 },
    { month: "Jan", receita: 24500, despesa: 12800 }, { month: "Fev", receita: 27300, despesa: 13500 },
    { month: "Mar", receita: 21600, despesa: 11200 },
  ],
  courseDistribution: [
    { name: "PPL", value: 22, color: "#3b82f6" },
    { name: "CPL", value: 9, color: "#8b5cf6" },
    { name: "IR", value: 5, color: "#06b6d4" },
    { name: "ATPL", value: 4, color: "#f59e0b" },
  ],
};

export const mockDashboardMetrics = {
  totalStudents: 40,
  activeStudents: 35,
  scheduledFlights: 12,
  availableAircraft: 3,
  pendingPayments: 3,
  maintenanceOngoing: 2,
  monthlyRevenue: 21600,
  flightHoursThisMonth: 132,
};

// ─── Logged-in student (for FrontOffice) ──────────────────────────────────────
export const loggedStudent = {
  ...mockStudents[0],
  flightLog: [
    { date: "2025-03-15", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 1.5, type: "Local", objectives: "Exercícios de coordenação", grade: "B" },
    { date: "2025-03-14", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 1.5, type: "Local", objectives: "Controlo em baixa velocidade", grade: "A" },
    { date: "2025-03-13", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPCS", duration: 2.0, type: "Navigation", objectives: "Navegação VFR", grade: "B+" },
    { date: "2025-03-10", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 1.0, type: "Local", objectives: "Aterragens", grade: "A" },
    { date: "2025-03-06", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 1.5, type: "Local", objectives: "Viragens inclinadas", grade: "B" },
    { date: "2025-03-03", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 2.0, type: "Local", objectives: "Trabalho de motor", grade: "B+" },
    { date: "2025-02-27", aircraft: "CS-FLY", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPFR", duration: 2.5, type: "Navigation", objectives: "Rota LPPT-LPFR", grade: "A" },
    { date: "2025-02-24", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 1.5, type: "Local", objectives: "Circuito de tráfego", grade: "B+" },
    { date: "2025-02-20", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 1.0, type: "Local", objectives: "Introdução manobras avançadas", grade: "B" },
    { date: "2025-02-17", aircraft: "CS-AER", instructor: "Capt. António Ferreira", origin: "LPPT", destination: "LPPT", duration: 1.5, type: "Local", objectives: "Voo solo supervisionado", grade: "A" },
  ],
  upcomingFlights: [
    { date: "2025-03-16", time: "08:30", aircraft: "CS-AER", instructor: "Capt. António Ferreira", type: "Local", objectives: "Circuito de tráfego – descolagem e aterragem" },
    { date: "2025-03-17", time: "09:00", aircraft: "CS-AER", instructor: "Capt. António Ferreira", type: "Navigation", objectives: "Navegação VFR – rota LPPT-LPBJ" },
    { date: "2025-03-18", time: "09:00", aircraft: "CS-AER", instructor: "Capt. António Ferreira", type: "Local", objectives: "Revisão procedimentos de emergência" },
  ],
  schedule: [
    { day: "Segunda", date: "17 Mar", time: "08:30", subject: "Meteorologia Avançada", type: "theoretical", room: "Sala 3", status: "scheduled" },
    { day: "Terça", date: "18 Mar", time: "10:00", subject: "Regulamentos Aéreos – Cap. 4", type: "theoretical", room: "Sala 1", status: "scheduled" },
    { day: "Quarta", date: "19 Mar", time: "09:00", subject: "Simulador de Voo", type: "simulator", room: "Sim. A", status: "scheduled" },
    { day: "Quinta", date: "20 Mar", time: "14:00", subject: "Navegação – Exercícios", type: "theoretical", room: "Sala 2", status: "scheduled" },
    { day: "Sexta", date: "21 Mar", time: "08:00", subject: "Voo Local – CS-AER", type: "practical", room: "Pista", status: "scheduled" },
  ],
  documents: [
    { id: "d1", name: "Certificado de Matrícula", type: "certificate", date: "2024-01-15", status: "valid" },
    { id: "d2", name: "Certificado Médico Classe 2", type: "medical", date: "2024-08-20", status: "valid", expiry: "2026-08-20" },
    { id: "d3", name: "Cartão de Identificação de Estudante", type: "id", date: "2024-01-15", status: "valid" },
    { id: "d4", name: "Contrato de Formação", type: "contract", date: "2024-01-15", status: "valid" },
    { id: "d5", name: "Seguro de Acidentes Pessoais", type: "insurance", date: "2024-01-01", status: "valid", expiry: "2025-12-31" },
    { id: "d6", name: "Declaração de Frequência – Fev 2025", type: "declaration", date: "2025-02-28", status: "valid" },
  ],
  payments: [
    { id: "sp1", description: "Matrícula – PPL", amount: 1500, dueDate: "2024-01-15", paidDate: "2024-01-15", status: "paid" },
    { id: "sp2", description: "Propina – Semestre 1/2024", amount: 3500, dueDate: "2024-03-01", paidDate: "2024-02-28", status: "paid" },
    { id: "sp3", description: "Propina – Semestre 2/2024", amount: 3500, dueDate: "2024-09-01", paidDate: "2024-08-30", status: "paid" },
    { id: "sp4", description: "Propina – Março 2025", amount: 580, dueDate: "2025-03-31", paidDate: null, status: "pending" },
  ],
  evaluations: mockEvaluations.filter(e => e.studentId === "1"),
};

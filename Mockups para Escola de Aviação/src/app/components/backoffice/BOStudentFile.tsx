import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import { ArrowLeft, Phone, Mail, MapPin, Calendar, Plane, FileText, CreditCard, Edit2, Download, CheckCircle2 } from "lucide-react";
import { mockStudents, mockFlights, mockEvaluations, mockPayments } from "../../data/mockData";

const STATUS_MAP: Record<string, { label: string; bg: string; color: string }> = {
  active: { label: "Ativo", bg: "#DCFCE7", color: "#16A34A" },
  suspended: { label: "Suspenso", bg: "#FEF3C7", color: "#D97706" },
  completed: { label: "Concluído", bg: "#DBEAFE", color: "#1D4ED8" },
};

const TABS = ["Dados Pessoais", "Progresso do Curso", "Histórico de Voos", "Avaliações", "Pagamentos"];

export function BOStudentFile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [tab, setTab] = useState(0);

  const student = mockStudents.find(s => s.id === id) || mockStudents[0];
  const st = STATUS_MAP[student.status];
  const studentFlights = mockFlights.filter(f => f.studentId === student.id);
  const studentEvals = mockEvaluations.filter(e => e.studentId === student.id);
  const studentPayments = mockPayments.filter(p => p.studentId === student.id);

  const minHours = student.courseId === "ppl" ? 45 : student.courseId === "cpl" ? 200 : student.courseId === "ir" ? 50 : 500;

  return (
    <div className="p-8">
      {/* Back + Header */}
      <button onClick={() => navigate("/bo/students")} className="flex items-center gap-2 text-sm mb-6 hover:opacity-70 transition-opacity" style={{ color: "#64748B" }}>
        <ArrowLeft className="w-4 h-4" />
        Voltar à lista de alunos
      </button>

      {/* Student Header Card */}
      <div className="bg-white rounded-2xl p-6 mb-6 flex flex-col sm:flex-row items-start sm:items-center gap-5" style={{ border: "1px solid #E2E8F0" }}>
        <div className="w-20 h-20 rounded-2xl flex items-center justify-center text-2xl text-white flex-shrink-0" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)", fontWeight: 800 }}>
          {student.avatar}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex flex-wrap items-center gap-3 mb-2">
            <h1 className="text-2xl" style={{ color: "#0F2344", fontWeight: 800 }}>{student.name}</h1>
            <span className="px-3 py-1 rounded-full text-sm" style={{ background: st.bg, color: st.color, fontWeight: 600 }}>{st.label}</span>
          </div>
          <p className="text-sm mb-3" style={{ color: "#64748B" }}>{student.course} · Nº Aluno: {100 + parseInt(student.id)}</p>
          <div className="flex flex-wrap gap-4 text-sm" style={{ color: "#64748B" }}>
            <span className="flex items-center gap-1.5"><Mail className="w-4 h-4" />{student.email}</span>
            <span className="flex items-center gap-1.5"><Phone className="w-4 h-4" />{student.phone}</span>
          </div>
        </div>
        <div className="flex gap-2">
          <button className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm hover:bg-gray-50 transition-colors" style={{ border: "1.5px solid #E2E8F0", color: "#374151" }}>
            <Edit2 className="w-4 h-4" />
            Editar
          </button>
          <button className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm text-white" style={{ background: "#1565C0" }}>
            <Download className="w-4 h-4" />
            Exportar Ficha
          </button>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: "Horas de Voo", value: `${student.flightHours}h`, sub: `de ${minHours}h mínimas`, color: "#1565C0" },
          { label: "Progresso Geral", value: `${student.progress}%`, sub: "Progresso do curso", color: "#7C3AED" },
          { label: "Horas Teóricas", value: `${student.theoreticalHours}h`, sub: "Completadas", color: "#059669" },
          { label: "Matrícula", value: new Date(student.enrollmentDate).toLocaleDateString("pt-PT", { day: "numeric", month: "short", year: "numeric" }), sub: "Data de início", color: "#D97706" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="text-2xl mb-1" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{s.label}</div>
            <div className="text-xs" style={{ color: "#94A3B8" }}>{s.sub}</div>
          </div>
        ))}
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
        <div className="flex overflow-x-auto" style={{ borderBottom: "1px solid #E2E8F0" }}>
          {TABS.map((t, i) => (
            <button
              key={t}
              onClick={() => setTab(i)}
              className="px-6 py-4 text-sm whitespace-nowrap transition-colors"
              style={{
                color: tab === i ? "#1565C0" : "#64748B",
                fontWeight: tab === i ? 700 : 400,
                borderBottom: tab === i ? "2px solid #1565C0" : "2px solid transparent",
                background: "transparent",
              }}
            >
              {t}
            </button>
          ))}
        </div>

        <div className="p-6">
          {/* Tab 0: Personal Data */}
          {tab === 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <h3 className="text-sm mb-4 uppercase tracking-wider" style={{ color: "#94A3B8", fontWeight: 600 }}>Dados Pessoais</h3>
                <div className="space-y-4">
                  {[
                    { label: "Nome Completo", value: student.name },
                    { label: "Data de Nascimento", value: new Date(student.birthdate).toLocaleDateString("pt-PT") },
                    { label: "NIF", value: student.nif },
                    { label: "Nacionalidade", value: student.nationality },
                    { label: "Morada", value: student.address, icon: MapPin },
                    { label: "Telefone", value: student.phone, icon: Phone },
                    { label: "Email", value: student.email, icon: Mail },
                  ].map(f => (
                    <div key={f.label} className="flex items-start gap-3">
                      <div className="flex-1 min-w-0">
                        <div className="text-xs mb-0.5" style={{ color: "#94A3B8", fontWeight: 500 }}>{f.label}</div>
                        <div className="text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>{f.value}</div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
              <div>
                <h3 className="text-sm mb-4 uppercase tracking-wider" style={{ color: "#94A3B8", fontWeight: 600 }}>Informação Académica</h3>
                <div className="space-y-4">
                  {[
                    { label: "Curso", value: student.course },
                    { label: "Instrutor Responsável", value: student.instructor },
                    { label: "Estado", value: STATUS_MAP[student.status].label },
                    { label: "Data de Matrícula", value: new Date(student.enrollmentDate).toLocaleDateString("pt-PT") },
                  ].map(f => (
                    <div key={f.label}>
                      <div className="text-xs mb-0.5" style={{ color: "#94A3B8", fontWeight: 500 }}>{f.label}</div>
                      <div className="text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>{f.value}</div>
                    </div>
                  ))}
                </div>
                <div className="mt-6 p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                  <h4 className="text-sm mb-3" style={{ color: "#374151", fontWeight: 600 }}>Documentos</h4>
                  {["Certificado Médico Classe 2", "Bilhete de Identidade", "Contrato de Formação"].map(d => (
                    <div key={d} className="flex items-center justify-between py-2" style={{ borderBottom: "1px solid #E2E8F0" }}>
                      <div className="flex items-center gap-2 text-sm" style={{ color: "#374151" }}>
                        <FileText className="w-4 h-4" style={{ color: "#1565C0" }} />
                        {d}
                      </div>
                      <button><Download className="w-4 h-4" style={{ color: "#94A3B8" }} /></button>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {/* Tab 1: Progress */}
          {tab === 1 && (
            <div>
              <div className="mb-6">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm" style={{ color: "#374151", fontWeight: 600 }}>Progresso Geral do Curso</span>
                  <span className="text-sm" style={{ color: "#1565C0", fontWeight: 700 }}>{student.progress}%</span>
                </div>
                <div className="w-full h-3 rounded-full" style={{ background: "#E2E8F0" }}>
                  <div className="h-3 rounded-full transition-all" style={{ width: `${student.progress}%`, background: "linear-gradient(90deg, #1565C0, #42A5F5)" }} />
                </div>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {[
                  { label: "Horas de Voo", current: student.flightHours, total: minHours, color: "#1565C0" },
                  { label: "Horas Teóricas", current: student.theoreticalHours, total: 100, color: "#7C3AED" },
                  { label: "Módulos Concluídos", current: Math.round(student.progress / 12.5), total: 8, color: "#059669" },
                  { label: "Avaliações Aprovadas", current: studentEvals.filter(e => e.status === "passed").length, total: studentEvals.length || 5, color: "#D97706" },
                ].map(item => (
                  <div key={item.label} className="p-5 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                    <div className="flex justify-between mb-3">
                      <span className="text-sm" style={{ color: "#374151", fontWeight: 600 }}>{item.label}</span>
                      <span className="text-sm" style={{ color: item.color, fontWeight: 700 }}>{item.current} / {item.total}</span>
                    </div>
                    <div className="w-full h-2 rounded-full" style={{ background: "#E2E8F0" }}>
                      <div className="h-2 rounded-full" style={{ width: `${Math.min(100, (item.current / item.total) * 100)}%`, background: item.color }} />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Tab 2: Flight Log */}
          {tab === 2 && (
            <div>
              <table className="w-full">
                <thead>
                  <tr style={{ borderBottom: "1px solid #E2E8F0" }}>
                    {["Data", "Aeronave", "Tipo", "Origem → Destino", "Duração", "Objetivos", "Estado"].map(h => (
                      <th key={h} className="pb-3 text-left text-xs" style={{ color: "#94A3B8", fontWeight: 600, textTransform: "uppercase" }}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {studentFlights.map((f, i) => (
                    <tr key={f.id} style={{ borderBottom: i < studentFlights.length - 1 ? "1px solid #F1F5F9" : undefined }}>
                      <td className="py-3 text-sm" style={{ color: "#374151" }}>{new Date(f.date).toLocaleDateString("pt-PT")}</td>
                      <td className="py-3 text-sm font-mono" style={{ color: "#1565C0" }}>{f.aircraft}</td>
                      <td className="py-3 text-sm" style={{ color: "#374151" }}>{f.type}</td>
                      <td className="py-3 text-sm" style={{ color: "#374151" }}>{f.origin} → {f.destination}</td>
                      <td className="py-3 text-sm" style={{ color: "#374151" }}>{f.duration}</td>
                      <td className="py-3 text-sm" style={{ color: "#64748B" }}>{f.objectives}</td>
                      <td className="py-3">
                        <span className="px-2 py-0.5 rounded-full text-xs" style={{ background: f.status === "completed" ? "#DCFCE7" : "#DBEAFE", color: f.status === "completed" ? "#16A34A" : "#1D4ED8" }}>
                          {f.status === "completed" ? "Concluído" : "Agendado"}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Tab 3: Evaluations */}
          {tab === 3 && (
            <div className="space-y-3">
              {studentEvals.length > 0 ? studentEvals.map(e => (
                <div key={e.id} className="flex items-center justify-between p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full flex items-center justify-center" style={{ background: e.status === "passed" ? "#DCFCE7" : "#FEE2E2" }}>
                      <CheckCircle2 className="w-5 h-5" style={{ color: e.status === "passed" ? "#16A34A" : "#DC2626" }} />
                    </div>
                    <div>
                      <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{e.exam}</div>
                      <div className="text-xs" style={{ color: "#94A3B8" }}>{new Date(e.date).toLocaleDateString("pt-PT")} · {e.type === "theoretical" ? "Teórico" : e.type === "practical" ? "Prático" : "Simulador"}</div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-lg" style={{ color: e.score >= 75 ? "#16A34A" : "#DC2626", fontWeight: 800 }}>{e.score}/100</div>
                    <span className="text-xs px-2 py-0.5 rounded-full" style={{ background: e.status === "passed" ? "#DCFCE7" : "#FEE2E2", color: e.status === "passed" ? "#16A34A" : "#DC2626" }}>
                      {e.status === "passed" ? "Aprovado" : "Reprovado"}
                    </span>
                  </div>
                </div>
              )) : (
                <p className="text-center py-8" style={{ color: "#94A3B8" }}>Sem avaliações registadas.</p>
              )}
            </div>
          )}

          {/* Tab 4: Payments */}
          {tab === 4 && (
            <div>
              <div className="grid grid-cols-3 gap-4 mb-6">
                {[
                  { label: "Total Pago", value: `€${studentPayments.filter(p => p.status === "paid").reduce((acc, p) => acc + p.amount, 0).toLocaleString("pt-PT")}`, color: "#16A34A" },
                  { label: "Pendente", value: `€${studentPayments.filter(p => p.status === "pending").reduce((acc, p) => acc + p.amount, 0).toLocaleString("pt-PT")}`, color: "#D97706" },
                  { label: "Em Atraso", value: `€${studentPayments.filter(p => p.status === "overdue").reduce((acc, p) => acc + p.amount, 0).toLocaleString("pt-PT")}`, color: "#DC2626" },
                ].map(s => (
                  <div key={s.label} className="p-4 rounded-xl text-center" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                    <div className="text-2xl" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
                    <div className="text-xs" style={{ color: "#64748B" }}>{s.label}</div>
                  </div>
                ))}
              </div>
              <div className="space-y-2">
                {studentPayments.map(p => (
                  <div key={p.id} className="flex items-center justify-between p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                    <div>
                      <div className="text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>{p.description}</div>
                      <div className="text-xs" style={{ color: "#94A3B8" }}>Vencimento: {new Date(p.dueDate).toLocaleDateString("pt-PT")}</div>
                    </div>
                    <div className="flex items-center gap-4">
                      <span className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>€{p.amount.toLocaleString("pt-PT")}</span>
                      <span className="px-2.5 py-1 rounded-full text-xs" style={{
                        background: p.status === "paid" ? "#DCFCE7" : p.status === "pending" ? "#FEF3C7" : "#FEE2E2",
                        color: p.status === "paid" ? "#16A34A" : p.status === "pending" ? "#D97706" : "#DC2626",
                        fontWeight: 600,
                      }}>
                        {p.status === "paid" ? "Pago" : p.status === "pending" ? "Pendente" : "Em Atraso"}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

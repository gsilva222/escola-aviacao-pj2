import { useState } from "react";
import { useNavigate } from "react-router";
import { Search, Plus, Filter, ChevronRight, Download } from "lucide-react";
import { mockStudents } from "../../data/mockData";

const STATUS_MAP: Record<string, { label: string; bg: string; color: string }> = {
  active: { label: "Ativo", bg: "#DCFCE7", color: "#16A34A" },
  suspended: { label: "Suspenso", bg: "#FEF3C7", color: "#D97706" },
  completed: { label: "Concluído", bg: "#DBEAFE", color: "#1D4ED8" },
  inactive: { label: "Inativo", bg: "#F1F5F9", color: "#64748B" },
};

const PAYMENT_MAP: Record<string, { label: string; bg: string; color: string }> = {
  up_to_date: { label: "Regularizado", bg: "#DCFCE7", color: "#16A34A" },
  pending: { label: "Pendente", bg: "#FEF3C7", color: "#D97706" },
  overdue: { label: "Em Atraso", bg: "#FEE2E2", color: "#DC2626" },
};

export function BOStudents() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [courseFilter, setCourseFilter] = useState("all");
  const [statusFilter, setStatusFilter] = useState("all");

  const filtered = mockStudents.filter(s => {
    const matchSearch = s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.email.toLowerCase().includes(search.toLowerCase());
    const matchCourse = courseFilter === "all" || s.courseId === courseFilter;
    const matchStatus = statusFilter === "all" || s.status === statusFilter;
    return matchSearch && matchCourse && matchStatus;
  });

  return (
    <div className="p-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <p className="text-sm" style={{ color: "#64748B" }}>{mockStudents.length} alunos registados · {mockStudents.filter(s => s.status === "active").length} ativos</p>
        </div>
        <div className="flex gap-3">
          <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm transition-colors hover:bg-gray-50" style={{ border: "1.5px solid #E2E8F0", color: "#64748B" }}>
            <Download className="w-4 h-4" />
            Exportar
          </button>
          <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm text-white transition-all hover:opacity-90" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)" }}>
            <Plus className="w-4 h-4" />
            Novo Aluno
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-2xl p-4 mb-5 flex flex-wrap gap-3" style={{ border: "1px solid #E2E8F0" }}>
        <div className="relative flex-1 min-w-48">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Pesquisar por nome ou email..."
            className="w-full pl-9 pr-4 py-2 rounded-xl text-sm outline-none"
            style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#0F2344" }}
          />
        </div>
        <select
          value={courseFilter}
          onChange={e => setCourseFilter(e.target.value)}
          className="px-3 py-2 rounded-xl text-sm outline-none"
          style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#374151" }}
        >
          <option value="all">Todos os Cursos</option>
          <option value="ppl">PPL – Piloto Privado</option>
          <option value="cpl">CPL – Piloto Comercial</option>
          <option value="ir">IR – Instrumentos</option>
          <option value="atpl">ATPL – Transporte Aéreo</option>
        </select>
        <select
          value={statusFilter}
          onChange={e => setStatusFilter(e.target.value)}
          className="px-3 py-2 rounded-xl text-sm outline-none"
          style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#374151" }}
        >
          <option value="all">Todos os Estados</option>
          <option value="active">Ativo</option>
          <option value="suspended">Suspenso</option>
          <option value="completed">Concluído</option>
        </select>
        <button className="flex items-center gap-2 px-3 py-2 rounded-xl text-sm" style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#374151" }}>
          <Filter className="w-4 h-4" />
          Mais filtros
        </button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
        <table className="w-full">
          <thead>
            <tr style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0" }}>
              {["Aluno", "Curso", "Instrutor", "Progresso", "Horas Voo", "Pagamentos", "Estado", ""].map(h => (
                <th key={h} className="px-5 py-3.5 text-left text-xs" style={{ color: "#64748B", fontWeight: 600, letterSpacing: "0.04em", textTransform: "uppercase" }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.map((student, i) => {
              const st = STATUS_MAP[student.status];
              const pm = PAYMENT_MAP[student.payments];
              return (
                <tr
                  key={student.id}
                  onClick={() => navigate(`/bo/students/${student.id}`)}
                  className="cursor-pointer transition-colors hover:bg-blue-50/40"
                  style={{ borderBottom: i < filtered.length - 1 ? "1px solid #F1F5F9" : undefined }}
                >
                  <td className="px-5 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-full flex items-center justify-center text-sm text-white flex-shrink-0" style={{ background: "#1565C0", fontWeight: 700 }}>
                        {student.avatar}
                      </div>
                      <div>
                        <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{student.name}</div>
                        <div className="text-xs" style={{ color: "#94A3B8" }}>{student.email}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-5 py-4">
                    <div className="text-sm" style={{ color: "#374151", fontWeight: 500 }}>{student.course}</div>
                    <div className="text-xs" style={{ color: "#94A3B8" }}>Desde {new Date(student.enrollmentDate).toLocaleDateString("pt-PT", { month: "short", year: "numeric" })}</div>
                  </td>
                  <td className="px-5 py-4">
                    <div className="text-sm" style={{ color: "#374151" }}>{student.instructor}</div>
                  </td>
                  <td className="px-5 py-4">
                    <div className="flex items-center gap-2">
                      <div className="flex-1 h-1.5 rounded-full" style={{ background: "#E2E8F0", minWidth: 80 }}>
                        <div className="h-1.5 rounded-full" style={{ width: `${student.progress}%`, background: student.progress === 100 ? "#22C55E" : "#1565C0" }} />
                      </div>
                      <span className="text-xs" style={{ color: "#64748B", fontWeight: 500 }}>{student.progress}%</span>
                    </div>
                  </td>
                  <td className="px-5 py-4">
                    <span className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{student.flightHours}h</span>
                  </td>
                  <td className="px-5 py-4">
                    <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: pm.bg, color: pm.color, fontWeight: 600 }}>{pm.label}</span>
                  </td>
                  <td className="px-5 py-4">
                    <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: st.bg, color: st.color, fontWeight: 600 }}>{st.label}</span>
                  </td>
                  <td className="px-5 py-4">
                    <ChevronRight className="w-4 h-4" style={{ color: "#CBD5E1" }} />
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {filtered.length === 0 && (
          <div className="text-center py-12" style={{ color: "#94A3B8" }}>
            <p>Nenhum aluno encontrado.</p>
          </div>
        )}

        {/* Pagination */}
        <div className="flex items-center justify-between px-5 py-4" style={{ borderTop: "1px solid #F1F5F9" }}>
          <span className="text-xs" style={{ color: "#94A3B8" }}>A mostrar {filtered.length} de {mockStudents.length} alunos</span>
          <div className="flex gap-1">
            {[1, 2, 3].map(p => (
              <button key={p} className="w-8 h-8 rounded-lg text-sm transition-colors" style={{ background: p === 1 ? "#1565C0" : "transparent", color: p === 1 ? "white" : "#64748B", fontWeight: p === 1 ? 600 : 400 }}>
                {p}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

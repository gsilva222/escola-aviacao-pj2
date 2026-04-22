import { useState } from "react";
import { Search, Plus, CheckCircle2, XCircle, BarChart2 } from "lucide-react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from "recharts";
import { mockEvaluations } from "../../data/mockData";

const TYPE_MAP: Record<string, { label: string; bg: string; color: string }> = {
  theoretical: { label: "Teórico", bg: "#DBEAFE", color: "#1D4ED8" },
  practical: { label: "Prático", bg: "#DCFCE7", color: "#16A34A" },
  simulator: { label: "Simulador", bg: "#F3E8FF", color: "#7C3AED" },
};

const scoreData = [
  { range: "0-49", count: 0 }, { range: "50-59", count: 1 },
  { range: "60-69", count: 0 }, { range: "70-79", count: 2 },
  { range: "80-89", count: 3 }, { range: "90-100", count: 2 },
];

export function BOEvaluations() {
  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("all");
  const [statusFilter, setStatusFilter] = useState("all");

  const filtered = mockEvaluations.filter(e => {
    const ms = e.student.toLowerCase().includes(search.toLowerCase()) || e.exam.toLowerCase().includes(search.toLowerCase());
    const mt = typeFilter === "all" || e.type === typeFilter;
    const mst = statusFilter === "all" || e.status === statusFilter;
    return ms && mt && mst;
  });

  const passRate = Math.round((mockEvaluations.filter(e => e.status === "passed").length / mockEvaluations.length) * 100);
  const avgScore = Math.round(mockEvaluations.reduce((acc, e) => acc + e.score, 0) / mockEvaluations.length);

  return (
    <div className="p-8">
      {/* Stats Row */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: "Total de Avaliações", value: mockEvaluations.length, color: "#1565C0" },
          { label: "Taxa de Aprovação", value: `${passRate}%`, color: "#16A34A" },
          { label: "Nota Média", value: `${avgScore}/100`, color: "#7C3AED" },
          { label: "Reprovações", value: mockEvaluations.filter(e => e.status === "failed").length, color: "#DC2626" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="text-2xl mb-1" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
        {/* Chart */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-sm mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>Distribuição de Notas</h3>
          <ResponsiveContainer width="100%" height={160}>
            <BarChart data={scoreData} barSize={24}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" vertical={false} />
              <XAxis dataKey="range" tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} allowDecimals={false} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
              <Bar dataKey="count" name="Alunos" radius={[4, 4, 0, 0]}>
                {scoreData.map((entry, i) => (
                  <Cell key={i} fill={i < 2 ? "#EF4444" : i < 3 ? "#F59E0B" : "#22C55E"} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Quick stats */}
        <div className="lg:col-span-2 bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-sm mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>Avaliações Recentes</h3>
          <div className="space-y-3">
            {mockEvaluations.slice(0, 4).map(e => (
              <div key={e.id} className="flex items-center justify-between p-3 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full flex items-center justify-center" style={{ background: e.status === "passed" ? "#DCFCE7" : "#FEE2E2" }}>
                    {e.status === "passed"
                      ? <CheckCircle2 className="w-4 h-4" style={{ color: "#16A34A" }} />
                      : <XCircle className="w-4 h-4" style={{ color: "#DC2626" }} />}
                  </div>
                  <div>
                    <div className="text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>{e.student} · {e.exam}</div>
                    <div className="text-xs" style={{ color: "#94A3B8" }}>{new Date(e.date).toLocaleDateString("pt-PT")}</div>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className="text-base" style={{ color: e.score >= 75 ? "#16A34A" : "#DC2626", fontWeight: 800 }}>{e.score}</span>
                  <span className="px-2 py-0.5 text-xs rounded-full" style={TYPE_MAP[e.type]}>{TYPE_MAP[e.type].label}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-2xl p-4 mb-4 flex flex-wrap gap-3" style={{ border: "1px solid #E2E8F0" }}>
        <div className="relative flex-1 min-w-48">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Pesquisar aluno ou exame..."
            className="w-full pl-9 pr-4 py-2 rounded-xl text-sm outline-none"
            style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0" }}
          />
        </div>
        <select value={typeFilter} onChange={e => setTypeFilter(e.target.value)} className="px-3 py-2 rounded-xl text-sm outline-none" style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#374151" }}>
          <option value="all">Todos os Tipos</option>
          <option value="theoretical">Teórico</option>
          <option value="practical">Prático</option>
          <option value="simulator">Simulador</option>
        </select>
        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)} className="px-3 py-2 rounded-xl text-sm outline-none" style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#374151" }}>
          <option value="all">Todos os Resultados</option>
          <option value="passed">Aprovado</option>
          <option value="failed">Reprovado</option>
        </select>
        <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm text-white ml-auto" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)" }}>
          <Plus className="w-4 h-4" />
          Registar Avaliação
        </button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
        <table className="w-full">
          <thead>
            <tr style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0" }}>
              {["Aluno", "Exame / Avaliação", "Curso", "Tipo", "Data", "Nota", "Resultado"].map(h => (
                <th key={h} className="px-5 py-3.5 text-left text-xs uppercase tracking-wider" style={{ color: "#64748B", fontWeight: 600 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.map((e, i) => {
              const tp = TYPE_MAP[e.type];
              return (
                <tr key={e.id} className="hover:bg-blue-50/30 transition-colors cursor-pointer" style={{ borderBottom: i < filtered.length - 1 ? "1px solid #F1F5F9" : undefined }}>
                  <td className="px-5 py-4">
                    <div className="flex items-center gap-2">
                      <div className="w-7 h-7 rounded-full flex items-center justify-center text-xs text-white flex-shrink-0" style={{ background: "#1565C0", fontWeight: 700 }}>
                        {e.student.split(" ").map(n => n[0]).join("").slice(0, 2)}
                      </div>
                      <span className="text-sm" style={{ color: "#374151" }}>{e.student}</span>
                    </div>
                  </td>
                  <td className="px-5 py-4 text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>{e.exam}</td>
                  <td className="px-5 py-4 text-sm" style={{ color: "#64748B" }}>{e.course}</td>
                  <td className="px-5 py-4">
                    <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: tp.bg, color: tp.color, fontWeight: 600 }}>{tp.label}</span>
                  </td>
                  <td className="px-5 py-4 text-sm" style={{ color: "#64748B" }}>{new Date(e.date).toLocaleDateString("pt-PT")}</td>
                  <td className="px-5 py-4">
                    <div className="flex items-center gap-2">
                      <span className="text-base" style={{ color: e.score >= 75 ? "#16A34A" : "#DC2626", fontWeight: 800 }}>{e.score}</span>
                      <span className="text-xs" style={{ color: "#94A3B8" }}>/100</span>
                    </div>
                  </td>
                  <td className="px-5 py-4">
                    <span className="flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-full w-fit" style={{ background: e.status === "passed" ? "#DCFCE7" : "#FEE2E2", color: e.status === "passed" ? "#16A34A" : "#DC2626", fontWeight: 600 }}>
                      {e.status === "passed" ? <CheckCircle2 className="w-3 h-3" /> : <XCircle className="w-3 h-3" />}
                      {e.status === "passed" ? "Aprovado" : "Reprovado"}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

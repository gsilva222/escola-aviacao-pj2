import { useState } from "react";
import { Search, Download, TrendingUp, AlertCircle, CheckCircle2, Clock } from "lucide-react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";
import { mockPayments, mockChartData } from "../../data/mockData";

const STATUS_MAP: Record<string, { label: string; bg: string; color: string }> = {
  paid: { label: "Pago", bg: "#DCFCE7", color: "#16A34A" },
  pending: { label: "Pendente", bg: "#FEF3C7", color: "#D97706" },
  overdue: { label: "Em Atraso", bg: "#FEE2E2", color: "#DC2626" },
};

export function BOPayments() {
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");

  const filtered = mockPayments.filter(p => {
    const ms = p.student.toLowerCase().includes(search.toLowerCase()) || p.description.toLowerCase().includes(search.toLowerCase());
    const mst = statusFilter === "all" || p.status === statusFilter;
    return ms && mst;
  });

  const totalRevenue = mockPayments.filter(p => p.status === "paid").reduce((acc, p) => acc + p.amount, 0);
  const totalPending = mockPayments.filter(p => p.status === "pending").reduce((acc, p) => acc + p.amount, 0);
  const totalOverdue = mockPayments.filter(p => p.status === "overdue").reduce((acc, p) => acc + p.amount, 0);

  return (
    <div className="p-8">
      {/* Financial Summary */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { icon: TrendingUp, label: "Receita Recebida", value: `€${totalRevenue.toLocaleString("pt-PT")}`, sub: "Este mês", color: "#16A34A", bg: "#DCFCE7" },
          { icon: Clock, label: "Pendente", value: `€${totalPending.toLocaleString("pt-PT")}`, sub: `${mockPayments.filter(p => p.status === "pending").length} pagamentos`, color: "#D97706", bg: "#FEF3C7" },
          { icon: AlertCircle, label: "Em Atraso", value: `€${totalOverdue.toLocaleString("pt-PT")}`, sub: `${mockPayments.filter(p => p.status === "overdue").length} pagamentos`, color: "#DC2626", bg: "#FEE2E2" },
          { icon: CheckCircle2, label: "Taxa de Cobrança", value: "78%", sub: "Alunos em dia", color: "#1565C0", bg: "#DBEAFE" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="flex items-center gap-2 mb-3">
              <div className="w-9 h-9 rounded-xl flex items-center justify-center" style={{ background: s.bg }}>
                <s.icon className="w-4.5 h-4.5" style={{ color: s.color, width: 18, height: 18 }} />
              </div>
            </div>
            <div className="text-2xl mb-0.5" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#374151", fontWeight: 600 }}>{s.label}</div>
            <div className="text-xs" style={{ color: "#94A3B8" }}>{s.sub}</div>
          </div>
        ))}
      </div>

      {/* Revenue Chart */}
      <div className="bg-white rounded-2xl p-6 mb-6" style={{ border: "1px solid #E2E8F0" }}>
        <div className="flex items-center justify-between mb-5">
          <div>
            <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>Receita vs. Despesa</h3>
            <p className="text-xs" style={{ color: "#94A3B8" }}>Últimos 7 meses</p>
          </div>
          <button className="flex items-center gap-2 px-3 py-2 rounded-xl text-xs hover:bg-gray-50 transition-colors" style={{ border: "1.5px solid #E2E8F0", color: "#374151" }}>
            <Download className="w-3.5 h-3.5" />
            Exportar
          </button>
        </div>
        <ResponsiveContainer width="100%" height={220}>
          <BarChart data={mockChartData.revenueData} barGap={4} barSize={24}>
            <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" vertical={false} />
            <XAxis dataKey="month" tick={{ fontSize: 12, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
            <YAxis tick={{ fontSize: 12, fill: "#94A3B8" }} axisLine={false} tickLine={false} tickFormatter={v => `€${(v / 1000).toFixed(0)}k`} />
            <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} formatter={(v: any) => `€${v.toLocaleString("pt-PT")}`} />
            <Bar dataKey="receita" name="Receita" fill="#1565C0" radius={[4, 4, 0, 0]} />
            <Bar dataKey="despesa" name="Despesa" fill="#E2E8F0" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-2xl p-4 mb-4 flex flex-wrap gap-3" style={{ border: "1px solid #E2E8F0" }}>
        <div className="relative flex-1 min-w-48">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
          <input
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Pesquisar aluno ou descrição..."
            className="w-full pl-9 pr-4 py-2 rounded-xl text-sm outline-none"
            style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0" }}
          />
        </div>
        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)} className="px-3 py-2 rounded-xl text-sm outline-none" style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#374151" }}>
          <option value="all">Todos os Estados</option>
          <option value="paid">Pago</option>
          <option value="pending">Pendente</option>
          <option value="overdue">Em Atraso</option>
        </select>
      </div>

      {/* Payments Table */}
      <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
        <table className="w-full">
          <thead>
            <tr style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0" }}>
              {["Aluno", "Descrição", "Valor", "Vencimento", "Pagamento", "Método", "Estado", ""].map(h => (
                <th key={h} className="px-5 py-3.5 text-left text-xs uppercase tracking-wider" style={{ color: "#64748B", fontWeight: 600 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.map((p, i) => {
              const st = STATUS_MAP[p.status];
              return (
                <tr key={p.id} className="hover:bg-gray-50/50 cursor-pointer transition-colors" style={{ borderBottom: i < filtered.length - 1 ? "1px solid #F1F5F9" : undefined }}>
                  <td className="px-5 py-4">
                    <div className="flex items-center gap-2">
                      <div className="w-7 h-7 rounded-full flex items-center justify-center text-xs text-white" style={{ background: "#1565C0", fontWeight: 700 }}>
                        {p.student.split(" ").map(n => n[0]).join("").slice(0, 2)}
                      </div>
                      <span className="text-sm" style={{ color: "#374151" }}>{p.student}</span>
                    </div>
                  </td>
                  <td className="px-5 py-4 text-sm" style={{ color: "#374151" }}>{p.description}</td>
                  <td className="px-5 py-4">
                    <span className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>€{p.amount.toLocaleString("pt-PT")}</span>
                  </td>
                  <td className="px-5 py-4 text-sm" style={{ color: "#64748B" }}>{new Date(p.dueDate).toLocaleDateString("pt-PT")}</td>
                  <td className="px-5 py-4 text-sm" style={{ color: "#64748B" }}>{p.paidDate ? new Date(p.paidDate).toLocaleDateString("pt-PT") : "—"}</td>
                  <td className="px-5 py-4 text-sm" style={{ color: "#64748B" }}>{p.method || "—"}</td>
                  <td className="px-5 py-4">
                    <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: st.bg, color: st.color, fontWeight: 600 }}>{st.label}</span>
                  </td>
                  <td className="px-5 py-4">
                    {p.status !== "paid" && (
                      <button className="text-xs px-3 py-1.5 rounded-lg text-white" style={{ background: "#1565C0", fontWeight: 500 }}>
                        Registar
                      </button>
                    )}
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

import { Download, TrendingUp } from "lucide-react";
import {
  AreaChart, Area, BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend
} from "recharts";
import { mockChartData, mockDashboardMetrics } from "../../data/mockData";

const KPI_CARDS = [
  { label: "Total de Alunos", value: "40", sub: "+12% vs ano anterior", color: "#1565C0", bg: "#DBEAFE" },
  { label: "Horas Voadas (2025)", value: "900h", sub: "Meta: 1200h anuais", color: "#7C3AED", bg: "#F3E8FF" },
  { label: "Taxa de Aprovação", value: "87%", sub: "Exames e avaliações", color: "#16A34A", bg: "#DCFCE7" },
  { label: "Receita Acumulada", value: "€148k", sub: "Jan–Mar 2025", color: "#D97706", bg: "#FEF3C7" },
];

const passRateData = [
  { month: "Set", ppl: 88, cpl: 80, ir: 75 }, { month: "Out", ppl: 90, cpl: 85, ir: 70 },
  { month: "Nov", ppl: 85, cpl: 78, ir: 80 }, { month: "Dez", ppl: 92, cpl: 82, ir: 72 },
  { month: "Jan", ppl: 88, cpl: 88, ir: 85 }, { month: "Fev", ppl: 95, cpl: 90, ir: 88 },
  { month: "Mar", ppl: 87, cpl: 85, ir: 82 },
];

const instructorLoadData = [
  { name: "Capt. Ferreira", horas: 145, alunos: 12 },
  { name: "Capt. Lopes", horas: 118, alunos: 8 },
  { name: "Capt. Pereira", horas: 165, alunos: 6 },
];

export function BOReports() {
  return (
    <div className="p-8">
      {/* Header Controls */}
      <div className="flex items-center justify-between mb-6 flex-wrap gap-3">
        <div className="flex gap-2">
          {["Jan–Mar 2025", "2024", "Personalizado"].map(p => (
            <button key={p} className="px-4 py-2 rounded-xl text-sm transition-all" style={{ background: p === "Jan–Mar 2025" ? "#1565C0" : "white", color: p === "Jan–Mar 2025" ? "white" : "#64748B", border: "1.5px solid " + (p === "Jan–Mar 2025" ? "#1565C0" : "#E2E8F0") }}>
              {p}
            </button>
          ))}
        </div>
        <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm text-white" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)" }}>
          <Download className="w-4 h-4" />
          Exportar Relatório PDF
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        {KPI_CARDS.map(k => (
          <div key={k.label} className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
            <div className="w-10 h-10 rounded-xl flex items-center justify-center mb-4" style={{ background: k.bg }}>
              <TrendingUp className="w-5 h-5" style={{ color: k.color }} />
            </div>
            <div className="text-3xl mb-1" style={{ color: k.color, fontWeight: 800 }}>{k.value}</div>
            <div className="text-sm mb-1" style={{ color: "#0F2344", fontWeight: 600 }}>{k.label}</div>
            <div className="text-xs" style={{ color: "#94A3B8" }}>{k.sub}</div>
          </div>
        ))}
      </div>

      {/* Row 1: Revenue + Enrollments */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Receita Mensal</h3>
          <p className="text-xs mb-5" style={{ color: "#94A3B8" }}>Receitas vs. despesas operacionais</p>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={mockChartData.revenueData}>
              <defs>
                <linearGradient id="gRecita" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#1565C0" stopOpacity={0.15} />
                  <stop offset="95%" stopColor="#1565C0" stopOpacity={0} />
                </linearGradient>
                <linearGradient id="gDespesa" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#EF4444" stopOpacity={0.1} />
                  <stop offset="95%" stopColor="#EF4444" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
              <XAxis dataKey="month" tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} tickFormatter={v => `€${(v / 1000).toFixed(0)}k`} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} formatter={(v: any) => `€${v.toLocaleString("pt-PT")}`} />
              <Legend wrapperStyle={{ fontSize: 12 }} />
              <Area type="monotone" dataKey="receita" name="Receita" stroke="#1565C0" strokeWidth={2} fill="url(#gRecita)" />
              <Area type="monotone" dataKey="despesa" name="Despesa" stroke="#EF4444" strokeWidth={2} fill="url(#gDespesa)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Distribuição por Curso</h3>
          <p className="text-xs mb-5" style={{ color: "#94A3B8" }}>Alunos matriculados por tipo de licença</p>
          <div className="flex items-center gap-6">
            <ResponsiveContainer width="55%" height={180}>
              <PieChart>
                <Pie data={mockChartData.courseDistribution} cx="50%" cy="50%" outerRadius={75} paddingAngle={3} dataKey="value">
                  {mockChartData.courseDistribution.map((entry, i) => (
                    <Cell key={i} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
              </PieChart>
            </ResponsiveContainer>
            <div className="flex-1 space-y-3">
              {mockChartData.courseDistribution.map(d => (
                <div key={d.name}>
                  <div className="flex justify-between text-xs mb-1">
                    <span className="flex items-center gap-2">
                      <span className="w-2.5 h-2.5 rounded-full inline-block" style={{ background: d.color }} />
                      <span style={{ color: "#374151" }}>{d.name}</span>
                    </span>
                    <span style={{ color: d.color, fontWeight: 700 }}>{d.value}</span>
                  </div>
                  <div className="h-1 rounded-full" style={{ background: "#E2E8F0" }}>
                    <div className="h-1 rounded-full" style={{ width: `${(d.value / 40) * 100}%`, background: d.color }} />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Row 2: Pass Rate + Instructor Load */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Taxa de Aprovação por Curso</h3>
          <p className="text-xs mb-5" style={{ color: "#94A3B8" }}>% de aprovações em avaliações</p>
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={passRateData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
              <XAxis dataKey="month" tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis domain={[60, 100]} tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} tickFormatter={v => `${v}%`} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} formatter={(v: any) => `${v}%`} />
              <Legend wrapperStyle={{ fontSize: 12 }} />
              <Line type="monotone" dataKey="ppl" name="PPL" stroke="#1565C0" strokeWidth={2.5} dot={false} />
              <Line type="monotone" dataKey="cpl" name="CPL" stroke="#7C3AED" strokeWidth={2.5} dot={false} />
              <Line type="monotone" dataKey="ir" name="IR" stroke="#059669" strokeWidth={2.5} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Carga de Instrutores</h3>
          <p className="text-xs mb-5" style={{ color: "#94A3B8" }}>Horas e nº de alunos por instrutor</p>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={instructorLoadData} layout="vertical" barSize={20}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" horizontal={false} />
              <XAxis type="number" tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis dataKey="name" type="category" width={120} tick={{ fontSize: 11, fill: "#64748B" }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
              <Legend wrapperStyle={{ fontSize: 12 }} />
              <Bar dataKey="horas" name="Horas" fill="#1565C0" radius={[0, 4, 4, 0]} />
              <Bar dataKey="alunos" name="Alunos" fill="#42A5F5" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}

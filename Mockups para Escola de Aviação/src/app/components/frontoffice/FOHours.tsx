import { loggedStudent } from "../../data/mockData";
import {
  AreaChart, Area, BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer
} from "recharts";
import { Plane, TrendingUp, Clock, Navigation2 } from "lucide-react";

const monthlyData = [
  { month: "Out 24", local: 3.0, nav: 2.5 },
  { month: "Nov 24", local: 2.5, nav: 0 },
  { month: "Dez 24", local: 1.5, nav: 2.0 },
  { month: "Jan 25", local: 4.5, nav: 2.0 },
  { month: "Fev 25", local: 6.5, nav: 4.5 },
  { month: "Mar 25", local: 5.0, nav: 4.5 },
];

const cumulativeData = [
  { month: "Out 24", total: 5.5 },
  { month: "Nov 24", total: 8.0 },
  { month: "Dez 24", total: 11.5 },
  { month: "Jan 25", total: 18.0 },
  { month: "Fev 25", total: 29.0 },
  { month: "Mar 25", total: 35.5 },
];

export function FOHours() {
  const student = loggedStudent;
  const minRequired = 45;
  const remaining = Math.max(0, minRequired - student.flightHours);
  const pctComplete = Math.min(100, (student.flightHours / minRequired) * 100);

  const localTotal = student.flightLog.filter(f => f.type === "Local").reduce((a, f) => a + f.duration, 0);
  const navTotal = student.flightLog.filter(f => f.type === "Navigation").reduce((a, f) => a + f.duration, 0);
  const totalFlights = student.flightLog.length;

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Header Goal Card */}
      <div
        className="rounded-2xl p-6 mb-6 relative overflow-hidden"
        style={{ background: "linear-gradient(135deg, #0F2344, #1565C0)" }}
      >
        <div className="relative z-10">
          <p className="text-blue-300 text-sm mb-1">Progresso em Horas de Voo</p>
          <div className="flex items-end gap-4 mb-4">
            <div>
              <span className="text-white text-5xl" style={{ fontWeight: 800 }}>{student.flightHours}h</span>
              <span className="text-blue-300 text-lg ml-2">de {minRequired}h mínimas</span>
            </div>
            <div className="mb-1 px-3 py-1 rounded-full text-xs" style={{ background: "rgba(34,197,94,0.2)", color: "#86EFAC", border: "1px solid rgba(34,197,94,0.3)" }}>
              {pctComplete.toFixed(0)}% concluído
            </div>
          </div>
          <div className="h-3 rounded-full mb-2" style={{ background: "rgba(255,255,255,0.15)" }}>
            <div
              className="h-3 rounded-full"
              style={{ width: `${pctComplete}%`, background: "linear-gradient(90deg, #60A5FA, #34D399)" }}
            />
          </div>
          <p className="text-blue-300 text-sm">
            Faltam {remaining}h para completar os requisitos mínimos do curso
          </p>
        </div>
        <Plane className="absolute right-6 top-1/2 -translate-y-1/2 w-32 h-32 opacity-5 text-white" />
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { icon: Plane, label: "Total de Voos", value: totalFlights, unit: "", color: "#1565C0", bg: "#DBEAFE" },
          { icon: Clock, label: "Voos Locais", value: localTotal.toFixed(1), unit: "h", color: "#059669", bg: "#DCFCE7" },
          { icon: Navigation2, label: "Navegação", value: navTotal.toFixed(1), unit: "h", color: "#7C3AED", bg: "#F3E8FF" },
          { icon: TrendingUp, label: "Média Mensal", value: (student.flightHours / 6).toFixed(1), unit: "h", color: "#D97706", bg: "#FEF3C7" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="w-10 h-10 rounded-xl flex items-center justify-center mb-3" style={{ background: s.bg }}>
              <s.icon className="w-5 h-5" style={{ color: s.color }} />
            </div>
            <div className="text-2xl mb-0.5" style={{ color: s.color, fontWeight: 800 }}>{s.value}{s.unit}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 mb-6">
        {/* Cumulative Area */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Horas Acumuladas</h3>
          <p className="text-xs mb-5" style={{ color: "#94A3B8" }}>Evolução desde o início do curso</p>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={cumulativeData}>
              <defs>
                <linearGradient id="gradHoras" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#1565C0" stopOpacity={0.2} />
                  <stop offset="95%" stopColor="#1565C0" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
              <XAxis dataKey="month" tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <Tooltip
                contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }}
                formatter={(v: any) => [`${v}h`, "Horas acumuladas"]}
              />
              <Area type="monotone" dataKey="total" stroke="#1565C0" strokeWidth={2.5} fill="url(#gradHoras)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Monthly Bar */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Horas por Mês</h3>
          <p className="text-xs mb-5" style={{ color: "#94A3B8" }}>Local vs. Navegação</p>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={monthlyData} barGap={4} barSize={18}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" vertical={false} />
              <XAxis dataKey="month" tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
              <Bar dataKey="local" name="Local" fill="#1565C0" radius={[4, 4, 0, 0]} />
              <Bar dataKey="nav" name="Navegação" fill="#42A5F5" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Detailed Flight Log */}
      <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
        <div className="px-6 py-4" style={{ borderBottom: "1px solid #E2E8F0" }}>
          <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>Registo de Horas de Voo</h3>
          <p className="text-xs" style={{ color: "#94A3B8" }}>Detalhe completo de cada missão</p>
        </div>
        <table className="w-full">
          <thead>
            <tr style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0" }}>
              {["Data", "Aeronave", "Origem", "Destino", "Duração", "Tipo", "Acumulado"].map(h => (
                <th key={h} className="px-5 py-3.5 text-left text-xs uppercase tracking-wider" style={{ color: "#64748B", fontWeight: 600 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {(() => {
              let acc = 0;
              return [...student.flightLog].reverse().map((f, i) => {
                acc += f.duration;
                const cumulative = acc;
                return (
                  <tr key={i} style={{ borderBottom: i < student.flightLog.length - 1 ? "1px solid #F1F5F9" : undefined }}>
                    <td className="px-5 py-3.5 text-sm" style={{ color: "#374151" }}>
                      {new Date(f.date).toLocaleDateString("pt-PT", { day: "2-digit", month: "short" })}
                    </td>
                    <td className="px-5 py-3.5">
                      <span className="font-mono text-sm" style={{ color: "#1565C0", fontWeight: 700 }}>{f.aircraft}</span>
                    </td>
                    <td className="px-5 py-3.5 text-sm" style={{ color: "#374151" }}>{f.origin}</td>
                    <td className="px-5 py-3.5 text-sm" style={{ color: "#374151" }}>{f.destination}</td>
                    <td className="px-5 py-3.5">
                      <span className="text-sm" style={{ color: "#0F2344", fontWeight: 700 }}>{f.duration}h</span>
                    </td>
                    <td className="px-5 py-3.5">
                      <span
                        className="px-2 py-0.5 rounded-full text-xs"
                        style={{
                          background: f.type === "Local" ? "#DBEAFE" : "#DCFCE7",
                          color: f.type === "Local" ? "#1D4ED8" : "#16A34A",
                          fontWeight: 600,
                        }}
                      >
                        {f.type === "Local" ? "Local" : "Navegação"}
                      </span>
                    </td>
                    <td className="px-5 py-3.5 text-sm" style={{ color: "#64748B", fontWeight: 600 }}>{cumulative.toFixed(1)}h</td>
                  </tr>
                );
              });
            })()}
          </tbody>
        </table>
        <div className="px-5 py-4 flex items-center justify-between" style={{ borderTop: "1px solid #F1F5F9", background: "#F8FAFC" }}>
          <span className="text-sm" style={{ color: "#374151", fontWeight: 700 }}>Total: {student.flightHours}h</span>
          <span className="text-xs" style={{ color: "#94A3B8" }}>Meta PPL: {45}h · Progresso: {pctComplete.toFixed(0)}%</span>
        </div>
      </div>
    </div>
  );
}
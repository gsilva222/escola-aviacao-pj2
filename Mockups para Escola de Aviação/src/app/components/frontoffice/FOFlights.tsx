import { useState } from "react";
import { loggedStudent } from "../../data/mockData";
import { Plane, Clock, Search } from "lucide-react";

const TYPE_COLORS: Record<string, { bg: string; color: string; label: string }> = {
  Local: { bg: "#DBEAFE", color: "#1D4ED8", label: "Local" },
  Navigation: { bg: "#DCFCE7", color: "#16A34A", label: "Navegação" },
  IFR: { bg: "#F3E8FF", color: "#7C3AED", label: "IFR" },
};

const GRADE_COLORS: Record<string, { bg: string; color: string }> = {
  "A": { bg: "#DCFCE7", color: "#16A34A" },
  "A+": { bg: "#DCFCE7", color: "#16A34A" },
  "B+": { bg: "#DBEAFE", color: "#1D4ED8" },
  "B": { bg: "#DBEAFE", color: "#1D4ED8" },
  "C": { bg: "#FEF3C7", color: "#D97706" },
};

export function FOFlights() {
  const [tab, setTab] = useState<"upcoming" | "history">("upcoming");
  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState("all");

  const student = loggedStudent;

  const filteredHistory = student.flightLog.filter(f => {
    const ms = f.objectives.toLowerCase().includes(search.toLowerCase()) ||
      f.aircraft.toLowerCase().includes(search.toLowerCase());
    const mt = typeFilter === "all" || f.type === typeFilter;
    return ms && mt;
  });

  const totalHours = student.flightLog.reduce((acc, f) => acc + f.duration, 0);
  const localHours = student.flightLog.filter(f => f.type === "Local").reduce((acc, f) => acc + f.duration, 0);
  const navHours = student.flightLog.filter(f => f.type === "Navigation").reduce((acc, f) => acc + f.duration, 0);

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: "Total de Voos", value: student.flightLog.length + student.upcomingFlights.length, unit: "", color: "#1565C0", bg: "#DBEAFE" },
          { label: "Horas Totais", value: totalHours.toFixed(1), unit: "h", color: "#7C3AED", bg: "#F3E8FF" },
          { label: "Voos Locais", value: localHours.toFixed(1), unit: "h", color: "#059669", bg: "#DCFCE7" },
          { label: "Navegação", value: navHours.toFixed(1), unit: "h", color: "#D97706", bg: "#FEF3C7" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="text-2xl mb-1" style={{ color: s.color, fontWeight: 800 }}>{s.value}{s.unit}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-5 bg-white rounded-xl p-1.5" style={{ border: "1px solid #E2E8F0", width: "fit-content" }}>
        {(["upcoming", "history"] as const).map(t => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className="px-5 py-2 rounded-lg text-sm transition-all"
            style={{
              background: tab === t ? "#1565C0" : "transparent",
              color: tab === t ? "white" : "#64748B",
              fontWeight: tab === t ? 700 : 400,
            }}
          >
            {t === "upcoming" ? `Próximos Voos (${student.upcomingFlights.length})` : `Histórico (${student.flightLog.length})`}
          </button>
        ))}
      </div>

      {/* Upcoming Flights */}
      {tab === "upcoming" && (
        <div className="space-y-4">
          {student.upcomingFlights.map((f, i) => {
            const tc = TYPE_COLORS[f.type] || { bg: "#F1F5F9", color: "#64748B", label: f.type };
            return (
              <div key={i} className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
                <div className="flex items-start justify-between flex-wrap gap-4">
                  <div className="flex items-start gap-4">
                    <div className="w-14 h-14 rounded-2xl flex flex-col items-center justify-center flex-shrink-0" style={{ background: "#DBEAFE" }}>
                      <Plane className="w-6 h-6 mb-0.5" style={{ color: "#1D4ED8" }} />
                    </div>
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>{f.objectives}</h3>
                        <span className="px-2.5 py-0.5 rounded-full text-xs" style={{ background: tc.bg, color: tc.color, fontWeight: 600 }}>
                          {tc.label}
                        </span>
                      </div>
                      <div className="flex flex-wrap gap-4 text-sm" style={{ color: "#64748B" }}>
                        <span className="flex items-center gap-1.5">
                          <Clock className="w-4 h-4" />
                          {new Date(f.date).toLocaleDateString("pt-PT", { weekday: "long", day: "numeric", month: "long" })} às {f.time}
                        </span>
                        <span className="flex items-center gap-1.5 font-mono" style={{ color: "#1565C0", fontWeight: 600 }}>
                          {f.aircraft}
                        </span>
                        <span>{f.instructor}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <button className="px-4 py-2 rounded-xl text-sm hover:bg-gray-50 transition-colors" style={{ border: "1.5px solid #E2E8F0", color: "#64748B" }}>
                      Ver detalhes
                    </button>
                  </div>
                </div>
                <div className="mt-4 p-3 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                  <p className="text-xs" style={{ color: "#64748B" }}>
                    <span style={{ color: "#374151", fontWeight: 600 }}>Objetivos: </span>
                    {f.objectives}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Flight History */}
      {tab === "history" && (
        <>
          {/* Filters */}
          <div className="bg-white rounded-2xl p-4 mb-4 flex flex-wrap gap-3" style={{ border: "1px solid #E2E8F0" }}>
            <div className="relative flex-1 min-w-48">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Pesquisar voos..."
                className="w-full pl-9 pr-4 py-2 rounded-xl text-sm outline-none"
                style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#0F2344" }}
              />
            </div>
            <select
              value={typeFilter}
              onChange={e => setTypeFilter(e.target.value)}
              className="px-3 py-2 rounded-xl text-sm outline-none"
              style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#374151" }}
            >
              <option value="all">Todos os Tipos</option>
              <option value="Local">Local</option>
              <option value="Navigation">Navegação</option>
              <option value="IFR">IFR</option>
            </select>
          </div>

          {/* History Table */}
          <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
            <table className="w-full">
              <thead>
                <tr style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0" }}>
                  {["Data", "Aeronave", "Tipo", "Rota", "Duração", "Objetivos", "Nota"].map(h => (
                    <th key={h} className="px-5 py-3.5 text-left text-xs uppercase tracking-wider" style={{ color: "#64748B", fontWeight: 600 }}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filteredHistory.map((f, i) => {
                  const tc = TYPE_COLORS[f.type] || { bg: "#F1F5F9", color: "#64748B", label: f.type };
                  const gc = GRADE_COLORS[f.grade] || { bg: "#F1F5F9", color: "#64748B" };
                  return (
                    <tr
                      key={i}
                      className="hover:bg-blue-50/30 transition-colors cursor-pointer"
                      style={{ borderBottom: i < filteredHistory.length - 1 ? "1px solid #F1F5F9" : undefined }}
                    >
                      <td className="px-5 py-4 text-sm" style={{ color: "#374151", fontWeight: 500 }}>
                        {new Date(f.date).toLocaleDateString("pt-PT", { day: "2-digit", month: "short", year: "numeric" })}
                      </td>
                      <td className="px-5 py-4">
                        <span className="font-mono text-sm" style={{ color: "#1565C0", fontWeight: 700 }}>{f.aircraft}</span>
                      </td>
                      <td className="px-5 py-4">
                        <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: tc.bg, color: tc.color, fontWeight: 600 }}>{tc.label}</span>
                      </td>
                      <td className="px-5 py-4 text-sm" style={{ color: "#374151" }}>
                        {f.origin} → {f.destination}
                      </td>
                      <td className="px-5 py-4 text-sm" style={{ color: "#374151", fontWeight: 600 }}>
                        {f.duration}h
                      </td>
                      <td className="px-5 py-4 text-sm" style={{ color: "#64748B" }}>
                        {f.objectives}
                      </td>
                      <td className="px-5 py-4">
                        <span className="px-2.5 py-1 rounded-full text-sm" style={{ background: gc.bg, color: gc.color, fontWeight: 800 }}>
                          {f.grade}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}
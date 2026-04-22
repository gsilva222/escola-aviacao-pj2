import { useState } from "react";
import { Plus, ChevronLeft, ChevronRight, Filter, Plane } from "lucide-react";
import { mockFlights } from "../../data/mockData";

const DAYS = ["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"];
const DATES = [10, 11, 12, 13, 14, 15, 16];
const HOURS = ["08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"];

const STATUS_MAP: Record<string, { label: string; bg: string; color: string; border: string }> = {
  completed: { label: "Concluído", bg: "#DCFCE7", color: "#16A34A", border: "#BBF7D0" },
  scheduled: { label: "Agendado", bg: "#DBEAFE", color: "#1D4ED8", border: "#BFDBFE" },
  cancelled: { label: "Cancelado", bg: "#FEE2E2", color: "#DC2626", border: "#FECACA" },
};

const FLIGHT_COLORS = ["#1565C0", "#7C3AED", "#059669", "#D97706", "#DC2626"];

export function BOFlights() {
  const [view, setView] = useState<"calendar" | "list">("list");
  const [statusFilter, setStatusFilter] = useState("all");
  const [weekOffset, setWeekOffset] = useState(0);

  const filtered = mockFlights.filter(f => statusFilter === "all" || f.status === statusFilter);

  return (
    <div className="p-8">
      {/* Toolbar */}
      <div className="flex items-center justify-between mb-6 flex-wrap gap-3">
        <div className="flex items-center gap-2">
          <button onClick={() => setView("list")} className="px-4 py-2 rounded-xl text-sm transition-all" style={{ background: view === "list" ? "#1565C0" : "white", color: view === "list" ? "white" : "#64748B", border: "1.5px solid " + (view === "list" ? "#1565C0" : "#E2E8F0"), fontWeight: view === "list" ? 600 : 400 }}>
            Lista
          </button>
          <button onClick={() => setView("calendar")} className="px-4 py-2 rounded-xl text-sm transition-all" style={{ background: view === "calendar" ? "#1565C0" : "white", color: view === "calendar" ? "white" : "#64748B", border: "1.5px solid " + (view === "calendar" ? "#1565C0" : "#E2E8F0"), fontWeight: view === "calendar" ? 600 : 400 }}>
            Calendário
          </button>
        </div>
        <div className="flex gap-3 flex-wrap">
          <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)} className="px-3 py-2 rounded-xl text-sm outline-none" style={{ background: "white", border: "1.5px solid #E2E8F0", color: "#374151" }}>
            <option value="all">Todos os Estados</option>
            <option value="scheduled">Agendado</option>
            <option value="completed">Concluído</option>
            <option value="cancelled">Cancelado</option>
          </select>
          <button className="flex items-center gap-2 px-4 py-2 rounded-xl text-sm" style={{ background: "white", border: "1.5px solid #E2E8F0", color: "#374151" }}>
            <Filter className="w-4 h-4" />Filtros
          </button>
          <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm text-white" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)" }}>
            <Plus className="w-4 h-4" />
            Agendar Voo
          </button>
        </div>
      </div>

      {view === "list" ? (
        /* ── List View ──────────────────────────────────────────────────────── */
        <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
          <table className="w-full">
            <thead>
              <tr style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0" }}>
                {["Data & Hora", "Aluno", "Instrutor", "Aeronave", "Rota", "Tipo", "Duração", "Estado"].map(h => (
                  <th key={h} className="px-5 py-3.5 text-left text-xs uppercase tracking-wider" style={{ color: "#64748B", fontWeight: 600 }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.sort((a, b) => a.date.localeCompare(b.date)).map((f, i) => {
                const st = STATUS_MAP[f.status];
                return (
                  <tr key={f.id} className="hover:bg-blue-50/30 cursor-pointer transition-colors" style={{ borderBottom: i < filtered.length - 1 ? "1px solid #F1F5F9" : undefined }}>
                    <td className="px-5 py-4">
                      <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{new Date(f.date).toLocaleDateString("pt-PT", { day: "2-digit", month: "short" })}</div>
                      <div className="text-xs" style={{ color: "#94A3B8" }}>{f.time}</div>
                    </td>
                    <td className="px-5 py-4">
                      <div className="flex items-center gap-2">
                        <div className="w-7 h-7 rounded-full flex items-center justify-center text-xs text-white" style={{ background: "#1565C0", fontWeight: 700 }}>
                          {f.student.split(" ").map(n => n[0]).join("").slice(0, 2)}
                        </div>
                        <span className="text-sm" style={{ color: "#374151" }}>{f.student}</span>
                      </div>
                    </td>
                    <td className="px-5 py-4 text-sm" style={{ color: "#374151" }}>{f.instructor.replace("Capt. ", "")}</td>
                    <td className="px-5 py-4">
                      <span className="font-mono text-sm" style={{ color: "#1565C0", fontWeight: 600 }}>{f.aircraft}</span>
                    </td>
                    <td className="px-5 py-4 text-sm" style={{ color: "#374151" }}>{f.origin} → {f.destination}</td>
                    <td className="px-5 py-4">
                      <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: "#F0F9FF", color: "#0369A1", fontWeight: 600 }}>{f.type}</span>
                    </td>
                    <td className="px-5 py-4 text-sm" style={{ color: "#374151" }}>{f.duration}</td>
                    <td className="px-5 py-4">
                      <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: st.bg, color: st.color, fontWeight: 600 }}>{st.label}</span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      ) : (
        /* ── Calendar View ──────────────────────────────────────────────────── */
        <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
          {/* Week Navigator */}
          <div className="flex items-center justify-between px-6 py-4" style={{ borderBottom: "1px solid #E2E8F0" }}>
            <button onClick={() => setWeekOffset(w => w - 1)} className="p-2 rounded-lg hover:bg-gray-50 transition-colors">
              <ChevronLeft className="w-5 h-5" style={{ color: "#64748B" }} />
            </button>
            <div className="text-center">
              <h3 className="text-sm" style={{ color: "#0F2344", fontWeight: 700 }}>10 – 16 Março 2025</h3>
              <p className="text-xs" style={{ color: "#94A3B8" }}>Semana {10 + weekOffset}</p>
            </div>
            <button onClick={() => setWeekOffset(w => w + 1)} className="p-2 rounded-lg hover:bg-gray-50 transition-colors">
              <ChevronRight className="w-5 h-5" style={{ color: "#64748B" }} />
            </button>
          </div>

          {/* Grid */}
          <div className="overflow-x-auto">
            <div style={{ display: "grid", gridTemplateColumns: "64px repeat(7, 1fr)", minWidth: 900 }}>
              {/* Header Row */}
              <div style={{ background: "#F8FAFC", padding: "12px 8px", borderBottom: "1px solid #E2E8F0" }} />
              {DAYS.map((d, i) => (
                <div key={d} className="text-center py-3" style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0", borderLeft: "1px solid #E2E8F0" }}>
                  <div className="text-xs uppercase tracking-wider" style={{ color: "#94A3B8", fontWeight: 600 }}>{d}</div>
                  <div className="text-base mt-0.5" style={{ color: i === 5 ? "#1565C0" : "#0F2344", fontWeight: i === 5 ? 800 : 600 }}>{DATES[i]}</div>
                </div>
              ))}

              {/* Time Rows */}
              {HOURS.map(hour => (
                <>
                  <div key={`h-${hour}`} className="flex items-start justify-end pr-3 pt-2 text-xs" style={{ color: "#94A3B8", borderBottom: "1px solid #F1F5F9", minHeight: 72 }}>
                    {hour}
                  </div>
                  {DATES.map((date, di) => {
                    const dateStr = `2025-03-${String(date).padStart(2, "0")}`;
                    const flightsHere = mockFlights.filter(f => f.date === dateStr && f.time.startsWith(hour.slice(0, 2)));
                    return (
                      <div key={`${hour}-${di}`} className="relative" style={{ borderBottom: "1px solid #F1F5F9", borderLeft: "1px solid #F1F5F9", minHeight: 72, padding: "4px" }}>
                        {flightsHere.map((f, fi) => {
                          const st = STATUS_MAP[f.status];
                          return (
                            <div key={f.id} className="rounded-lg p-2 text-xs mb-1 cursor-pointer hover:opacity-80 transition-opacity" style={{ background: st.bg, border: `1px solid ${st.border}`, color: st.color }}>
                              <div className="flex items-center gap-1 mb-0.5">
                                <Plane className="w-3 h-3" />
                                <span style={{ fontWeight: 700 }}>{f.aircraft}</span>
                              </div>
                              <div className="truncate" style={{ fontSize: 10 }}>{f.student.split(" ")[0]}</div>
                            </div>
                          );
                        })}
                      </div>
                    );
                  })}
                </>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Today's Flights Summary */}
      <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
        {[
          { label: "Total Agendados", value: mockFlights.filter(f => f.status === "scheduled").length, color: "#1D4ED8", bg: "#DBEAFE" },
          { label: "Concluídos Hoje", value: mockFlights.filter(f => f.status === "completed" && f.date === "2025-03-15").length, color: "#16A34A", bg: "#DCFCE7" },
          { label: "Horas Voadas Hoje", value: "3.5h", color: "#7C3AED", bg: "#F3E8FF" },
        ].map(s => (
          <div key={s.label} className="p-5 rounded-2xl" style={{ background: "white", border: "1px solid #E2E8F0" }}>
            <div className="w-10 h-10 rounded-xl flex items-center justify-center mb-3" style={{ background: s.bg }}>
              <Plane className="w-5 h-5" style={{ color: s.color }} />
            </div>
            <div className="text-2xl" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

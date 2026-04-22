import { useState } from "react";
import { loggedStudent } from "../../data/mockData";
import { Clock, MapPin, ChevronLeft, ChevronRight } from "lucide-react";

const WEEKS = [
  {
    label: "17 – 21 Mar 2025",
    days: [
      { day: "Segunda", date: "17 Mar", items: [
        { time: "08:30", subject: "Meteorologia Avançada", type: "theoretical", room: "Sala 3", instructor: "Prof. Lima", duration: "2h" },
        { time: "14:00", subject: "Estudo Autónomo – Reg. Aéreos", type: "study", room: "Biblioteca", instructor: "", duration: "1h" },
      ]},
      { day: "Terça", date: "18 Mar", items: [
        { time: "10:00", subject: "Regulamentos Aéreos – Cap. 4", type: "theoretical", room: "Sala 1", instructor: "Prof. Costa", duration: "2h" },
      ]},
      { day: "Quarta", date: "19 Mar", items: [
        { time: "09:00", subject: "Simulador de Voo", type: "simulator", room: "Sim. A", instructor: "Capt. Ferreira", duration: "2h" },
        { time: "11:30", subject: "Briefing Pré-Voo", type: "briefing", room: "Sala Ops", instructor: "Capt. Ferreira", duration: "30min" },
      ]},
      { day: "Quinta", date: "20 Mar", items: [
        { time: "14:00", subject: "Navegação – Exercícios Práticos", type: "theoretical", room: "Sala 2", instructor: "Prof. Santos", duration: "2h" },
      ]},
      { day: "Sexta", date: "21 Mar", items: [
        { time: "08:00", subject: "Voo Local – CS-AER", type: "practical", room: "Pista", instructor: "Capt. Ferreira", duration: "1h30" },
        { time: "10:30", subject: "Debriefing Pós-Voo", type: "briefing", room: "Sala Ops", instructor: "Capt. Ferreira", duration: "30min" },
      ]},
    ]
  },
  {
    label: "24 – 28 Mar 2025",
    days: [
      { day: "Segunda", date: "24 Mar", items: [
        { time: "09:00", subject: "Fatores Humanos na Aviação", type: "theoretical", room: "Sala 1", instructor: "Prof. Lima", duration: "2h" },
      ]},
      { day: "Terça", date: "25 Mar", items: [] },
      { day: "Quarta", date: "26 Mar", items: [
        { time: "09:00", subject: "Simulador – ILS Approach", type: "simulator", room: "Sim. B", instructor: "Capt. Ferreira", duration: "2h" },
      ]},
      { day: "Quinta", date: "27 Mar", items: [
        { time: "10:00", subject: "Meteorologia – Exame Prático", type: "exam", room: "Sala 3", instructor: "Prof. Lima", duration: "2h" },
      ]},
      { day: "Sexta", date: "28 Mar", items: [
        { time: "08:30", subject: "Voo de Navegação LPPT-LPBJ", type: "practical", room: "Pista", instructor: "Capt. Ferreira", duration: "2h30" },
      ]},
    ]
  }
];

const TYPE_STYLES: Record<string, { bg: string; color: string; label: string; border: string }> = {
  theoretical: { bg: "#DBEAFE", color: "#1D4ED8", label: "Teórico", border: "#BFDBFE" },
  practical: { bg: "#DCFCE7", color: "#16A34A", label: "Prático", border: "#BBF7D0" },
  simulator: { bg: "#F3E8FF", color: "#7C3AED", label: "Simulador", border: "#DDD6FE" },
  briefing: { bg: "#FEF3C7", color: "#D97706", label: "Briefing", border: "#FDE68A" },
  exam: { bg: "#FEE2E2", color: "#DC2626", label: "Exame", border: "#FECACA" },
  study: { bg: "#F1F5F9", color: "#64748B", label: "Estudo", border: "#E2E8F0" },
};

export function FOSchedule() {
  const [weekIdx, setWeekIdx] = useState(0);
  const week = WEEKS[weekIdx];

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-xl mb-1" style={{ color: "#0F2344", fontWeight: 800 }}>Horário Semanal</h1>
          <p className="text-sm" style={{ color: "#64748B" }}>
            {loggedStudent.course} · Instrutor: {loggedStudent.instructor}
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => setWeekIdx(i => Math.max(0, i - 1))}
            disabled={weekIdx === 0}
            className="p-2 rounded-xl hover:bg-gray-100 transition-colors disabled:opacity-40"
            style={{ border: "1.5px solid #E2E8F0" }}
          >
            <ChevronLeft className="w-4 h-4" style={{ color: "#64748B" }} />
          </button>
          <span className="text-sm px-4 py-2 rounded-xl" style={{ background: "white", border: "1.5px solid #E2E8F0", color: "#0F2344", fontWeight: 600 }}>
            {week.label}
          </span>
          <button
            onClick={() => setWeekIdx(i => Math.min(WEEKS.length - 1, i + 1))}
            disabled={weekIdx === WEEKS.length - 1}
            className="p-2 rounded-xl hover:bg-gray-100 transition-colors disabled:opacity-40"
            style={{ border: "1.5px solid #E2E8F0" }}
          >
            <ChevronRight className="w-4 h-4" style={{ color: "#64748B" }} />
          </button>
        </div>
      </div>

      {/* Legend */}
      <div className="flex flex-wrap gap-3 mb-5">
        {Object.entries(TYPE_STYLES).map(([key, val]) => (
          <span key={key} className="flex items-center gap-1.5 text-xs px-3 py-1.5 rounded-full" style={{ background: val.bg, color: val.color, fontWeight: 600 }}>
            <span className="w-2 h-2 rounded-full" style={{ background: val.color }} />
            {val.label}
          </span>
        ))}
      </div>

      {/* Desktop Grid */}
      <div className="hidden md:grid gap-4" style={{ gridTemplateColumns: "repeat(5, 1fr)" }}>
        {week.days.map((day, di) => (
          <div key={di}>
            <div className="text-center py-3 rounded-t-xl mb-2" style={{ background: "white", border: "1px solid #E2E8F0" }}>
              <div className="text-xs uppercase tracking-wider" style={{ color: "#94A3B8", fontWeight: 600 }}>{day.day}</div>
              <div className="text-sm mt-0.5" style={{ color: "#0F2344", fontWeight: 700 }}>{day.date}</div>
            </div>
            <div className="space-y-2 min-h-48">
              {day.items.length === 0 ? (
                <div className="text-center py-6 rounded-xl" style={{ background: "white", border: "1px dashed #E2E8F0" }}>
                  <p className="text-xs" style={{ color: "#CBD5E1" }}>Sem aulas</p>
                </div>
              ) : (
                day.items.map((item, ii) => {
                  const ts = TYPE_STYLES[item.type] || TYPE_STYLES.study;
                  return (
                    <div key={ii} className="p-3 rounded-xl" style={{ background: ts.bg, border: `1px solid ${ts.border}` }}>
                      <div className="text-xs mb-1" style={{ color: ts.color, fontWeight: 700 }}>{ts.label}</div>
                      <div className="text-xs mb-2" style={{ color: "#0F2344", fontWeight: 600, lineHeight: 1.4 }}>{item.subject}</div>
                      <div className="flex items-center gap-1 text-xs" style={{ color: "#64748B" }}>
                        <Clock className="w-3 h-3" />
                        {item.time} · {item.duration}
                      </div>
                      {item.room && (
                        <div className="flex items-center gap-1 text-xs mt-1" style={{ color: "#64748B" }}>
                          <MapPin className="w-3 h-3" />
                          {item.room}
                        </div>
                      )}
                    </div>
                  );
                })
              )}
            </div>
          </div>
        ))}
      </div>

      {/* Mobile List */}
      <div className="md:hidden space-y-4">
        {week.days.map((day, di) => (
          <div key={di} className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
            <div className="px-4 py-3 flex items-center gap-3" style={{ background: "#F8FAFC", borderBottom: "1px solid #E2E8F0" }}>
              <div>
                <span className="text-sm" style={{ color: "#0F2344", fontWeight: 700 }}>{day.day}</span>
                <span className="text-xs ml-2" style={{ color: "#94A3B8" }}>{day.date}</span>
              </div>
            </div>
            {day.items.length === 0 ? (
              <div className="p-4 text-sm" style={{ color: "#94A3B8" }}>Sem aulas agendadas</div>
            ) : (
              <div className="p-3 space-y-2">
                {day.items.map((item, ii) => {
                  const ts = TYPE_STYLES[item.type] || TYPE_STYLES.study;
                  return (
                    <div key={ii} className="flex items-start gap-3 p-3 rounded-xl" style={{ background: ts.bg, border: `1px solid ${ts.border}` }}>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <span className="text-xs px-2 py-0.5 rounded-full" style={{ background: ts.color, color: "white", fontWeight: 600 }}>{ts.label}</span>
                        </div>
                        <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{item.subject}</div>
                        <div className="flex gap-3 mt-1 text-xs" style={{ color: "#64748B" }}>
                          <span>{item.time} · {item.duration}</span>
                          <span>{item.room}</span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

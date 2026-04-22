import { useNavigate } from "react-router";
import { loggedStudent } from "../../data/mockData";
import {
  Plane, Clock, CalendarDays, CheckCircle2, AlertCircle,
  ChevronRight, TrendingUp, BookOpen, Bell
} from "lucide-react";

const TYPE_COLORS: Record<string, { bg: string; color: string; label: string }> = {
  Local: { bg: "#DBEAFE", color: "#1D4ED8", label: "Local" },
  Navigation: { bg: "#DCFCE7", color: "#16A34A", label: "Navegação" },
  IFR: { bg: "#F3E8FF", color: "#7C3AED", label: "IFR" },
};

const SUBJECT_COLORS: Record<string, { bg: string; color: string }> = {
  theoretical: { bg: "#DBEAFE", color: "#1D4ED8" },
  practical: { bg: "#DCFCE7", color: "#16A34A" },
  simulator: { bg: "#F3E8FF", color: "#7C3AED" },
};

export function FODashboard() {
  const navigate = useNavigate();
  const student = loggedStudent;

  const notifications = [
    { type: "info", text: "Voo agendado para amanhã às 08:30 – CS-AER", time: "Hoje" },
    { type: "success", text: "Avaliação de Meteorologia aprovada com 82/100", time: "20 Fev" },
    { type: "warning", text: "Propina de Março com vencimento a 31/03/2025", time: "Pendente" },
  ];

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Welcome Banner */}
      <div
        className="rounded-2xl p-6 mb-7 relative overflow-hidden"
        style={{ background: "linear-gradient(135deg, #0F2344 0%, #1565C0 60%, #42A5F5 100%)" }}
      >
        <div className="relative z-10 flex items-start justify-between flex-wrap gap-4">
          <div>
            <p className="text-blue-300 text-sm mb-1">Olá de volta,</p>
            <h1 className="text-white text-2xl mb-1" style={{ fontWeight: 800 }}>
              {student.name.split(" ")[0]} {student.name.split(" ")[1]} ✈️
            </h1>
            <p className="text-blue-200 text-sm">
              {student.course} · Nº Aluno 100{student.id}
            </p>
          </div>
          <div className="flex gap-3">
            <div className="text-center px-5 py-3 rounded-xl" style={{ background: "rgba(255,255,255,0.12)" }}>
              <div className="text-white text-2xl" style={{ fontWeight: 800 }}>{student.flightHours}h</div>
              <div className="text-blue-300 text-xs">Horas de Voo</div>
            </div>
            <div className="text-center px-5 py-3 rounded-xl" style={{ background: "rgba(255,255,255,0.12)" }}>
              <div className="text-white text-2xl" style={{ fontWeight: 800 }}>{student.progress}%</div>
              <div className="text-blue-300 text-xs">Progresso</div>
            </div>
          </div>
        </div>
        {/* Decorative plane icon */}
        <Plane className="absolute right-6 bottom-4 w-24 h-24 opacity-5 text-white" />
      </div>

      {/* Progress Card */}
      <div className="bg-white rounded-2xl p-6 mb-5" style={{ border: "1px solid #E2E8F0" }}>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>Progresso do Curso</h2>
          <button
            onClick={() => navigate("/fo/flights")}
            className="text-xs flex items-center gap-1"
            style={{ color: "#1565C0", fontWeight: 600 }}
          >
            Ver detalhes <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-5">
          {[
            { label: "Horas de Voo", current: student.flightHours, total: 45, unit: "h", color: "#1565C0" },
            { label: "Horas Teóricas", current: student.theoreticalHours, total: 100, unit: "h", color: "#7C3AED" },
            { label: "Módulos", current: Math.round(student.progress / 12.5), total: 8, unit: "", color: "#059669" },
            { label: "Avaliações", current: student.evaluations.filter(e => e.status === "passed").length, total: student.evaluations.length, unit: "", color: "#D97706" },
          ].map(item => (
            <div key={item.label} className="p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
              <div className="flex justify-between items-end mb-2">
                <span className="text-xs" style={{ color: "#64748B" }}>{item.label}</span>
                <span className="text-xs" style={{ color: item.color, fontWeight: 700 }}>{item.current}{item.unit}/{item.total}{item.unit}</span>
              </div>
              <div className="h-2 rounded-full" style={{ background: "#E2E8F0" }}>
                <div
                  className="h-2 rounded-full transition-all"
                  style={{ width: `${Math.min(100, (item.current / item.total) * 100)}%`, background: item.color }}
                />
              </div>
            </div>
          ))}
        </div>
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm" style={{ color: "#374151", fontWeight: 600 }}>Progresso Geral</span>
          <span className="text-sm" style={{ color: "#1565C0", fontWeight: 700 }}>{student.progress}%</span>
        </div>
        <div className="h-3 rounded-full" style={{ background: "#E2E8F0" }}>
          <div
            className="h-3 rounded-full"
            style={{ width: `${student.progress}%`, background: "linear-gradient(90deg, #1565C0, #42A5F5)" }}
          />
        </div>
      </div>

      {/* Main Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 mb-5">
        {/* Upcoming Flights */}
        <div className="lg:col-span-2 bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>
              <Plane className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
              Próximos Voos
            </h2>
            <button onClick={() => navigate("/fo/flights")} className="text-xs flex items-center gap-1" style={{ color: "#1565C0", fontWeight: 600 }}>
              Ver todos <ChevronRight className="w-3.5 h-3.5" />
            </button>
          </div>
          <div className="space-y-3">
            {student.upcomingFlights.map((f, i) => {
              const tc = TYPE_COLORS[f.type] || { bg: "#F1F5F9", color: "#64748B", label: f.type };
              return (
                <div key={i} className="flex items-center gap-4 p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                  <div className="w-12 h-12 rounded-xl flex flex-col items-center justify-center flex-shrink-0" style={{ background: "#DBEAFE" }}>
                    <span className="text-xs" style={{ color: "#1565C0", fontWeight: 700 }}>
                      {new Date(f.date).toLocaleDateString("pt-PT", { day: "2-digit", month: "short" }).split(" ")[0]}
                    </span>
                    <span className="text-xs" style={{ color: "#1565C0" }}>
                      {new Date(f.date).toLocaleDateString("pt-PT", { month: "short" })}
                    </span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{f.objectives}</span>
                    </div>
                    <div className="flex items-center gap-3 text-xs" style={{ color: "#64748B" }}>
                      <span className="flex items-center gap-1"><Clock className="w-3 h-3" />{f.time}</span>
                      <span className="font-mono" style={{ color: "#1565C0", fontWeight: 600 }}>{f.aircraft}</span>
                      <span>{f.instructor.replace("Capt. ", "Inst. ")}</span>
                    </div>
                  </div>
                  <span className="px-2.5 py-1 rounded-full text-xs flex-shrink-0" style={{ background: tc.bg, color: tc.color, fontWeight: 600 }}>
                    {tc.label}
                  </span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Notifications */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h2 className="text-base mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>
            <Bell className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
            Notificações
          </h2>
          <div className="space-y-3">
            {notifications.map((n, i) => (
              <div key={i} className="flex items-start gap-3 p-3 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                <div className="w-7 h-7 rounded-lg flex items-center justify-center flex-shrink-0" style={{
                  background: n.type === "success" ? "#DCFCE7" : n.type === "warning" ? "#FEF3C7" : "#DBEAFE"
                }}>
                  {n.type === "success" && <CheckCircle2 className="w-4 h-4" style={{ color: "#16A34A" }} />}
                  {n.type === "warning" && <AlertCircle className="w-4 h-4" style={{ color: "#D97706" }} />}
                  {n.type === "info" && <Bell className="w-4 h-4" style={{ color: "#1D4ED8" }} />}
                </div>
                <div className="min-w-0 flex-1">
                  <p className="text-xs" style={{ color: "#374151", lineHeight: 1.5 }}>{n.text}</p>
                  <p className="text-xs mt-1" style={{ color: "#94A3B8" }}>{n.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Schedule This Week */}
      <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>
            <CalendarDays className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
            Agenda desta Semana
          </h2>
          <button onClick={() => navigate("/fo/schedule")} className="text-xs flex items-center gap-1" style={{ color: "#1565C0", fontWeight: 600 }}>
            Ver horário completo <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
          {student.schedule.map((s, i) => {
            const sc = SUBJECT_COLORS[s.type] || { bg: "#F1F5F9", color: "#64748B" };
            return (
              <div key={i} className="p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                <div className="flex justify-between items-center mb-2">
                  <span className="text-xs" style={{ color: "#94A3B8", fontWeight: 600 }}>{s.day}</span>
                  <span className="text-xs px-2 py-0.5 rounded-full" style={{ background: sc.bg, color: sc.color, fontWeight: 600 }}>
                    {s.type === "theoretical" ? "Teórico" : s.type === "practical" ? "Prático" : "Sim."}
                  </span>
                </div>
                <div className="text-sm mb-1" style={{ color: "#0F2344", fontWeight: 600, lineHeight: 1.3 }}>{s.subject}</div>
                <div className="flex items-center gap-1 text-xs" style={{ color: "#64748B" }}>
                  <Clock className="w-3 h-3" />{s.time} · {s.room}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

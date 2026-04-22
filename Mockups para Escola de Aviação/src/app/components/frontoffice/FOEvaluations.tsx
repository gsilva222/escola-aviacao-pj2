import { loggedStudent } from "../../data/mockData";
import { CheckCircle2, XCircle, Clock, BookOpen, Plane, Monitor } from "lucide-react";
import { RadarChart, Radar, PolarGrid, PolarAngleAxis, ResponsiveContainer, Tooltip } from "recharts";

const TYPE_MAP: Record<string, { label: string; bg: string; color: string; icon: any }> = {
  theoretical: { label: "Teórico", bg: "#DBEAFE", color: "#1D4ED8", icon: BookOpen },
  practical: { label: "Prático", bg: "#DCFCE7", color: "#16A34A", icon: Plane },
  simulator: { label: "Simulador", bg: "#F3E8FF", color: "#7C3AED", icon: Monitor },
};

const radarData = [
  { subject: "Meteorologia", A: 82 },
  { subject: "Teoria do Voo", A: 91 },
  { subject: "Regulamentos", A: 74 },
  { subject: "Navegação", A: 78 },
  { subject: "Comunicações", A: 85 },
  { subject: "Prático", A: 78 },
];

const upcomingExams = [
  { name: "Navegação – Exame Teórico", date: "28 Mar 2025", type: "theoretical", room: "Sala 2" },
  { name: "Avaliação Prática – Procedimentos de Emergência", date: "04 Abr 2025", type: "practical", room: "Pista / CS-AER" },
];

export function FOEvaluations() {
  const evals = loggedStudent.evaluations;
  const passed = evals.filter(e => e.status === "passed").length;
  const avgScore = evals.length > 0 ? Math.round(evals.reduce((a, e) => a + e.score, 0) / evals.length) : 0;
  const best = evals.length > 0 ? Math.max(...evals.map(e => e.score)) : 0;

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: "Avaliações Realizadas", value: evals.length, color: "#1565C0", bg: "#DBEAFE" },
          { label: "Aprovadas", value: passed, color: "#16A34A", bg: "#DCFCE7" },
          { label: "Nota Média", value: `${avgScore}/100`, color: "#7C3AED", bg: "#F3E8FF" },
          { label: "Melhor Nota", value: `${best}/100`, color: "#D97706", bg: "#FEF3C7" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="text-2xl mb-1" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 mb-6">
        {/* Radar Chart */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Desempenho por Área</h3>
          <p className="text-xs mb-3" style={{ color: "#94A3B8" }}>Pontuação em cada disciplina</p>
          <ResponsiveContainer width="100%" height={220}>
            <RadarChart data={radarData}>
              <PolarGrid stroke="#E2E8F0" />
              <PolarAngleAxis dataKey="subject" tick={{ fontSize: 10, fill: "#64748B" }} />
              <Radar name="Nota" dataKey="A" stroke="#1565C0" fill="#1565C0" fillOpacity={0.15} strokeWidth={2} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
            </RadarChart>
          </ResponsiveContainer>
        </div>

        {/* Upcoming Exams */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>Próximas Avaliações</h3>
          <div className="space-y-3">
            {upcomingExams.map((ex, i) => {
              const tp = TYPE_MAP[ex.type];
              const Icon = tp.icon;
              return (
                <div key={i} className="p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                  <div className="flex items-start gap-3">
                    <div className="w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0" style={{ background: tp.bg }}>
                      <Icon className="w-4 h-4" style={{ color: tp.color }} />
                    </div>
                    <div className="min-w-0">
                      <div className="text-sm mb-1" style={{ color: "#0F2344", fontWeight: 600, lineHeight: 1.3 }}>{ex.name}</div>
                      <div className="flex items-center gap-2 text-xs" style={{ color: "#64748B" }}>
                        <Clock className="w-3 h-3" />
                        {ex.date} · {ex.room}
                      </div>
                    </div>
                  </div>
                  <div className="mt-3">
                    <span className="px-2.5 py-1 rounded-full text-xs" style={{ background: tp.bg, color: tp.color, fontWeight: 600 }}>
                      {tp.label}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
          <div className="mt-4 p-3 rounded-xl" style={{ background: "#FFF7ED", border: "1px solid #FED7AA" }}>
            <p className="text-xs" style={{ color: "#92400E" }}>
              📌 Nota mínima de aprovação: <strong>75/100</strong> em todos os exames
            </p>
          </div>
        </div>

        {/* Score Distribution */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>Resultados</h3>
          <div className="space-y-4">
            {evals.map(e => (
              <div key={e.id}>
                <div className="flex justify-between items-center mb-1">
                  <span className="text-xs" style={{ color: "#374151", fontWeight: 500 }}>{e.exam.split("–")[0].trim()}</span>
                  <span className="text-sm" style={{ color: e.score >= 75 ? "#16A34A" : "#DC2626", fontWeight: 800 }}>{e.score}</span>
                </div>
                <div className="h-2 rounded-full" style={{ background: "#E2E8F0" }}>
                  <div
                    className="h-2 rounded-full"
                    style={{
                      width: `${e.score}%`,
                      background: e.score >= 90 ? "#22C55E" : e.score >= 75 ? "#1565C0" : "#EF4444"
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Evaluations List */}
      <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
        <div className="px-6 py-4" style={{ borderBottom: "1px solid #E2E8F0" }}>
          <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>Histórico de Avaliações</h3>
        </div>
        <div className="p-4 space-y-3">
          {evals.map(e => {
            const tp = TYPE_MAP[e.type] || TYPE_MAP.theoretical;
            const Icon = tp.icon;
            return (
              <div key={e.id} className="flex items-center justify-between p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                <div className="flex items-center gap-4">
                  <div
                    className="w-11 h-11 rounded-xl flex items-center justify-center flex-shrink-0"
                    style={{ background: e.status === "passed" ? "#DCFCE7" : "#FEE2E2" }}
                  >
                    {e.status === "passed"
                      ? <CheckCircle2 className="w-5 h-5" style={{ color: "#16A34A" }} />
                      : <XCircle className="w-5 h-5" style={{ color: "#DC2626" }} />
                    }
                  </div>
                  <div>
                    <div className="text-sm mb-0.5" style={{ color: "#0F2344", fontWeight: 600 }}>{e.exam}</div>
                    <div className="flex items-center gap-3 text-xs" style={{ color: "#94A3B8" }}>
                      <span>{new Date(e.date).toLocaleDateString("pt-PT", { day: "numeric", month: "long", year: "numeric" })}</span>
                      <span className="px-2 py-0.5 rounded-full" style={{ background: tp.bg, color: tp.color, fontWeight: 600 }}>{tp.label}</span>
                    </div>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-2xl mb-0.5" style={{ color: e.score >= 75 ? "#16A34A" : "#DC2626", fontWeight: 800 }}>{e.score}<span className="text-sm font-normal" style={{ color: "#94A3B8" }}>/100</span></div>
                  <div
                    className="text-xs px-2.5 py-1 rounded-full"
                    style={{
                      background: e.status === "passed" ? "#DCFCE7" : "#FEE2E2",
                      color: e.status === "passed" ? "#16A34A" : "#DC2626",
                      fontWeight: 600,
                    }}
                  >
                    {e.status === "passed" ? "Aprovado" : "Reprovado"}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

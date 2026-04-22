import { useState } from "react";
import { Wrench, Plus, AlertTriangle, CheckCircle2, Clock, Package } from "lucide-react";
import { mockMaintenance } from "../../data/mockData";

const STATUS_CONFIG: Record<string, { label: string; icon: any; bg: string; color: string }> = {
  in_progress: { label: "Em Curso", icon: Wrench, bg: "#DBEAFE", color: "#1D4ED8" },
  waiting_parts: { label: "Aguarda Peças", icon: Package, bg: "#FEF3C7", color: "#D97706" },
  completed: { label: "Concluído", icon: CheckCircle2, bg: "#DCFCE7", color: "#16A34A" },
  scheduled: { label: "Agendado", icon: Clock, bg: "#F3E8FF", color: "#7C3AED" },
};

const PRIORITY_MAP: Record<string, { label: string; bg: string; color: string }> = {
  high: { label: "Alta", bg: "#FEE2E2", color: "#DC2626" },
  medium: { label: "Média", bg: "#FEF3C7", color: "#D97706" },
  low: { label: "Baixa", bg: "#DCFCE7", color: "#16A34A" },
};

const TYPE_MAP: Record<string, { bg: string; color: string }> = {
  "Major": { bg: "#FEE2E2", color: "#DC2626" },
  "Scheduled 50h": { bg: "#DBEAFE", color: "#1D4ED8" },
  "Scheduled 100h": { bg: "#DBEAFE", color: "#1D4ED8" },
  "Unscheduled": { bg: "#FEF3C7", color: "#D97706" },
  "Avionics": { bg: "#F3E8FF", color: "#7C3AED" },
};

export function BOMaintenance() {
  const [selectedId, setSelectedId] = useState<string | null>("m1");
  const selected = mockMaintenance.find(m => m.id === selectedId);

  const ongoing = mockMaintenance.filter(m => m.status === "in_progress" || m.status === "waiting_parts");
  const total_cost = mockMaintenance.reduce((acc, m) => acc + m.cost, 0);

  return (
    <div className="p-8">
      {/* Summary */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: "Em Curso", value: mockMaintenance.filter(m => m.status === "in_progress").length, color: "#1D4ED8", bg: "#DBEAFE" },
          { label: "Aguarda Peças", value: mockMaintenance.filter(m => m.status === "waiting_parts").length, color: "#D97706", bg: "#FEF3C7" },
          { label: "Agendadas", value: mockMaintenance.filter(m => m.status === "scheduled").length, color: "#7C3AED", bg: "#F3E8FF" },
          { label: "Custo Total", value: `€${total_cost.toLocaleString("pt-PT")}`, color: "#374151", bg: "#F1F5F9" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="text-2xl mb-1" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>

      <div className="flex items-center justify-between mb-5">
        <p className="text-sm" style={{ color: "#64748B" }}>{mockMaintenance.length} intervenções registadas</p>
        <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm text-white" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)" }}>
          <Plus className="w-4 h-4" />
          Nova Intervenção
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-5">
        {/* List */}
        <div className="lg:col-span-2 space-y-3">
          {mockMaintenance.map(m => {
            const sc = STATUS_CONFIG[m.status];
            const pr = PRIORITY_MAP[m.priority];
            const Icon = sc.icon;
            const isSelected = selectedId === m.id;
            return (
              <button
                key={m.id}
                onClick={() => setSelectedId(m.id)}
                className="w-full text-left bg-white rounded-2xl p-4 transition-all hover:shadow-sm"
                style={{ border: isSelected ? "2px solid #1565C0" : "1.5px solid #E2E8F0" }}
              >
                <div className="flex items-start justify-between mb-2">
                  <span className="font-mono text-base" style={{ color: "#0F2344", fontWeight: 800 }}>{m.aircraft}</span>
                  <span className="flex items-center gap-1 px-2 py-0.5 rounded-full text-xs" style={{ background: sc.bg, color: sc.color, fontWeight: 600 }}>
                    <Icon className="w-3 h-3" />
                    {sc.label}
                  </span>
                </div>
                <p className="text-sm mb-3 line-clamp-2" style={{ color: "#374151" }}>{m.description}</p>
                <div className="flex items-center justify-between">
                  <div className="flex gap-2">
                    <span className="text-xs px-2 py-0.5 rounded-full" style={{ background: TYPE_MAP[m.type]?.bg || "#F1F5F9", color: TYPE_MAP[m.type]?.color || "#64748B", fontWeight: 600 }}>{m.type}</span>
                    <span className="text-xs px-2 py-0.5 rounded-full" style={{ background: pr.bg, color: pr.color, fontWeight: 600 }}>{pr.label}</span>
                  </div>
                  <span className="text-xs" style={{ color: "#94A3B8" }}>até {new Date(m.estimatedEnd).toLocaleDateString("pt-PT", { day: "2-digit", month: "short" })}</span>
                </div>
              </button>
            );
          })}
        </div>

        {/* Detail */}
        {selected && (
          <div className="lg:col-span-3 bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
            <div className="flex items-start justify-between mb-6">
              <div>
                <div className="flex items-center gap-3 mb-1">
                  <span className="font-mono text-2xl" style={{ color: "#0F2344", fontWeight: 800 }}>{selected.aircraft}</span>
                  <span className="px-3 py-1 rounded-full text-sm" style={{ background: STATUS_CONFIG[selected.status].bg, color: STATUS_CONFIG[selected.status].color, fontWeight: 700 }}>
                    {STATUS_CONFIG[selected.status].label}
                  </span>
                </div>
                <div className="flex gap-2">
                  <span className="text-xs px-2.5 py-1 rounded-full" style={{ background: TYPE_MAP[selected.type]?.bg || "#F1F5F9", color: TYPE_MAP[selected.type]?.color || "#64748B", fontWeight: 600 }}>{selected.type}</span>
                  <span className="text-xs px-2.5 py-1 rounded-full" style={{ background: PRIORITY_MAP[selected.priority].bg, color: PRIORITY_MAP[selected.priority].color, fontWeight: 600 }}>Prioridade {PRIORITY_MAP[selected.priority].label}</span>
                </div>
              </div>
              <div className="text-right">
                <div className="text-2xl" style={{ color: "#0F2344", fontWeight: 800 }}>€{selected.cost.toLocaleString("pt-PT")}</div>
                <div className="text-xs" style={{ color: "#94A3B8" }}>Custo estimado</div>
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <h4 className="text-xs uppercase tracking-wider mb-2" style={{ color: "#94A3B8", fontWeight: 600 }}>Descrição Técnica</h4>
                <p className="text-sm p-4 rounded-xl" style={{ color: "#374151", background: "#F8FAFC", border: "1px solid #E2E8F0", lineHeight: 1.7 }}>{selected.description}</p>
              </div>

              <div className="grid grid-cols-2 gap-4">
                {[
                  { label: "Técnico Responsável", value: selected.technician },
                  { label: "Data de Início", value: new Date(selected.startDate).toLocaleDateString("pt-PT") },
                  { label: "Conclusão Estimada", value: new Date(selected.estimatedEnd).toLocaleDateString("pt-PT") },
                  { label: "Duração", value: `${Math.ceil((new Date(selected.estimatedEnd).getTime() - new Date(selected.startDate).getTime()) / 86400000)} dias` },
                ].map(f => (
                  <div key={f.label} className="p-4 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                    <div className="text-xs mb-1" style={{ color: "#94A3B8" }}>{f.label}</div>
                    <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{f.value}</div>
                  </div>
                ))}
              </div>

              {selected.status === "in_progress" && (
                <div>
                  <div className="flex justify-between text-xs mb-2" style={{ color: "#64748B" }}>
                    <span>Progresso estimado</span>
                    <span style={{ fontWeight: 600 }}>60%</span>
                  </div>
                  <div className="h-2 rounded-full" style={{ background: "#E2E8F0" }}>
                    <div className="h-2 rounded-full" style={{ width: "60%", background: "linear-gradient(90deg, #1565C0, #42A5F5)" }} />
                  </div>
                </div>
              )}

              <div className="flex gap-3 mt-6">
                <button className="flex-1 py-2.5 rounded-xl text-sm hover:bg-gray-50 transition-colors" style={{ border: "1.5px solid #E2E8F0", color: "#374151", fontWeight: 500 }}>
                  Editar Intervenção
                </button>
                <button className="flex-1 py-2.5 rounded-xl text-sm text-white" style={{ background: "#1565C0", fontWeight: 500 }}>
                  Registar Progresso
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

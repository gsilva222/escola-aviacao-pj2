import { Gauge, Wrench, MapPin, Fuel, AlertTriangle, CheckCircle2, XCircle, Plus } from "lucide-react";
import { mockAircraft } from "../../data/mockData";

const STATUS_CONFIG: Record<string, { label: string; icon: any; bg: string; color: string; border: string }> = {
  operational: { label: "Operacional", icon: CheckCircle2, bg: "#DCFCE7", color: "#16A34A", border: "#BBF7D0" },
  maintenance: { label: "Em Manutenção", icon: Wrench, bg: "#FEF3C7", color: "#D97706", border: "#FDE68A" },
  grounded: { label: "Imobilizado", icon: XCircle, bg: "#FEE2E2", color: "#DC2626", border: "#FECACA" },
};

function FuelBar({ value }: { value: number }) {
  const color = value > 60 ? "#22C55E" : value > 30 ? "#F59E0B" : "#EF4444";
  return (
    <div>
      <div className="flex justify-between text-xs mb-1" style={{ color: "#64748B" }}>
        <span className="flex items-center gap-1"><Fuel className="w-3.5 h-3.5" />Combustível</span>
        <span style={{ color, fontWeight: 600 }}>{value}%</span>
      </div>
      <div className="h-1.5 rounded-full" style={{ background: "#E2E8F0" }}>
        <div className="h-1.5 rounded-full transition-all" style={{ width: `${value}%`, background: color }} />
      </div>
    </div>
  );
}

function HoursBar({ current, max = 2500 }: { current: number; max?: number }) {
  const pct = Math.min(100, (current / max) * 100);
  const color = pct > 80 ? "#EF4444" : pct > 60 ? "#F59E0B" : "#1565C0";
  return (
    <div>
      <div className="flex justify-between text-xs mb-1" style={{ color: "#64748B" }}>
        <span className="flex items-center gap-1"><Gauge className="w-3.5 h-3.5" />Horas TT</span>
        <span style={{ color, fontWeight: 600 }}>{current.toLocaleString("pt-PT")}h</span>
      </div>
      <div className="h-1.5 rounded-full" style={{ background: "#E2E8F0" }}>
        <div className="h-1.5 rounded-full transition-all" style={{ width: `${pct}%`, background: color }} />
      </div>
    </div>
  );
}

export function BOAircraft() {
  const stats = {
    operational: mockAircraft.filter(a => a.status === "operational").length,
    maintenance: mockAircraft.filter(a => a.status === "maintenance").length,
    grounded: mockAircraft.filter(a => a.status === "grounded").length,
  };

  return (
    <div className="p-8">
      {/* Summary Bar */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        {[
          { label: "Operacionais", value: stats.operational, ...STATUS_CONFIG.operational },
          { label: "Em Manutenção", value: stats.maintenance, ...STATUS_CONFIG.maintenance },
          { label: "Imobilizados", value: stats.grounded, ...STATUS_CONFIG.grounded },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5 flex items-center gap-4" style={{ border: `1.5px solid ${s.border}` }}>
            <div className="w-12 h-12 rounded-xl flex items-center justify-center" style={{ background: s.bg }}>
              <s.icon className="w-6 h-6" style={{ color: s.color }} />
            </div>
            <div>
              <div className="text-2xl" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
              <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
            </div>
          </div>
        ))}
      </div>

      <div className="flex items-center justify-between mb-6">
        <p className="text-sm" style={{ color: "#64748B" }}>{mockAircraft.length} aeronaves registadas na frota</p>
        <button className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm text-white" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)" }}>
          <Plus className="w-4 h-4" />
          Registar Aeronave
        </button>
      </div>

      {/* Aircraft Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-5">
        {mockAircraft.map(a => {
          const sc = STATUS_CONFIG[a.status];
          const Icon = sc.icon;
          const daysToMaintenance = Math.ceil((new Date(a.nextMaintenance).getTime() - new Date("2025-03-15").getTime()) / 86400000);
          return (
            <div key={a.id} className="bg-white rounded-2xl overflow-hidden transition-all hover:shadow-md" style={{ border: `1.5px solid ${sc.border}` }}>
              {/* Card Header */}
              <div className="p-5" style={{ background: `${sc.bg}60`, borderBottom: `1px solid ${sc.border}` }}>
                <div className="flex items-start justify-between mb-3">
                  <div>
                    <div className="font-mono text-xl" style={{ color: "#0F2344", fontWeight: 800, letterSpacing: "0.05em" }}>{a.registration}</div>
                    <div className="text-sm" style={{ color: "#64748B" }}>{a.model}</div>
                  </div>
                  <span className="flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs" style={{ background: sc.bg, color: sc.color, fontWeight: 700, border: `1px solid ${sc.border}` }}>
                    <Icon className="w-3.5 h-3.5" />
                    {sc.label}
                  </span>
                </div>
                <div className="flex gap-4 text-xs" style={{ color: "#64748B" }}>
                  <span className="flex items-center gap-1"><MapPin className="w-3.5 h-3.5" />{a.location}</span>
                  <span>Ano: {a.year}</span>
                  <span>{a.type}</span>
                </div>
              </div>

              {/* Card Body */}
              <div className="p-5 space-y-4">
                <HoursBar current={a.flightHours} />
                {a.status === "operational" && <FuelBar value={a.fuel} />}

                {/* Maintenance Dates */}
                <div className="grid grid-cols-2 gap-3 text-xs">
                  <div className="p-3 rounded-xl" style={{ background: "#F8FAFC", border: "1px solid #E2E8F0" }}>
                    <div style={{ color: "#94A3B8", marginBottom: 2 }}>Última Manutenção</div>
                    <div style={{ color: "#374151", fontWeight: 600 }}>{new Date(a.lastMaintenance).toLocaleDateString("pt-PT")}</div>
                  </div>
                  <div className="p-3 rounded-xl" style={{ background: daysToMaintenance <= 30 ? "#FEF3C7" : "#F8FAFC", border: `1px solid ${daysToMaintenance <= 30 ? "#FDE68A" : "#E2E8F0"}` }}>
                    <div style={{ color: "#94A3B8", marginBottom: 2 }}>Próxima Manutenção</div>
                    <div style={{ color: daysToMaintenance <= 30 ? "#D97706" : "#374151", fontWeight: 600 }}>
                      {new Date(a.nextMaintenance).toLocaleDateString("pt-PT")}
                      {daysToMaintenance <= 30 && <span className="ml-1">({daysToMaintenance}d)</span>}
                    </div>
                  </div>
                </div>

                {a.notes && (
                  <div className="flex items-start gap-2 p-3 rounded-xl text-xs" style={{ background: "#FFF7ED", border: "1px solid #FED7AA" }}>
                    <AlertTriangle className="w-3.5 h-3.5 mt-0.5 flex-shrink-0" style={{ color: "#D97706" }} />
                    <span style={{ color: "#92400E" }}>{a.notes}</span>
                  </div>
                )}

                {/* Actions */}
                <div className="flex gap-2 pt-1">
                  <button className="flex-1 py-2 rounded-xl text-xs text-center transition-colors hover:bg-blue-50" style={{ border: "1.5px solid #E2E8F0", color: "#374151", fontWeight: 500 }}>
                    Ver Histórico
                  </button>
                  <button className="flex-1 py-2 rounded-xl text-xs text-center text-white" style={{ background: "#1565C0", fontWeight: 500 }}>
                    Agendar Manutenção
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

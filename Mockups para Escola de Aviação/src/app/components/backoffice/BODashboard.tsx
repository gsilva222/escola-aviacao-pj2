import { useNavigate } from "react-router";
import {
  AreaChart, Area, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend
} from "recharts";
import {
  Users, Plane, Wrench, CreditCard, CalendarClock,
  TrendingUp, ArrowRight, CheckCircle2, Clock, AlertCircle
} from "lucide-react";
import { mockDashboardMetrics, mockChartData, mockFlights, mockMaintenance } from "../../data/mockData";

const COLORS = { green: "#22C55E", yellow: "#F59E0B", red: "#EF4444", blue: "#3B82F6" };

function StatCard({ icon: Icon, label, value, sub, color, onClick }: any) {
  return (
    <button onClick={onClick} className="bg-white rounded-2xl p-6 text-left transition-all hover:-translate-y-0.5 hover:shadow-md w-full" style={{ border: "1px solid #E2E8F0" }}>
      <div className="flex items-start justify-between mb-4">
        <div className="w-12 h-12 rounded-xl flex items-center justify-center" style={{ background: `${color}18` }}>
          <Icon className="w-6 h-6" style={{ color }} />
        </div>
        <ArrowRight className="w-4 h-4 mt-1" style={{ color: "#CBD5E1" }} />
      </div>
      <div className="text-3xl mb-1" style={{ color: "#0F2344", fontWeight: 800 }}>{value}</div>
      <div className="text-sm" style={{ color: "#64748B", fontWeight: 600 }}>{label}</div>
      {sub && <div className="text-xs mt-1" style={{ color: "#94A3B8" }}>{sub}</div>}
    </button>
  );
}

export function BODashboard() {
  const navigate = useNavigate();
  const m = mockDashboardMetrics;
  const todayFlights = mockFlights.filter(f => f.date === "2025-03-15" || f.date === "2025-03-16");
  const ongoing = mockMaintenance.filter(m => m.status === "in_progress" || m.status === "waiting_parts");

  return (
    <div className="p-8">
      {/* Welcome banner */}
      <div className="rounded-2xl p-6 mb-8 flex items-center justify-between" style={{ background: "linear-gradient(135deg, #0F2344 0%, #1565C0 100%)" }}>
        <div>
          <h2 className="text-white text-xl mb-1" style={{ fontWeight: 700 }}>Bom dia, Administrador! ✈️</h2>
          <p className="text-blue-200 text-sm">Hoje é sábado, 15 de Março de 2025 · {todayFlights.length} voos agendados para hoje</p>
        </div>
        <div className="hidden md:flex gap-4">
          <div className="text-center px-6 py-3 rounded-xl" style={{ background: "rgba(255,255,255,0.1)" }}>
            <div className="text-white text-2xl" style={{ fontWeight: 800 }}>{m.flightHoursThisMonth}h</div>
            <div className="text-blue-300 text-xs">Horas este mês</div>
          </div>
          <div className="text-center px-6 py-3 rounded-xl" style={{ background: "rgba(255,255,255,0.1)" }}>
            <div className="text-white text-2xl" style={{ fontWeight: 800 }}>€{(m.monthlyRevenue / 1000).toFixed(1)}k</div>
            <div className="text-blue-300 text-xs">Receita mensal</div>
          </div>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 mb-8">
        <StatCard icon={Users} label="Alunos Ativos" value={m.activeStudents} sub={`de ${m.totalStudents} matriculados`} color="#1565C0" onClick={() => navigate("/bo/students")} />
        <StatCard icon={CalendarClock} label="Voos Hoje" value={m.scheduledFlights} sub="3 completados, 9 agendados" color="#7C3AED" onClick={() => navigate("/bo/flights")} />
        <StatCard icon={Plane} label="Aeronaves Disponíveis" value={m.availableAircraft} sub="de 5 no total" color="#059669" onClick={() => navigate("/bo/aircraft")} />
        <StatCard icon={CreditCard} label="Pagamentos Pendentes" value={m.pendingPayments} sub="Valor: €2.980" color="#F59E0B" onClick={() => navigate("/bo/payments")} />
        <StatCard icon={Wrench} label="Manutenções em Curso" value={m.maintenanceOngoing} sub="1 aguarda peças" color="#EF4444" onClick={() => navigate("/bo/maintenance")} />
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
        {/* Enrollments Area Chart */}
        <div className="lg:col-span-2 bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>Matrículas por Mês</h3>
              <p className="text-xs" style={{ color: "#94A3B8" }}>Últimos 7 meses</p>
            </div>
            <div className="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs" style={{ background: "#DCFCE7", color: "#16A34A" }}>
              <TrendingUp className="w-3.5 h-3.5" />
              <span>+18% vs ano anterior</span>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={mockChartData.enrollments}>
              <defs>
                <linearGradient id="colorAlunos" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#1565C0" stopOpacity={0.15} />
                  <stop offset="95%" stopColor="#1565C0" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" />
              <XAxis dataKey="month" tick={{ fontSize: 12, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 12, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
              <Area type="monotone" dataKey="alunos" name="Matrículas" stroke="#1565C0" strokeWidth={2.5} fill="url(#colorAlunos)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        {/* Aircraft Status Pie */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>Estado das Aeronaves</h3>
          <p className="text-xs mb-4" style={{ color: "#94A3B8" }}>5 aeronaves no total</p>
          <ResponsiveContainer width="100%" height={160}>
            <PieChart>
              <Pie data={mockChartData.aircraftStatus} cx="50%" cy="50%" innerRadius={45} outerRadius={70} paddingAngle={3} dataKey="value">
                {mockChartData.aircraftStatus.map((entry, i) => (
                  <Cell key={i} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
          <div className="space-y-2">
            {mockChartData.aircraftStatus.map((s, i) => (
              <div key={i} className="flex items-center justify-between text-xs">
                <div className="flex items-center gap-2">
                  <div className="w-2.5 h-2.5 rounded-full" style={{ background: s.color }} />
                  <span style={{ color: "#64748B" }}>{s.name}</span>
                </div>
                <span style={{ color: "#0F2344", fontWeight: 600 }}>{s.value}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Flight Hours Bar + Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Bar Chart */}
        <div className="lg:col-span-2 bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>Horas de Voo Mensais</h3>
              <p className="text-xs" style={{ color: "#94A3B8" }}>Todas as aeronaves</p>
            </div>
          </div>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={mockChartData.flightHoursPerMonth} barSize={28}>
              <CartesianGrid strokeDasharray="3 3" stroke="#F1F5F9" vertical={false} />
              <XAxis dataKey="month" tick={{ fontSize: 12, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 12, fill: "#94A3B8" }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ border: "1px solid #E2E8F0", borderRadius: 12, fontSize: 12 }} />
              <Bar dataKey="horas" name="Horas" fill="#1565C0" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Recent Activity */}
        <div className="bg-white rounded-2xl p-6" style={{ border: "1px solid #E2E8F0" }}>
          <h3 className="text-base mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>Atividade Recente</h3>
          <div className="space-y-3">
            {[
              { icon: CheckCircle2, color: "#22C55E", text: "Voo CS-AER concluído", sub: "João Silva · há 2h", bg: "#DCFCE7" },
              { icon: CheckCircle2, color: "#22C55E", text: "Pagamento recebido", sub: "Sofia Ferreira · €1.200 · há 4h", bg: "#DCFCE7" },
              { icon: AlertCircle, color: "#EF4444", text: "Manutenção em curso", sub: "CS-NAV – Motor · Dia 3", bg: "#FEE2E2" },
              { icon: Clock, color: "#F59E0B", text: "Pagamento pendente", sub: "Ana Rodrigues · €1.200", bg: "#FEF3C7" },
              { icon: CheckCircle2, color: "#22C55E", text: "Novo aluno matriculado", sub: "Beatriz Santos · PPL · há 1d", bg: "#DCFCE7" },
              { icon: AlertCircle, color: "#F59E0B", text: "Manutenção agendada", sub: "CS-IFR – Garmin · 28 Mar", bg: "#FEF3C7" },
            ].map((a, i) => (
              <div key={i} className="flex items-start gap-3">
                <div className="w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0" style={{ background: a.bg }}>
                  <a.icon className="w-4 h-4" style={{ color: a.color }} />
                </div>
                <div className="min-w-0">
                  <div className="text-sm" style={{ color: "#0F2344", fontWeight: 500 }}>{a.text}</div>
                  <div className="text-xs" style={{ color: "#94A3B8" }}>{a.sub}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

import { useState } from "react";
import { Outlet, useNavigate, useLocation } from "react-router";
import {
  Plane, LayoutDashboard, Users, BookOpen, CalendarClock,
  Wrench, ClipboardList, CreditCard, BarChart3, Bell,
  Search, ChevronDown, LogOut, Settings, Menu, X,
  Gauge
} from "lucide-react";

const NAV_ITEMS = [
  { label: "Dashboard", icon: LayoutDashboard, path: "/bo/dashboard" },
  { label: "Alunos", icon: Users, path: "/bo/students" },
  { label: "Cursos", icon: BookOpen, path: "/bo/courses" },
  { label: "Agendamento de Voos", icon: CalendarClock, path: "/bo/flights" },
  { label: "Aeronaves", icon: Gauge, path: "/bo/aircraft" },
  { label: "Manutenção", icon: Wrench, path: "/bo/maintenance" },
  { label: "Avaliações", icon: ClipboardList, path: "/bo/evaluations" },
  { label: "Pagamentos", icon: CreditCard, path: "/bo/payments" },
  { label: "Relatórios", icon: BarChart3, path: "/bo/reports" },
];

const PAGE_TITLES: Record<string, string> = {
  "/bo/dashboard": "Dashboard",
  "/bo/students": "Gestão de Alunos",
  "/bo/courses": "Cursos e Módulos",
  "/bo/flights": "Agendamento de Voos",
  "/bo/aircraft": "Gestão de Aeronaves",
  "/bo/maintenance": "Manutenção",
  "/bo/evaluations": "Avaliações e Exames",
  "/bo/payments": "Pagamentos",
  "/bo/reports": "Relatórios",
};

export function BOLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [profileOpen, setProfileOpen] = useState(false);
  const [searchVal, setSearchVal] = useState("");

  const activePath = NAV_ITEMS.find(i => location.pathname.startsWith(i.path))?.path || "";
  const pageTitle = location.pathname.startsWith("/bo/students/")
    ? "Ficha do Aluno"
    : PAGE_TITLES[location.pathname] || "BackOffice";

  return (
    <div className="flex h-screen overflow-hidden" style={{ background: "#EEF2F7", fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* ── Sidebar ─────────────────────────────────────────────────────────── */}
      <aside
        className="flex flex-col transition-all duration-300 flex-shrink-0"
        style={{
          width: sidebarOpen ? 256 : 72,
          background: "#0F2344",
          borderRight: "1px solid rgba(255,255,255,0.06)",
        }}
      >
        {/* Logo */}
        <div className="flex items-center px-4 py-5 gap-3 flex-shrink-0" style={{ borderBottom: "1px solid rgba(255,255,255,0.07)" }}>
          <div className="w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0" style={{ background: "#1565C0" }}>
            <Plane className="w-5 h-5 text-white" strokeWidth={2} />
          </div>
          {sidebarOpen && (
            <div className="min-w-0">
              <div className="text-white text-base" style={{ fontWeight: 800, lineHeight: 1.2 }}>AeroSchool</div>
              <div className="text-xs" style={{ color: "#60A5FA", letterSpacing: "0.1em", fontSize: "10px" }}>BACKOFFICE</div>
            </div>
          )}
        </div>

        {/* Nav */}
        <nav className="flex-1 overflow-y-auto py-4 px-2">
          {sidebarOpen && (
            <div className="px-2 mb-2">
              <span className="text-xs uppercase tracking-widest" style={{ color: "#4A6FA5", fontSize: "10px" }}>Navegação</span>
            </div>
          )}
          {NAV_ITEMS.map(item => {
            const Icon = item.icon;
            const isActive = activePath === item.path;
            return (
              <button
                key={item.path}
                onClick={() => navigate(item.path)}
                title={!sidebarOpen ? item.label : undefined}
                className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl mb-1 text-left transition-all"
                style={{
                  background: isActive ? "#1E4D8C" : "transparent",
                  color: isActive ? "#FFFFFF" : "#93A9C8",
                }}
              >
                <Icon className="w-[18px] h-[18px] flex-shrink-0" strokeWidth={isActive ? 2.5 : 1.8} />
                {sidebarOpen && (
                  <span className="text-sm truncate" style={{ fontWeight: isActive ? 600 : 400 }}>{item.label}</span>
                )}
                {isActive && sidebarOpen && (
                  <div className="ml-auto w-1.5 h-1.5 rounded-full bg-blue-400" />
                )}
              </button>
            );
          })}
        </nav>

        {/* Bottom */}
        <div className="p-3 flex-shrink-0" style={{ borderTop: "1px solid rgba(255,255,255,0.07)" }}>
          {sidebarOpen ? (
            <div className="flex items-center gap-3 px-2 py-2 rounded-xl" style={{ background: "rgba(255,255,255,0.05)" }}>
              <div className="w-8 h-8 rounded-full flex items-center justify-center text-xs text-white flex-shrink-0" style={{ background: "#1565C0", fontWeight: 700 }}>
                AD
              </div>
              <div className="min-w-0 flex-1">
                <div className="text-sm text-white truncate" style={{ fontWeight: 600 }}>Admin. Geral</div>
                <div className="text-xs truncate" style={{ color: "#60A5FA" }}>Administrador</div>
              </div>
              <button onClick={() => navigate("/bo/login")} title="Sair">
                <LogOut className="w-4 h-4" style={{ color: "#60A5FA" }} />
              </button>
            </div>
          ) : (
            <button onClick={() => navigate("/bo/login")} className="w-full flex justify-center py-2 rounded-xl" style={{ background: "rgba(255,255,255,0.05)" }} title="Sair">
              <LogOut className="w-4 h-4" style={{ color: "#60A5FA" }} />
            </button>
          )}
        </div>
      </aside>

      {/* ── Main Area ───────────────────────────────────────────────────────── */}
      <div className="flex flex-col flex-1 min-w-0">
        {/* Top Bar */}
        <header className="flex items-center gap-4 px-6 py-3.5 flex-shrink-0" style={{ background: "white", borderBottom: "1px solid #E2E8F0" }}>
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="p-2 rounded-lg hover:bg-gray-100 transition-colors"
          >
            {sidebarOpen ? <X className="w-5 h-5 text-gray-500" /> : <Menu className="w-5 h-5 text-gray-500" />}
          </button>

          <div>
            <h1 className="text-base" style={{ color: "#0F2344", fontWeight: 700 }}>{pageTitle}</h1>
            <p className="text-xs" style={{ color: "#94A3B8" }}>AeroSchool BackOffice · 15 Mar 2025</p>
          </div>

          {/* Search */}
          <div className="flex-1 max-w-md mx-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
              <input
                value={searchVal}
                onChange={e => setSearchVal(e.target.value)}
                placeholder="Pesquisar alunos, voos, aeronaves..."
                className="w-full pl-10 pr-4 py-2 rounded-xl text-sm outline-none"
                style={{ background: "#F1F5F9", border: "1px solid #E2E8F0", color: "#0F2344" }}
              />
            </div>
          </div>

          <div className="flex items-center gap-2 ml-auto">
            {/* Notifications */}
            <button className="relative p-2 rounded-xl hover:bg-gray-50 transition-colors">
              <Bell className="w-5 h-5" style={{ color: "#64748B" }} />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full" style={{ background: "#EF4444" }} />
            </button>

            {/* Profile */}
            <div className="relative">
              <button
                onClick={() => setProfileOpen(!profileOpen)}
                className="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-gray-50 transition-colors"
              >
                <div className="w-8 h-8 rounded-full flex items-center justify-center text-xs text-white" style={{ background: "#1565C0", fontWeight: 700 }}>
                  AD
                </div>
                <div className="text-left hidden sm:block">
                  <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>Admin. Geral</div>
                  <div className="text-xs" style={{ color: "#94A3B8" }}>Administrador</div>
                </div>
                <ChevronDown className="w-4 h-4" style={{ color: "#94A3B8" }} />
              </button>
              {profileOpen && (
                <div className="absolute right-0 top-full mt-2 w-48 bg-white rounded-xl shadow-lg z-50 py-2" style={{ border: "1px solid #E2E8F0" }}>
                  <button className="w-full flex items-center gap-2.5 px-4 py-2 text-sm hover:bg-gray-50 transition-colors" style={{ color: "#374151" }}>
                    <Settings className="w-4 h-4" />
                    Definições
                  </button>
                  <div style={{ borderTop: "1px solid #E2E8F0", margin: "4px 0" }} />
                  <button onClick={() => navigate("/bo/login")} className="w-full flex items-center gap-2.5 px-4 py-2 text-sm hover:bg-gray-50 transition-colors" style={{ color: "#EF4444" }}>
                    <LogOut className="w-4 h-4" />
                    Terminar Sessão
                  </button>
                </div>
              )}
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

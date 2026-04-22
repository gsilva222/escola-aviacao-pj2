import { useState } from "react";
import { Outlet, useNavigate, useLocation } from "react-router";
import {
  Plane, LayoutDashboard, CalendarDays, Clock, ClipboardList,
  FileText, CreditCard, User, Bell, Menu, X, ChevronDown, LogOut
} from "lucide-react";
import { loggedStudent } from "../../data/mockData";

const NAV_ITEMS = [
  { label: "Dashboard", icon: LayoutDashboard, path: "/fo/dashboard" },
  { label: "Horário", icon: CalendarDays, path: "/fo/schedule" },
  { label: "Voos", icon: Plane, path: "/fo/flights" },
  { label: "Horas de Voo", icon: Clock, path: "/fo/hours" },
  { label: "Avaliações", icon: ClipboardList, path: "/fo/evaluations" },
  { label: "Documentos", icon: FileText, path: "/fo/documents" },
  { label: "Pagamentos", icon: CreditCard, path: "/fo/payments" },
  { label: "Perfil", icon: User, path: "/fo/profile" },
];

const PAGE_TITLES: Record<string, string> = {
  "/fo/dashboard": "Dashboard",
  "/fo/schedule": "Horário",
  "/fo/flights": "Voos",
  "/fo/hours": "Horas de Voo",
  "/fo/evaluations": "Avaliações",
  "/fo/documents": "Documentos",
  "/fo/payments": "Pagamentos",
  "/fo/profile": "Perfil",
};

export function FOLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);

  const activePath = NAV_ITEMS.find(i => location.pathname.startsWith(i.path))?.path || "";
  const pageTitle = PAGE_TITLES[location.pathname] || "Portal do Aluno";

  return (
    <div className="min-h-screen" style={{ background: "#F0F6FF", fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* ── Top Navigation Bar ────────────────────────────────────────────── */}
      <header className="sticky top-0 z-40 bg-white shadow-sm" style={{ borderBottom: "1px solid #E2E8F0" }}>
        <div className="max-w-7xl mx-auto flex items-center justify-between px-4 sm:px-6 h-16">
          {/* Logo */}
          <div className="flex items-center gap-3">
            <button className="md:hidden p-2 rounded-lg hover:bg-gray-50" onClick={() => setMobileOpen(!mobileOpen)}>
              <Menu className="w-5 h-5" style={{ color: "#64748B" }} />
            </button>
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg flex items-center justify-center" style={{ background: "#0F2344" }}>
                <Plane className="w-4 h-4 text-white" strokeWidth={2} />
              </div>
              <div className="hidden sm:block">
                <span className="text-sm" style={{ color: "#0F2344", fontWeight: 800 }}>AeroSchool</span>
                <span className="text-xs block" style={{ color: "#94A3B8" }}>Portal do Aluno</span>
              </div>
            </div>
          </div>

          {/* Desktop Nav */}
          <nav className="hidden md:flex items-center gap-1">
            {NAV_ITEMS.map(item => {
              const Icon = item.icon;
              const isActive = activePath === item.path;
              return (
                <button
                  key={item.path}
                  onClick={() => navigate(item.path)}
                  className="flex items-center gap-2 px-3 py-2 rounded-xl text-sm transition-all"
                  style={{
                    background: isActive ? "#EFF6FF" : "transparent",
                    color: isActive ? "#1565C0" : "#64748B",
                    fontWeight: isActive ? 700 : 400,
                  }}
                >
                  <Icon className="w-4 h-4" strokeWidth={isActive ? 2.5 : 1.8} />
                  <span>{item.label}</span>
                </button>
              );
            })}
          </nav>

          {/* Right: Notifications + Profile */}
          <div className="flex items-center gap-2">
            <button className="relative p-2 rounded-xl hover:bg-gray-50 transition-colors">
              <Bell className="w-5 h-5" style={{ color: "#64748B" }} />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full" style={{ background: "#1565C0" }} />
            </button>

            <div className="relative">
              <button
                onClick={() => setProfileOpen(!profileOpen)}
                className="flex items-center gap-2 px-2 py-1.5 rounded-xl hover:bg-gray-50 transition-colors"
              >
                <div className="w-8 h-8 rounded-full flex items-center justify-center text-xs text-white" style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)", fontWeight: 700 }}>
                  {loggedStudent.avatar}
                </div>
                <div className="hidden sm:block text-left">
                  <div className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{loggedStudent.name.split(" ")[0]}</div>
                  <div className="text-xs" style={{ color: "#94A3B8" }}>PPL · {loggedStudent.progress}% concluído</div>
                </div>
                <ChevronDown className="w-3.5 h-3.5 hidden sm:block" style={{ color: "#94A3B8" }} />
              </button>
              {profileOpen && (
                <div className="absolute right-0 top-full mt-2 w-48 bg-white rounded-xl shadow-lg z-50 py-2" style={{ border: "1px solid #E2E8F0" }}>
                  <button onClick={() => { navigate("/fo/profile"); setProfileOpen(false); }} className="w-full flex items-center gap-2.5 px-4 py-2 text-sm hover:bg-gray-50 transition-colors" style={{ color: "#374151" }}>
                    <User className="w-4 h-4" />Perfil
                  </button>
                  <div style={{ borderTop: "1px solid #E2E8F0", margin: "4px 0" }} />
                  <button onClick={() => navigate("/fo/login")} className="w-full flex items-center gap-2.5 px-4 py-2 text-sm hover:bg-gray-50 transition-colors" style={{ color: "#EF4444" }}>
                    <LogOut className="w-4 h-4" />Terminar Sessão
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Mobile Drawer */}
      {mobileOpen && (
        <div className="fixed inset-0 z-50 md:hidden">
          <div className="absolute inset-0 bg-black/40" onClick={() => setMobileOpen(false)} />
          <div className="absolute left-0 top-0 h-full w-72 bg-white p-6 shadow-xl">
            <div className="flex items-center justify-between mb-8">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-lg flex items-center justify-center" style={{ background: "#0F2344" }}>
                  <Plane className="w-4 h-4 text-white" />
                </div>
                <span style={{ color: "#0F2344", fontWeight: 800 }}>Portal do Aluno</span>
              </div>
              <button onClick={() => setMobileOpen(false)}><X className="w-5 h-5" style={{ color: "#64748B" }} /></button>
            </div>
            {NAV_ITEMS.map(item => {
              const Icon = item.icon;
              const isActive = activePath === item.path;
              return (
                <button
                  key={item.path}
                  onClick={() => { navigate(item.path); setMobileOpen(false); }}
                  className="w-full flex items-center gap-3 px-4 py-3 rounded-xl mb-1 text-left"
                  style={{ background: isActive ? "#EFF6FF" : "transparent", color: isActive ? "#1565C0" : "#64748B", fontWeight: isActive ? 700 : 400 }}
                >
                  <Icon className="w-5 h-5" />
                  {item.label}
                </button>
              );
            })}
          </div>
        </div>
      )}

      {/* Page Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
        <Outlet />
      </main>
    </div>
  );
}

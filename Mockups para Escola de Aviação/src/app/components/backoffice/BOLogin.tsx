import { useState } from "react";
import { useNavigate } from "react-router";
import { Plane, Eye, EyeOff, Lock, Mail, ArrowLeft } from "lucide-react";

const COCKPIT_IMG = "https://images.unsplash.com/photo-1764304561157-a5c022549f91?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhdmlhdGlvbiUyMHBpbG90JTIwdHJhaW5pbmclMjBjb2NrcGl0JTIwc21hbGwlMjBwbGFuZXxlbnwxfHx8fDE3NzM2MTUzOTV8MA&ixlib=rb-4.1.0&q=80&w=1080";

export function BOLogin() {
  const navigate = useNavigate();
  const [showPwd, setShowPwd] = useState(false);
  const [email, setEmail] = useState("admin@aeroschool.pt");
  const [password, setPassword] = useState("••••••••");
  const [role, setRole] = useState("Administrador");

  const handleLogin = () => navigate("/bo/dashboard");

  const roles = ["Administrador", "Secretaria", "Gestor Operacional", "Instrutor", "Técnico de Manutenção"];

  return (
    <div className="min-h-screen flex" style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Left Panel – Image */}
      <div className="hidden lg:flex lg:w-1/2 relative flex-col justify-between p-12">
        <div className="absolute inset-0 bg-cover bg-center" style={{ backgroundImage: `url(${COCKPIT_IMG})` }} />
        <div className="absolute inset-0" style={{ background: "linear-gradient(180deg, rgba(10,25,68,0.7) 0%, rgba(10,25,68,0.9) 100%)" }} />
        <div className="relative z-10">
          <button onClick={() => navigate("/")} className="flex items-center gap-2 text-blue-300 hover:text-white transition-colors text-sm">
            <ArrowLeft className="w-4 h-4" />
            <span>Voltar ao início</span>
          </button>
        </div>
        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-8">
            <div className="w-12 h-12 rounded-xl flex items-center justify-center" style={{ background: "rgba(255,255,255,0.12)", border: "1px solid rgba(255,255,255,0.2)" }}>
              <Plane className="w-6 h-6 text-white" />
            </div>
            <div>
              <div className="text-white text-2xl" style={{ fontWeight: 800 }}>AeroSchool</div>
              <div className="text-blue-300 text-xs" style={{ letterSpacing: "0.15em" }}>ACADEMIA DE AVIAÇÃO</div>
            </div>
          </div>
          <h2 className="text-white text-3xl mb-4" style={{ fontWeight: 700, lineHeight: 1.2 }}>Sistema de Gestão<br />BackOffice</h2>
          <p className="text-blue-300" style={{ lineHeight: 1.7 }}>Plataforma integrada para administração, gestão operacional, instrutores e manutenção.</p>
          <div className="mt-8 grid grid-cols-2 gap-4">
            {[{ n: "40+", l: "Alunos Ativos" }, { n: "5", l: "Aeronaves" }, { n: "3", l: "Instrutores" }, { n: "EASA", l: "Aprovado ATO" }].map(s => (
              <div key={s.l} className="p-4 rounded-xl" style={{ background: "rgba(255,255,255,0.08)", border: "1px solid rgba(255,255,255,0.1)" }}>
                <div className="text-white text-xl" style={{ fontWeight: 700 }}>{s.n}</div>
                <div className="text-blue-300 text-sm">{s.l}</div>
              </div>
            ))}
          </div>
        </div>
        <div className="relative z-10 text-blue-400 text-xs">© 2025 AeroSchool · EASA Part-FCL ATO</div>
      </div>

      {/* Right Panel – Form */}
      <div className="flex-1 flex items-center justify-center p-8" style={{ background: "#F0F4F8" }}>
        <div className="w-full max-w-md">
          {/* Mobile logo */}
          <div className="lg:hidden flex items-center gap-2 mb-8">
            <button onClick={() => navigate("/")} className="flex items-center gap-2 text-gray-500 hover:text-gray-700 text-sm mr-4">
              <ArrowLeft className="w-4 h-4" />
            </button>
            <Plane className="w-6 h-6" style={{ color: "#1565C0" }} />
            <span style={{ color: "#0F2344", fontWeight: 700 }}>AeroSchool BackOffice</span>
          </div>

          <div className="bg-white rounded-2xl p-10 shadow-sm" style={{ border: "1px solid rgba(0,0,0,0.06)" }}>
            <div className="mb-8">
              <h1 className="mb-1" style={{ color: "#0F2344", fontSize: "1.75rem", fontWeight: 800 }}>Iniciar Sessão</h1>
              <p className="text-sm" style={{ color: "#64748B" }}>Acesso exclusivo para colaboradores da AeroSchool</p>
            </div>

            {/* Role selector */}
            <div className="mb-6">
              <label className="block text-sm mb-2" style={{ color: "#374151", fontWeight: 600 }}>Perfil de Acesso</label>
              <select
                value={role}
                onChange={e => setRole(e.target.value)}
                className="w-full px-4 py-3 rounded-xl text-sm outline-none transition-all"
                style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#0F2344" }}
              >
                {roles.map(r => <option key={r} value={r}>{r}</option>)}
              </select>
            </div>

            {/* Email */}
            <div className="mb-4">
              <label className="block text-sm mb-2" style={{ color: "#374151", fontWeight: 600 }}>Email</label>
              <div className="relative">
                <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
                <input
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 rounded-xl text-sm outline-none transition-all"
                  style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#0F2344" }}
                  placeholder="utilizador@aeroschool.pt"
                />
              </div>
            </div>

            {/* Password */}
            <div className="mb-6">
              <label className="block text-sm mb-2" style={{ color: "#374151", fontWeight: 600 }}>Password</label>
              <div className="relative">
                <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
                <input
                  type={showPwd ? "text" : "password"}
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  className="w-full pl-10 pr-12 py-3 rounded-xl text-sm outline-none"
                  style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#0F2344" }}
                />
                <button onClick={() => setShowPwd(!showPwd)} className="absolute right-3.5 top-1/2 -translate-y-1/2">
                  {showPwd ? <EyeOff className="w-4 h-4" style={{ color: "#94A3B8" }} /> : <Eye className="w-4 h-4" style={{ color: "#94A3B8" }} />}
                </button>
              </div>
            </div>

            <button
              onClick={handleLogin}
              className="w-full py-3.5 rounded-xl text-white text-sm transition-all hover:opacity-90 active:scale-98"
              style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)", fontWeight: 600, letterSpacing: "0.01em" }}
            >
              Entrar no BackOffice
            </button>

            <div className="mt-4 text-center">
              <button className="text-sm" style={{ color: "#1565C0" }}>Esqueci-me da password</button>
            </div>
          </div>

          <p className="text-center text-xs mt-6" style={{ color: "#94A3B8" }}>
            É aluno? <button onClick={() => navigate("/fo/login")} className="underline" style={{ color: "#1565C0" }}>Aceder ao Portal do Aluno</button>
          </p>
        </div>
      </div>
    </div>
  );
}

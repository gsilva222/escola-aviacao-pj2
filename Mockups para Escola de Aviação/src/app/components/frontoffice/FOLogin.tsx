import { useState } from "react";
import { useNavigate } from "react-router";
import { Plane, Eye, EyeOff, Lock, User, ArrowLeft, GraduationCap } from "lucide-react";

export function FOLogin() {
  const navigate = useNavigate();
  const [showPwd, setShowPwd] = useState(false);
  const [userId, setUserId] = useState("joao.silva");
  const [password, setPassword] = useState("••••••••");

  return (
    <div className="min-h-screen flex items-center justify-center p-6" style={{ background: "linear-gradient(135deg, #F0F6FF 0%, #E8F4FD 100%)", fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Back button */}
      <button onClick={() => navigate("/")} className="absolute top-6 left-6 flex items-center gap-2 text-sm hover:opacity-70 transition-opacity" style={{ color: "#64748B" }}>
        <ArrowLeft className="w-4 h-4" />
        Voltar
      </button>

      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="text-center mb-10">
          <div className="w-20 h-20 rounded-3xl flex items-center justify-center mx-auto mb-5 shadow-lg" style={{ background: "linear-gradient(135deg, #0F2344, #1565C0)" }}>
            <GraduationCap className="w-10 h-10 text-white" />
          </div>
          <h1 className="mb-2" style={{ color: "#0F2344", fontSize: "1.875rem", fontWeight: 800, letterSpacing: "-0.02em" }}>Portal do Aluno</h1>
          <p className="text-sm" style={{ color: "#64748B" }}>AeroSchool Academia de Aviação</p>
        </div>

        <div className="bg-white rounded-3xl p-10 shadow-xl" style={{ border: "1px solid rgba(0,0,0,0.06)" }}>
          <h2 className="text-lg mb-6" style={{ color: "#0F2344", fontWeight: 700 }}>Iniciar Sessão</h2>

          {/* User ID */}
          <div className="mb-4">
            <label className="block text-sm mb-2" style={{ color: "#374151", fontWeight: 600 }}>Nº de Aluno / Username</label>
            <div className="relative">
              <User className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
              <input
                value={userId}
                onChange={e => setUserId(e.target.value)}
                className="w-full pl-10 pr-4 py-3.5 rounded-2xl text-sm outline-none transition-all"
                style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#0F2344" }}
                placeholder="ex: joao.silva ou 100001"
              />
            </div>
          </div>

          {/* Password */}
          <div className="mb-7">
            <label className="block text-sm mb-2" style={{ color: "#374151", fontWeight: 600 }}>Password</label>
            <div className="relative">
              <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4" style={{ color: "#94A3B8" }} />
              <input
                type={showPwd ? "text" : "password"}
                value={password}
                onChange={e => setPassword(e.target.value)}
                className="w-full pl-10 pr-12 py-3.5 rounded-2xl text-sm outline-none"
                style={{ background: "#F8FAFC", border: "1.5px solid #E2E8F0", color: "#0F2344" }}
              />
              <button onClick={() => setShowPwd(!showPwd)} className="absolute right-3.5 top-1/2 -translate-y-1/2">
                {showPwd ? <EyeOff className="w-4 h-4" style={{ color: "#94A3B8" }} /> : <Eye className="w-4 h-4" style={{ color: "#94A3B8" }} />}
              </button>
            </div>
          </div>

          <button
            onClick={() => navigate("/fo/dashboard")}
            className="w-full py-4 rounded-2xl text-white transition-all hover:opacity-90 active:scale-98"
            style={{ background: "linear-gradient(135deg, #1565C0, #0D47A1)", fontWeight: 700, fontSize: "1rem" }}
          >
            Entrar
          </button>

          <div className="flex justify-between mt-4 text-sm">
            <button style={{ color: "#94A3B8" }}>Esqueci a password</button>
            <button style={{ color: "#1565C0" }}>Ajuda</button>
          </div>
        </div>

        {/* Staff link */}
        <p className="text-center text-xs mt-6" style={{ color: "#94A3B8" }}>
          É colaborador?{" "}
          <button onClick={() => navigate("/bo/login")} className="underline" style={{ color: "#1565C0" }}>
            Aceder ao BackOffice
          </button>
        </p>

        {/* Demo Notice */}
        <div className="mt-4 p-3 rounded-2xl text-center text-xs" style={{ background: "#FFF7ED", border: "1px solid #FED7AA", color: "#92400E" }}>
          Demo: utilizador <strong>joao.silva</strong> · qualquer password
        </div>
      </div>
    </div>
  );
}

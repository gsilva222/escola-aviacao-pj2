import { useNavigate } from "react-router";
import { Plane, Shield, User, ChevronRight, GraduationCap } from "lucide-react";

const BG_IMG = "https://images.unsplash.com/photo-1664951988856-c6f48073716e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmbGlnaHQlMjBzY2hvb2wlMjBhaXJwbGFuZSUyMHJ1bndheSUyMGFlcmlhbHxlbnwxfHx8fDE3NzM2MTUzOTV8MA&ixlib=rb-4.1.0&q=80&w=1080";

export function LandingPage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col" style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Hero Background */}
      <div className="relative flex-1 flex flex-col">
        <div
          className="absolute inset-0 bg-cover bg-center"
          style={{ backgroundImage: `url(${BG_IMG})` }}
        />
        <div className="absolute inset-0" style={{ background: "linear-gradient(135deg, rgba(10,25,68,0.92) 0%, rgba(21,101,192,0.80) 100%)" }} />

        {/* Header */}
        <header className="relative z-10 flex items-center justify-between px-10 py-6">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl flex items-center justify-center" style={{ background: "rgba(255,255,255,0.15)", backdropFilter: "blur(8px)" }}>
              <Plane className="w-5 h-5 text-white" strokeWidth={2} />
            </div>
            <div>
              <span className="text-white text-xl" style={{ fontWeight: 700, letterSpacing: "-0.02em" }}>AeroSchool</span>
              <span className="text-blue-300 text-xs block" style={{ letterSpacing: "0.12em" }}>ACADEMIA DE AVIAÇÃO</span>
            </div>
          </div>
          <div className="text-blue-200 text-sm">Aeródromo de Lisboa-Cascais · LPPT</div>
        </header>

        {/* Main Content */}
        <main className="relative z-10 flex-1 flex flex-col items-center justify-center px-6 py-20">
          <div className="text-center mb-16">
            <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full text-blue-200 text-sm mb-6" style={{ background: "rgba(255,255,255,0.1)", border: "1px solid rgba(255,255,255,0.15)" }}>
              <Plane className="w-3.5 h-3.5" />
              <span>Sistema de Gestão Académica</span>
            </div>
            <h1 className="text-white mb-4" style={{ fontSize: "clamp(2rem, 5vw, 3.5rem)", fontWeight: 800, letterSpacing: "-0.03em", lineHeight: 1.1 }}>
              Bem-vindo à AeroSchool
            </h1>
            <p className="text-blue-200 max-w-lg mx-auto" style={{ fontSize: "1.1rem", lineHeight: 1.7 }}>
              Plataforma integrada de gestão para a academia de aviação. Selecione o seu perfil de acesso.
            </p>
          </div>

          {/* Portal Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-3xl">
            {/* BackOffice Card */}
            <button
              onClick={() => navigate("/bo/login")}
              className="group text-left rounded-2xl p-8 transition-all duration-300 hover:-translate-y-1"
              style={{ background: "rgba(255,255,255,0.08)", border: "1px solid rgba(255,255,255,0.15)", backdropFilter: "blur(16px)" }}
            >
              <div className="w-14 h-14 rounded-2xl flex items-center justify-center mb-6 transition-all group-hover:scale-110" style={{ background: "rgba(21,101,192,0.6)", border: "1px solid rgba(66,165,250,0.3)" }}>
                <Shield className="w-7 h-7 text-blue-200" />
              </div>
              <h2 className="text-white text-2xl mb-2" style={{ fontWeight: 700 }}>BackOffice</h2>
              <p className="text-blue-300 text-sm mb-6" style={{ lineHeight: 1.6 }}>
                Para administradores, secretaria, instrutores e técnicos de manutenção. Gestão completa da academia.
              </p>
              <div className="flex flex-wrap gap-2 mb-6">
                {["Administrador", "Secretaria", "Instrutor", "Manutenção"].map(r => (
                  <span key={r} className="text-xs px-2.5 py-1 rounded-full text-blue-200" style={{ background: "rgba(255,255,255,0.08)", border: "1px solid rgba(255,255,255,0.12)" }}>{r}</span>
                ))}
              </div>
              <div className="flex items-center gap-2 text-blue-300 text-sm group-hover:text-white transition-colors">
                <span>Aceder ao BackOffice</span>
                <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
              </div>
            </button>

            {/* FrontOffice Card */}
            <button
              onClick={() => navigate("/fo/login")}
              className="group text-left rounded-2xl p-8 transition-all duration-300 hover:-translate-y-1"
              style={{ background: "rgba(255,255,255,0.12)", border: "1px solid rgba(255,255,255,0.25)", backdropFilter: "blur(16px)" }}
            >
              <div className="w-14 h-14 rounded-2xl flex items-center justify-center mb-6 transition-all group-hover:scale-110" style={{ background: "rgba(255,255,255,0.15)", border: "1px solid rgba(255,255,255,0.25)" }}>
                <GraduationCap className="w-7 h-7 text-white" />
              </div>
              <h2 className="text-white text-2xl mb-2" style={{ fontWeight: 700 }}>Portal do Aluno</h2>
              <p className="text-blue-200 text-sm mb-6" style={{ lineHeight: 1.6 }}>
                Área exclusiva para alunos. Consulte voos, horários, avaliações, documentos e pagamentos.
              </p>
              <div className="flex flex-wrap gap-2 mb-6">
                {["Voos", "Horários", "Avaliações", "Documentos", "Pagamentos"].map(r => (
                  <span key={r} className="text-xs px-2.5 py-1 rounded-full text-blue-100" style={{ background: "rgba(255,255,255,0.12)", border: "1px solid rgba(255,255,255,0.2)" }}>{r}</span>
                ))}
              </div>
              <div className="flex items-center gap-2 text-blue-200 text-sm group-hover:text-white transition-colors">
                <span>Aceder ao Portal do Aluno</span>
                <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
              </div>
            </button>
          </div>
        </main>

        {/* Footer */}
        <footer className="relative z-10 text-center py-6 text-blue-400 text-xs">
          © 2025 AeroSchool Academia de Aviação · Todos os direitos reservados · EASA Part-FCL Approved ATO
        </footer>
      </div>
    </div>
  );
}

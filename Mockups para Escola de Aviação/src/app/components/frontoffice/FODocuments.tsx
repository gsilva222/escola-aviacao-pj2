import { loggedStudent } from "../../data/mockData";
import {
  FileText, Shield, CreditCard, FileCheck, Download, Eye,
  AlertTriangle, CheckCircle2, Award
} from "lucide-react";

const DOC_TYPE_MAP: Record<string, { icon: any; color: string; bg: string; label: string }> = {
  certificate: { icon: Award, color: "#1D4ED8", bg: "#DBEAFE", label: "Certificado" },
  medical: { icon: Shield, color: "#16A34A", bg: "#DCFCE7", label: "Médico" },
  id: { icon: CreditCard, color: "#7C3AED", bg: "#F3E8FF", label: "Identificação" },
  contract: { icon: FileCheck, color: "#D97706", bg: "#FEF3C7", label: "Contrato" },
  insurance: { icon: Shield, color: "#DC2626", bg: "#FEE2E2", label: "Seguro" },
  declaration: { icon: FileText, color: "#64748B", bg: "#F1F5F9", label: "Declaração" },
};

const certificates = [
  {
    id: "cert1",
    name: "PPL – Certificado de Matrícula",
    issued: "15 Jan 2024",
    status: "active",
    description: "Certificado de inscrição no curso de Piloto Privado (PPL) da AeroSchool",
    number: "AS-2024-PPL-001",
  },
  {
    id: "cert2",
    name: "Frequência – Semestre 1",
    issued: "30 Jun 2024",
    status: "active",
    description: "Declaração de frequência do 1.º semestre do curso PPL",
    number: "AS-2024-FREQ-001",
  },
];

export function FODocuments() {
  const docs = loggedStudent.documents;

  return (
    <div style={{ fontFamily: "Inter, system-ui, sans-serif" }}>
      {/* Summary */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: "Documentos Ativos", value: docs.filter(d => d.status === "valid").length, color: "#16A34A", bg: "#DCFCE7" },
          { label: "A Expirar em 30d", value: 0, color: "#D97706", bg: "#FEF3C7" },
          { label: "Certificados", value: docs.filter(d => d.type === "certificate").length, color: "#1565C0", bg: "#DBEAFE" },
          { label: "Total de Documentos", value: docs.length, color: "#64748B", bg: "#F1F5F9" },
        ].map(s => (
          <div key={s.label} className="bg-white rounded-2xl p-5" style={{ border: "1px solid #E2E8F0" }}>
            <div className="text-2xl mb-1" style={{ color: s.color, fontWeight: 800 }}>{s.value}</div>
            <div className="text-sm" style={{ color: "#64748B" }}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* Certificates Section */}
      <div className="mb-6">
        <h2 className="text-base mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>
          <Award className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
          Certificados e Diplomas
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {certificates.map(cert => (
            <div
              key={cert.id}
              className="bg-white rounded-2xl p-6"
              style={{ border: "1.5px solid #BFDBFE" }}
            >
              <div className="flex items-start justify-between mb-4">
                <div className="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0" style={{ background: "#DBEAFE" }}>
                  <Award className="w-6 h-6" style={{ color: "#1D4ED8" }} />
                </div>
                <span className="flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs" style={{ background: "#DCFCE7", color: "#16A34A", fontWeight: 600 }}>
                  <CheckCircle2 className="w-3 h-3" />
                  Válido
                </span>
              </div>
              <h3 className="text-base mb-1" style={{ color: "#0F2344", fontWeight: 700 }}>{cert.name}</h3>
              <p className="text-xs mb-3" style={{ color: "#64748B" }}>{cert.description}</p>
              <div className="flex items-center justify-between">
                <div>
                  <div className="text-xs" style={{ color: "#94A3B8" }}>Nº: {cert.number}</div>
                  <div className="text-xs" style={{ color: "#94A3B8" }}>Emitido: {cert.issued}</div>
                </div>
                <div className="flex gap-2">
                  <button className="p-2 rounded-lg hover:bg-gray-50 transition-colors" style={{ border: "1px solid #E2E8F0" }}>
                    <Eye className="w-4 h-4" style={{ color: "#64748B" }} />
                  </button>
                  <button className="p-2 rounded-lg text-white" style={{ background: "#1565C0" }}>
                    <Download className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* All Documents */}
      <div>
        <h2 className="text-base mb-4" style={{ color: "#0F2344", fontWeight: 700 }}>
          <FileText className="inline w-4 h-4 mr-2" style={{ color: "#1565C0" }} />
          Todos os Documentos
        </h2>
        <div className="bg-white rounded-2xl overflow-hidden" style={{ border: "1px solid #E2E8F0" }}>
          {docs.map((doc, i) => {
            const dt = DOC_TYPE_MAP[doc.type] || DOC_TYPE_MAP.declaration;
            const Icon = dt.icon;
            const isExpiring = doc.expiry && new Date(doc.expiry) < new Date("2026-01-01");

            return (
              <div
                key={doc.id}
                className="flex items-center gap-4 px-5 py-4"
                style={{ borderBottom: i < docs.length - 1 ? "1px solid #F1F5F9" : undefined }}
              >
                <div className="w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0" style={{ background: dt.bg }}>
                  <Icon className="w-5 h-5" style={{ color: dt.color }} />
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-0.5">
                    <span className="text-sm" style={{ color: "#0F2344", fontWeight: 600 }}>{doc.name}</span>
                    <span className="px-2 py-0.5 rounded-full text-xs" style={{ background: dt.bg, color: dt.color, fontWeight: 600 }}>
                      {dt.label}
                    </span>
                  </div>
                  <div className="flex items-center gap-4 text-xs" style={{ color: "#94A3B8" }}>
                    <span>Emitido: {new Date(doc.date).toLocaleDateString("pt-PT")}</span>
                    {doc.expiry && (
                      <span className={isExpiring ? "" : ""} style={{ color: isExpiring ? "#D97706" : "#94A3B8" }}>
                        {isExpiring && <AlertTriangle className="inline w-3 h-3 mr-1" />}
                        Validade: {new Date(doc.expiry).toLocaleDateString("pt-PT")}
                      </span>
                    )}
                  </div>
                </div>
                <div className="flex items-center gap-3 flex-shrink-0">
                  <span
                    className="flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs"
                    style={{
                      background: doc.status === "valid" ? "#DCFCE7" : "#FEE2E2",
                      color: doc.status === "valid" ? "#16A34A" : "#DC2626",
                      fontWeight: 600,
                    }}
                  >
                    <CheckCircle2 className="w-3 h-3" />
                    {doc.status === "valid" ? "Válido" : "Inválido"}
                  </span>
                  <div className="flex gap-1">
                    <button className="p-1.5 rounded-lg hover:bg-gray-50 transition-colors">
                      <Eye className="w-4 h-4" style={{ color: "#94A3B8" }} />
                    </button>
                    <button className="p-1.5 rounded-lg hover:bg-gray-50 transition-colors">
                      <Download className="w-4 h-4" style={{ color: "#94A3B8" }} />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Info Banner */}
      <div className="mt-5 p-4 rounded-2xl flex items-start gap-3" style={{ background: "#EFF6FF", border: "1px solid #BFDBFE" }}>
        <Shield className="w-5 h-5 flex-shrink-0 mt-0.5" style={{ color: "#1D4ED8" }} />
        <div>
          <p className="text-sm" style={{ color: "#1D4ED8", fontWeight: 600 }}>Segurança dos Documentos</p>
          <p className="text-xs mt-0.5" style={{ color: "#3B82F6", lineHeight: 1.6 }}>
            Todos os seus documentos são armazenados de forma segura e encriptada. Apenas você e os administradores autorizados da AeroSchool têm acesso aos seus documentos.
          </p>
        </div>
      </div>
    </div>
  );
}

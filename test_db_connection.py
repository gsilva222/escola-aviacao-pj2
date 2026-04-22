#!/usr/bin/env python3
"""
Script para testar a conexão à base de dados PostgreSQL
Baseado nas credenciais do persistence.xml
"""

import sys

try:
    import psycopg2
except ImportError:
    print("❌ psycopg2 não está instalado. Instalando...")
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "psycopg2-binary", "-q"])
    import psycopg2

# Credenciais do persistence.xml
DB_CONFIG = {
    'host': 'doberserver222.cc',
    'port': 2345,
    'user': 'admin_escola',
    'password': 'admin_password123',
    'database': 'escola_aviacao'
}

print("🔍 A tentar conectar ao PostgreSQL...")
print(f"   Host: {DB_CONFIG['host']}:{DB_CONFIG['port']}")
print(f"   Base de dados: {DB_CONFIG['database']}")
print(f"   Utilizador: {DB_CONFIG['user']}\n")

try:
    # Tenta conectar com timeout de 5 segundos
    conn = psycopg2.connect(**DB_CONFIG, connect_timeout=5)
    cursor = conn.cursor()
    
    print("✅ Ligação estabelecida com sucesso!\n")
    
    # Verifica a versão do PostgreSQL
    cursor.execute("SELECT version();")
    version = cursor.fetchone()[0]
    print(f"📊 PostgreSQL Version: {version}\n")
    
    # Lista as tabelas da BD
    cursor.execute("""
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'public'
        ORDER BY table_name;
    """)
    
    tables = cursor.fetchall()
    
    if tables:
        print("📋 Tabelas encontradas:")
        for table in tables:
            print(f"   - {table[0]}")
        
        # Se existe a tabela 'perfil', mostra a sua estrutura
        if any(t[0] == 'perfil' for t in tables):
            print("\n🔍 Estrutura da tabela 'perfil':")
            cursor.execute("""
                SELECT column_name, data_type, is_nullable, column_default
                FROM information_schema.columns
                WHERE table_name = 'perfil'
                ORDER BY ordinal_position;
            """)
            columns = cursor.fetchall()
            for col in columns:
                nullable = "NULL" if col[2] == 'YES' else "NOT NULL"
                default = f" DEFAULT {col[3]}" if col[3] else ""
                print(f"   - {col[0]}: {col[1]} {nullable}{default}")
            
            # Conta registos
            cursor.execute("SELECT COUNT(*) FROM perfil;")
            count = cursor.fetchone()[0]
            print(f"\n📊 Total de registos em 'perfil': {count}")
        else:
            print("\n⚠️  AVISO: A tabela 'perfil' NÃO existe na base de dados!")
            print("      Será necessário criá-la antes de executar a aplicação Java.")
    else:
        print("⚠️  AVISO: Nenhuma tabela encontrada. A base de dados está vazia!")
    
    cursor.close()
    conn.close()
    
except psycopg2.OperationalError as e:
    print(f"❌ Erro de ligação: {e}")
    print("\nVerifique:")
    print("  1. O servidor PostgreSQL está em execução?")
    print("  2. O host e porto estão corretos?")
    print("  3. As credenciais (utilizador/password) estão corretas?")
except Exception as e:
    print(f"❌ Erro: {e}")
    sys.exit(1)

print("\n✅ Teste concluído!")

-- =============================================================================
-- SCRIPT DE POPULAÇÃO PARA TESTES MANUAIS - PLATAFORMA Q-PROJETOS (FINAL CORRIGIDO)
-- Este script APENAS limpa os dados.
-- A criação de usuários deve ser feita via API (ver GUIA_TESTES.md).
-- =============================================================================

USE q_projetos;

-- 1. LIMPEZA
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE favorito;
TRUNCATE TABLE interesses;
TRUNCATE TABLE vinculo_equipe;
TRUNCATE TABLE projetos;
TRUNCATE TABLE usuarios;
SET FOREIGN_KEY_CHECKS = 1;

-- IMPORTANTE:
-- Não popule usuários manualmente aqui para evitar erros de hash BCrypt.
-- Siga a Seção 1 do GUIA_TESTES.md para registrar os usuários via API.

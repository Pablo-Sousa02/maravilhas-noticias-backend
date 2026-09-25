# Maravilhas Notícias — back-end

API REST do portal local Maravilhas Notícias. Administradores gerenciam categorias e notícias, publicam destaques e urgências, enviam imagens ao Cloudinary e notificam navegadores inscritos via Web Push. Leitura pública e cadastro/login de usuários são suportados.

## Tecnologias

Java 21, Spring Boot 4.1.1, Maven Wrapper, PostgreSQL, Flyway, Spring Data JPA, Spring Security OAuth2 Resource Server, JWT HS256, Cloudinary, Web Push/VAPID, Actuator, springdoc-openapi e Lombok.

## Organização

- `auth`: cadastro, login e emissão JWT.
- `user`: usuários, perfil autenticado e bootstrap do administrador.
- `category`: categorias públicas e administração.
- `news`: notícias, busca, filtros e fluxo editorial.
- `media`: abstração e implementação Cloudinary.
- `push`: inscrições e envio assíncrono Web Push.
- `config`: segurança, JWT, CORS e OpenAPI.
- `common`: erros e utilitários compartilhados.

## Requisitos

- JDK 21
- PostgreSQL disponível
- PowerShell no Windows ou shell POSIX
- Cloudinary e chaves VAPID são opcionais para iniciar a aplicação

## Variáveis de ambiente

Copie `.env.example` como referência. O Spring não carrega `.env` automaticamente; exporte as variáveis no terminal, IDE, container ou plataforma de deploy.

Obrigatórias:

- `DB_PASSWORD`: senha do PostgreSQL.
- `JWT_SECRET`: segredo Base64 com pelo menos 32 bytes aleatórios.

Configuração geral: `DB_URL`, `DB_USERNAME`, `JWT_EXPIRATION_SECONDS`, `APP_CORS_ALLOWED_ORIGINS`.

Bootstrap opcional: `ADMIN_NAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`. O administrador só é criado quando as três existem e o e-mail ainda não está cadastrado.

Integrações opcionais: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`, `VAPID_PUBLIC_KEY`, `VAPID_PRIVATE_KEY`, `VAPID_SUBJECT`.

Gere uma chave JWT no PowerShell sem colocá-la no código:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

## Banco de dados

```sql
CREATE DATABASE maravilhas_noticias_db;
```

Defina `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O Flyway cria e valida o schema ao iniciar. Nunca edite migrations já aplicadas.

## Executar e testar

```powershell
$env:DB_PASSWORD = "sua-senha"
$env:JWT_SECRET = "segredo-base64-de-32-bytes-ou-mais"
.\mvnw test
.\mvnw spring-boot:run
```

Validação completa:

```powershell
.\mvnw clean verify
```

Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`  
Health: `http://localhost:8080/actuator/health`

## Autenticação

Faça login e envie o token em `Authorization: Bearer <token>`. Rotas `/api/admin/**` exigem claim `roles` contendo `ADMIN`.

```json
{
  "email": "admin@example.com",
  "password": "senha-segura"
}
```

## Endpoints principais

| Método | Rota | Acesso |
|---|---|---|
| POST | `/api/auth/register` | Público |
| POST | `/api/auth/login` | Público |
| GET | `/api/users/me` | Autenticado |
| GET | `/api/categories`, `/api/categories/{slug}` | Público |
| POST/PUT/DELETE | `/api/admin/categories/**` | ADMIN |
| GET | `/api/news`, `/api/news/{slug}`, `/api/news/featured`, `/api/news/search?q=` | Público |
| GET/POST/PUT/PATCH/DELETE | `/api/admin/news/**` | ADMIN |
| POST | `/api/admin/uploads/images` | ADMIN |
| POST/DELETE | `/api/push/subscriptions` | Público |

Listagens aceitam `page`, `size`, `sort`; notícias aceitam filtro `category`. A busca consulta título e resumo.

Exemplo de criação de notícia:

```json
{
  "title": "Nova obra começa no centro",
  "summary": "Intervenção começa nesta semana.",
  "content": "Conteúdo completo da notícia.",
  "coverImageUrl": "https://exemplo.com/imagem.jpg",
  "featured": true,
  "urgent": false,
  "categoryId": "00000000-0000-0000-0000-000000000000"
}
```

## Fluxo editorial e push

Notícias nascem como `DRAFT`. O administrador pode editar, publicar ou arquivar. A primeira publicação define `publishedAt`. Se a notícia estiver marcada como `urgent`, a primeira publicação dispara envio assíncrono às inscrições ativas; falhas individuais não interrompem a publicação e respostas definitivas 404/410 desativam a inscrição.

Sem credenciais Cloudinary, somente o endpoint de upload retorna HTTP 503. Sem VAPID, a aplicação inicia normalmente e ignora envios.

## Deploy

- Provisionar PostgreSQL e executar backup antes de migrations em produção.
- Configurar todas as variáveis obrigatórias no gerenciador de segredos.
- Usar uma chave JWT exclusiva por ambiente e HTTPS obrigatório.
- Restringir `APP_CORS_ALLOWED_ORIGINS` aos front-ends reais.
- Configurar Cloudinary/VAPID quando esses recursos forem habilitados.
- Executar `.\mvnw clean verify` antes do deploy.
- Validar `/actuator/health`, login, leitura pública e uma rota ADMIN.
- Manter apenas `health` e `info` expostos pelo Actuator.

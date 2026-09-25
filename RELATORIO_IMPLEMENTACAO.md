# Relatório de implementação — Maravilhas Notícias

Data da consolidação: 25/09/2026

## 1. Objetivo

Finalizar o MVP do back-end do portal local Maravilhas Notícias, permitindo:

- cadastro e autenticação opcional de usuários;
- administração protegida por JWT e perfil `ADMIN`;
- gerenciamento de categorias e notícias;
- publicação de destaques e notícias urgentes;
- upload de imagens de capa;
- inscrição de navegadores e envio de notificações Web Push;
- consulta pública de categorias e notícias;
- documentação, monitoramento e configuração segura por ambiente.

## 2. Diagnóstico inicial

O projeto já possuía:

- Java 21 e Spring Boot 4.1.1;
- PostgreSQL, JPA e Flyway;
- entidade e repository de usuários;
- cadastro de usuários com BCrypt;
- login e emissão inicial de JWT;
- migration `V1` para usuários;
- validação inicial com Bean Validation;
- tratamento parcial de erros com `ProblemDetail`.

Os principais pontos faltantes eram:

- ativação do OAuth2 Resource Server no Spring Security;
- conversão da claim `roles` para authorities `ROLE_*`;
- controle público/privado das rotas;
- perfil do usuário autenticado;
- bootstrap seguro do primeiro administrador;
- categorias, notícias, upload e Web Push;
- CORS, Actuator e OpenAPI;
- testes das regras centrais;
- documentação de execução e deploy.

Também foi corrigido o erro anterior de login no qual `LoginRequest` declarava o campo `senha` e possuía um método `password()` que sempre retornava `null`. O DTO passou a usar os campos `email` e `password` diretamente.

## 3. Segurança e autenticação

Foi configurado o Spring Security como OAuth2 Resource Server usando o `JwtDecoder` existente.

Implementações realizadas:

- autenticação stateless;
- JWT HS256;
- validação de expiração e issuer `maravilhas-noticias-api`;
- conversão da claim `roles` com prefixo `ROLE_`;
- exigência de `ROLE_ADMIN` em `/api/admin/**`;
- respostas HTTP 401 para token ausente, expirado ou inválido;
- respostas HTTP 403 para usuário autenticado sem permissão;
- desativação de form login, HTTP Basic e CSRF para a API stateless;
- configuração de CORS por `APP_CORS_ALLOWED_ORIGINS`.

Rotas públicas configuradas:

- `POST /api/auth/register`;
- `POST /api/auth/login`;
- métodos GET de `/api/categories/**`;
- métodos GET de `/api/news/**`;
- `POST /api/push/subscriptions`;
- `DELETE /api/push/subscriptions`;
- `/v3/api-docs/**`;
- `/swagger-ui/**`;
- `/swagger-ui.html`;
- `/actuator/health`;
- `/actuator/info`.

Foi criado:

- `GET /api/users/me` para consultar o usuário autenticado;
- inicializador idempotente do primeiro administrador usando `ADMIN_NAME`, `ADMIN_EMAIL` e `ADMIN_PASSWORD`.

O inicializador:

- só executa a criação quando as três variáveis existem;
- normaliza o e-mail;
- não sobrescreve usuários existentes;
- não registra senha ou e-mail nos logs.

## 4. Categorias

Foi criado o módulo `category`, contendo:

- entidade JPA;
- repository;
- service transacional;
- DTOs com records;
- controller público;
- controller administrativo.

Campos implementados:

- UUID `id`;
- `name`;
- `slug` único;
- `active`;
- `createdAt`;
- `updatedAt`.

Regras implementadas:

- slug normalizado sem acentos;
- conflito para nomes/slugs duplicados;
- categorias inativas não aparecem nas consultas públicas;
- uma categoria utilizada por notícias não pode ser excluída.

Endpoints:

- `GET /api/categories`;
- `GET /api/categories/{slug}`;
- `POST /api/admin/categories`;
- `PUT /api/admin/categories/{id}`;
- `DELETE /api/admin/categories/{id}`.

## 5. Notícias

Foi criado o módulo `news`, contendo:

- entidade JPA;
- enum `NewsStatus`;
- repository com specifications;
- service transacional;
- DTOs com records;
- controllers público e administrativo.

Campos implementados:

- UUID `id`;
- `title`;
- `slug` único;
- `summary`;
- `content`;
- `coverImageUrl`;
- `status`: `DRAFT`, `PUBLISHED` ou `ARCHIVED`;
- `featured`;
- `urgent`;
- `category`;
- `author`;
- `publishedAt`;
- `createdAt`;
- `updatedAt`.

Regras implementadas:

- notícias são criadas como `DRAFT`;
- slug é gerado automaticamente a partir do título;
- colisões de slug recebem sufixos numéricos;
- apenas administradores gerenciam notícias;
- consultas públicas retornam somente notícias publicadas;
- a primeira publicação define `publishedAt`;
- republicar não altera a data da primeira publicação;
- entidades JPA não são retornadas pelos controllers;
- validação de campos obrigatórios e limites de tamanho;
- exclusão física no MVP;
- paginação e ordenação;
- filtro por categoria;
- busca por título e resumo;
- carregamento conjunto de categoria e autor para evitar N+1;
- serialização paginada estável via DTO.

Endpoints públicos:

- `GET /api/news`;
- `GET /api/news/{slug}`;
- `GET /api/news/featured`;
- `GET /api/news/search?q=...`.

Endpoints administrativos:

- `GET /api/admin/news`;
- `GET /api/admin/news/{id}`;
- `POST /api/admin/news`;
- `PUT /api/admin/news/{id}`;
- `PATCH /api/admin/news/{id}/publish`;
- `PATCH /api/admin/news/{id}/archive`;
- `DELETE /api/admin/news/{id}`.

## 6. Upload de imagens

Foi criada a abstração `MediaStorageService` e a implementação `CloudinaryMediaStorageService`.

O endpoint implementado é:

- `POST /api/admin/uploads/images`.

Regras:

- exige perfil `ADMIN`;
- aceita somente arquivos cujo tipo seja imagem;
- limite de 5 MB;
- retorna URL HTTPS segura e identificador público;
- utiliza `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY` e `CLOUDINARY_API_SECRET`;
- sem credenciais, a aplicação continua iniciando e o endpoint retorna HTTP 503 com mensagem de configuração clara;
- nenhum segredo é registrado nos logs.

## 7. Web Push

Foi criado o módulo `push` com:

- entidade de inscrição;
- repository;
- service de cadastro/cancelamento;
- DTOs;
- controller público;
- serviço assíncrono de envio Web Push com VAPID.

Campos persistidos:

- UUID `id`;
- `endpoint` único;
- `p256dh`;
- `auth`;
- `active`;
- usuário opcional;
- `createdAt`;
- `updatedAt`.

Endpoints:

- `POST /api/push/subscriptions`;
- `DELETE /api/push/subscriptions`.

Regras:

- cadastro idempotente;
- endpoint único impede duplicidade;
- uma nova inscrição reativa e atualiza uma inscrição existente;
- vínculo opcional com usuário autenticado;
- publicação inicial de notícia urgente dispara notificação assíncrona;
- falha de uma inscrição não impede a publicação nem os demais envios;
- respostas definitivas 404 ou 410 desativam a inscrição;
- ausência de VAPID não impede o startup;
- logs informam indisponibilidade sem revelar chaves.

Variáveis utilizadas:

- `VAPID_PUBLIC_KEY`;
- `VAPID_PRIVATE_KEY`;
- `VAPID_SUBJECT`.

## 8. Tratamento de erros

O tratamento centralizado utiliza `ProblemDetail` para:

- validação de DTOs — HTTP 400;
- recurso não encontrado — HTTP 404;
- conflitos — HTTP 409;
- credenciais inválidas — HTTP 401;
- token ausente, expirado ou inválido — HTTP 401;
- acesso negado — HTTP 403;
- upload inválido ou grande demais — HTTP 400;
- integração externa não configurada — HTTP 503.

As mensagens retornadas pela API estão em português.

## 9. Infraestrutura e documentação

Foram adicionados:

- Spring Boot Actuator;
- exposição somente de `health` e `info`;
- springdoc-openapi compatível com Spring Boot 4;
- esquema de autenticação Bearer JWT no OpenAPI;
- Swagger UI;
- configuração CORS por variável de ambiente;
- `.env.example` sem valores secretos;
- README completo;
- coleção Postman;
- checklist de deploy no README;
- reparo no launcher Windows `mvnw.cmd` para funcionar no ambiente atual;
- SQL detalhado desligado por padrão, podendo ser habilitado por `JPA_SHOW_SQL`.

Arquivos de referência:

- `README.md`;
- `.env.example`;
- `postman/Maravilhas-Noticias.postman_collection.json`.

## 10. Migrations

Migrations existentes e criadas:

1. `V1__create_users_table.sql` — usuários;
2. `V2__create_categories_and_news.sql` — categorias, notícias, índices, constraints e relacionamentos;
3. `V3__create_push_subscriptions.sql` — inscrições push, índices e relacionamento opcional com usuário.

As migrations foram aplicadas e validadas pelo Flyway. Nenhuma migration previamente executada foi alterada.

## 11. Dependências adicionadas

- Spring Boot Actuator;
- springdoc-openapi `3.1.1`;
- Cloudinary HTTP5 `2.4.0`;
- Web Push `5.1.2`;
- Apache HttpClient `4.5.14`;
- jose4j `0.7.9`.

As versões foram verificadas no Maven Central e compiladas com Spring Boot 4.1.1 e Java 21.

## 12. Testes

Foram mantidos os testes existentes e adicionados testes para:

- normalização de slugs;
- preservação da primeira data de publicação;
- rejeição de credenciais desconhecidas;
- carregamento completo do contexto Spring.

Resultados finais:

- testes executados: 4;
- falhas: 0;
- erros: 0;
- ignorados: 0;
- classes compiladas: 55;
- `mvnw test`: sucesso;
- `mvnw clean verify`: sucesso;
- JAR executável gerado com sucesso.

## 13. Smoke tests

A aplicação foi iniciada usando o JAR gerado e o PostgreSQL local.

Resultados:

| Verificação | Resultado |
|---|---:|
| `GET /actuator/health` | HTTP 200 |
| `GET /api/categories` | HTTP 200 |
| `GET /api/news` | HTTP 200 |
| `GET /v3/api-docs` | HTTP 200 |
| Rota ADMIN sem token | HTTP 401 |
| Login com campos ausentes | HTTP 400 |

## 14. Commits locais criados

```text
92f606a feat: complete JWT security and admin bootstrap
6d3bded feat: implement categories and news management
172ef8e feat: add media upload and web push notifications
f97ed37 test: add backend coverage and API documentation
bb6c7b4 docs: complete backend setup and deployment guide
6598161 fix: stabilize paginated API responses
```

Nenhum push foi realizado.

## 15. Variáveis de ambiente

Obrigatórias:

- `DB_PASSWORD`;
- `JWT_SECRET` — valor Base64 correspondente a pelo menos 32 bytes aleatórios.

Configuração geral:

- `DB_URL`;
- `DB_USERNAME`;
- `JWT_EXPIRATION_SECONDS`;
- `APP_CORS_ALLOWED_ORIGINS`;
- `JPA_SHOW_SQL`.

Administrador inicial opcional:

- `ADMIN_NAME`;
- `ADMIN_EMAIL`;
- `ADMIN_PASSWORD`.

Cloudinary opcional:

- `CLOUDINARY_CLOUD_NAME`;
- `CLOUDINARY_API_KEY`;
- `CLOUDINARY_API_SECRET`.

Web Push opcional:

- `VAPID_PUBLIC_KEY`;
- `VAPID_PRIVATE_KEY`;
- `VAPID_SUBJECT`.

## 16. Pendências externas reais

- Configurar uma conta Cloudinary para testar upload real.
- Gerar e configurar o par de chaves VAPID para testar notificações em navegadores reais.
- Definir as três variáveis do administrador para criar o primeiro usuário `ADMIN` automaticamente.
- Configurar origens CORS reais antes do deploy.

Essas pendências não impedem a aplicação de iniciar.

## 17. Comandos de execução

No PowerShell, defina as variáveis obrigatórias:

```powershell
$env:DB_PASSWORD = "senha-do-postgresql"

$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$rng.Dispose()
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

Executar testes:

```powershell
.\mvnw test
.\mvnw clean verify
```

Iniciar a aplicação:

```powershell
.\mvnw spring-boot:run
```

URLs locais:

- API: `http://localhost:8080`;
- Swagger: `http://localhost:8080/swagger-ui.html`;
- OpenAPI: `http://localhost:8080/v3/api-docs`;
- Health: `http://localhost:8080/actuator/health`.

## 18. Estado final do repositório na entrega

- todas as alterações do MVP foram commitadas localmente;
- working tree estava limpo após a implementação;
- migrations estavam ordenadas de `V1` a `V3`;
- varredura não encontrou senhas, tokens, chaves privadas ou segredos reais versionados;
- nenhum commit foi enviado ao repositório remoto.

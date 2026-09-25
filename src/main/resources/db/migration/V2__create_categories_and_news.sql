CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_categories_slug UNIQUE (slug)
);

CREATE TABLE news (
    id UUID PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    summary VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    cover_image_url VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    urgent BOOLEAN NOT NULL DEFAULT FALSE,
    category_id UUID NOT NULL,
    author_id UUID NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_news_slug UNIQUE (slug),
    CONSTRAINT ck_news_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    CONSTRAINT fk_news_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_news_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE INDEX idx_categories_active_name ON categories(active, name);
CREATE INDEX idx_news_publication ON news(status, published_at DESC);
CREATE INDEX idx_news_category_status ON news(category_id, status, published_at DESC);
CREATE INDEX idx_news_featured ON news(featured, status, published_at DESC);
CREATE INDEX idx_news_urgent ON news(urgent, status, published_at DESC);
CREATE INDEX idx_news_author ON news(author_id);

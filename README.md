# PollCore

Aplicación móvil Android para la creación y gestión de encuestas. Proyecto desarrollado como Trabajo de Fin de Grado (TFG) en Desarrollo de Aplicaciones Multiplataforma.

## Características

*   Registro e inicio de sesión de usuarios.
*   Creación de encuestas con hasta 4 opciones.
*   Sistema de votación con verificación de duplicidad.
*   Visualización de resultados con gráficos de progreso.
*   Sección de comentarios en cada encuesta.
*   Reporte de contenido inapropiado (encuestas y comentarios).
*   Panel de usuario para gestionar el perfil y eliminar la cuenta.

## Tecnologías utilizadas

*   **Lenguaje:** Java
*   **IDE:** Android Studio
*   **Base de Datos:** PostgreSQL

## Requisitos previos

Antes de ejecutar la aplicación, asegúrate de tener instalado lo siguiente:

1.  **Android Studio**
2.  **PostgreSQL** con pgAdmin.

## Configuración de la Base de Datos

Sigue estos pasos para crear y configurar la base de datos en pgAdmin:

### Paso 1: Crear la base de datos

1.  Abre **pgAdmin**.
2.  Haz clic Query Tool.
3.  Copia y pega en pgAdmin el script.
4.  Ejecuta el script.

SCRIPT:

-- ============================================

-- 1. SCHEMA
-- DROP SCHEMA IF EXISTS pollcore CASCADE;

CREATE SCHEMA IF NOT EXISTS pollcore;

SET search_path TO pollcore;

-- ============================================
-- 2. TABLAS

-- 2.1 Tabla Usuario

CREATE TABLE pollcore.users (

    id_user SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_private BOOLEAN DEFAULT TRUE,
    answered_polls INTEGER[] DEFAULT '{}',  -- Array de ids de encuestas respondidas
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2.2 Tabla Encuesta

CREATE TABLE pollcore.polls (

    id_poll SERIAL PRIMARY KEY,
    id_user INTEGER NOT NULL,               -- Creador de la encuesta
    title VARCHAR(200) NOT NULL,
    description TEXT,
    question TEXT NOT NULL,                 -- Pregunta directa en la tabla
    option1 VARCHAR(255) NOT NULL,
    option2 VARCHAR(255) NOT NULL,
    option3 VARCHAR(255),                   -- Opcional (puede ser NULL)
    option4 VARCHAR(255),                   -- Opcional (puede ser NULL)
    count_option1 INTEGER DEFAULT 0,
    count_option2 INTEGER DEFAULT 0,
    count_option3 INTEGER DEFAULT 0,
    count_option4 INTEGER DEFAULT 0,
    total_votes INTEGER DEFAULT 0,          -- Contador de usuarios que han respondido
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES pollcore.users(id_user) ON DELETE CASCADE
);

-- 2.3 Tabla Voto (para controlar quién votó qué)

CREATE TABLE pollcore.votes (

    id_vote SERIAL PRIMARY KEY,
    id_user INTEGER NOT NULL,
    id_poll INTEGER NOT NULL,
    selected_option INTEGER NOT NULL CHECK (selected_option IN (1,2,3,4)),
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_user) REFERENCES pollcore.users(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_poll) REFERENCES pollcore.polls(id_poll) ON DELETE CASCADE,
    UNIQUE(id_user, id_poll)  -- Un usuario solo puede votar una vez por encuesta
);

-- 2.4 Tabla Comentario (con array de respuestas)

CREATE TABLE pollcore.comments (

    id_comment SERIAL PRIMARY KEY,
    id_poll INTEGER NOT NULL,
    id_user INTEGER NOT NULL,
    content TEXT NOT NULL,
    reply_to INTEGER NULL,                  -- ID del comentario al que responde (NULL = comentario raíz)
    replies_ids INTEGER[] DEFAULT '{}',     -- Array de ids de respuestas (hijos)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_poll) REFERENCES pollcore.polls(id_poll) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES pollcore.users(id_user) ON DELETE CASCADE,
    FOREIGN KEY (reply_to) REFERENCES pollcore.comments(id_comment) ON DELETE CASCADE
);

-- 2.5 Tabla ReporteEncuesta

CREATE TABLE pollcore.poll_reports (

    id_report SERIAL PRIMARY KEY,
    id_poll INTEGER NOT NULL,
    id_user INTEGER NOT NULL,               -- Usuario que reporta
    reason VARCHAR(255) NOT NULL,
    details TEXT,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_poll) REFERENCES pollcore.polls(id_poll) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES pollcore.users(id_user) ON DELETE CASCADE,
    UNIQUE(id_poll, id_user)                -- Un usuario no puede reportar la misma encuesta dos veces
);

-- 2.6 Tabla ReporteComentario

CREATE TABLE pollcore.comment_reports (

    id_report SERIAL PRIMARY KEY,
    id_comment INTEGER NOT NULL,
    id_user INTEGER NOT NULL,               -- Usuario que reporta
    reason VARCHAR(255) NOT NULL,
    details TEXT,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_comment) REFERENCES pollcore.comments(id_comment) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES pollcore.users(id_user) ON DELETE CASCADE,
    UNIQUE(id_comment, id_user)             -- Un usuario no puede reportar el mismo comentario dos veces
);

-- ============================================
-- 3. ÍNDICES

CREATE INDEX idx_users_answered_polls ON pollcore.users USING GIN (answered_polls);
CREATE INDEX idx_polls_user ON pollcore.polls(id_user);
CREATE INDEX idx_polls_created_at ON pollcore.polls(created_at DESC);
CREATE INDEX idx_votes_user ON pollcore.votes(id_user);
CREATE INDEX idx_votes_poll ON pollcore.votes(id_poll);
CREATE INDEX idx_comments_poll ON pollcore.comments(id_poll);
CREATE INDEX idx_comments_user ON pollcore.comments(id_user);
CREATE INDEX idx_comments_reply_to ON pollcore.comments(reply_to);
CREATE INDEX idx_comments_replies_ids ON pollcore.comments USING GIN (replies_ids);
CREATE INDEX idx_poll_reports_poll ON pollcore.poll_reports(id_poll);
CREATE INDEX idx_comment_reports_comment ON pollcore.comment_reports(id_comment);


===================================================================================
                               FIN DEL SCRIPT
===================================================================================

Abre el proyecto en Android Studio.

Configura la conexión a la base de datos en la clase ConexionBBDD.java (ruta: app/src/main/java/com/example/pollcore/connection/):

private static final String URL = "jdbc:postgresql://10.0.2.2:5432/postgres";

private static final String USER = "postgres"; // Tu usuario de PostgreSQL

private static final String PASSWORD = "12020206"; // Tu contraseña

Conecta tu dispositivo (o inicia un emulador).

Ejecuta la aplicación pulsando el botón Run en Android Studio.


Enlace al repositorio

https://github.com/41lici61/PollCore

Autor

Alicia (41lici61) - Desarrollo completo del proyecto

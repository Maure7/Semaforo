create table if not exists antigos_donos (
  id                integer generated always as identity primary key,
  nome              varchar(100) not null,
  cidade            varchar(100),
  telefone          varchar(50),
  cedula            varchar(50)
);

create table if not exists clientes (
  id                integer generated always as identity primary key,
  nome              varchar(120) not null,
  ci                varchar(60) not null,
  domicilio         varchar(200),
  estado_civil      varchar(40)
);

create table if not exists vendedores (
  id_vendedor       integer generated always as identity primary key,
  nome              varchar(120) not null,
  ci                varchar(60),
  domicilio         varchar(200),
  estado_civil      varchar(40)
);

create table if not exists veiculos (
  id                integer generated always as identity primary key,
  marca             varchar(100) not null,
  modelo            varchar(100) not null,
  km                integer,
  preco             numeric(12,2),
  cor               varchar(50),
  placa             varchar(50),
  ano               integer,
  cidade            varchar(100),
  disponivel        boolean not null default true,
  id_antigo_dono    integer references antigos_donos(id) on delete set null,
  image_path        bytea,
  padron            integer,
  tipo_vehiculo     varchar(60),
  combustible       varchar(60),
  numero_motor      varchar(120),
  numero_chasis     varchar(120)
);

create table if not exists compra (
  id                integer generated always as identity primary key,
  veiculo_id        integer not null references veiculos(id) on delete cascade,
  data              date not null,
  preco             numeric(12,2) not null,
  divida            boolean not null default false
);

create table if not exists venda (
  id                integer generated always as identity primary key,
  veiculo_id        integer not null references veiculos(id) on delete restrict,
  cliente_id        integer not null references clientes(id) on delete restrict,
  preco             numeric(12,2) not null,
  data              date not null,
  metodo_pagamento  varchar(60),
  parcelas          integer,
  vendedor_id       integer references vendedores(id_vendedor) on delete set null
);

create table if not exists manutencao (
  id                integer generated always as identity primary key,
  data              date not null,
  descricao         text,
  custo             numeric(12,2) not null,
  veiculo_id        integer not null references veiculos(id) on delete cascade,
  km_manutencao     integer
);

create table if not exists documentos_vehiculo (
  id_documento      integer generated always as identity primary key,
  veiculo_id        integer not null references veiculos(id) on delete cascade,
  tipo_documento    varchar(100),
  numero_documento  varchar(100),
  fecha_emision     date,
  fecha_vencimiento date,
  estado_posesion   varchar(60),
  fecha_entrega     date,
  observaciones     text
);

create index if not exists idx_veiculos_disponivel on veiculos(disponivel);
create index if not exists idx_venda_data on venda(data desc);
create index if not exists idx_compra_data on compra(data desc);

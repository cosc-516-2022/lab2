create table part
				 (p_partkey             int                     not null primary key clustered,
				  p_name                varchar(55)             not null,
				  p_mfgr                char(25)                not null,
				  p_brand               char(10)                not null,
				  p_type                varchar(25)             not null,
				  p_size                int                     not null,
				  p_container           char(10)                not null,
				  p_retailprice         money                   not null,
				  p_comment             varchar(23)             not null);

create table supplier
				 (s_suppkey             int                    not null primary key clustered,
				  s_name                char(25)               not null,
				  s_address             char(40)               not null,
				  s_nationkey           int                    not null,
				  s_phone               char(15)               not null,
				  s_acctbal             money                  not null,
				  s_comment             varchar(101)           not null);

create table partsupp
				 (ps_partkey    int                    not null,
				  ps_suppkey    int                    not null,
				  ps_availqty   int                    not null,
				  ps_supplycost money                  not null,
				  ps_comment    varchar(199)           not null,
				constraint ps_pk primary key clustered (ps_partkey, ps_suppkey));

create table customer
				 (c_custkey     int                    not null primary key clustered,
				  c_name        varchar(25)            not null,
				  c_address     varchar(40)            not null,
				  c_nationkey   int                    not null,
				  c_phone       char(15)               not null,
				  c_acctbal     money                  not null,
				  c_mktsegment  char(10)               not null,
				  c_comment     char(117)              not null);

create table orders
				 (o_orderkey      int                  primary key clustered,
				  o_custkey       int                  not null,
				  o_orderstatus   char(1)              ,
				  o_totalprice    money                ,
				  o_orderdate     datetime             not null,
				  o_orderpriority char(15)             ,
				  o_clerk         char(15)             ,
				  o_shippriority  int                  ,
				  o_comment       varchar(79)          );

create table lineitem
				(l_orderkey               int          not null,
				 l_partkey                int          not null,
				 l_suppkey                int          not null,
				 l_linenumber             int          not null,
				 l_quantity               money        not null,
				 l_extendedprice          money        not null,
				 l_discount               money        ,
				 l_tax                    money        ,
				 l_returnflag             char(1)      ,
				 l_linestatus             char(1)      ,
				 l_shipdate               datetime     ,
				 l_commitdate             datetime     ,
				 l_receiptdate            datetime     ,
				 l_shipinstruct           char(25)     ,
				 l_shipmode               char(10)     ,
				 l_comment                varchar(44)  ,
				constraint l_pk primary key clustered (l_orderkey, l_linenumber));

create table nation
				(n_nationkey        int          not null primary key clustered,
				 n_name             char(25)     not null,
				 n_regionkey        int          not null,
				 n_comment          varchar(152) not null);

create table region
				(r_regionkey        int          not null primary key clustered,
				 r_name             char(25)     not null,
				 r_comment          varchar(152) not null);


alter table supplier add constraint fknation foreign key (s_nationkey) references nation (n_nationkey);

alter table customer add constraint fkcnation foreign key (c_nationkey) references nation (n_nationkey);

alter table orders add constraint fkcust foreign key (o_custkey) references customer (c_custkey);

alter table lineitem add constraint fkorders foreign key (l_orderkey) references orders (o_orderkey);

alter table lineitem add constraint fkpps foreign key (l_partkey, l_suppkey) references partsupp (ps_partkey, ps_suppkey);

alter table nation add constraint fkregion foreign key (n_regionkey) references region (r_regionkey);

alter table partsupp add constraint fkp foreign key (ps_partkey) references part(p_partkey);

alter table partsupp add constraint fks foreign key (ps_suppkey) references supplier(s_suppkey);

alter table lineitem add constraint partkeyFK foreign key (l_partkey) references dbo.part (p_partkey);

alter table lineitem add constraint suppkeyFK foreign key (l_suppkey) references dbo.supplier (s_suppkey);





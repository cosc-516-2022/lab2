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


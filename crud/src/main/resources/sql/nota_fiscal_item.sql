create table nota_fiscal_item (
       id bigint not null,
        preco_unitario float,
        quantidade integer,
        valor_total float,
        nota_fiscal_id integer,
        produto_id integer,
        primary key (id)
    )
    
        alter table nota_fiscal_item 
       add constraint FK_nota_fiscal_id
       foreign key (nota_fiscal_id) 
       references nota_fiscal
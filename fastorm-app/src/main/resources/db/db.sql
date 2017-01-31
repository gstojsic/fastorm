create table if not exists Customer (
    id     bigint primary key,
    firstName    varchar(100),
    lastName    varchar(100)
);

drop table Customer

INSERT INTO public.customer
(id, first_name, last_name)
VALUES(0, '', '');

INSERT INTO public.customer
(id, first_name, last_name)
VALUES(0, 'sds', 'sds');


select count(*) from customer

select firstname from customer
select * from customer
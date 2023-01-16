insert into ordered
(ordered_id,customer_id,total_price)
values(1,10001,50);
insert into ordered
(ordered_id,customer_id,total_price)
values(2,10001,20);
insert into ordered
(ordered_id,customer_id,total_price)
values(3,10002,20);
insert into ordered
(ordered_id,customer_id,total_price)
values(4,10003,30);

insert into order_detail
(order_detail_id,ordered_id,product_id,quantity)
values(101,1,1001,1);
insert into order_detail
(order_detail_id,ordered_id,product_id,quantity)
values(102,1,1003,2);
insert into order_detail
(order_detail_id,ordered_id,product_id,quantity)
values(103,2,1002,1);
insert into order_detail
(order_detail_id,ordered_id,product_id,quantity)
values(104,3,1001,1);
insert into order_detail
(order_detail_id,ordered_id,product_id,quantity)
values(105,4,1002,3);
use ssafy_cafe;
	

-- t_user
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('test', 'testUser', 'test', 50, '010-8019-4628', 200000);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('admin', 'admin', 'admin', 100, '010-6242-7712', 1000000);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id01', 'name01', 'pass01', 4, '010-0001-0001', 0);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id02', 'name02', 'pass02', 1, '010-0002-0002', 10000);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id03', 'name03', 'pass03', 0, '010-0003-0003', 20000);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id04', 'name04', 'pass04', 0, '010-0004-0004', 300);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id05', 'name05', 'pass05', 0, '010-0005-0005', 4000);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id06', 'name06', 'pass06', 0, '010-0006-0006', 0);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id07', 'name07', 'pass07', 0, '010-0007-0007', 0);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id08', 'name08', 'pass08', 0, '010-0008-0008', 0);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id09', 'name09', 'pass09', 0, '010-0009-0009', 0);
INSERT INTO t_user (id, name, pass, stamps, phone, money) VALUES ('id10', 'name10', 'pass10', 0, '010-0010-0010', 0);


-- t_product_type
INSERT INTO t_product_type(type_name) VALUES('coffee');
INSERT INTO t_product_type(type_name) VALUES('tea');
INSERT INTO t_product_type(type_name) VALUES('ade');
INSERT INTO t_product_type(type_name) VALUES('frappuccino');
INSERT INTO t_product_type(type_name) VALUES('dessert');


-- t_product
-- type : 1(coffee)
INSERT INTO t_product (name, type, price, img) VALUES ('아메리카노', 1, 4100, 'americano.png');
INSERT INTO t_product (name, type, price, img) VALUES ('카페 라떼', 1, 4600, 'cafelatte.png');
INSERT INTO t_product (name, type, price, img) VALUES ('카라멜 마키아또', 1, 5600, 'caramelmacchiatocoffee.png');
INSERT INTO t_product (name, type, price, img) VALUES ('아인슈페너', 1, 4600, 'ainsupeno.png');

-- type : 2(tea)
INSERT INTO t_product (name, type, price, img) VALUES ('그린티', 2, 5600, 'greantea.png');
INSERT INTO t_product (name, type, price, img) VALUES ('아이스티', 2, 5800, 'icetea.png');
INSERT INTO t_product (name, type, price, img) VALUES ('레드오렌지티', 2, 5600, 'redorangetea.png');
INSERT INTO t_product (name, type, price, img) VALUES ('레몬티', 2, 5300, 'remontea.png');

-- type : 3(ade)
INSERT INTO t_product (name, type, price, img) VALUES ('핑크 캐모마일 릴렉서', 3, 6100, 'pinkchamomilerelaxer.png');
INSERT INTO t_product (name, type, price, img) VALUES ('블랙 티 레모네이드 피지오', 3, 5400, 'blackteaLemonadefizzio.png');
INSERT INTO t_product (name, type, price, img) VALUES ('패션 탱고 티 레모네이드 피지오', 3, 5400, 'passiontangotealemonadefizzio.png');
INSERT INTO t_product (name, type, price, img) VALUES ('샤이닝 머스켓 에이드', 3, 5900, 'shiningmuscatade.png');
INSERT INTO t_product (name, type, price, img) VALUES ('샹그리아 에이드', 3, 6500, 'sangriaade.png');
INSERT INTO t_product (name, type, price, img) VALUES ('레몬 셔벗 에이드', 3, 6000, 'lemonsorbetade.png');
INSERT INTO t_product (name, type, price, img) VALUES ('오렌지 에이드', 3, 5500, 'orangeade.png');

-- type : 4(frappuccino)
INSERT INTO t_product (name, type, price, img) VALUES ('딸기 프라푸치노', 4, 6300, 'stroberyfrapp.png');
INSERT INTO t_product (name, type, price, img) VALUES ('오레오 프라푸치노', 4, 6100, 'oreofrapp.png');
INSERT INTO t_product (name, type, price, img) VALUES ('자바 칩 프라푸치노', 4, 6100, 'javachipfrapp.png');
INSERT INTO t_product (name, type, price, img) VALUES ('초콜릿 크림 칩 프라푸치노', 4, 5700, 'chocolatecreamchipfrappuccino.png');

-- type : 5(dessert)
INSERT INTO t_product (name, type, price, img) VALUES ('티라미슈 케이크', 5, 6900, 'tiramisu.png');
INSERT INTO t_product (name, type, price, img) VALUES ('초코 케이크', 5, 6100, 'chococake.png');
INSERT INTO t_product (name, type, price, img) VALUES ('초코 머핀', 5, 5500, 'chocomuffin.png');
INSERT INTO t_product (name, type, price, img) VALUES ('쿠키', 5, 5500, 'cookies.png');
INSERT INTO t_product (name, type, price, img) VALUES ('프레즐', 5, 6200, 'pretzel.png');


-- t_comment
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('test', 01, 01, 'test comment 01');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('test', 01, 10, 'test comment 02');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('test', 02, 01, 'test comment 03');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('test', 03, 01, 'test comment 04');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('test', 04, 01, 'test comment 05');

INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id01', 01, 01, 'comment 01');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id02', 01, 02, 'comment 02');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id03', 01, 03, 'comment 03');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id04', 04, 04, 'comment 04');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id05', 05, 05, 'comment 05');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id06', 06, 06, 'comment 06');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id07', 07, 07, 'comment 07');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id08', 08, 08, 'comment 08');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id09', 09, 09, 'comment 09');
INSERT INTO t_comment (user_id, product_id, rating, comment) VALUES ('id10', 10, 10, 'comment 10');


-- t_order
INSERT INTO t_order (user_id, order_table, completed) VALUES ('test', 'order_table 01', 0);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('test', 'order_table 05', 0);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('test', 'order_table 03', 0);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('test', 'order_table 10', 0);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('test', 'take-out', 1);

INSERT INTO t_order (user_id, order_table, completed) VALUES ('id01', 'order_table 01', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id02', 'order_table 02', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id03', 'order_table 03', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id04', 'order_table 04', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id05', 'order_table 05', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id06', 'order_table 06', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id07', 'order_table 07', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id08', 'order_table 08', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id09', 'order_table 09', 1);
INSERT INTO t_order (user_id, order_table, completed) VALUES ('id10', 'order_table 10', 1);


-- t_order_detail
INSERT INTO t_order_detail (order_id, product_id, quantity, type, syrup, shot) VALUES (01, 01, 01, 1, '카라멜', 2);
INSERT INTO t_order_detail (order_id, product_id, quantity, type) VALUES (01, 02, 03, 1);
INSERT INTO t_order_detail (order_id, product_id, quantity, type) VALUES (01, 24, 01, 1);

INSERT INTO t_order_detail (order_id, product_id, quantity, type) VALUES (02, 13, 01, 1);

INSERT INTO t_order_detail (order_id, product_id, quantity, type) VALUES (03, 11, 01, 1);

INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (04, 22, 01);

INSERT INTO t_order_detail (order_id, product_id, quantity, type, syrup) VALUES (05, 03, 01, 0, '바닐라');

INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (02, 01, 01);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (03, 03, 03);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (04, 04, 04);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (05, 05, 05);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (06, 06, 06);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (07, 07, 07);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (08, 08, 08);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (09, 09, 09);
INSERT INTO t_order_detail (order_id, product_id, quantity) VALUES (10, 10, 10);


-- t_stamp
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('test', 1, 09);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id01', 2, 04);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id02', 3, 01);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id03', 4, 03);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id04', 5, 04);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id05', 6, 05);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id06', 7, 06);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id07', 8, 07);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id08', 9, 08);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id09', 10, 09);
INSERT INTO t_stamp (user_id, order_id, quantity) VALUES ('id10', 11, 10);



-- t_notification order/event/user
INSERT INTO t_notification (user_id, category, content) VALUES ('admin', 'event', 'cafe 이벤트');
INSERT INTO t_notification (user_id, category, content) VALUES ('admin', 'order', '주문하신 메뉴 제조 완료되었으니 찾아가셈');
INSERT INTO t_notification (user_id, category, content) VALUES ('admin', 'user', '비밀번호 변경하셈');
INSERT INTO t_notification (user_id, category, content) VALUES ('admin', 'order', '주문 접수함');
INSERT INTO t_notification (user_id, category, content) VALUES ('test', 'event', 'event1');
INSERT INTO t_notification (user_id, category, content) VALUES ('test', 'order', '이보연바보');
INSERT INTO t_notification (user_id, category, content) VALUES ('test', 'user', '스타벅스짱');

-- t_user_custom
INSERT INTO t_user_custom (user_id, product_id, type, syrup, shot) VALUES ('test', 2, 1, '바닐라', '2');
INSERT INTO t_user_custom (user_id, product_id, type, syrup) VALUES ('test', 1, 1, '카라멜');
INSERT INTO t_user_custom (user_id, product_id, type) VALUES ('test', 9, 1);

select * from t_order;
select * from t_order_detail;

commit;
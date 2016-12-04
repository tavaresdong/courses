.read data.sql

-- Q1
CREATE TABLE flight_costs as
with
  cost(day, pp, p, cur) as (
    select 1, 0, 0, 20 union
    select 2, 0, 20, 30 union
    select 3, 20, 30, 40 union
    select day + 1, p, cur, (cur + p) / 2 + 5 * ((day + 1) % 7) FROM cost WHERE day >= 3 AND day < 25 
  )
  SELECT day, cur FROM cost ORDER BY day;

-- Q2
CREATE TABLE schedule as
with
  plan(route, relay, src, dest, cost) as (
    select departure || ", " || arrival, 1, departure, arrival, price FROM flights union
    select p.route || ", " || f.arrival, p.relay + 1, p.src, f.arrival, p.cost + f.price FROM plan as p, flights as f WHERE p.dest = f.departure and p.relay <= 1
  )
  SELECT route, cost FROM plan WHERE src = 'SFO' and dest = 'PDX' ORDER BY cost;


-- Q3
CREATE TABLE shopping_cart as
with
  shopping_list(list, last_price, remain_price) as (
    select item, price, 60 - price FROM supermarket WHERE price <= 60 union
    select list || ", " || s.item, s.price, l.remain_price - s.price FROM shopping_list as l, supermarket as s WHERE l.last_price <= s.price AND s.price <= l.remain_price
  )
  SELECT list, remain_price FROM shopping_list ORDER BY remain_price, list;


-- Q4
CREATE TABLE number_of_options as
  select count(DISTINCT meat) FROM main_course;


-- Q5
CREATE TABLE calories as
  SELECT count(*) FROM main_course as m, pies as p WHERE m.calories + p.calories < 2500;


-- Q6
CREATE TABLE healthiest_meats as
  SELECT m.meat, MIN(m.calories + p.calories) FROM main_course as m, pies as p GROUP BY m.meat HAVING MAX(m.calories + p.calories) < 3000;


-- Q7
CREATE TABLE average_prices as
  SELECT category, AVG(MSRP) FROM products GROUP BY category;


-- Q8
CREATE TABLE lowest_prices as
with
  min_price(prod, price) as (
    select item, MIN(price) from inventory group by item
  )
  SELECT i.item as prod, i.store as store, m.price as price FROM inventory as i, min_price as m WHERE i.item = m.prod AND i.price = m.price;


-- Q9
CREATE TABLE shopping_list as
with
  best_prod(category, performance) as (
    select category, MIN(MSRP / rating) from products group by category
  ),
  best_prodname(prod) as (
    select p.name from best_prod as b, products as p WHERE p.MSRP / p.rating = b.performance and p.category = b.category
  )
  SELECT b.prod as prod, l.store as store FROM best_prodname as b, lowest_prices as l WHERE b.prod = l.prod ORDER BY b.prod;


-- Q10
CREATE TABLE total_bandwidth as
  SELECT SUM(st.MiBs) FROM shopping_list as sl, stores as st WHERE sl.store = st.store  ;


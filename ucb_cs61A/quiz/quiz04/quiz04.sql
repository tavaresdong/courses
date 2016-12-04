create table pizzas as
  select "Pizzahhh" as name, 12 as open, 15 as close union
  select "La Val's"        , 11        , 22          union
  select "Sliver"          , 11        , 20          union
  select "Cheeseboard"     , 16        , 23          union
  select "Emilia's"        , 13        , 18;

create table meals as
  select "breakfast" as meal, 11 as time union
  select "lunch"            , 13         union
  select "dinner"           , 19         union
  select "snack"            , 22;

-- Two meals at the same place
create table double as
select m1.meal, m2.meal, p.name FROM meals as m1, meals as m2, pizzas as p WHERE m1.time - m2.time < -6 AND m1.time >= p.open AND m1.time <= p.close ANd m2.time >= p.open AND m2.time <= p.close ;


-- Pizza options for every meal
create table options as
with
  choices(meal, time, num, last, restaurants) as (
    select m.meal, m.time, 1, p.name, p.name from meals as m, pizzas as p where m.time >= p.open and m.time <= p.close union
    select c.meal, c.time, c.num + 1, p.name, restaurants || ", " || p.name FROM choices as c,  pizzas as p where p.name > c.last and c.time >= p.open and c.time <= p.close   
  ),
  max_rest(meal, num) as (
    select meal, MAX(num) from choices group by meal 
  )
select c.meal, c.num, c.restaurants from choices as c, max_rest as m where c.num = m.num and c.meal = m.meal order by c.time;



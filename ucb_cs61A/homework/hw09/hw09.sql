create table parents as
  select "abraham" as parent, "barack" as child union
  select "abraham"          , "clinton"         union
  select "delano"           , "herbert"         union
  select "fillmore"         , "abraham"         union
  select "fillmore"         , "delano"          union
  select "fillmore"         , "grover"          union
  select "eisenhower"       , "fillmore";

create table dogs as
  select "abraham" as name, "long" as fur, 26 as height union
  select "barack"         , "short"      , 52           union
  select "clinton"        , "long"       , 47           union
  select "delano"         , "long"       , 46           union
  select "eisenhower"     , "short"      , 35           union
  select "fillmore"       , "curly"      , 32           union
  select "grover"         , "short"      , 28           union
  select "herbert"        , "curly"      , 31;

create table sizes as
  select "toy" as size, 24 as min, 28 as max union
  select "mini",        28,        35        union
  select "medium",      35,        45        union
  select "standard",    45,        60;

-------------------------------------------------------------
-- PLEASE DO NOT CHANGE ANY SQL STATEMENTS ABOVE THIS LINE --
-------------------------------------------------------------

-- The size of each dog
create table size_of_dogs as
select d.name, s.size FROM dogs as d, sizes as s WHERE d.height > s.min AND d.height <= s.max;


-- All dogs with parents ordered by decreasing height of their parent
create table by_height as
select p.child FROM parents as p, dogs as d WHERE p.parent = d.name ORDER BY d.height DESC;


-- Sentences about siblings that are the same size
create table sentences as
select d1.name || " and " || d2.name || " are " || s.size || " siblings" FROM parents as p1, parents as p2, dogs as d1, dogs as d2, sizes as s WHERE p1.parent = p2.parent AND p1.child < p2.child AND p1.child = d1.name AND p2.child = d2.name AND d1.height > s.min AND d1.height <= s.max AND d2.height > s.min AND d2.height <= s.max;


-- Ways to stack 4 dogs to a height of at least 170, ordered by total height
create table stacks as
with
  dog_stack(fullname, num, prev_ht, total_ht) as (
    select name, 1, height, height from dogs union
    select fullname || ", " || name, num + 1, height, total_ht + height FROM dog_stack, dogs WHERE prev_ht < height AND num < 4
  )
select fullname, total_ht FROM dog_stack WHERE total_ht > 170 ORDER BY total_ht; 


create table tallest as
with
  dog_info(tenth_height, ht, name) as (
    select height / 10, height, name FROM dogs
  ),
  height_info(max_ht, cnt) as (
    select MAX(ht), COUNT(*) FROM dog_info GROUP BY tenth_height
  )
select d.height, d.name FROM dogs as d, height_info as h WHERE d.height = h.max_ht AND h.cnt > 1;

-- All non-parent relations ordered by height difference
create table non_parents as
with 
  ancestor(one, two, generation) as (
    select parent, child, 1  FROM parents union
    select a.one, p.child, a.generation + 1 FROM ancestor as a, parents as p WHERE a.two = p.parent AND a.generation > 0
  ),
  no_parent(member1, member2, diff) as (
    select a.one, a.two, d1.height - d2.height FROM ancestor as a, dogs as d1, dogs as d2 WHERE a.generation > 1 AND a.one = d1.name AND a.two = d2.name union
    select a.two, a.one, d2.height - d1.height FROM ancestor as a, dogs as d1, dogs as d2 WHERE a.generation > 1 AND a.one = d1.name AND a.two = d2.name
  )
select member1, member2 FROM no_parent ORDER BY diff; 



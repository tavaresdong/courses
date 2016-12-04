.read lab12.sql

-- Q7
CREATE TABLE smallest_int_count as
  SELECT smallest, COUNT(*) FROM students WHERE smallest >= 1 GROUP BY smallest ORDER BY smallest;


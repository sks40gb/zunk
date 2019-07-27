QUERY
========================================================================================================================
http://www.techinterviews.com/31-more-mysql-questions



SELECT user_name, user_isp FROM users LEFT JOIN isps USING (user_id)
 - It’s equivalent to saying 
SELECT user_name, user_isp FROM users LEFT JOIN isps WHERE users.user_id=isps.user_id

------------------------------------------------------------------------------------------------------------------------
How do you find out which auto increment was assigned on the last insert? -
SELECT LAST_INSERT_ID()
will return the last value assigned by the auto_increment function. Note that you don’t have to specify the table name.

------------------------------------------------------------------------------------------------------------------------
When would you use ORDER BY in DELETE statement? -
When you’re not deleting by row ID. Such as in 
DELETE FROM user ORDER BY timestamp LIMIT 1
This will delete the most recently posted question in the table

------------------------------------------------------------------------------------------------------------------------
SHOW INDEX FROM user                        - show all indexes

SHOW COLUMNS FROM user                      - show all columns from address table

SHOW TABLES;                                - show all tables from the selected schema
SHOW TABLES LIKE 'FCL%';                    - show all tables which name starts with FCL

DESC address                                - describe table file

SHOW DATABASES LIKE '%in%';                 - display all schema which contains the word in

SELECT CONCAT ('A', '-', 'B', '*', 'C')     - concate string

SELECT SUBSTR(title, 1, 10)                 - SELECT SUBSTR(COLUMN_NAME, OFFSET STARTS FROM 1, LENGTH)


-----------------------------------------------------------------------------------------------------------------------
How do you convert a string to UTF-8?
SELECT (user_name USING utf8);


------------------------------------------------------------------------------------------------------------------------
What do % and _ mean inside LIKE statement?
% corresponds to 0 or more characters, _ is exactly one character.


------------------------------------------------------------------------------------------------------------------------
REGEXP
SELECT size FROM FILE WHERE size REGEXP '[1-9]'

http://dev.mysql.com/doc/refman/5.0/en/regexp.html

B[an]*s - Bananas, Baaaaas, Bs,

------------------------------------------------------------------------------------------------------------------------
How do you return the a hundred books starting from 25th? - 

SELECT book_title FROM books LIMIT 25, 100

The first number in LIMIT is the offset, the second is the number.

------------------------------------------------------------------------------------------------------------------------

Find the second largest id from the table

SELECT * FROM USER ORDER BY id DESC LIMIT 1,1

------------------------------------------------------------------------------------------------------------------------

What are Aggregate and Scalar functions?

An aggregate function performs operations on a collection of values to return a single scalar value.

AVG()       - Calculates the mean of a collection of values.
COUNT()     - Counts the total number of records in a specific table or view.
MIN()       - Calculates the minimum of a collection of values.
MAX()       - Calculates the maximum of a collection of values.
SUM()       - Calculates the sum of a collection of values.
FIRST()     - Fetches the first element in a collection of values.
LAST()      - Fetches the last element in a collection of values.

A scalar function returns a single value based on the input value.

LEN()       - Calculates the total length of the given field (column).
UCASE()     - Converts a collection of string values to uppercase characters.
LCASE()     - Converts a collection of string values to lowercase characters.
MID()       - Extracts substrings from a collection of string values in a table.
CONCAT()    - Concatenates two or more strings.
RAND()      - Generates a random collection of numbers of given length.
ROUND()     - Calculates the round off integer value for a numeric field (or decimal point values).
NOW()       - Returns the current data & time.
FORMAT()    - Sets the format to display a collection of values.














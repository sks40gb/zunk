STORED PROCEDURE
=======================================================================================================================
VIP
========================================================================================================================
There must be space between DELIMITER and delimiter value
ex : DELIMITER $$

After end of all statement semicolon(;) is required.
ex :
END IF;



http://forums.mysql.com/read.php?98,358569
------------------------------------------------
1. Stored procedure reduced the traffic between application and database server because instead of sending multiple
uncompiled long SQL commands statement, application only has to send the stored procedure name and get the result back.
2. reusable and transparent - to any application which wants to use it.
3. secured - Database administrator can grant the right to application.

disadvantages
-------------
1. database server high load in both memory for and processors
2. you could be asking the database server to perform a number of logical operations or a complex of business logic which is not the role of it.
3. contains declarative SQL so it is very difficult to write a procedure with complexity of business like other languages.
4. cannot debug stored procedure in almost RDMBSs.
5. Writing and maintain stored procedure usually required specialized skill set.


The first stored procedure is very simple. It retrieves all products from products table. First let’s take a look at the stored procedure source code bellow:
############################################################################################
DELIMITER //
CREATE PROCEDURE GetAllProducts()
BEGIN
SELECT * FROM products;
END //
DELIMITER ;
############################################################################################

* The first command you see is DELIMITER //. This command is not related to the stored procedure. DELIMITER statement is used to change the standard delimiter (semicolon) to another

CALL GetAllProducts();


Declaring variables
-------------------
DECLARE variable_name datatype(size) DEFAULT default_value;
DECLARE x, y INT DEFAULT 0;

Assigning variables
-------------------
DECLARE total_count INT DEFAULT 0
SET total_count = 10;

DECLARE total_products INT DEFAULT 0;
SELECT COUNT(*) INTO total_products


Stored Procedure Parameters
----------------------------
* a parameter has one of three modes
1. IN    - indicates that a parameter can be passed into stored procedures but any modification inside stored procedure does not change parameter
2. OUT   - indicates that stored procedure can change this parameter and pass back to the calling program.
3. INOUT -  mode is combined of IN and OUT mode; you can pass parameter into stored procedure and get it back with the new value from calling program.

############################################################################################
DELIMITER $$

CREATE PROCEDURE getTotalFileNumber(IN p_file_type INT(100), OUT total INT)
BEGIN
   SELECT COUNT(*) INTO total FROM fcl_file_number WHERE file_type = p_file_type ;
END$$

DELIMITER ;

CALL getTotalFileNumber('EXPORT',@total);
SELECT @total AS Total;

############################################################################################

@<variable_name> - it will store the variable value in session
ex : @total


Conditional Control
--------------------------------
IF expression THEN commands
[ELSEIF expression THEN commands]
[ELSE commands]
END IF;

** Don't forget to use THEN



CASE
WHEN expression THEN commands
…
WHEN expression THEN commands
ELSE commands
END CASE;


Loop
--------------------------------

1. WHILE

WHILE expression DO
Statements
END WHILE;


2. REPEAT

REPEAT
Statements;
UNTIL expression
END REPEAT;


3. LOOP

DELIMITER $$
DROP PROCEDURE IF EXISTS LOOPLoopProc$$
CREATE PROCEDURE LOOPLoopProc()
BEGIN
DECLARE x INT;
DECLARE str VARCHAR(255);
SET x = 1;
SET str = '';
loop_label: LOOP
IF x > 10 THEN
LEAVE loop_label;
END IF;
SET x = x + 1;
IF (x mod 2) THEN
ITERATE loop_label;
ELSE
SET str = CONCAT(str,x,',');
END IF;

END LOOP;
SELECT str;
END$$
DELIMITER;



4. SQL Cursor - Cursor is used to iterate through a set of rows, which returned by a query, and process individual row

** One of the most important point when working with cursor is you should use a NOT FOUND handler to avoid raising a fatal “no data to fetch” condition.

DECLARE cursor_name CURSOR FOR SELECT_statement;

MySQL supports following statements for working with cursor.
First you have to declare a cursor using DECLARE statement:
DECLARE cursor_name CURSOR FOR SELECT_statement;
Second you have to open the cursor using OPEN statement. You must open cursor before fetching rows from it.
OPEN cursor_name;
Next you can retrieve next row from cursor and move the cursor to the following row in a result set by using FETCH statement.
FETCH cursor_name INTO variable list;
And finally, you must close the cursor to deactivate it and release the memory associated with that cursor. To close the cursor you use CLOSE statement:
CLOSE cursor_name;


############################################################################################
DELIMITER $$
DROP PROCEDURE IF EXISTS CursorProc$$
CREATE PROCEDURE CursorProc()
BEGIN
DECLARE no_more_products, quantity_in_stock INT DEFAULT 0;
DECLARE prd_code VARCHAR(255);
DECLARE cur_product CURSOR FOR SELECT productCode FROM products;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_products = 1;

/* for loggging information */
CREATE TABLE infologs (
Id int(11) NOT NULL AUTO_INCREMENT,
Msg varchar(255) NOT NULL,
PRIMARY KEY (Id)
);
OPEN cur_product;

FETCH cur_product INTO prd_code;
REPEAT
SELECT quantityInStock INTO quantity_in_stock
FROM products
WHERE productCode = prd_code;

IF quantity_in_stock < 100 THEN
INSERT INTO infologs(msg)
VALUES (prd_code);
END IF;
FETCH cur_product INTO prd_code;
UNTIL no_more_products = 1
END REPEAT;
CLOSE cur_product;
SELECT * FROM infologs;
DROP TABLE infologs;
END$$
DELIMITER;
############################################################################################

** Make sure DECLARE statement is immediate next to BEGIN statement of procedure.
** Error Code : 1338 - Cursor declaration after handler declaration - Declaration of cursor should be handler





TRIGGER
========================================================================================================================
DELIMITER $$
CREATE TRIGGER `edi_TR_INSERT` AFTER INSERT ON `edi`

    FOR EACH ROW BEGIN
        DECLARE count_a INT;
	UPDATE fcl_file_number SET edi_status = NEW.status, edi_success = NEW.success WHERE id = NEW.fcl_file_number_id;
	SELECT id FROM FILE INTO count_a;
    END;
$$

** Error Code: 1422. Explicit or implicit commit is not allowed in stored function or trigger
ANS : Creation or update of tables or other such actions are not allowed in function or trigger.
      Basically we can insert or update the content of the tables only.


FUNCTION
========================================================================================================================
DELIMITER $$
DROP FUNCTION IF EXISTS `user_FN`$$

CREATE FUNCTION `user_FN`(param_id INT) RETURNS TEXT
    READS SQL DATA
    DETERMINISTIC
    BEGIN
	DECLARE full_name TEXT;
	IF (param_id != '') THEN
		SELECT CONCAT(u.first_name,' ',edi_TR_UPDATE(u.last_name,'')) INTO full_name FROM USER u WHERE u.id = param_id;
		RETURN full_name;
	END IF;
	RETURN '';
    END$$

DELIMITER ;

** it is RETURNS not RETURN

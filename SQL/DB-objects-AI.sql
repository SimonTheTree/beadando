----------------------------------------------------
----------------------------------------------------
-- 
--   EZ CSAK AZ OLVASHATÓSÁG KEDVÉÉRT VAN. NE EZT 
--   ADJUK BE, VAN EGY MÁSIK A MINDENT LÉTREHOZ. (DBexport.sql)
--
----------------------------------------------------
----------------------------------------------------

----------------------------------------------------
-- NORMAL QUESTIONS AUTO INCREMENT TRIGGER
----------------------------------------------------
CREATE SEQUENCE Norm_Quest_seq START WITH 1;

CREATE OR REPLACE TRIGGER n_quest_ai
BEFORE INSERT ON NORMAL_QUESTIONS 
FOR EACH ROW
BEGIN
  SELECT Norm_Quest_seq.NEXTVAL
  INTO   :new.question_id
  FROM   dual;
END;

----------------------------------------------------
-- RACE QUESTIONS AUTO INCREMENT TRIGGER
----------------------------------------------------
CREATE SEQUENCE RACE_QUES_SEQ START WITH 1;

CREATE OR REPLACE TRIGGER R_QUEST_AI
BEFORE INSERT ON RACE_QUESTIONS 
FOR EACH ROW
BEGIN
  SELECT RACE_QUES_SEQ.NEXTVAL
  INTO   :new.QUESTION_ID
  FROM   dual;
END;

----------------------------------------------------
-- FORUM_ENTRIES INCREMENT TRIGGER
----------------------------------------------------
CREATE SEQUENCE FORUM_ENTRIES_SEQ START WITH 1;

CREATE OR REPLACE TRIGGER FORUM_ENTRIES_AI
BEFORE INSERT ON FORUM_ENTRIES 
FOR EACH ROW
BEGIN
  SELECT FORUM_ENTRIES_SEQ.NEXTVAL
  INTO   :new.COMMENT_ID
  FROM   dual;
END;

----------------------------------------------------
-- FORUM_TOPICS INCREMENT TRIGGER
----------------------------------------------------
CREATE SEQUENCE FORUM_TOPICS_SEQ START WITH 1;

CREATE OR REPLACE TRIGGER FORUM_TOPICS_AI
BEFORE INSERT ON FORUM_TOPICS 
FOR EACH ROW
BEGIN
  SELECT FORUM_TOPICS_SEQ.NEXTVAL
  INTO   :new.TOPIC_ID
  FROM   dual;
END;

----------------------------------------------------
-- GAMES INCREMENT TRIGGER
----------------------------------------------------
CREATE SEQUENCE GAMES_SEQ START WITH 1;

CREATE OR REPLACE TRIGGER GAMES_AI
BEFORE INSERT ON GAMES
FOR EACH ROW
BEGIN
  SELECT GAMES_SEQ.NEXTVAL
  INTO   :new.GAME_ID
  FROM   dual;
END;

----------------------------------------------------
-- MAPS INCREMENT TRIGGER
----------------------------------------------------
CREATE SEQUENCE MAPS_SEQ START WITH 1;

CREATE OR REPLACE TRIGGER MAPS_AI
BEFORE INSERT ON MAPS
FOR EACH ROW
BEGIN
  SELECT MAPS_SEQ.NEXTVAL
  INTO   :new.MAP_ID
  FROM   dual;
END;

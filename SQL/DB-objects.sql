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

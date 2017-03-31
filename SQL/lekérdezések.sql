--1 kilistázza hogy az egyes kategóriákból hány kérdés van összesen, azaz a kétfajta kérdéstáblát összesítve.
SELECT 
	T1.NAME AS "Témakör", 	
	(T1."count" + T2."count") AS "Kérdések száma" 
FROM (
	SELECT 
		QT.NAME, 
		COUNT(NQ.TOPIC_ID) AS "count" 
	FROM QUESTION_TOPICS QT 
		LEFT JOIN NORMAL_QUESTIONS NQ ON QT.TOPIC_ID=NQ.TOPIC_ID
	GROUP BY QT.NAME
) T1
	LEFT JOIN (
		SELECT 
			QT.NAME, 
			COUNT(RQ.TOPIC_ID) AS "count" 
		FROM QUESTION_TOPICS QT 
			LEFT JOIN RACE_QUESTIONS RQ ON QT.TOPIC_ID=RQ.TOPIC_ID
		GROUP BY QT.NAME
	) T2 ON T1.NAME = T2.NAME
ORDER BY "Kérdések száma" DESC;

--2 egy max 10 hosszú top ten rangsort készít a userekből
SELECT * FROM(
	SELECT 
		REAL_NAME, 
		POINTS, 
		WINS, 
		DEFEATS, 
		RIGHT_ANSWERS, 
		WRONG_ANSWERS, 
		RIGHT_TIPS, 
		WRONG_TIPS 
	FROM USERS
	ORDER BY 
		POINTS DESC, 
		WINS DESC, 
		DEFEATS ASC, 
		RIGHT_ANSWERS DESC, 
		WRONG_ANSWERS ASC, 
		RIGHT_TIPS DESC , 
		WRONG_TIPS ASC, 
		AGE ASC
) 
WHERE ROWNUM <= 10;
	
--3 kilistázza hogy melyik felhasználó hány kérdéssel járult hozzá az adatbázishoz
SELECT 
	T1.UNAME AS "Username", 	
	(T1."count" + T2."count") AS "Kérdések száma" 
FROM (
	SELECT 
		UT.UNAME,
		COUNT(UT.UNAME) AS "count" 
	FROM USERS UT 
		LEFT JOIN NORMAL_QUESTIONS NQ ON UT.UNAME=NQ.AUTHOR
	GROUP BY UT.UNAME
) T1
	LEFT JOIN (
		SELECT 
			UT.UNAME,
			COUNT(UT.UNAME) AS "count" 
		FROM USERS UT 
			LEFT JOIN RACE_QUESTIONS NQ ON UT.UNAME=NQ.AUTHOR
		GROUP BY UT.UNAME
	) T2 ON T1.UNAME = T2.UNAME
ORDER BY "Kérdések száma" DESC;

--4 melyik az 5 leggyakrabban használt map
SELECT * FROM (
	SELECT 
		M.NAME AS "Térkép", 
		T.N_MAP AS "Népszerűség" 
	FROM (
		SELECT 
			MAP_ID, 
			COUNT(MAP_ID) AS N_MAP 
		FROM GAMES 
		GROUP BY MAP_ID
	) T 
	LEFT JOIN MAPS M ON M.MAP_ID=T.MAP_ID
	ORDER BY "Népszerűség"
)
WHERE ROWNUM <= 5;

--5 egy user által felrakott kérdések topicnévvel kiírva
SELECT "Kérdés", "Válasz", "Témakör" FROM (
	SELECT QUESTION AS "Kérdés", TO_CHAR(RIGHT_ANSWER) AS "Válasz", QUESTION_TOPICS.NAME AS "Témakör" 
    FROM RACE_QUESTIONS, QUESTION_TOPICS
    WHERE RACE_QUESTIONS.TOPIC_ID = QUESTION_TOPICS.TOPIC_ID AND RACE_QUESTIONS.AUTHOR LIKE 'kitoista'
)
UNION ALL (
	SELECT QUESTION AS "Kérdés", RIGHT_ANSWER AS "Válasz", QUESTION_TOPICS.NAME AS "Témakör" 
    FROM NORMAL_QUESTIONS, QUESTION_TOPICS
    WHERE NORMAL_QUESTIONS.TOPIC_ID = QUESTION_TOPICS.TOPIC_ID AND NORMAL_QUESTIONS.AUTHOR LIKE 'kitoista'
);
    
--6 kilistázza a befejezett játékok nyerteseit, a nevüket, a nyerő pontszámot, és a csatateret (térkép)
SELECT 
	U.UNAME AS "Username", 
	U.REAL_NAME AS "Név", 
	T.WINNERSCORE AS "Elért pontszám",
	M.NAME AS "Térkép"
FROM (
	SELECT 
		MAP_ID,
		CASE GREATEST(PLAYER1_SCORE, PLAYER2_SCORE, PLAYER3_SCORE) 
         	WHEN PLAYER1_SCORE THEN PLAYER1
         	WHEN PLAYER2_SCORE THEN PLAYER2
         	WHEN PLAYER3_SCORE THEN PLAYER3
       	END AS winner,
    	GREATEST(PLAYER1_SCORE, PLAYER2_SCORE, PLAYER3_SCORE) AS winnerScore
	FROM GAMES
	WHERE STATE = 'finished'
) T
    LEFT JOIN USERS U ON winner = U.UNAME
    LEFT JOIN MAPS M ON T.MAP_ID = M.MAP_ID;

--7 az adott mapon történt játékok nyerteseit és pontszámát (mapnév alapján!)
SELECT 
	CASE GREATEST(PLAYER1_SCORE,PLAYER2_SCORE,PLAYER3_SCORE) 
         WHEN PLAYER1_SCORE THEN PLAYER1
         WHEN PLAYER2_SCORE THEN PLAYER2
         WHEN PLAYER3_SCORE THEN PLAYER3
       END AS winner,
    GREATEST(PLAYER1_SCORE,PLAYER2_SCORE,PLAYER3_SCORE) AS winnerScore
FROM MAPS M LEFT JOIN GAMES G ON M.MAP_ID = G.MAP_ID 
WHERE M.NAME LIKE 'testMap'

--8 kilistázza adott user kedvenc mapjait
SELECT "Térkép", count("Térkép") AS "Játékok száma" FROM (
	SELECT 
		'ganter' AS "Játékos neve", 
		M.NAME AS "Térkép", 
		G.PLAYER1 AS "p1",
		G.PLAYER2 AS "p2",
		G.PLAYER3 AS "p3"	
	FROM GAMES G
	LEFT JOIN MAPS M ON G.MAP_ID = M.MAP_ID
	WHERE M.NAME IS NOT NULL
) T
WHERE
	(T."p1" = T."Játékos neve"
	OR T."p2" = T."Játékos neve"
	OR T."p3" = T."Játékos neve")
GROUP BY "Térkép"
ORDER BY "Játékok száma" DESC;


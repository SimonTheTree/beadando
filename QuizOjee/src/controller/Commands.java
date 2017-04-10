package controller;

/**
 * Ez az osztaly felelos a szerver - kliens kapcsolatban az uzenetek jelenteseert.<br> 
 * Az uzenetek leirasanal 2 lehetoseg van:<br>
 * - kuldheti: ilyenkor a szerver nem kuldi automatikusan, hanem neked kell megtenned.<br>
 * - kuldi: ezeket automatikusan kuldi es a masik fel lekezeli.<br>
 *  */
public class Commands {

	/**Azon uzenetek elott talalhato, amelyeket automatikusan kuldenek egymasnak. */
	public static final String AUTOMATIC = "auto@";
	/**A szerver kuldheti.<br>Kell ele vagy YOUR vagy NOT_YOUR_QUESTION. <br> tovabbi 5 parametere van: <br> Question - rightAnswer - answer1 - answer2 - answer3*/
	public static String NORMAL_QUESTION = "[normal-question]";
	/**A szerver kuldheti.<br>Kell ele vagy YOUR vagy NOT_YOUR_QUESTION. <br> tovabbi 2 parametere van: <br> Question - rightAnswer*/
	public static String RACE_QUESTION = "[race-question]";
	/**A szerver kuldi miutan a kliens bejelentkezett de az lekesett a szerverre valo kapcsolodasrol. <br> Nincs parametere.*/
	public static String ALREADY_RUNNING = "[already-running]";
	/**A szerver kuldi miutan a kliens bejelentkezett es meg nincs meg a teljes jatekosszam az adott szerveren. <br> Nincs parametere.*/
	public static String JOINED = "[joined]";
	/**A szerver kuldi minden kliensnek, aki csatlakozik. <br> Nincs parametere.*/
	public static String WHO_ARE_YOU = "[give-me-your-love]";
	/**A szerver kuldheti annak a kliensnek, akinek nem valaszolnia kell a kerdesre.<br>Parametere egy RACE VAGY NORMAL_QUESTION -t és annak a parameterei is.*/
	public static String NOT_YOUR_QUESTION = "[not-your-question]";
	/**A szerver kuldheti annak a kliensnek, akinek valaszolnia kell a kerdesre. <br>Parametere egy RACE VAGY NORMAL_QUESTION -t és annak a parameterei is.*/
	public static String YOUR_QUESTION = "[your-question]";
	/**A kliens kuldi bejelentkezesnel. <br>1 parametere van, a userName. */
	public static String LOG_IN = "[logolokmá]";
	/**A szerver kuldi mindenkinek.<br> 1 parametere van, a userName.*/
	public static String SOMEONE_LEFT = "[o-faszom]";
	public static String WAIT_FOR_RESPONSE = "[akkor-várunk]";
	/**A szerver kuldi mindenkinek.<br> Nincs parametere */
	public static String END = "[oktybye]";
	/**A szerver kuldi adott idokozonkent. <br> Nincs parametere */
	public static String PING = "[ping]";
	/**A szerver es a kliens is kuldheti, tamado kor eseten. <br> Ket parametere van: ki es melyik teruletet. */
	public static String ATTACK = "[ATTAAAACK]";
	/**A szerver es a kliens is kuldheti. szabad teruletvalasztas eseten. <br> Ket parametere van: ki es melyik teruletet. */
	public static String CHOOSE = "[choose]";
	/**A szerver kuldheti ki a jatek teljes informaciojat. <br> Egy parametere van: A {@link model.Game Game} toString-je*/
	public static String GAME = "[game]";

}

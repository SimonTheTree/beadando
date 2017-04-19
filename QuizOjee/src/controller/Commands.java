package controller;

/**
 * <b>Commands osztaly.</b><p>
 * Ez az osztaly felelos a szerver - kliens kapcsolatban az uzenetek <b>jelenteseert</b>.<p> 
 * Az uzenetek leirasanal 2 lehetoseg van:<br>
 * - kuldheti: ilyenkor a szerver nem kuldi automatikusan, hanem neked kell megtenned.<br>
 * - kuldi: ezeket automatikusan kuldi es a masik fel lekezeli.<br>
 *  */
public class Commands {

	/**A szerver kuldheti.<br>Kell ele vagy YOUR vagy NOT_YOUR_QUESTION. <br> tovabbi 5 parametere van: <br> Question - rightAnswer - answer1 - answer2 - answer3*/
	public static String NORMAL_QUESTION = "[normal-question]";
	/**A szerver kuldheti.<br>Kell ele vagy YOUR vagy NOT_YOUR_QUESTION. <br> tovabbi 2 parametere van: <br> Question - rightAnswer*/
	public static String RACE_QUESTION = "[race-question]";
	/**A szerver kuldi miutan a kliens bejelentkezett de az lekesett a szerverre valo kapcsolodasrol. <br> Nincs parametere.*/
	public static String ALREADY_RUNNING = "[already-running]";
	/**A szerver kuldi miutan a kliens bejelentkezett es meg nincs meg a teljes jatekosszam az adott szerveren. <br> Nincs parametere.*/
	public static String JOINED = "[joined]";
	/**A szerver kuldi minden kliensnek, aki csatlakozik. <br> Nincs parametere.*/
	public static String WHO_ARE_YOU = "[who-are-you]";
	/**A szerver kuldheti annak a kliensnek, akinek nem valaszolnia kell a kerdesre.<br>Parametere egy RACE VAGY NORMAL_QUESTION -t és annak a parameterei is.*/
	public static String NOT_YOUR_QUESTION = "[not-your-question]";
	/**A szerver kuldheti annak a kliensnek, akinek valaszolnia kell a kerdesre. <br>Parametere egy RACE VAGY NORMAL_QUESTION -t és annak a parameterei is.*/
	public static String YOUR_QUESTION = "[your-question]";
	/**A kliens kuldi bejelentkezesnel. <br>1 parametere van, a userName. */
	public static String LOG_IN = "[log-in]";
	/**A szerver kuldi mindenkinek, ha valaki kilepett.<br> 1 parametere van, a userName.*/
	public static String SOMEONE_LEFT = "[someone-left]";
	/**A szerver kuldi mindenkinek, ha valaki visszatert.<br> 1 parametere van, a userName.*/
	public static String RETURNED = "[returned]";
	/**A szerver kuldi mindenkinek, ha abortaltak.<br> Nincs parametere */
	public static String END = "[oktybye]";
	/**A szerver kuldi mindenkinek, ha csatlakoztak es elindult.<br> Nincs parametere */
	public static String START = "[start]";
	/**A szerver kuldi adott idokozonkent. <br> Nincs parametere */
	public static String PING = "[ping]";
	/**A szerver es a kliens is kuldheti, tamado kor eseten. <br> 2 parametere van: ki es melyik teruletet. */
	public static String ATTACK = "[ATTAAAACK]";
	/**A szerver es a kliens is kuldheti. szabad teruletvalasztas eseten. <br> 2 parametere van: ki es melyik teruletet. */
	public static String CHOOSE = "[choose]";
	/**A szerver kuldheti ki a jatek teljes informaciojat. <br> 2 parametere van: A {@link model.Game Game} toString-je es az hogy melyik terulet kie.*/
	public static String GAME = "[game]";

}

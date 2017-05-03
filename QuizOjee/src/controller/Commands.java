package controller;

import game.GameBoard;
import game.GameSettings;
import model.Question;
import model.RaceQuestion;

/**
 * <b>Commands osztaly.</b><p>
 * Ez az osztaly felelos a szerver - kliens kapcsolatban az uzenetek <b>jelenteseert</b>.<p> 
 * Az uzenetek leirasanal 2 lehetoseg van:<br>
 * - kuldheti: ilyenkor a szerver nem kuldi automatikusan, hanem neked kell megtenned.<br>
 * - kuldi: ezeket automatikusan kuldi es a masik fel lekezeli.<br>
 *  */
public class Commands {

	/**A szerver kuldi miutan a kliens bejelentkezett de az lekesett a szerverre valo kapcsolodasrol. <br> Nincs parametere.*/
	public static final String ALREADY_RUNNING = "[already-running]";
	/**A szerver kuldi miutan a kliens bejelentkezett es meg nincs meg a teljes jatekosszam az adott szerveren. <br> Nincs parametere.*/
	public static final String JOINED = "[joined]";
	/**A client kuldi amikor megkapj a start uzenetet.<br> Nincs parametere. */
	public static final String IM_LISTENING = "[im-listening]";
	/**A szerver kuldi minden kliensnek, aki csatlakozik. <br> Nincs parametere.*/
	public static final String WHO_ARE_YOU = "[who-are-you]";
	/**A szerver kuldheti annak a kliensnek, akinek nem valaszolnia kell a kerdesre.<br>Parametere egy RACE VAGY NORMAL_QUESTION -t �s annak a parameterei is.*/
	public static final String NOT_YOUR_QUESTION = "[not-your-question]";
	/**A szerver kuldheti annak a kliensnek, akinek valaszolnia kell a kerdesre. <br>Parametere egy RACE VAGY NORMAL_QUESTION -t �s annak a parameterei is.*/
	public static final String YOUR_QUESTION = "[your-question]";
	/**A kliens kuldi bejelentkezesnel. <br>1 parametere van, a userName. */
	public static final String LOG_IN = "[log-in]";
	/**A szerver kuldi mindenkinek, ha valaki kilepett.<br> 1 parametere van, a userName.*/
	public static final String SOMEONE_LEFT = "[someone-left]";
	/**A szerver kuldi mindenkinek, ha valaki visszatert.<br> 1 parametere van, a userName.*/
	public static final String RETURNED = "[returned]";
	/**A szerver kuldi mindenkinek, ha abortaltak.<br> Nincs parametere */
	public static final String END = "[oktybye]";
	
	/**A szerver kuldi adott idokozonkent. <br> Nincs parametere */
	public static final String PING = "[ping]";
	/**A szerver es a kliens is kuldheti. szabad teruletvalasztas eseten. <br> 2 parametere van: ki es melyik teruletet. */
	public static final String CHOOSE = "[choose]";
	/**A szerver kuldheti ki a jatek teljes informaciojat. <br> 2 parametere van: A {@link model.Game Game} toString-je es az hogy melyik terulet kie.*/
	public static final String GAME = "[game]";
	
	/**
	 * A Cliens alsőre ezt kérdi a szervertől. Ha valaki másodjára
	 *  lép be, és lemaradt a START utasításról, ezzel kérhet újra 
	 *  START-ot a szervertől, hogy beindíthassa a játékot.
	 *  <br>Nincs paramétere.
	 *  @sender kliens
	 */
	public static final String IS_STARTED = "[start]";
	/**
	 * A szerver kuldi mindenkinek, ha csatlakoztak es elindult.
	 * <br> Nincs parametere.
	 * @sender server 
	 */
	public static final String START = "[start]";
	/** 
	 * Egyetlen parametere egy serializalt gameboard
	 * @sender server
	 * @param0 {@link GameBoard} serialized 
	 */
	public static final String GAMEBOARD = "[gameboard]";
	/** 
	 * Egyetlen parametere egy serializalt GameSettings
	 * @sender server
	 * @param0 {@link GameSettings} serialized 
	 */
	public static final String SETTINGS = "[settings]";
	/** 
	 * A kliens küldi a szervernek, ha le szeretné kérni a szerver aktuális {@link GameBoard} objektumát.<br>
	 * Nincs paramétere.
	 * @sender kliens
	 */
	public static final String GAMEBOARD_REQUEST = "[gimme a gameboard]";
	/** 
	 * A kliens küldi a szervernek, ha le szeretné kérni a szerver aktuális {@link GameSettings} objektumát.<br>
	 * Nincs paramétere.
	 * @sender kliens
	 */
	public static final String SETTINGS_REQUEST = "[gimme a settings]";
	
	/** 
	 * A server ezzel broadcastolja ki kerul sorra
	 * @sender server 
	 * @param0 {@link String} a játékos username-je aki soron van
	 */
	public static final String YOUR_TURN = "[your turn]";	
	/** 
	 * A kliens kuldi a szervernek, amikor befejezte a koret
	 * @sender kliens
	 * @param0 {@link String} username
	 */
	public static final String END_TURN = "[I'm done]";
		/**
		 * A server kuldi egy jatekosnak, amikor o van soron, es lejart az ideje, vege a korenek
		 * nincs parametere
		 */
		public static final String HALT_TURN = "[time is up]";
	/**
	 * @sender server, kliens 
	 * @param0 {@link String} username tamado
	 * @param1 {@link String} teruletID (szam) 
	 ************/
	public static final String ATTACK = "[ATTAAAACK]";
	/**
	 * @sender server, kliens 
	 * @param0 {@link String} username uj tulaj
	 * @param1 {@link String} teruletID (szam) 
	 ************/
	public static final String NEW_OWNER = "[The times they are a'changin]";
	/**
	 * @sender server
	 * @param0 {@link String} PARAM_YOURS|PARAM_NOT_YOURS
	 * @param1 {@link Question} a kerdes 
	 ************/
	public static final String NORMAL_QUESTION = "[normal-question]";
	/**
	 * @sender server
	 * @param0 {@link String} PARAM_YOURS|PARAM_NOT_YOURS 
	 * @param1 {@link RaceQuestion} a kerdes 
	 ************/
	public static final String RACE_QUESTION = "[race-question]";
	/**
	 * Valasz egy {@link Question}-re. Ha a server kuldi, akkor mindig a helyes valaszt tartalmazza.
	 * @sender kliens, server
	 * @param0 {@link String} valasz 
	 ************/
	public static final String NORM_ANSWER = "[norm-answer]";
	/**
	 * Valasz egy {@link Question}-re. Ezt a server broadcastolja, hogy informalja a jatekosokat masok valaszairol.
	 * @sender kliens, server
	 * @param0 {@link String} valasz 
	 * @param1 {@link String}, a kuldo jatekos username-je 
	 ************/
	public static final String NORM_PLAYER_ANSWER = "[norm-answer-player]";
	/**
	 * Valasz egy {@link RaceQuestion}-re. Ha a server kuldi, akkor mindig a helyes valaszt tartalmazza.
	 * @sender kliens, server
	 * @param0 {@link Number}, a valasz
	 */
	public static final String RQ_ANSWER = "[rq-answer]";
	/**
	 * Valasz egy {@link RaceQuestion}-re. Ezt a server broadcastolja, hogy informalja a jatekosokat masok valaszairol.
	 * @sender server
	 * @param0 {@link Number}, a valasz
	 * @param1 {@link String}, a kuldo jatekos username-je 
	 */
	public static final String RQ_PLAYER_ANSWER = "[rq-answer-player]";
//	/**
//	 * A szerver kerdesek kikuldese utan masodpercenkent kuldi, h mennyi ido van meg vissza. Ha param1=0, akkor lejart
//	 * @sender server
//	 * @param0 {@link String} (int) ido ami visszavan, mertekegyseg: secundum
//	 */
//	public static final String TIME_LEFT = "[question-timeleft]";
	
	
	public static final String PARAM_YOURS= "yours";
	public static final String PARAM_NOT_YOURS= "not yours";

}

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class Namer {
	
	static Twitter twitter;
	private static TwitterFactory twitterFactory;
	private static TwitterStream twitterStream;
	private static int num;
	static String MyScreenName, message;

	private static String ConsumerKey, ConsumerSecret,
	sugtao4423Token, sugtao4423TokenSecret,
	tsubasaneko83Token, tsubasaneko83TokenSecret,
	keykiyuToken, keykiyuTokenSecret,
	marumimioToken, marumimioTokenSecret,
	a_a1225jojoToken, a_a1225jojoTokenSecret,
	miiiko_24Token, miiiko_24TokenSecret;
	
	public static void main(String[] args) throws Exception {
		Properties conf = new Properties();
		InputStream is = Namer.class.getResourceAsStream("properties");
		conf.load(is);
		//クライアントConsumer Key / Secret
		ConsumerKey = conf.getProperty("ConsumerKey");
		ConsumerSecret = conf.getProperty("ConsumerSecret");
		//各AccessToken / AccessTokenSecret
		//sugtao4423
		sugtao4423Token = conf.getProperty("sugtao4423Token");
		sugtao4423TokenSecret = conf.getProperty("sugtao4423TokenSecret");
		//tsubasaneko83
		tsubasaneko83Token = conf.getProperty("tsubasaneko83Token");
		tsubasaneko83TokenSecret = conf.getProperty("tsubasaneko83TokenSecret");
		//keykiyu
		keykiyuToken = conf.getProperty("keykiyuToken");
		keykiyuTokenSecret = conf.getProperty("keykiyuTokenSecret");
		//marumimio
		marumimioToken = conf.getProperty("marumimioToken");
		marumimioTokenSecret = conf.getProperty("marumimioTokenSecret");
		//a_a1225jojo
		a_a1225jojoToken = conf.getProperty("a_a1225jojoToken");
		a_a1225jojoTokenSecret = conf.getProperty("a_a1225jojoTokenSecret");
		//miiiko_24
		miiiko_24Token = conf.getProperty("miiiko_24Token");
		miiiko_24TokenSecret = conf.getProperty("miiiko_24TokenSecret");
		is.close();
		
		System.out.println("Namer起動");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if(args.length != 1){
			System.out.println("1：sugtao4423\n2：tsubasaneko83\n3：keykiyu\n4：marumimio\n5：a_a1225jojo\n6：miiiko_24\n99：新しいアカウントのアクセストークン");
			try{
				num = Integer.parseInt(br.readLine());
			}catch(Exception e){
				System.exit(0);
			}
		}else{
			if(Integer.parseInt(args[0]) <= 6 || Integer.parseInt(args[0]) == 99)
				num = Integer.parseInt(args[0]);
		}
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(ConsumerKey).setOAuthConsumerSecret(ConsumerSecret);
		Configuration jconf = builder.build();
		twitterFactory = new TwitterFactory(jconf);
		
		AccessToken accesstoken = null;
		
		if(num == 99){
			twitter = twitterFactory.getInstance();
			RequestToken rt = twitter.getOAuthRequestToken();
			String url = rt.getAuthorizationURL();
			System.out.println("URLは\n" + url + "\nブラウザで開きますか？\n1：はい");
			if(br.readLine().matches("1")){
				Desktop desk = Desktop.getDesktop();
				URI uri = new URI(url);
				desk.browse(uri);
			}
			System.out.println("Pinの入力");
			String pin = br.readLine();
			AccessToken at = twitter.getOAuthAccessToken(rt, pin);
			System.out.println("Token：" + at.getToken());
			System.out.println("TokenSecret：" + at.getTokenSecret());
		}if(num == 1){
			accesstoken = new AccessToken(sugtao4423Token, sugtao4423TokenSecret);
		}if(num == 2){
			accesstoken = new AccessToken(tsubasaneko83Token, tsubasaneko83TokenSecret);
		}if(num == 3){
			accesstoken = new AccessToken(keykiyuToken, keykiyuTokenSecret);
		}if(num == 4){
			accesstoken = new AccessToken(marumimioToken, marumimioTokenSecret);
		}if(num == 5){
			accesstoken = new AccessToken(a_a1225jojoToken, a_a1225jojoTokenSecret);
		}if(num == 6){
			accesstoken = new AccessToken(miiiko_24Token, miiiko_24TokenSecret);
		}
		
		TwitterStreamFactory factory = new TwitterStreamFactory(jconf);
		twitterStream = factory.getInstance(accesstoken);
		twitter = twitterFactory.getInstance(accesstoken);
		MyScreenName = twitter.getScreenName();
		System.out.println(MyScreenName);
		twitterStream.addListener(new Streaming());
		twitterStream.user();
//		twitter.updateStatus("Namerを起動しました。 " + date());
		//終了時の処理を投げる
		Namer main = new Namer();
		main.exit();
		String stop = br.readLine();
		if(stop.matches("stop") || stop.matches("exit")){
			twitterStream.shutdown();
			System.out.println("Namer停止");
			System.exit(0);
		}
	}
	//終了処理
	public void exit(){
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				twitterStream.shutdown();
				System.out.println("Namer停止");
				System.out.flush();
			}
		});
	}
	
	public static void tweet(String content, long tweetId){
		if(content.length() > 140)
			content = content.substring(0, 137) + "...";
		try{
		if(tweetId == -1)
			twitter.updateStatus(content);
		else
			twitter.updateStatus(new StatusUpdate(content).inReplyToStatusId(tweetId));
		}catch(twitter4j.TwitterException e){
			String e2 = e.toString();
			if(e2.length() > 140)
				e2.substring(0, 140);
			try { twitter.updateStatus(e2); }catch(twitter4j.TwitterException e1){}
		}
	}
	
	//UpdateName
	public static void updateName(String name, String user, long tweetId) throws TwitterException{
		twitter.updateProfile(name, null, null, null);
		message = "名前を「" + name + "」に変更しました。 by @" + user + " " + date();
		tweet(message, tweetId);
		show(message, true);
	}
	public static void ChageNameError(String user, long tweetId){
		message = "@" + user + " 変更する名前の文字が長過ぎます。20文字以内にしてください。";
		tweet(message, tweetId);
		show(message, true);
	}
	//UpdateBio
	public static void updateBio(String bio, String user, long tweetId) throws TwitterException{
		message = "bioを「" + bio + "」に変更しました。 by @" + user + " " + date();
		twitter.updateProfile(null, null, null, bio);
		tweet(message, tweetId);
		show(message, true);
	}
	public static void ChangeBioError(String user, long tweetId){
		message = "@" + user + " 変更するbioの文字が長過ぎます。160文字以内にしてください。";
		tweet(message, tweetId);
		show(message, true);
	}
	//newTweet
	public static void newTweet(String newTweet, String user, long tweetId) throws TwitterException{
		twitter.updateStatus(newTweet);
		message = "ツイートしました。 by @" + user + " " + date();
		tweet(message, tweetId);
		show(message, true);
	}
	public static void LongTweetStringError(String user, long tweetId){
		message = "@" + user + " ツイートの文字が長過ぎます。140文字以内にしてください。";
		tweet(message, tweetId);
		show(message, true);
	}
	//Like? Don't Like?
	public static void Like(String user, long tweetId){
		try{
			message = "@" + user + " 好き " + date();
			tweet(message, tweetId);
			show(message, true);
		}catch(Exception e){
			message = "@" + user + " 好き " + date_milli();
			tweet(message, tweetId);
			show(message, true);
		}
	}
	public static void DoNotLike(String user, long tweetId){
		try{
			message = "@" + user + " 嫌い " + date();
			tweet(message, tweetId);
			show(message, true);
		}catch(Exception e){
			message = "@" + user + " 嫌い " + date_milli();
			tweet(message, tweetId);
			show(message, true);
		}
	}
	//起動しています！
	public static void WorkingNamer(String user, long tweetId){
		try{
			message = "@" + user + " 起動しています！ " + date();
			tweet(message, tweetId);
			show(message, true);
		}catch(Exception e){
			message = "@" + user + " 起動しています！ " + date_milli();
			tweet(message, tweetId);
			show(message, true);
		}
	}
	//メモリー
	public static void NamerMemoryTweet(String user, long tweetId){
		message = "@" + user + "\n" + memory() + "\n" + date();
		tweet(message, tweetId);
		show(message, true);
	}
	//ping
	public static void ping(String user, long tweetId){
		long tweetId2time = (tweetId >> 22) + 1288834974657L;
		long now = new Date().getTime();
		message = "@" + user + " " + String.valueOf((double)(tweetId2time - now) / 1000) + " " + date();
		tweet(message, tweetId);
		show(message, true);
	}
	//のあちゃんが学習！
	public static void Noa_tyan_Learned(String LearnedText){
		if(LearnedText.length() > 107){ //テキストが文字数オーバーになってしまう場合
			message = "のあちゃんが\n「" + abbreviation(LearnedText, 107) + "」\nを学習した！\n" + date();
			tweet(message, -1);
			sarasty_sisters_Log(message, true);
		}else{
			message = "のあちゃんが\n「" + LearnedText + "」\nを学習した！\n" + date();
			tweet(message, -1);
			sarasty_sisters_Log(message, true);
		}
	}
	//ゆあちゃんが学習！
	public static void Yua_tyan_Learned(String LearnedText){
		if(LearnedText.length() > 107){ //テキストが文字数オーバーになってしまう場合
			message = "ゆあちゃんが\n「" + abbreviation(LearnedText, 107) + "」\nを学習した！\n" + date();
			tweet(message, -1);
			sarasty_sisters_Log(message, true);
		}else{
			message = "ゆあちゃんが\n「" + LearnedText + "」\nを学習した！\n" + date();
			tweet(message, -1);
			sarasty_sisters_Log(message, true);
		}
	}
	//ももかちゃんが学習！
	public static void Momoka_tyan_Learned(String LearnedText){
		if(LearnedText.length() > 107){ //テキストが文字数オーバーになってしまう場合
			message = "ももかちゃんが\n「" + abbreviation(LearnedText, 106) + "」\nを学習した！\n" + date();
			tweet(message, -1);
		}else{
			message = "ももかちゃんが\n「" + LearnedText + "」\nを学習した！\n" + date();
			tweet(message, -1);
		}
	}
	//Minecraft Server start
		//start
	public static void MinecraftServer_start(String user, long tweetId){
		message = "@" + user + " Minecraft Server start! " + date();
		tweet(message, tweetId);
		show(message, true);
	}
		//started
	public static void MinecraftServer_started(String user, long tweetId){
		message = "@" + user + " Minecraft Server is already started! " + date();
		tweet(message, tweetId);
		show(message, true);
	}
	//Minecraft Server stop
		//stop
	public static void MinecraftServer_stop(String user, long tweetId){
		message = "@" + user + " Minecraft Server stop! " + date();
		tweet(message, tweetId);
		show(message, true);
	}
		//stopped
	public static void MinecraftServer_stopped(String user, long tweetId){
		message = "@" + user + " Minecraft Server is already stopped! " + date();
		tweet(message, tweetId);
		show(message, true);
	}
	//なんかのエラー
	public static void TwitterException(String Exception){
		if(Exception.length() > 115){
			message = "なんかのエラー\n" + abbreviation(Exception, 115) + date();
			tweet(message, -1);
			show(message, true);
		}else{
			message = "なんかのエラー\n" + Exception + date();
			tweet(message, -1);
			show(message, true);
		}
	}
	//Namer停止
	public static void NamerStop() throws Exception{
		twitterStream.shutdown();
		message = "Namerを停止しました。 " + date();
		tweet(message, -1);
		show(message, true);
		main(null);
	}
	
	//日付
	public static String date(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
		return "(" + sdf.format(date) + ")";
	}
	public static String date_milli(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss,SSS");
		return "(" + sdf.format(date) + ")";
	}
	
	//Memory取得
	public static String memory(){
		long free, total, max, used;
		DecimalFormat f1, f2;
		f1 = new DecimalFormat("#,###MB");
		f2 = new DecimalFormat("##.#");
		free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		max = Runtime.getRuntime().maxMemory() /1024 / 1024;
		used = total - free;
		double per = (used * 100 / (double)total);
		return "MemoryInfo\n合計：" + f1.format(total) + " \n使用量：" + f1.format(used) +
				" (" + f2.format(per) + "%)" + "\n使用可能最大：" + f1.format(max); 
	}
	
	private static String abbreviation(String shortenText, int EndNumber){
		return shortenText.substring(0, EndNumber - 3) + "...";
	}
	
	//ログ保存関連
	/* htmlのテーブル仕様 */
	public static void show(String show, boolean kaigyou){ //true = 送信 false = 受信
		try{
			FileOutputStream fos = new FileOutputStream("/var/www/html/NamerLog/NamerLog/" + MyScreenName + ".txt", true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			if(!kaigyou)
				bw.write("<tr><td>" + show + "</td>");
			else
				bw.write("<td>" + show + "</td></tr>");
			bw.flush();
			bw.close();
			fos.close();
		}catch(IOException e){
			tweet("@" + MyScreenName + " ログファイル出力エラー", -1);
		}
	}
	
	//ログ - サラスティ姉妹専用 コードは上記と全く同じ
	public static void sarasty_sisters_Log(String show, boolean kaigyou){
		try{
			FileOutputStream fos = new FileOutputStream("/var/www/html/NamerLog/NamerLog/sarasty_sisters_log.txt", true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);
			if(!kaigyou)
				bw.write("<tr><td>" + show + "</td>");
			else
				bw.write("<td>" + show + "</td></tr>");
			bw.flush();
			bw.close();
			fos.close();
		}catch(IOException e){
			tweet("@" + MyScreenName + " ログファイル出力エラー", -1);
		}
	}
}
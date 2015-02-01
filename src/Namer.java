import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import twitter4j.conf.ConfigurationBuilder;


public class Namer {
	
	static Twitter twitter;
	static TwitterStream twitterStream;
	static int num;
	static String MemoryInfo, MyScreenName, message;

	public static String ConsumerKey, ConsumerSecret,
	sugtao4423Token, sugtao4423TokenSecret,
	tsubasaneko83Token, tsubasaneko83TokenSecret,
	keykiyuToken, keykiyuTokenSecret,
	marumimioToken, marumimioTokenSecret,
	a_a1225jojoToken, a_a1225jojoTokenSecret;
	
	public static void main(String[] args) throws Exception {
		Properties conf = new Properties();
		FileInputStream fis = new FileInputStream("/home/tao/デスクトップ/NamerConf.conf");
		//FileInputStream fis = new FileInputStream("/Users/tao/Desktop/NamerConf.conf");
		conf.load(fis);
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
		fis.close();
		
		System.out.println("Namer起動");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		if(args.length != 1){
			System.out.println("1：sugtao4423\n2：tsubasaneko83\n3：keykiyu\n4：marumimio\n5：a_a1225jojo\n99：新しいアカウントのアクセストークン");
			try{
				num = Integer.parseInt(br.readLine());
			}catch(Exception e){
				System.exit(0);
			}
		}else{
			if(Integer.parseInt(args[0]) <= 5 || Integer.parseInt(args[0]) == 99)
				num = Integer.parseInt(args[0]);
		}
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(ConsumerKey, ConsumerSecret);
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(ConsumerKey).setOAuthConsumerSecret(ConsumerSecret);
		AccessToken accesstoken = null;
		
		if(num == 99){
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
			System.out.println(at.getToken());
			System.out.println(at.getTokenSecret());
		}if(num == 1){
			builder.setOAuthAccessToken(sugtao4423Token).setOAuthAccessTokenSecret(sugtao4423TokenSecret);
		
			accesstoken = new AccessToken(sugtao4423Token, sugtao4423TokenSecret);
		}if(num == 2){
			builder.setOAuthAccessToken(tsubasaneko83Token).setOAuthAccessTokenSecret(tsubasaneko83TokenSecret);
		
			accesstoken = new AccessToken(tsubasaneko83Token, tsubasaneko83TokenSecret);
		}if(num == 3){
			builder.setOAuthAccessToken(keykiyuToken).setOAuthAccessTokenSecret(keykiyuTokenSecret);
		
			accesstoken = new AccessToken(keykiyuToken, keykiyuTokenSecret);
		}if(num == 4){
			builder.setOAuthAccessToken(marumimioToken).setOAuthAccessTokenSecret(marumimioTokenSecret);
			
			accesstoken = new AccessToken(marumimioToken, marumimioTokenSecret);
		}if(num == 5){
			builder.setOAuthAccessToken(a_a1225jojoToken).setOAuthAccessTokenSecret(a_a1225jojoTokenSecret);
			
			accesstoken = new AccessToken(a_a1225jojoToken, a_a1225jojoTokenSecret);
		}
		
		twitter.setOAuthAccessToken(accesstoken);
		twitter4j.conf.Configuration jconf = builder.build();
		TwitterStreamFactory factory = new TwitterStreamFactory(jconf);
		twitterStream = factory.getInstance();
		MyScreenName = twitter.getScreenName();
		System.out.println(MyScreenName);
		twitterStream.addListener(new Streaming());
		twitterStream.user();
//		Date();
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
	
	//UpdateName
	public void updateName(String name, String user, long tweetId) throws TwitterException{
		twitter.updateProfile(name, null, null, null);
		message = "名前を「" + name + "」に変更しました。 by @" + user + " " + date();
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	public void ChageNameError(String user, long tweetId) throws TwitterException{
		message = "@" + user + " 変更する名前の文字が長過ぎます。20文字以内にしてください。";
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	//UpdateBio
	public void updateBio(String bio, String user, long tweetId) throws TwitterException{
		message = "bioを「" + bio + "」に変更しました。 by @" + user + " " + date();
		twitter.updateProfile(null, null, null, bio);
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	public void ChangeBioError(String user, long tweetId) throws TwitterException{
		message = "@" + user + " 変更するbioの文字が長過ぎます。160文字以内にしてください。";
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	//newTweet
	public void newTweet(String newTweet, String user, long tweetId) throws TwitterException{
		twitter.updateStatus(newTweet);
		message = "ツイートしました。 by @" + user + " " + date();
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	public void LongTweetStringError(String user, long tweetId) throws TwitterException{
		message = "@" + user + " ツイートの文字が長過ぎます。140文字以内にしてください。";
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	//Like? Don't Like?
	public void Like(String user, long tweetId) throws TwitterException{
		message = "@" + user + " 好き " + date();
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	public void DoNotLike(String user, long tweetId) throws TwitterException{
		message = "@" + user + " 嫌い " + date();
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	//起動しています！
	public void WorkingNamer(String user, long tweetId) throws TwitterException{
		message = "@" + user + " 起動しています！ " + date();
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	//メモリー
	public void NamerMemoryTweet(String user, long tweetId) throws TwitterException{
		memory();
		message = "@" + user + " " + MemoryInfo + " " + date();
		twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
		show(message, true);
	}
	//なんかのエラー
	public void TwitterException() throws TwitterException{
		message = "なんかのエラー " + date();
		twitter.updateStatus(message);
		show(message, true);
	}
	//Namer停止
	public static void NamerStop() throws Exception{
		twitterStream.shutdown();
		message = "Namerを停止しました。 " + date();
		twitter.updateStatus(message);
		show(message, true);
		main(null);
	}
	
	
	public static String date(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
		return "(" + sdf.format(date) + ")";
	}
	
	public void memory(){
		long free, total, max, used;
		DecimalFormat f1, f2;
		f1 = new DecimalFormat("#,###MB");
		f2 = new DecimalFormat("##.#");
		free = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
		max = Runtime.getRuntime().maxMemory() /1024 / 1024;
		used = total - free;
		double per = (used * 100 / (double)total);
		MemoryInfo = "MemoryInfo：合計：" + f1.format(total) + "  使用量：" + f1.format(used) +
				" (" + f2.format(per) + "%)  " + "使用可能最大：" + f1.format(max); 
	}
	
	public static void show(String show, boolean kaigyou){
		try{
			FileOutputStream fos = new FileOutputStream("/var/www/html/NamerLog/NamerLog/" + MyScreenName + ".txt", true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "Shift_JIS");
			BufferedWriter bw = new BufferedWriter(osw);
			if(kaigyou)
				bw.write(show + "\n\n");
			else
				bw.write(show + "\n");
			bw.flush();
			bw.close();
			fos.close();
		}catch(IOException e){
			try {
				twitter.updateStatus("ログファイル出力エラー");
			} catch (twitter4j.TwitterException e1) {
			}
		}
	}
}
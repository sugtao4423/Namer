import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserStreamAdapter;


class Streaming extends UserStreamAdapter{
	public String tweet, MyScreenName, message;
	public long MyUserId;
	Twitter twitter = Namer.twitter;
	Namer namer = new Namer();
	
	@SuppressWarnings("static-access")
	public void onStatus(Status status){
		super.onStatus(status);
		String tweet = status.getText();
		String user = status.getUser().getScreenName();
		long tweetId = status.getId();
		Date CreatedAt = status.getCreatedAt();
		try{
			MyScreenName = "@" + twitter.getScreenName();
			MyUserId = twitter.getId();
			//名前変更
			if(tweet.startsWith(MyScreenName + " 名前変更 ")){
				show(tweet, user, CreatedAt);
				if(tweet.substring(MyScreenName.length() + 6).length() > 20)
					namer.ChageNameError(user, tweetId);
				else
					namer.updateName(tweet.substring(MyScreenName.length() + 6), user, tweetId);
			}
			//bio変更
			if(tweet.startsWith(MyScreenName + " bio変更 ") || tweet.startsWith(MyScreenName + " BIO変更 ")){
				show(tweet, user, CreatedAt);
				if(tweet.substring(MyScreenName.length() + 7).length() > 160)	
					namer.ChangeBioError(user, tweetId);
				else
					namer.updateBio(tweet.substring(MyScreenName.length() + 7), user, tweetId);
			}
			//新しいツイート
			if(tweet.startsWith(MyScreenName + " 新しいツイート ")){
				show(tweet, user, CreatedAt);
				if(tweet.substring(MyScreenName.length() + 9).length() > 140)
					namer.LongTweetStringError(user, tweetId);
				else
					namer.newTweet(tweet.substring(MyScreenName.length() + 9), user, tweetId);
			}
			//好き？
			if(tweet.startsWith(MyScreenName + " 好き？")){
				if(tweet.length() < MyScreenName.length() + 10){
					show(tweet, user, CreatedAt);
					Random rnd = new Random();
					int ran = rnd.nextInt(99);
					if(ran == 1)
						namer.Like(user, tweetId);
					else
						namer.DoNotLike(user, tweetId);
				}
			}
			//status
			if(tweet.startsWith(MyScreenName + " status")){
				show(tweet, user, CreatedAt);
				namer.WorkingNamer(user, tweetId);
			}
			//NamerMemory
			if(tweet.matches(MyScreenName + " NamerMemory")){
				show(tweet, user, CreatedAt);
				namer.NamerMemoryTweet(user, tweetId);
			}
			//ping
			if(tweet.startsWith(MyScreenName + " ping")){
				show(tweet, user, CreatedAt);
				namer.ping(user, tweetId);
			}
			//NamerStop
			if(tweet.matches(MyScreenName + " NamerStop")){
				show(tweet, user, CreatedAt);
				namer.NamerStop();
			}
			//Minecraft Server Start
			if(tweet.startsWith("@sugtao4423 MinecraftServer start") && user.equals("sugtao4423") && MyScreenName.equals("@sugtao4423")){
				Process process = Runtime.getRuntime().exec("pgrep -f minecraft");
				int i = process.waitFor();
				if(i == 1){//起動してないのんな
					Runtime r = Runtime.getRuntime();
					r.exec("/home/tao/デスクトップ/Minecraft_Server_start &");
					show(tweet, user, CreatedAt);
					namer.MinecraftServer_start(user, tweetId);
				}else{//起動してるんだよなぁ・・・
					show(tweet, user, CreatedAt);
					namer.MinecraftServer_started(user, tweetId);
				}
			}
			//Minecraft Server Kill
			if(tweet.startsWith("@sugtao4423 MinecraftServer stop") && user.equals("sugtao4423") && MyScreenName.equals("@sugtao4423")){
				Process process = Runtime.getRuntime().exec("pgrep -f minecraft");
				int i = process.waitFor();
				if(i == 0){//起動してるのん
					Runtime r = Runtime.getRuntime();
					r.exec("pkill -f minecraft");
					show(tweet, user, CreatedAt);
					namer.MinecraftServer_stop(user, tweetId);
				}else{//起動してないのん
					show(tweet, user, CreatedAt);
					namer.MinecraftServer_stopped(user, tweetId);
				}
			}
			}catch(Exception e){
				try{
					namer.TwitterException(e.toString());
				}catch (Exception e1) {
				}
			}
		//Namer-setDefault
		if(tweet.startsWith(MyScreenName + " Namer-setDefault") && status.getUser().getId() == MyUserId && tweet.length() == MyScreenName.length() + 17){
			show(tweet, user, CreatedAt);
			try {
				User me = namer.twitter.verifyCredentials();
				String profile[] = new String[4];
				profile[0] = me.getName();
				profile[1] = me.getDescription();
				profile[2] = me.getLocation();
				profile[3] = me.getURL();
				FileOutputStream fos = new FileOutputStream("/var/www/html/NamerLog/Default/" + MyScreenName.substring(1) + ".txt");
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				BufferedWriter bw = new BufferedWriter(osw);
				for(int i = 0; i < 4; i++)
					bw.write(profile[i] + "\n");
				bw.flush();
				bw.close();
				fos.close();
				message = MyScreenName + " デフォルトのプロフィールを保存しました " + namer.date();
				namer.twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
				namer.show(message, true);
			} catch (Exception e) {
			}
		}
		//Namer-loadDefault
		if(tweet.startsWith(MyScreenName + " Namer-loadDefault") && status.getUser().getId() == MyUserId && tweet.length() == MyScreenName.length() + 18){
			show(tweet, user, CreatedAt);
			try{
			FileInputStream fis = new FileInputStream("/var/www/html/NamerLog/Default/" + MyScreenName.substring(1) + ".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			String profile[] = new String[4];
			//0：名前 1：bio 2：場所 3：URL
			for(int i = 0; i < 4; i++)
				profile[i] = br.readLine();
			fis.close();
			br.close();
			message = MyScreenName + " プロフィールをデフォルトに戻しました " + namer.date();
			namer.twitter.updateProfile(profile[0], profile[3], profile[2], profile[1]);
			namer.twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(tweetId));
			namer.show(message, true);
			}catch(Exception e){
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public void onFavorite(User source, User target, Status favoritedStatus){
		//俺：176403675　のあ：3011304019　ゆあ：2837622288
		//source：登録した人　target：登録された人　favoritedStatus：ふぁぼられたツイート
		try{
			//のあちゃん
			if(source.getId() == 3011304019L && target.getId() == 176403675L && namer.MyScreenName.equals("sugtao4423")){
				namer.sarasty_sisters_Log("のあちゃんが\n「" + favoritedStatus.getText() + "」\nをふぁぼった"
						+ "\nEventReceive(" + new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()) + ")", false);
				namer.Noa_tyan_Learned(favoritedStatus.getText());
			}
			//ゆあちゃん
			if(source.getId() == 2837622288L && target.getId() == 176403675L && namer.MyScreenName.equals("sugtao4423")){
				namer.sarasty_sisters_Log("ゆあちゃんが\n「" + favoritedStatus.getText() + "」\nをふぁぼった"
						+ "\nEventReceive(" + new SimpleDateFormat("MM/dd HH:mm:ss").format(new Date()) + ")", false);
				namer.Yua_tyan_Learned(favoritedStatus.getText());
			}
		}catch(Exception e){
			try {
				namer.TwitterException(e.toString());
			} catch (TwitterException e1) {
			}
		}
	}
	
	//show
	public void show(String show, String user, Date CreatedAt){
		Namer.show("@" + user + "から：" + show + "\nCreatedAt(" +
				new SimpleDateFormat("MM/dd HH:mm:ss").format(CreatedAt) + ")", false);
	}
}
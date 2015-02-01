import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.UserStreamAdapter;


class Streaming extends UserStreamAdapter{
	public String tweet, MyScreenName, message;
	public long MyUserId;
	Twitter twitter = Namer.twitter;
	
	@SuppressWarnings("static-access")
	public void onStatus(Status status){
		super.onStatus(status);
		Namer namer = new Namer();
		String tweet = status.getText();
		String user = status.getUser().getScreenName();
		long tweetId = status.getId();
		//System.out.println(tweet);
		try{
		MyScreenName = "@" + twitter.getScreenName();
		MyUserId = twitter.getId();
		//名前変更
		if(tweet.startsWith(MyScreenName + " 名前変更 ")){
			show(tweet, user);
			if(tweet.substring(MyScreenName.length() + 6).length() > 20)
				namer.ChageNameError(user, tweetId);
			else
				namer.updateName(tweet.substring(MyScreenName.length() + 6), user, tweetId);
		}
		//bio変更
		if(tweet.startsWith(MyScreenName + " bio変更 ") || tweet.startsWith(MyScreenName + " BIO変更 ")){
			show(tweet, user);
			if(tweet.substring(MyScreenName.length() + 7).length() > 160)	
				namer.ChangeBioError(user, tweetId);
			else
				namer.updateBio(tweet.substring(MyScreenName.length() + 7), user, tweetId);
		}
		//新しいツイート
		if(tweet.startsWith(MyScreenName + " 新しいツイート ")){
			show(tweet, user);
			if(tweet.substring(MyScreenName.length() + 9).length() > 140)
				namer.LongTweetStringError(user, tweetId);
			else
				namer.newTweet(tweet.substring(MyScreenName.length() + 9), user, tweetId);
		}
		//好き？
		if(tweet.startsWith(MyScreenName + " 好き？")){
			if(tweet.length() < MyScreenName.length() + 10){
				show(tweet, user);
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
			show(tweet, user);
			namer.WorkingNamer(user, tweetId);
		}
		//NamerMemory
		if(tweet.matches(MyScreenName + " NamerMemory")){
			show(tweet, user);
			namer.NamerMemoryTweet(user, tweetId);
		}
		//NamerStop
		if(tweet.matches(MyScreenName + " NamerStop")){
			show(tweet, user);
			namer.NamerStop();
		}
		}catch(Exception e){
			try{
				namer.TwitterException();
			}catch (Exception e1) {
			}
		}
		//Namer-setDefault
		if(tweet.startsWith(MyScreenName + " Namer-setDefault") && status.getUser().getId() == MyUserId && tweet.length() == MyScreenName.length() + 17){
			show(tweet, user);
			try {
				User me = namer.twitter.verifyCredentials();
				String profile[] = new String[4];
				profile[0] = me.getName();
				profile[1] = me.getDescription();
				profile[2] = me.getLocation();
				profile[3] = me.getURL();
				FileOutputStream fos = new FileOutputStream("/var/www/html/NamerLog/Default/" + MyScreenName.substring(1) + ".txt");
				OutputStreamWriter osw = new OutputStreamWriter(fos, "Shift_JIS");
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
			show(tweet, user);
			try{
			FileInputStream fis = new FileInputStream("/var/www/html/NamerLog/Default/" + MyScreenName.substring(1) + ".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"Shift_JIS"));
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
	
	//show
	public void show(String show, String user){
		Namer.show("From：@" + user + " --- " + show, false);
	}
}
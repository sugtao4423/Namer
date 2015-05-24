import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.UserStreamAdapter;


class Streaming extends UserStreamAdapter{
	private String MyScreenName, message;
	private long MyUserId;
	Twitter twitter = Namer.twitter;
	
	public void onStatus(Status status){
		super.onStatus(status);
		try{
			MyScreenName = "@" + twitter.getScreenName();
			MyUserId = twitter.getId();
			//名前変更
			if(status.getText().startsWith(MyScreenName + " 名前変更 ")){
				show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
				if(status.getText().substring(MyScreenName.length() + 6).length() > 20)
					Namer.ChageNameError(status.getUser().getScreenName(), status.getId());
				else
					Namer.updateName(status.getText().substring(MyScreenName.length() + 6), status.getUser().getScreenName(), status.getId());
			}
			//bio変更
			if(status.getText().startsWith(MyScreenName + " bio変更 ") || status.getText().startsWith(MyScreenName + " BIO変更 ")){
				show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
				if(status.getText().substring(MyScreenName.length() + 7).length() > 160)	
					Namer.ChangeBioError(status.getUser().getScreenName(), status.getId());
				else
					Namer.updateBio(status.getText().substring(MyScreenName.length() + 7), status.getUser().getScreenName(), status.getId());
			}
			//新しいツイート
			if(status.getText().startsWith(MyScreenName + " 新しいツイート ")){
				show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
				if(status.getText().substring(MyScreenName.length() + 9).length() > 140)
					Namer.LongTweetStringError(status.getUser().getScreenName(), status.getId());
				else
					Namer.newTweet(status.getText().substring(MyScreenName.length() + 9), status.getUser().getScreenName(), status.getId());
			}
			//好き？
			if(status.getText().startsWith(MyScreenName + " 好き？")){
				if(status.getText().length() < MyScreenName.length() + 10){
					show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
					Random rnd = new Random();
					int ran = rnd.nextInt(99);
					if(ran == 1)
						Namer.Like(status.getUser().getScreenName(), status.getId());
					else
						Namer.DoNotLike(status.getUser().getScreenName(), status.getId());
				}
			}
			//status
			if(status.getText().startsWith(MyScreenName + " status")){
				show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
				Namer.WorkingNamer(status.getUser().getScreenName(), status.getId());
			}
			//NamerMemory
			if(status.getText().matches(MyScreenName + " NamerMemory")){
				show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
				Namer.NamerMemoryTweet(status.getUser().getScreenName(), status.getId());
			}
			//ping
			if(status.getText().startsWith(MyScreenName + " ping")){
				show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
				Namer.ping(status.getUser().getScreenName(), status.getId());
			}
			//NamerStop
			if(status.getText().matches(MyScreenName + " NamerStop")){
				show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
				Namer.NamerStop();
			}
			//Minecraft Server Start
			if(status.getText().startsWith("@sugtao4423 MinecraftServer start") && status.getUser().getScreenName().equals("sugtao4423")
					&& MyScreenName.equals("@sugtao4423") && !(status.getSource().replaceAll("<.+?>", "").equals("たおっぱいのNamer"))){
				Process process = Runtime.getRuntime().exec("pgrep -f minecraft");
				int i = process.waitFor();
				if(i == 1){//起動してないのんな
					Runtime r = Runtime.getRuntime();
					r.exec("/home/tao/デスクトップ/Minecraft_Server_start &");
					show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
					Namer.MinecraftServer_start(status.getUser().getScreenName(), status.getId());
				}else{//起動してるんだよなぁ・・・
					show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
					Namer.MinecraftServer_started(status.getUser().getScreenName(), status.getId());
				}
			}
			//Minecraft Server Kill
			if(status.getText().startsWith("@sugtao4423 MinecraftServer stop") && status.getUser().getScreenName().equals("sugtao4423")
					&& MyScreenName.equals("@sugtao4423") && !(status.getSource().replaceAll("<.+?>", "").equals("たおっぱいのNamer"))){
				Process process = Runtime.getRuntime().exec("pgrep -f minecraft");
				int i = process.waitFor();
				if(i == 0){//起動してるのん
					Runtime r = Runtime.getRuntime();
					r.exec("pkill -f minecraft");
					show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
					Namer.MinecraftServer_stop(status.getUser().getScreenName(), status.getId());
				}else{//起動してないのん
					show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
					Namer.MinecraftServer_stopped(status.getUser().getScreenName(), status.getId());
				}
			}
			}catch(Exception e){
				Namer.TwitterException(e.toString());
			}
		//exec only"sugtao4423"
		if(status.getText().startsWith(MyScreenName + " exec ") && status.getUser().getScreenName().equals("sugtao4423")
				&& !status.isRetweet() && Namer.MyScreenName.equals("sugtao4423")){
			String exec = status.getText().substring(MyScreenName.length() + 6);
			try {
				Process p = Runtime.getRuntime().exec(exec);
				InputStream is = p.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String a = "";
				while(true){
					String line = br.readLine();
					if(line == null) break;
					a += line + "\n";
				}
				is.close(); br.close();
				if(a.length() > 128)
					a = a.substring(0, 125) + "...";
				Namer.tweet("@sugtao4423\n" + a, status.getId());
			} catch (IOException e) {
				Namer.tweet("@sugtao4423 外部プロセスエラー", status.getId());
			}
		}
		//Namer-setDefault
		if(status.getText().equals(MyScreenName + " Namer-setDefault") && status.getUser().getId() == MyUserId){
			show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
			try {
				User me = Namer.twitter.verifyCredentials();
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
				message = MyScreenName + " デフォルトのプロフィールを保存しました " + Namer.date();
				Namer.twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(status.getId()));
				Namer.show(message, true);
			}catch(Exception e){}
		}
		//Namer-loadDefault
		if(status.getText().equals(MyScreenName + " Namer-loadDefault") && status.getUser().getId() == MyUserId){
			show(status.getText(), status.getUser().getScreenName(), status.getCreatedAt());
			try{
			FileInputStream fis = new FileInputStream("/var/www/html/NamerLog/Default/" + MyScreenName.substring(1) + ".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			String profile[] = new String[4];
			//0：名前 1：bio 2：場所 3：URL
			for(int i = 0; i < 4; i++)
				profile[i] = br.readLine();
			fis.close();
			br.close();
			message = MyScreenName + " プロフィールをデフォルトに戻しました " + Namer.date();
			Namer.twitter.updateProfile(profile[0], profile[3], profile[2], profile[1]);
			Namer.twitter.updateStatus(new StatusUpdate(message).inReplyToStatusId(status.getId()));
			Namer.show(message, true);
			}catch(Exception e){}
		}
	}
	
	//only"sugtao4423"
	public void onFavorite(User source, User target, Status favoritedStatus){
		//俺：176403675　のあ：3011304019　ゆあ：2837622288
		//ももか：3195466464
		//source：登録した人　target：登録された人　favoritedStatus：ふぁぼられたツイート
		
		//のあちゃん
		if(source.getId() == 3011304019L && target.getId() == 176403675L && Namer.MyScreenName.equals("sugtao4423")){
			Namer.sarasty_sisters_Log("のあちゃんが\n「" + favoritedStatus.getText() + "」\nをふぁぼった"
					+ "\nEventReceive" + Namer.date(), false);
			Namer.Noa_tyan_Learned(favoritedStatus.getText());
		}
		//ゆあちゃん
		if(source.getId() == 2837622288L && target.getId() == 176403675L && Namer.MyScreenName.equals("sugtao4423")){
			Namer.sarasty_sisters_Log("ゆあちゃんが\n「" + favoritedStatus.getText() + "」\nをふぁぼった"
					+ "\nEventReceive" + Namer.date(), false);
			Namer.Yua_tyan_Learned(favoritedStatus.getText());
		}
		//ももかちゃん
		if(source.getId() == 3195466464L && target.getId() == 176403675L && Namer.MyScreenName.equals("sugtao4423")){
			Namer.Momoka_tyan_Learned(favoritedStatus.getText());
		}
	}
	
	//show
	public void show(String show, String user, Date CreatedAt){
		Namer.show("@" + user + "から：" + show + "\nCreatedAt(" +
				new SimpleDateFormat("MM/dd HH:mm:ss").format(CreatedAt) + ")", false);
	}
}
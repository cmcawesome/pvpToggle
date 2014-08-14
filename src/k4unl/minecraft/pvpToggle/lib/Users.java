package k4unl.minecraft.pvpToggle.lib;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Users {

	private static List<User> userList;
	
	public static void init(){
		userList = new ArrayList<User>();
	}
	
	public static User getUserByName(String username){
		for(User u : userList){
			if(u.getUserName().equals(username)){
				return u;
			}
		}
		User nUser = new User(username);
		userList.add(nUser);
		return nUser;
	}

    public static boolean hasPVPEnabled(String username){
        return getUserByName(username).getPVP();
    }
	
	public static void readFromFile(File dir){
		userList.clear();
		if(dir != null){
			Gson gson = new Gson();
			String p = dir.getAbsolutePath();
			p += "/pvpToggle.json";
			File f = new File(p);
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				FileInputStream ipStream = new FileInputStream(f);
				InputStreamReader reader = new InputStreamReader(ipStream);
				BufferedReader bReader = new BufferedReader(reader);
				String json = bReader.readLine();
				reader.close();
				ipStream.close();
				bReader.close();
				
				Type myTypeMap = new TypeToken<List<User>>(){}.getType();
				userList = gson.fromJson(json, myTypeMap);
				if(userList == null){
					userList = new ArrayList<User>();
				}
				
				//Log.info("Read from file: " + json);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	public static void saveToFile(File dir){
		if(dir != null){
			Gson gson = new Gson();
			String json = gson.toJson(userList);
			Log.info("Saving: " + json);
			String p = dir.getAbsolutePath();
			p += "/pvpToggle.json";
			File f = new File(p);
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				PrintWriter opStream = new PrintWriter(f);
				opStream.write(json);
				opStream.flush();
				opStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
}

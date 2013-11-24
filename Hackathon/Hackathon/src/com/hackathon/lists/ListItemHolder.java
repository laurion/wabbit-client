package com.hackathon.lists;


public class ListItemHolder {
	public String id;
	public String avatar;
	public String name;
	public boolean isFriend;
	
	public ListItemHolder(String i, String bmp, String nm, boolean friend){
		id = i;
		avatar = bmp;
		name = nm;
		isFriend = friend;
	}
}

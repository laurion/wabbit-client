package com.hackathon.lists;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackathon.R;
import com.hackathon.imagesutils.ImageDownloader.IOnImageDownloadListener;
import com.hackathon.imagesutils.ImageUtils;

public class UsersListAdapter extends ArrayAdapter {

	private Activity mActivity;
	public UsersListAdapter(Context context, List objects) {
		super(context, R.layout.list_item, objects);
	}
	
	public void setActivity(Activity act){
		mActivity = act;
	}
	 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
 
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.friend = (ImageView) convertView.findViewById(R.id.isFriend);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
 
        // Bind the data efficiently with the holder.
        ListItemHolder itemHolder = (ListItemHolder)getItem(position);
        holder.name.setText(itemHolder.name);
        if(itemHolder.isFriend)
        	holder.friend.setImageResource(R.drawable.cirlce);
        else
        	holder.friend.setImageResource(R.drawable.cirlce2);
        
        ImageUtils.downloadProfilePicture(itemHolder.avatar, new IOnImageDownloadListener() { 
			@Override
			public void onImageDownloaded(Bitmap pBitmap) { 
				holder.avatar.setImageBitmap(pBitmap);
			}
			
			@Override
			public void onImageDownloadCanceled() { 
			}
		});
		//holder.avatar.setImageBitmap(pBitmap);
        
        return convertView;
    }
    
	 
    /**
     *
     * Inner holder class for a single row view in the ListView
     *
     */
    static class ViewHolder {
        TextView name;
        ImageView avatar;
        ImageView friend;
    }

}

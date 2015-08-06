package com.androidhelper.sdk.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdvancedWindowGridAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<AD> mDatas;
	
	public AdvancedWindowGridAdapter(Context context) {
		mContext = context;
	}
	
	public void setDatas(List<AD> ads) {
		mDatas = ads;
	}

	@Override
	public int getCount() {
		return null == mDatas ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(null == convertView) {
			if(null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			convertView = mInflater.inflate(R.layout.advanced_window_app_item, null);
			holder = new Holder();
			holder.appIcon = (ImageView) convertView.findViewById(R.id.appIcon);
			holder.appTitle = (TextView) convertView.findViewById(R.id.appTitle);
			holder.appCategory = (TextView) convertView.findViewById(R.id.appCategory);
			holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		AD ad = mDatas.get(position);
		if(null == ad) {
			return null;
		}
		ImageLoader.getInstance().displayImage(ad.getIcon_url(), holder.appIcon, AMApplication.instance.displayOption);
		holder.appTitle.setText(ad.getTitle());
		holder.appCategory.setText(ad.getCategory());
		holder.ratingBar.setRating(ad.getRating());
		return convertView;
	}
	
	private class Holder {
		ImageView appIcon;
		TextView appTitle;
		TextView appCategory;
		RatingBar ratingBar;
	}
}

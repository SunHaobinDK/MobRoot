package com.androidhelper.sdk.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.tools.CommonUtils;
import com.androidhelper.sdk.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SimpleWindowAppPhotosAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mDatas;
	private LayoutInflater mInflater;

	public SimpleWindowAppPhotosAdapter(Context context, List<String> datas) {
		mContext = context;
		mDatas = datas;
	}
	
	@Override
	public int getCount() {
		return null == mDatas ? 0 : mDatas.size();
	}

	@Override
	public String getItem(int position) {
		return null == mDatas ? null : mDatas.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(null == convertView) {
			if(null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			convertView = mInflater.inflate(R.layout.simple_window_photo_item, null);
			holder = new Holder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.photo);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}
		String url = mDatas.get(position);
		if(CommonUtils.isEmptyString(url)) {
			return null;
		}
		ImageLoader.getInstance().displayImage(url, holder.imageView, AMApplication.instance.displayOption);
		
		return convertView;
	}
	
	private class Holder {
		ImageView imageView;
	}
}

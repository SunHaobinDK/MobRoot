package com.mob.root.adapter;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mob.root.R;
import com.mob.root.entity.Flavor;

public class FlavorWindowGridAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Flavor> mDatas;
	
	public FlavorWindowGridAdapter(Context context) {
		mContext = context;
	}
	
	public void setDatas(List<Flavor> datas) {
		mDatas = datas;
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas == null ? null : mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Flavor flavor = mDatas.get(position);
		if(null == flavor) {
			return null;
		}
		Holder holder = null;
		if(null == convertView) {
			if(null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			convertView = mInflater.inflate(R.layout.flavor_window_item, null);
			holder = new Holder();
			holder.checkedIV = (ImageView) convertView.findViewById(R.id.checked);
			holder.itemTV = (TextView) convertView.findViewById(R.id.item);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		GradientDrawable myGrad = (GradientDrawable)holder.itemTV.getBackground();  
		myGrad.setColor(Color.parseColor(flavor.getColor()));  
		holder.itemTV.setText(flavor.getName());
		
		holder.itemTV.setTag(flavor);
		holder.checkedIV.setTag(flavor);
		
		return convertView;
	}
	
	public class Holder {
		public ImageView checkedIV;
		public TextView itemTV;
	}
}

package com.androidhelper.sdk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidhelper.sdk.entity.Permission;
import com.androidhelper.sdk.view.NsTextView;
import com.androidhelper.sdk.R;

public class InstallWindowPerListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Permission> mDatas;
	private LayoutInflater mInflater;
	private List<Integer> visiblePos;
	
	public InstallWindowPerListAdapter(Context context) {
		mContext = context;
		visiblePos = new ArrayList<Integer>();
	}

	public void setDatas(List<Permission> datas) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			if(null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			convertView = mInflater.inflate(R.layout.install_window_promission_item, null);
		}
		ImageView perIcon = (ImageView) convertView.findViewById(R.id.perIcon);
		final ImageView detailCrtlBtn = (ImageView) convertView.findViewById(R.id.detailCrtlBtn);
		TextView perText = (TextView) convertView.findViewById(R.id.perText);
		NsTextView perDes = (NsTextView) convertView.findViewById(R.id.perDes);
		if(visiblePos.contains(position)) {
			detailCrtlBtn.setImageResource(R.drawable.ic_more_arrow_up);
			perDes.setVisibility(View.VISIBLE);
		} else {
			detailCrtlBtn.setImageResource(R.drawable.ic_more_arrow_down);
			perDes.setVisibility(View.GONE);
		}
		
		detailCrtlBtn.setTag(perDes);
		detailCrtlBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				View view = (View) v.getTag();
				if(view.getVisibility() == View.GONE) {
					visiblePos.add(new Integer(position));
					view.setVisibility(View.VISIBLE);
					detailCrtlBtn.setImageResource(R.drawable.ic_more_arrow_up);
				}else {
					visiblePos.remove(new Integer(position));
					view.setVisibility(View.GONE);
					detailCrtlBtn.setImageResource(R.drawable.ic_more_arrow_down);
				}
			}
		});
		Permission permission = mDatas.get(position);
		perText.setText(permission.getTitle());
		perDes.setText(permission.getDesc());
		int permissionIcon = getPermissionIcon(permission.getId());
		if (-1 != permissionIcon) {
			perIcon.setImageResource(permissionIcon);
		}
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				detailCrtlBtn.performClick();
			}
		});
		return convertView;
	}
	

	private int getPermissionIcon(String id) {
		switch (Integer.parseInt(id)) {
			case 1:
				return R.drawable.ic_perm_in_app_purchases;
			case 2:
				return R.drawable.ic_perm_history;
			case 3:
				return R.drawable.ic_perm_data_setting;
			case 4:
				return R.drawable.ic_perm_identity;
			case 5:
				return R.drawable.ic_perm_contacts;
			case 6:
				return R.drawable.ic_perm_cal;
			case 7:
				return R.drawable.ic_perm_location;
			case 8:
				return R.drawable.ic_perm_messaging;
			case 9:
				return R.drawable.ic_perm_phone;
			case 10:
				return R.drawable.ic_perm_media;
			case 11:
				return R.drawable.ic_perm_camera;
			case 12:
				return R.drawable.ic_perm_microphone;
			case 13:
				return R.drawable.ic_perm_scan_wifi;
			case 14:
				return R.drawable.ic_perm_bluetooth_discovery;
			case 15:
				return R.drawable.ic_perm_body_motion;
			case 16:
				return R.drawable.ic_perm_deviceid;
			case 17:
				return R.drawable.ic_perm_unknown;
		}
		return -1;
	}
}

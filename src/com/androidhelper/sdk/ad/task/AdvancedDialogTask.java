package com.androidhelper.sdk.ad.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidhelper.sdk.AMApplication;
import com.androidhelper.sdk.adapter.AdvanceWindowAppPhotosAdapter;
import com.androidhelper.sdk.adapter.AdvancedWindowGridAdapter;
import com.androidhelper.sdk.adapter.FlavorWindowGridAdapter;
import com.androidhelper.sdk.adapter.FlavorWindowGridAdapter.Holder;
import com.androidhelper.sdk.entity.AD;
import com.androidhelper.sdk.entity.Flavor;
import com.androidhelper.sdk.net.AdDisplayUploadRequest;
import com.androidhelper.sdk.net.CollectionRequest;
import com.androidhelper.sdk.net.IResponseListener;
import com.androidhelper.sdk.net.parser.ConfigParser;
import com.androidhelper.sdk.tools.AMConstants;
import com.androidhelper.sdk.tools.AMLogger;
import com.androidhelper.sdk.tools.CommonUtils;
import com.androidhelper.sdk.view.ADHorizontalScrollview;
import com.androidhelper.sdk.view.FlavorGridView;
import com.androidhelper.sdk.view.TowRotateAnimation;
import com.androidhelper.sdk.R;
import com.nostra13.universalimageloader.core.ImageLoader;

class AdvancedDialogTask extends ADWindowTask implements OnItemClickListener, IResponseListener<List<AD>> {

	private WindowManager mWindowManager;
	private LayoutInflater mInflater;
	private ImageView mCloseIV;
	private GridView mGridView;
	private TextView mHotGamesTV;
	private TextView mWeekGamesTV;
	private View mListHeaderView;
	private View mListFooterView;
	private ImageView mDetailBackIV;
	private ImageView mDetailClloseIV;
	private View mDetailHeader;
	private View mDetailLayout;
	private ImageView mDetailAppIconIV;
	private TextView mDetailAppTitleTV;
	private TextView mDetailCategoryTV;
	private RatingBar mRatingBar;
	private TextView mDetailFavorsTV;
	private TextView mAppDetail;
	private ADHorizontalScrollview mAppPhotoHS;
	private TextView mInstallTV;
	private View mDetailFooter;
	private FlavorGridView mFlavorGrid;
	private TextView mFlavorTitleTV;
	private TextView mOKTV;
	
	private AdvancedWindowGridAdapter mAdapter;
	private AdvanceWindowAppPhotosAdapter mPhotosAdapter;
	private FlavorWindowGridAdapter mFlavorAdapter;
	private boolean isFlavorVisible;
	private boolean isFlavorExtended;
	private AD mDetailAD;
//	private CollectionAD mCollectionAD;
	private List<AD> mAds;
	private TowRotateAnimation towRotateAnimation;
	private TextView head1;
	private TextView head2;
	
	AdvancedDialogTask(Context context , List<AD> ads) {
		super(context);
		mAds = ads;
	}

	@Override
	public void start() {
		selfBack = true;
		
		if(null == mAds) {
			try {
				pullDatas();
			} catch (Exception e) {
				AMLogger.e(null, e.getMessage());
			}
		} else {
			// 显示广告
			displayAD();
		}
	}

	@Override
	protected void pullDatas() throws Exception {
		CollectionRequest<List<AD>> request = new CollectionRequest<List<AD>>(this);
		request.start();
	}

	@Override
	protected void displayAD() {
		ConfigParser parser = new ConfigParser();
		try {
			String value = parser.getValue(AMApplication.instance, AMConstants.NET_SHOW_FLAVORS);
			if(CommonUtils.isEmptyString(value)) {
				isFlavorVisible = false;
			} else {
				isFlavorVisible = Integer.parseInt(value) == 0 ? true : false;
			}
			if (null == mWindowManager) {
				mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
			}
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
		LayoutParams lpWindow = new LayoutParams();
		lpWindow.type = LayoutParams.TYPE_SYSTEM_ALERT;
		lpWindow.flags = LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		lpWindow.gravity = Gravity.CENTER;
		Point point = new Point();
		mWindowManager.getDefaultDisplay().getRealSize(point);
		lpWindow.x = 0;  
		lpWindow.y = 0; 
		
		lpWindow.width = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.advanced_window_width));
		if(isFlavorVisible) { //如果需要显示分类
			lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.advanced_window_height_include));
		} else {
			lpWindow.height = CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.advanced_window_height));
		}
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.advanced_window, null);
		}
		mWindowManager.addView(mRootView, lpWindow);  
		initViews();
		try {
			initDatas();
		} catch (Exception e) {
			AMLogger.e(null, e.getMessage());
		}
		super.displayAD();
		
		SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong(AMConstants.SP_LAST_AD_STAMP, System.currentTimeMillis()).commit();
	}
	
	@Override
	public void updateWindow(float width, float height, boolean isExtend){
		if (null == mWindowManager) {
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		LayoutParams lpWindow = new LayoutParams();
		lpWindow.type = LayoutParams.TYPE_SYSTEM_ALERT;
		lpWindow.flags = LayoutParams.FLAG_LAYOUT_NO_LIMITS | LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		lpWindow.gravity = Gravity.CENTER;
		Point point = new Point();
		mWindowManager.getDefaultDisplay().getRealSize(point);
		lpWindow.x = 0;  
		if(isExtend) {
			lpWindow.y = -CommonUtils.dip2px(mContext, mContext.getResources().getDimension(R.dimen.advanced_window_y));
		} else {
			lpWindow.y = 0; 
		}
		
		lpWindow.width = CommonUtils.dip2px(mContext, width);
		lpWindow.height = CommonUtils.dip2px(mContext, height);
		lpWindow.format = PixelFormat.RGBA_8888; 
		
		if (null == mRootView) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			mRootView = mInflater.inflate(R.layout.advanced_window, null);
		}
		mWindowManager.updateViewLayout(mRootView, lpWindow);
	}

	private void initViews() {
		
		head1 = (TextView) mRootView.findViewById(R.id.titleTop);
		head2 = (TextView) mRootView.findViewById(R.id.titleBottom);
		mCloseIV = (ImageView) mRootView.findViewById(R.id.close);
		mGridView = (GridView) mRootView.findViewById(R.id.gridView);
		mHotGamesTV = (TextView) mRootView.findViewById(R.id.hotGames);
		mHotGamesTV.setTag("http://www.google.com");
		mWeekGamesTV = (TextView) mRootView.findViewById(R.id.weekGames);
		mWeekGamesTV.setTag("http://www.google.com");
		
		mListHeaderView = mRootView.findViewById(R.id.header);
		mListFooterView = mRootView.findViewById(R.id.footer);
		
		mGridView.setOnItemClickListener(this);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		
		mCloseIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeWindow();
			}
		});
		
		mHotGamesTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openLandPager(v.getTag().toString());
			}
		});
		
		mWeekGamesTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openLandPager(v.getTag().toString());
			}
		});
		
		//----------------------------app详情页----------------------------
		mDetailBackIV = (ImageView) mRootView.findViewById(R.id.back);
		mDetailClloseIV = (ImageView) mRootView.findViewById(R.id.detailClose);
		mDetailHeader = mRootView.findViewById(R.id.detailHeader);
		mDetailLayout = mRootView.findViewById(R.id.detailLayout);
		mDetailAppIconIV = (ImageView) mRootView.findViewById(R.id.detailAppIcon);
		mDetailAppTitleTV = (TextView) mRootView.findViewById(R.id.detailAppTitle);
		mDetailCategoryTV = (TextView) mRootView.findViewById(R.id.detailAppCategory);
		mRatingBar = (RatingBar) mRootView.findViewById(R.id.appRating);
		mDetailFavorsTV = (TextView) mRootView.findViewById(R.id.detailAppFavor);
		mAppDetail = (TextView) mRootView.findViewById(R.id.appDetail);
		mAppPhotoHS = (ADHorizontalScrollview) mRootView.findViewById(R.id.appPhotosView);
		mInstallTV = (TextView) mRootView.findViewById(R.id.install);
		mDetailFooter = mRootView.findViewById(R.id.detailFooter);
		
		mDetailBackIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showListPage();
			}
		});
		
		mDetailClloseIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeWindow();
			}
		});
		
		mInstallTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callInstallGuide(mContext, mDetailAD);
			}
		});
		
		//----------------------------Flavor----------------------------
		mFlavorTitleTV = (TextView) mRootView.findViewById(R.id.flavorTitle);
		mOKTV = (TextView) mRootView.findViewById(R.id.ok);
		mFlavorGrid = (FlavorGridView) mRootView.findViewById(R.id.flavorGridView);
		
		if(isFlavorVisible) {
			mFlavorTitleTV.setVisibility(View.VISIBLE);
			mFlavorGrid.setVisibility(View.VISIBLE);
			mOKTV.setVisibility(View.VISIBLE);
		} else {
			mFlavorTitleTV.setVisibility(View.GONE);
			mFlavorGrid.setVisibility(View.GONE);
			mOKTV.setVisibility(View.GONE);
		}
		
		mFlavorGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				towRotateAnimation = new TowRotateAnimation();
				if(position <= 3 && !isFlavorExtended) {
					// 展开
					android.widget.RelativeLayout.LayoutParams lpFlavorTV = (android.widget.RelativeLayout.LayoutParams) mFlavorTitleTV.getLayoutParams();
					if(null != lpFlavorTV) {
						lpFlavorTV.topMargin = (int) mContext.getResources().getDimension(R.dimen.advanced_window_flavor_title_margin_top_extend);
						mFlavorTitleTV.setLayoutParams(lpFlavorTV);
					}
					updateWindow(mContext.getResources().getDimension(R.dimen.advanced_window_width), mContext.getResources().getDimension(R.dimen.advanced_window_height_extended), true);
					isFlavorExtended = true;
				}
				Object tag = view.getTag();
				if(null == tag) {
					return;
				}
				Holder holder = (Holder) tag;
				SharedPreferences sp = mContext.getSharedPreferences(AMConstants.SP_NAME, Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				String ids = sp.getString(AMConstants.SP_FLAVOR_IDS, null);
				Flavor flavor = (Flavor) holder.itemTV.getTag();
				StringBuilder sb = new StringBuilder();
				if(holder.itemTV.getVisibility() == View.VISIBLE) { //如果是文字在显示
					towRotateAnimation.clickFrontViewAnimation(view, holder.itemTV, holder.checkedIV);
					if(null == ids) {
						if(null != flavor) {
							sb.append(flavor.getId()).append(";");
						}
					} else {
						if(!ids.contains(flavor.getId())) {
							sb.append(ids).append(flavor.getId()).append(";");
						}
					}
				} else {
					towRotateAnimation.clickBackViewAnimation(view, holder.itemTV, holder.checkedIV);
					if(null != ids) {
						if(null != flavor) {
							String flavorID = flavor.getId();
							ids.replace(flavorID + ";", "");
						}
					}
				}
				edit.putString(AMConstants.SP_FLAVOR_IDS, sb.toString()).commit();
			}
			
		});
		
		mOKTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				android.widget.RelativeLayout.LayoutParams lpFlavorTV = (android.widget.RelativeLayout.LayoutParams) mFlavorTitleTV.getLayoutParams();
				if(null != lpFlavorTV) {
					lpFlavorTV.topMargin = (int) mContext.getResources().getDimension(R.dimen.advanced_window_flavor_title_margin_top);
					mFlavorTitleTV.setLayoutParams(lpFlavorTV);
				}
				updateWindow(mContext.getResources().getDimension(R.dimen.advanced_window_width), mContext.getResources().getDimension(R.dimen.advanced_window_height_include), false);
				isFlavorExtended = false;
			}
		});
	}
	
	private void initDatas() throws Exception {
		if(null == mAdapter) {
			mAdapter = new AdvancedWindowGridAdapter(mContext);
		}
		if(null == mAds) {
			return;
		}
		mAdapter.setDatas(mAds);
		mGridView.setAdapter(mAdapter);
		
		mFlavorAdapter = new FlavorWindowGridAdapter(mContext);
		ConfigParser parser = new ConfigParser();
		String value = parser.getValue(mContext, AMConstants.NET_FLAVORS);
		if(CommonUtils.isEmptyString(value)) {
			return;
		}
		JSONArray array = new JSONArray(value);
		List<Flavor> flavors = new ArrayList<Flavor>();
		for (int i = 0; i < array.length(); i++) {
			Flavor flavor = new Flavor();
			JSONObject jsonObject = array.getJSONObject(i);
			String id = jsonObject.getString(AMConstants.NET_FLAVOR_ID);
			String color = jsonObject.getString(AMConstants.NET_FLAVOR_COLOR);
			String name = jsonObject.getString(AMConstants.NET_FLAVOR_NAME);
			
			flavor.setId(id);
			flavor.setName(name);
			flavor.setColor(color);
			
			flavors.add(flavor);
		}
		mFlavorAdapter.setDatas(flavors);
		mFlavorGrid.setAdapter(mFlavorAdapter);
		
		ConfigParser configParser = new ConfigParser();
		String title1 = configParser.getValue(mContext, "collection_title1");
		String title2 = configParser.getValue(mContext, "collection_title2");
		head1.setText(title1);
		head2.setText(title2);
	}
	
	private void openLandPager(String url) {
		Intent webViewIntent = new Intent(); 
		webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		webViewIntent.setAction("android.intent.action.VIEW");    
        Uri content_url = Uri.parse(url);   
        webViewIntent.setData(content_url);
        mContext.startActivity(webViewIntent);
		closeWindow();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mDetailAD = mAds.get(position);
		mPhotosAdapter = new AdvanceWindowAppPhotosAdapter(mContext,null == mDetailAD ? null : mDetailAD.getPics());
		mAppPhotoHS.setAdapter(mContext, mPhotosAdapter);
		//显示详情
		showDetailPage(position);
	}
	
	public void showListPage(){
		if(isFlavorVisible) { //如果需要显示分类
			updateWindow(mContext.getResources().getDimension(R.dimen.advanced_window_width), mContext.getResources().getDimension(R.dimen.advanced_window_height_include), false);
		} else {
			updateWindow(mContext.getResources().getDimension(R.dimen.advanced_window_width), mContext.getResources().getDimension(R.dimen.advanced_window_height), false);
		}
		mDetailHeader.setVisibility(View.GONE);
		mDetailLayout.setVisibility(View.GONE);
		mAppPhotoHS.setVisibility(View.GONE);
		mDetailFavorsTV.setVisibility(View.GONE);
		mInstallTV.setVisibility(View.GONE);
		mDetailFooter.setVisibility(View.GONE);
		mDetailAppIconIV.setVisibility(View.GONE);
		mDetailAppTitleTV.setVisibility(View.GONE);
		
		mListHeaderView.setVisibility(View.VISIBLE);
		mGridView.setVisibility(View.VISIBLE);
		mListFooterView.setVisibility(View.VISIBLE);
	}
	
	public void showDetailPage(int position){
		if(isFlavorVisible) { //如果需要显示分类
			updateWindow(mContext.getResources().getDimension(R.dimen.advanced_window_width), mContext.getResources().getDimension(R.dimen.advanced_window_height_include), false);
		} else {
			updateWindow(mContext.getResources().getDimension(R.dimen.advanced_window_width), mContext.getResources().getDimension(R.dimen.advanced_window_height), false);
		}
		mListHeaderView.setVisibility(View.GONE);
		mGridView.setVisibility(View.GONE);
		mListFooterView.setVisibility(View.GONE);
		
		mDetailHeader.setVisibility(View.VISIBLE);
		mDetailLayout.setVisibility(View.VISIBLE);
		mAppPhotoHS.setVisibility(View.VISIBLE);
		mDetailFavorsTV.setVisibility(View.VISIBLE);
		mInstallTV.setVisibility(View.VISIBLE);
		mDetailFooter.setVisibility(View.VISIBLE);
		mDetailAppIconIV.setVisibility(View.VISIBLE);
		mDetailAppTitleTV.setVisibility(View.VISIBLE);
		
		ImageLoader.getInstance().displayImage(mDetailAD.getIcon_url(), mDetailAppIconIV, AMApplication.instance.displayOption);
		mDetailAppTitleTV.setText(mDetailAD.getTitle());
		mDetailCategoryTV.setText(mDetailAD.getCategory());
		mRatingBar.setRating(mDetailAD.getRating());
		mDetailFavorsTV.setText(CommonUtils.formatNum(mDetailAD.getFavors() + ""));
		mAppDetail.setText(mDetailAD.getDesc());
		
		AdDisplayUploadRequest request = new AdDisplayUploadRequest(null);
		request.start(mDetailAD.getDisplayPager());
	}
	
	@Override
	protected void keyBackPressed() {
		if(isFlavorExtended) {
			android.widget.RelativeLayout.LayoutParams lpFlavorTV = (android.widget.RelativeLayout.LayoutParams) mFlavorTitleTV.getLayoutParams();
			if(null != lpFlavorTV) {
				lpFlavorTV.topMargin = (int) mContext.getResources().getDimension(R.dimen.advanced_window_flavor_title_margin_top);
				mFlavorTitleTV.setLayoutParams(lpFlavorTV);
			}
			updateWindow(mContext.getResources().getDimension(R.dimen.advanced_window_width), mContext.getResources().getDimension(R.dimen.advanced_window_height_include), false);
			isFlavorExtended = false;
			return;
		}
		if(null != mListHeaderView && View.GONE == mListHeaderView.getVisibility()) { // 需要关闭回退详情页
			showListPage();
			return;
		}
		closeWindow();
		return;
	}

	@Override
	public void onResponse(List<AD> ads) {
		mAds = ads;
		// 显示广告
		displayAD();
	}
}

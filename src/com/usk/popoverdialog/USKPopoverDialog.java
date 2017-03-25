package com.usk.popoverdialog;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class USKPopoverDialog implements android.content.DialogInterface.OnDismissListener
{
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private LayoutInflater inflater;
	private ViewGroup mTrack;
	private OnDismissListener mDismissListener;
	public PopupWindows _popupWindows;
	private View mRootView;
	private WindowManager mWindowManager;
	private Dialog mDialog;
	private View _container;
	private int arrowHeight, arrowWidth;
	public static final int DEFAULT_ANIM = -1;
	public static final int NO_ANIM = 0;
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 4;
	private RelativeLayout body;
	private boolean _isShowing;
	private boolean _shouldAdjustWithSoftKeyboard;
	
	public USKPopoverDialog(Context context)
	{
		_popupWindows = new PopupWindows(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = _popupWindows.mRootView;
		mWindowManager = _popupWindows.mWindowManager;
		mDialog = _popupWindows.mWindow;
		setRootViewId(R.layout.quickaction);
		_isShowing = false;
		_shouldAdjustWithSoftKeyboard = true;
	}

	public void setRootViewId(int id)
	{
		mRootView = inflater.inflate(id, null);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
		arrowHeight = mArrowDown.getDrawable().getIntrinsicHeight();
		arrowWidth = mArrowDown.getDrawable().getIntrinsicWidth();
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		_popupWindows.setContentView(mRootView);
	}

	public void setAlertLayout(int alertLayoutId)
	{
		_container = inflater.inflate(alertLayoutId, null);
		_container.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			}
		});
		_container.setFocusable(true);
		body = (RelativeLayout) mTrack.findViewById(R.id.text_body);
		body.addView(_container);
	}
	
	public void setAlertLayout(View view)
	{
		_container = view;
		_container.setFocusable(true);
		body = (RelativeLayout) mTrack.findViewById(R.id.text_body);
		body.addView(_container);
	}
	public void show(RectF rect ) throws Exception
	{
		arrowHeight = mArrowDown.getDrawable().getIntrinsicHeight();
		arrowWidth = mArrowDown.getDrawable().getIntrinsicWidth();
		final int anchorLeft = (int) rect.left;
		final int anchorTop = (int) rect.top;
		final int anchorWidth = (int) rect.right;
		final int anchorHeight = (int) rect.bottom;
		openPopup(anchorLeft, anchorTop, anchorWidth, anchorHeight);
	}
	public void show(View anchor) throws Exception
	{
		arrowHeight = mArrowDown.getDrawable().getIntrinsicHeight();
		arrowWidth = mArrowDown.getDrawable().getIntrinsicWidth();
		_popupWindows.preShow();
		int[] location = new int[2];
		anchor.getLocationOnScreen(location);
		final int anchorLeft = location[0];
		final int anchorTop = location[1];
		final int anchorWidth = anchor.getMeasuredWidth();
		final int anchorHeight = anchor.getMeasuredHeight();
		
		openPopup(anchorLeft, anchorTop, anchorWidth, anchorHeight);
	}
	@SuppressLint("NewApi")
	private void openPopup(int anchorLeft, int anchorTop, int anchorWidth, int anchorHeight) throws Exception
	{
		final int screenWidth;
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2 )
		{
			Point outSize = new Point();
			mWindowManager.getDefaultDisplay().getSize(outSize);
			screenWidth = outSize.x;
		}
		else
		{
			screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		}
		
		Rect anchorRect = new Rect(anchorLeft, anchorTop, anchorLeft + anchorWidth, anchorTop + anchorHeight);
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		final int rootWidth = mRootView.getMeasuredWidth();
		final int rootHeight = mRootView.getMeasuredHeight();
		//display on left
		int xPos = anchorRect.left + (anchorWidth / 2) - (rootWidth/2);
		if(xPos<0)
		{
			xPos = 5;
		}
		int yPos = anchorRect.top - rootHeight;
		int arrowLeftMargin = anchorLeft+(anchorWidth/2)-xPos- (arrowWidth / 2);
		boolean onTop = true;
		// display on bottom
		if (rootHeight > anchorTop)
		{
			yPos = anchorRect.bottom;
			onTop = false;
		}
		// display on right
		if (rootWidth/2 > (screenWidth - (anchorRect.left + (anchorWidth / 2))))
		{
			xPos = screenWidth - rootWidth-5;
			arrowLeftMargin = anchorLeft+(anchorWidth/2)-xPos - (arrowWidth / 2);
		}
		
		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowLeftMargin);
		WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
		if(_shouldAdjustWithSoftKeyboard)
		{
			params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		}
		else
		{
			params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
		}
		
		params.x = xPos;
		params.y = yPos;
		params.gravity = Gravity.TOP + Gravity.LEFT;
		mDialog.getWindow().setAttributes(params);
		mDialog.show();
		_isShowing = true;
	}

	private void showArrow(int whichArrow, int requestedX)
	{
		final View showArrow, hideArrow;
		
		if(whichArrow == R.id.arrow_up)
		{
			showArrow = mArrowUp; 
			hideArrow =  mArrowDown;
		}
		else
		{
			showArrow = mArrowDown; 
			hideArrow =  mArrowUp;
		}
		showArrow.setVisibility(View.VISIBLE);
		(showArrow.getLayoutParams()).width = arrowWidth;
		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();
		param.leftMargin = requestedX;
		hideArrow.setVisibility(View.INVISIBLE);
	}

	
	public void setOnDismissListener(USKPopoverDialog.OnDismissListener listener)
	{
		_popupWindows.setOnDismissListener(this);
		mDismissListener = listener;
	}
	
	/**
	 * by default it is true
	 * @param condition
	 */
	public void setShouldAdjustWithSoftKeyboard(boolean condition)
	{
		_shouldAdjustWithSoftKeyboard = condition;
	}

	@Override
	public void onDismiss(DialogInterface dialog)
	{
		_isShowing = false;
		if( mDismissListener != null)
		{
			mDismissListener.onDismiss();
		}
	}
	public interface OnDismissListener
	{
		public abstract void onDismiss();
	}
	class PopupWindows
	{
		protected Context mContext;
		protected Dialog mWindow;
		protected View mRootView;
//		protected Drawable mBackground = null;
		protected WindowManager mWindowManager;

		PopupWindows(Context context)
		{
			mContext = context;
			mWindow = new Dialog(context, R.style.MyDialogTheme);
//			android.view.WindowManager.LayoutParams windowParams = mWindow.getWindow().getAttributes();
//			mWindow.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			
			
			
			DialogInterface.OnDismissListener listener = new DialogInterface.OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					_isShowing = false;
					if(mDismissListener != null)
					{
						mDismissListener.onDismiss();
					}
				}
			};
			mWindow.setOnDismissListener(listener);
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}

		protected void onDismiss()
		{
			_isShowing = false;
		}

//		protected void onShow()
//		{
//		}

		protected void preShow()
		{
			if (mRootView == null)
				throw new IllegalStateException("setContentView was not called with a view to display.");
//			onShow();
			if(_shouldAdjustWithSoftKeyboard)
			{
				mWindow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			}
			else
			{
				mWindow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			}
			
			mWindow.setContentView(mRootView);
		}

//		protected void setBackgroundDrawable(Drawable background)
//		{
//			mBackground = background;
//		}

		protected void setContentView(View root)
		{
			mRootView = root;
			mWindow.setContentView(root);
		}

		protected void setContentView(int layoutResID)
		{
			LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			setContentView(inflator.inflate(layoutResID, null));
		}

		protected void setOnDismissListener(DialogInterface.OnDismissListener listener)
		{
			mWindow.setOnDismissListener(listener);
		}

		protected void dismiss()
		{
			_isShowing = false;
			mWindow.dismiss();
		}
	}

	public View findViewById(int id)
	{
		return _container.findViewById(id);
	}

	public void dismiss()
	{
		_isShowing = false;
		mDialog.dismiss();
	}
	
	public Dialog getDialog()
	{
		return mDialog;
	}
	
	public RelativeLayout getBodyContainer()
	{
		return body;
	}
	

	public ImageView getArrowDown()
	{
		return mArrowDown;
	}
	
	public ImageView getArrowUp()
	{
		return mArrowUp;
	}
	
	public boolean isShowing()
	{
		return _isShowing;
	}
}
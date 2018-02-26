package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ab.util.AbDialogUtil;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.avast.android.dialogs.iface.ISimpleDialogListener;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.HashMap;
import java.util.List;

/**
 * 原生模板广告基本接入示例，演示了基本的原生模板广告功能，包括广告尺寸的ADSize.FULL_WIDTH，ADSize.AUTO_HEIGHT功能
 *
 * Created by noughtchen on 2017/4/17.
 */

public class NativeExpressActivity extends FragmentActivity implements View.OnClickListener,
        NativeExpressAD.NativeExpressADListener, CompoundButton.OnCheckedChangeListener, ISimpleDialogListener {

  private static final String TAG = "NativeExpressActivity";
  private ViewGroup container;
  private NativeExpressAD nativeExpressAD;
  private NativeExpressADView nativeExpressADView;
  private Button buttonRefresh, buttonResize;
  private EditText editTextWidth, editTextHeight; // 编辑框输入的宽高
  private int adWidth, adHeight; // 广告宽高
  private CheckBox checkBoxFullWidth, checkBoxAutoHeight;
  private boolean isAdFullWidth, isAdAutoHeight; // 是否采用了ADSize.FULL_WIDTH，ADSize.AUTO_HEIGHT
  private Button buttonShow;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_native_express);
    container = (ViewGroup) findViewById(R.id.container);
    editTextWidth = (EditText) findViewById(R.id.editWidth);
    editTextHeight = (EditText) findViewById(R.id.editHeight);
    buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
    buttonResize = (Button) findViewById(R.id.buttonDestroy);
    buttonShow = (Button) findViewById(R.id.buttonShow);
    buttonRefresh.setOnClickListener(this);
    buttonResize.setOnClickListener(this);
    buttonShow.setOnClickListener(this);
    checkBoxFullWidth = (CheckBox) findViewById(R.id.checkboxFullWidth);
    checkBoxAutoHeight =  (CheckBox) findViewById(R.id.checkboxAutoHeight);
    checkBoxFullWidth.setOnCheckedChangeListener(this);
    checkBoxAutoHeight.setOnCheckedChangeListener(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // 使用完了每一个NativeExpressADView之后都要释放掉资源
    if (nativeExpressADView != null) {
      nativeExpressADView.destroy();
    }
  }

  private static final int REQUEST_DONE_DLG = 11;
  private static final int REQUEST_EXIT_DLG = 12;
  private static final int REQUEST_PAUSE_DLG = 13;

  int i = 0;

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.buttonRefresh:
        refreshAd();
        break;
      case R.id.buttonDestroy:
        resizeAd();
        break;
      case R.id.buttonShow:
        i++;
        switch(i%3) {
          case 0:
            AdDialogFragment.createBuilder(this, getSupportFragmentManager())
                    .setDefautIconRes(R.drawable.zan)
                    .setAdView(nativeExpressADView)
                    .setTitle("亲，恭喜您已经完成本课程")
                    .setPositiveButtonText("继续练习")
                    .setNegativeButtonText("退出")
                    .setRequestCode(REQUEST_DONE_DLG)
                    .show();
                break;
          case 1:
            AdDialogFragment.createBuilder(this, getSupportFragmentManager())
                    .setDefautIconRes(R.drawable.zan)
                    .setAdView(nativeExpressADView)
                    .setTitle("亲，您真要退出么？")
                    .setPositiveButtonText("取消")
                    .setNegativeButtonText("退出")
                    .setRequestCode(REQUEST_EXIT_DLG)
                    .show();
            break;
          case 2:
            AdDialogFragment.createBuilder(this, getSupportFragmentManager())
                    .setDefautIconRes(R.drawable.bbd)
                    .setAdView(nativeExpressADView)
                    .setTitle("亲，休息一下吧？")
                    .setPositiveButtonText("继续练习")
                    .setNegativeButtonText("轻松一下")
                    .setRequestCode(REQUEST_PAUSE_DLG)
                    .show();
            break;
        }


        break;

    }
  }


  @Override
  public void onNegativeButtonClicked(int requestCode) {
    switch (requestCode) {
      case REQUEST_DONE_DLG:
        Toast.makeText(this, "REQUEST_DONE_DLG", Toast.LENGTH_SHORT).show();
        break;
      case REQUEST_EXIT_DLG:
        Toast.makeText(this, " REQUEST_EXIT_DLG", Toast.LENGTH_SHORT).show();
        break;
      case REQUEST_PAUSE_DLG:
        Toast.makeText(this, "REQUEST_PAUSE_DLG", Toast.LENGTH_SHORT).show();
        break;
    }

  }

  @Override
  public void onNeutralButtonClicked(int requestCode) {

  }

  @Override
  public void onPositiveButtonClicked(int requestCode) {

  }


  private void refreshAd() {
    try {
      if (checkEditTextEmpty()) {
        return;
      }

      adWidth = Integer.valueOf(editTextWidth.getText().toString());
      adHeight = Integer.valueOf(editTextHeight.getText().toString());
      hideSoftInput();
      /**
       *  如果选择支持视频的模版样式，请使用{@link Constants#NativeExpressSupportVideoPosID}
       */
      nativeExpressAD = new NativeExpressAD(this, getMyADSize(), Constants.APPID, Constants.NativeExpressPosID, this); // 这里的Context必须为Activity
      nativeExpressAD.setVideoOption(new VideoOption.Builder()
              .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // 设置什么网络环境下可以自动播放视频
              .setAutoPlayMuted(true) // 设置自动播放视频时，是否静音
              .build()); // setVideoOption是可选的，开发者可根据需要选择是否配置
      nativeExpressAD.loadAD(1);
    } catch (NumberFormatException e) {
      Log.w(TAG, "ad size invalid.");
      Toast.makeText(this, "请输入合法的宽高数值", Toast.LENGTH_SHORT).show();
    }
  }

  private ADSize getMyADSize() {
    int w = isAdFullWidth ? ADSize.FULL_WIDTH : adWidth;
    int h = isAdAutoHeight ? ADSize.AUTO_HEIGHT : adHeight;
    return new ADSize(w, h);
  }

  /**
   *
   * 如何设置广告的尺寸：
   *
   * 方法一：
   * 在接入、调试模板广告的过程中，可以利用NativeExpressADView.setAdSize这个方法调整广告View的大小，找到适合自己的广告位尺寸。
   * 发布时，把这个ADSize固定下来，并在构造NativeExpressAD的时候传入，给一个固定的广告位ID去使用。
   *
   * 方法二：
   * 根据App需要，可以选择一个固定的宽度（宽度也可以设置为ADSize.FULL_WIDTH让广告宽度铺满父控件，但是不能过小，否则将展示不完整），
   * 然后把广告的高度设置为ADSize.AUTO_HEIGHT，让广告的高度根据宽度去自适应。（我们建议开发者选择这种方法，但是目前AUTO_HEIGHT还不支持双图双文模板）
   *
   * 注意：setAdSize是NativeExpressADView的方法，而不是NativeExpressAD的方法，
   * 调用setAdSize只会对当前的NativeExpressADView尺寸进行改变，而不会影响NativeExpressADView加载出来的其他广告尺寸。
   */
  private void resizeAd() {
    if (nativeExpressADView == null) {
      return;
    }

    try {
      if (checkEditTextEmpty()) {
        return;
      }
      if (checkEditTextChanged()) {
        adWidth = Integer.valueOf(editTextWidth.getText().toString());
        adHeight = Integer.valueOf(editTextHeight.getText().toString());
        nativeExpressADView.setAdSize(getMyADSize());
        hideSoftInput();
      }
    } catch (NumberFormatException e) {
      Log.w(TAG, "ad size invalid.");
      Toast.makeText(this, "请输入合法的宽高数值", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onNoAD(AdError adError) {
    Log.i(
        TAG,
        String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
            adError.getErrorMsg()));
  }

  @Override
  public void onADLoaded(List<NativeExpressADView> adList) {
    Log.i(TAG, "onADLoaded: " + adList.size());
    // 释放前一个展示的NativeExpressADView的资源
    if (nativeExpressADView != null) {
      nativeExpressADView.destroy();
    }
//
//    if (container.getVisibility() != View.VISIBLE) {
//      container.setVisibility(View.VISIBLE);
//    }
//
//    if (container.getChildCount() > 0) {
//      container.removeAllViews();
//    }

    nativeExpressADView = adList.get(0);
    // 广告可见才会产生曝光，否则将无法产生收益。
    //container.addView(nativeExpressADView);
    //nativeExpressADView.render();
  }

  @Override
  public void onRenderFail(NativeExpressADView adView) {
    Log.i(TAG, "onRenderFail");
  }

  @Override
  public void onRenderSuccess(NativeExpressADView adView) {
    Log.i(TAG, "onRenderSuccess");
  }

  @Override
  public void onADExposure(NativeExpressADView adView) {
    Log.i(TAG, "onADExposure");
  }

  @Override
  public void onADClicked(NativeExpressADView adView) {
    Log.i(TAG, "onADClicked");
  }

  @Override
  public void onADClosed(NativeExpressADView adView) {
    Log.i(TAG, "onADClosed");
    // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，释放资源，不可以再用来展示。
    if (container != null && container.getChildCount() > 0) {
      container.removeAllViews();
      container.setVisibility(View.GONE);
    }
  }

  @Override
  public void onADLeftApplication(NativeExpressADView adView) {
    Log.i(TAG, "onADLeftApplication");
  }

  @Override
  public void onADOpenOverlay(NativeExpressADView adView) {
    Log.i(TAG, "onADOpenOverlay");
  }

  @Override
  public void onADCloseOverlay(NativeExpressADView adView) {
    Log.i(TAG, "onADCloseOverlay");
  }

  private boolean checkEditTextEmpty() {
    String width = editTextWidth.getText().toString();
    String height = editTextHeight.getText().toString();
    if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
      Toast.makeText(this, "请先输入广告位的宽、高！", Toast.LENGTH_SHORT).show();
      return true;
    }

    return false;
  }

  private boolean checkEditTextChanged() {
    return Integer.valueOf(editTextWidth.getText().toString()) != adWidth
        || Integer.valueOf(editTextHeight.getText().toString()) != adHeight;
  }

  // 隐藏软键盘，这只是个简单的隐藏软键盘示例实现，与广告sdk功能无关
  private void hideSoftInput() {
    if (NativeExpressActivity.this.getCurrentFocus() == null
        || NativeExpressActivity.this.getCurrentFocus().getWindowToken() == null) {
      return;
    }

    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
        NativeExpressActivity.this.getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (buttonView.getId() == R.id.checkboxFullWidth) {
      if (isChecked) {
        isAdFullWidth = true;
        editTextWidth.setText("-1");
        editTextWidth.setEnabled(false);
      } else {
        isAdFullWidth = false;
        editTextWidth.setText("340");
        editTextWidth.setEnabled(true);
      }
    }

    if (buttonView.getId() == R.id.checkboxAutoHeight) {
      if (isChecked) {
        isAdAutoHeight = true;
        editTextHeight.setText("-2");
        editTextHeight.setEnabled(false);
      } else {
        isAdAutoHeight = false;
        editTextHeight.setText("320");
        editTextHeight.setEnabled(true);
      }
    }
  }

  /**
   * 注意：带有视频的广告被点击后会进入全屏播放视频，此时视频可以跟随屏幕方向的旋转而旋转，
   * 请开发者注意处理好自己的Activity的运行时变更，不要让Activity销毁。
   * 例如，在AndroidManifest文件中给Activity添加属性android:configChanges="keyboard|keyboardHidden|orientation|screenSize"，
   */
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }


}

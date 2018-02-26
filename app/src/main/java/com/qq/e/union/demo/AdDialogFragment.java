/*
 * Copyright 2013 Inmite s.r.o. (www.inmite.eu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qq.e.union.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avast.android.dialogs.core.BaseDialogFragment;
import com.avast.android.dialogs.fragment.SimpleDialogFragment;
import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * Sample implementation of custom dialog by extending {@link SimpleDialogFragment}.
 *
 * @author David Vávra (david@inmite.eu)
 */
public class AdDialogFragment extends SimpleDialogFragment {
    protected final static String ARG_DEF_ICON = "defIconRes";
    static NativeExpressADView sAdView;

    public static AbDialogBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new AbDialogBuilder(context, fragmentManager, AdDialogFragment.class);
    }

    public static class AbDialogBuilder  extends SimpleDialogBuilder{

        private int mDefIconRes;

        protected AbDialogBuilder(Context context, FragmentManager fragmentManager, Class<? extends SimpleDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        public AbDialogBuilder setDefautIconRes(int defIconRes) {
            this.mDefIconRes = defIconRes;
            return this;
        }

        //静态需要
        public AbDialogBuilder setAdView(NativeExpressADView adView) {
            sAdView = adView;
            return this;
        }

        @Override
        protected Bundle prepareArguments() {
            Bundle args =  super.prepareArguments();
            args.putInt(AdDialogFragment.ARG_DEF_ICON, mDefIconRes);
            return  args;
        }
    }

    protected int getDefIconRes() {
        return getArguments().getInt(ARG_DEF_ICON, R.drawable.ask);
    }


    @Override
    public BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {
        super.build(builder);
        int resId = getDefIconRes();

        //    builder.setMessage("亲，您确定要退出么？");
        ViewGroup root = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.custom_view_dialog, null);
        // 广告可见才会产生曝光，否则将无法产生收益。
        ViewGroup container = root.findViewById(R.id.flContent);
        if (sAdView != null) {
            try {
                container.addView(sAdView);
                sAdView.render();
            } catch (Exception e) {
                container.addView(createDefaultView(resId));
            }

        } else {
            container.addView(createDefaultView(resId));
        }

        builder.setView(root);

        return builder;
    }

    ImageView createDefaultView(int resId){
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(resId);
        return  imageView;
    }
}

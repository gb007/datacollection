package com.hollysmart.formmodule.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.base.CaiBaseActivity;
import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.common.Constants;
import com.lwkandroid.imagepicker.widget.photoview.PhotoView;

import java.io.File;
import java.util.List;

public class BigPicActivity extends CaiBaseActivity {

    private RelativeLayout rl_title;
    private TextView tv_page;
    private Context context;
    private boolean isLoction;
    private List<PicBean> infos;

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public int layoutResID() {
        return R.layout.form_module_activity_big_pic;
    }

    @Override
    public void findView() {
        rl_title = findViewById(R.id.rl_title);
        tv_page = findViewById(R.id.tv_page);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void init() {
        context = this;
        isLoction = getIntent().getBooleanExtra("isLoction", false);
        infos = (List<PicBean>) getIntent().getSerializableExtra("pics");
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).getIsAddFlag() == 1) {
                infos.remove(i);
            }
        }
        int index = getIntent().getIntExtra("index", 0);
        tv_page.setText("(" + (index + 1) + "/" + infos.size() + ")");

        ViewPager vp_pic = findViewById(R.id.vp_pic_detail);
        vp_pic.setOnPageChangeListener(new MyOnPageChangeListener());

        ImageAdapter adapter = new ImageAdapter();
        vp_pic.setAdapter(adapter);
        vp_pic.setCurrentItem(index);

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int arg0) {
            tv_page.setText("(" + (arg0 + 1) + "/" + infos.size() + ")");
        }
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.iv_back) {
            finish();
            overridePendingTransition(R.anim.form_module_activity_yuandian, R.anim.form_module_activity_exit_right);
        } else if (id == R.id.image) {
            if (tag)
                gongTitle();
            else
                visibileTitle();
            tag = !tag;
        }
    }

    private boolean tag = true;

    private void gongTitle() {
        rl_title.setVisibility(View.GONE);
    }

    private void visibileTitle() {
        rl_title.setVisibility(View.VISIBLE);
    }


    private class ImageAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        ImageAdapter() {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return infos.size();
        }

        private Bitmap getBitMap(Bitmap bitmap) {
            WindowManager wm = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            float rw = (float) bitmap.getWidth() / (float) wm.getDefaultDisplay().getWidth();
            float rh = (float) bitmap.getHeight() / (float) wm.getDefaultDisplay().getHeight();
            if (rw > rh) {
                return Bitmap.createScaledBitmap(bitmap, wm.getDefaultDisplay().getWidth(), (int) (bitmap.getHeight() / rw), false);
            } else {
                return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / rh), wm.getDefaultDisplay().getHeight(), false);
            }
        }


        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.form_module_item_pager_image, view, false);
            assert imageLayout != null;
//            final GestureImageView imageView = imageLayout.findViewById(R.id.image);

            final PhotoView photoView = imageLayout.findViewById(R.id.image);

            PicBean picInfo = infos.get(position);
            if (picInfo.getFilePath() != null) {
                File file = new File(picInfo.getFilePath());
                if (file.exists()) {
                    Glide.with(BigPicActivity.this)
                            .load(new File(picInfo.getFilePath()))
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    photoView.setImageDrawable(resource);
                                }
                            });

                } else {
                    Glide.with(BigPicActivity.this)
                            .load(picInfo.isJsx() ? picInfo.getImageUrl(): Constants.PIC_IP + picInfo.getImageUrl())
                            .placeholder(R.drawable.form_module_pic_default_place_holder)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    photoView.setImageDrawable(resource);
                                }
                            });
                }
            } else {
                if (picInfo.getImageUrl() != null) {
                    Glide.with(BigPicActivity.this)
                            .load(picInfo.isJsx() ? picInfo.getImageUrl():Constants.PIC_IP + picInfo.getImageUrl())
                            .placeholder(R.drawable.form_module_pic_default_place_holder)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    photoView.setImageDrawable(resource);
                                }
                            });
                } else {
                    photoView.setImageResource(R.drawable.form_module_pic_default_place_holder);
                }
            }
            photoView.setOnClickListener(BigPicActivity.this);
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}

package com.dylan.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * 列表弹窗类，可显示横向，竖向形式的列表弹窗
 *
 * Created by Lizhou
 *
 * On 2022/09/27
 *
 */
public class BottomDialog {
    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
    public static final int VERTICAL = OrientationHelper.VERTICAL;

    public static final int LINEAR = 0;
    public static final int GRID = 1;

    private CustomDialog customDialog;

    public static String dialogid;

    public static String dialogshowid;


    public BottomDialog(Context context, int orientation, String showid) {
        dialogshowid = showid;
        customDialog = new CustomDialog(context, orientation);
        customDialog.orientation(orientation);
    }

    public BottomDialog title(String title) {
        customDialog.title(title);
        return this;
    }

    //设置标题
    public BottomDialog title(int title) {
        customDialog.title(title);
        return this;
    }

    //设置背景色或背景图片，参数为资源id
    public BottomDialog background(int res) {
        customDialog.background(res);
        return this;
    }

    //设置弹窗列表content以及对应相应操作，列表格式为menu
    public BottomDialog inflateMenu(int menu, OnItemClickListener onItemClickListener) {
        customDialog.inflateMenu(menu, onItemClickListener);
        return this;
    }

    //设置弹窗列表content以及对应相应操作，列表格式为 List
    public BottomDialog inflateList(List<RawItem> rawItems, OnItemClickListener onItemClickListener) {
        customDialog.inflateMenu(rawItems, onItemClickListener);
        return this;
    }

    //自定义列表容器内部布局，参数为布局id
    public BottomDialog layout(int layout) {
        customDialog.layout(layout);
        return this;
    }

    public BottomDialog addItems(List<Item> items, OnItemClickListener onItemClickListener) {
        customDialog.addItems(items, onItemClickListener);
        return this;
    }

    //移除列表容器内所有的view，目的是解决多次插入view后叠加的bug
    public BottomDialog removeView(){
        customDialog.removeView();
        return this;
    }

    /**
     * @deprecated
     */
    public BottomDialog itemClick(OnItemClickListener listener) {
        customDialog.setItemClick(listener);
        return this;
    }


    //显示弹窗，参数为弹窗id，解决横竖屏弹窗显示，可自定义，但需自己维护
    public void show(String id) {
        dialogid = id;
        customDialog.dialogid = id;
        customDialog.show();
    }

    //关闭弹窗
    public void dismiss(){
        dialogid = "dialogdismiss";
        customDialog.dialogid = "dialogdismiss";
        CustomDialog.getGetcheckedItem = null;
        customDialog.dismiss();
    }

    //弹窗实体类
    private static final class CustomDialog extends Dialog {
        private LinearLayout background;
        private LinearLayout container;
        private TextView titleView;
        private Button AlwaysView;
        private Button OnlyOnceView;

        private DialogAdapter adapter;

        private int padding;
        private int topPadding;
        private int leftPadding;
        private int topIcon;
        private int leftIcon;

        private int orientation;
        private int layout;
        private int maddingwidith;

        public String dialogid;

        public static Item getGetcheckedItem;

        private OnItemClickListener listener;

        private LinearLayout bottombutton;


        CustomDialog(Context context,int orientation) {
            super(context, R.style.BottomDialog);
            init(orientation);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            if (isOutOfBounds(getContext(), event)) {//判断是否点击弹窗外部
                cancel();
                dialogid = "dialogdismiss";
                BottomDialog.dialogid = "dialogdismiss";
                return true;
            }
            return super.onTouchEvent(event);
        }

        private boolean isOutOfBounds(Context context, MotionEvent event) {
            final int x = (int) event.getX();//相对弹窗左上角的x坐标
            final int y = (int) event.getY();//相对弹窗左上角的y坐标
            final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();//最小识别距离
            final View decorView = getWindow().getDecorView();//弹窗的根View
            return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
                    || (y > (decorView.getHeight() + slop));
        }


        private void init(int orientation) {
            int configuration = getContext().getResources().getConfiguration().orientation; //判断横竖屏状态

            padding = getContext().getResources().getDimensionPixelSize(R.dimen.app_normal_margin);
            topPadding = getContext().getResources().getDimensionPixelSize(R.dimen.app_tiny_margin);
            leftPadding = getContext().getResources().getDimensionPixelSize(R.dimen.app_normal_margin);
            topIcon = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_dialog_top_icon);
            leftIcon = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_dialog_left_icon);

            setContentView(R.layout.bottom_dialog);
            setCancelable(true);
            setCanceledOnTouchOutside(true);
            int widthlike = getContext().getResources().getDisplayMetrics().widthPixels;
            int heightlike = getContext().getResources().getDisplayMetrics().heightPixels;
            if(orientation == OrientationHelper.VERTICAL && !dialogshowid.equals("网格")){
                getWindow().setGravity(Gravity.CENTER_VERTICAL);

                double windowpercent = configuration==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?0.7:1.0;
                getWindow().setLayout((int) (widthlike*0.86), (int)((heightlike)*(windowpercent)));
                getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_round);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE);
            }else{
                getWindow().setGravity(Gravity.BOTTOM);
                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_round);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE);
            }




            background = (LinearLayout) findViewById(R.id.background);
            titleView = (TextView) findViewById(R.id.title);
            if(configuration==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                titleView.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            }else{
                titleView.setHeight(40);
            }
            container = (LinearLayout) findViewById(R.id.container);
            AlwaysView = (Button) findViewById(R.id.always);
            OnlyOnceView = (Button) findViewById(R.id.onlyonce);
            bottombutton = (LinearLayout)findViewById(R.id.bottombutton);

            if(orientation==0){
                bottombutton.removeView(AlwaysView);
                bottombutton.removeView(OnlyOnceView);
                background.removeView(bottombutton);
            }


        }

        void removeView(){
            container.removeAllViews();
        }


        void addItems(List<Item> items, OnItemClickListener onItemClickListener) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RecyclerView.LayoutManager manager;


            adapter = new DialogAdapter(items, layout, orientation);
            adapter.setItemClick(onItemClickListener);

            if (layout == LINEAR)
                manager = new LinearLayoutManager(getContext(), orientation, false);
            else if (layout == GRID || dialogshowid.equals("网格"))
                manager = new GridLayoutManager(getContext(), 5, orientation, false);
            else manager = new LinearLayoutManager(getContext(), orientation, false);

            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setLayoutParams(params);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);

            DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
            divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.divider)));
            recyclerView.addItemDecoration(divider);

            int widthlike = getContext().getResources().getDisplayMetrics().widthPixels;
            maddingwidith = (int)(widthlike*0.05);

            container.setPadding(maddingwidith,0,maddingwidith,0);
            container.addView(recyclerView);


        }

        public void title(int title) {
            title(getContext().getString(title));
        }

        public void title(String title) {

            titleView.setText(title);
            titleView.setVisibility(View.VISIBLE);
        }

        public void layout(int layout) {
            this.layout = layout;
            if (adapter != null) adapter.setLayout(layout);
        }

        public void orientation(int orientation) {
            this.orientation = orientation;
            if (adapter != null) adapter.setOrientation(orientation);
        }

        public void background(int res) {
            background.setBackgroundResource(res);
        }

        @SuppressLint("RestrictedApi")
        void inflateMenu(int menu, OnItemClickListener onItemClickListener) {
            listener = onItemClickListener;
            @SuppressLint("RestrictedApi") MenuInflater menuInflater = new SupportMenuInflater(getContext());
            @SuppressLint("RestrictedApi") MenuBuilder menuBuilder = new MenuBuilder(getContext());
            menuInflater.inflate(menu, menuBuilder);
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < menuBuilder.size(); i++) {
                MenuItem menuItem = menuBuilder.getItem(i);
                items.add(new Item(menuItem.getItemId(), menuItem.getTitle().toString(), menuItem.getIcon()));
            }


            addItems(items, onItemClickListener);
        }

        @SuppressLint("RestrictedApi")
        void inflateMenu(List<RawItem> rawItem, OnItemClickListener onItemClickListener) {
            listener = onItemClickListener;
            List<Item> items = new ArrayList<>();
            for (int i = 0; i < rawItem.size(); i++) {
                RawItem menuItem = rawItem.get(i);
                items.add(new Item(menuItem.getId(), menuItem.getTitle(), menuItem.getIcon()));
            }

            addItems(items, onItemClickListener);
        }

        void setItemClick(OnItemClickListener onItemClickListener) {
            adapter.setItemClick(onItemClickListener);
        }



        /**
         * recycler view adapter, provide HORIZONTAL and VERTICAL item style
         */
        private class DialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

            private List<Item> mItems = Collections.emptyList();
            private OnItemClickListener itemClickListener;
            private int is_check = 0;

            private int orientation;
            private int layout;

            public Item checkeditem;

            DialogAdapter(List<Item> mItems, int layout, int orientation) {
                setList(mItems);
                this.layout = layout;
                this.orientation = orientation;
            }

            public int getStatusBarHeight(Context context) {
                Class<?> c = null;
                Object obj = null;
                Field field = null;
                int x = 0;
                int top = 0;
                try {
                    c = Class.forName("com.android.internal.R$dimen");
                    obj = c.newInstance();
                    field = c.getField("status_bar_height");
                    x = Integer.parseInt(field.get(obj).toString());
                    top = context.getResources().getDimensionPixelSize(x);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return top;
            }

            private void setList(List<Item> items) {
                mItems = items == null ? new ArrayList<Item>() : items;
            }

            void setItemClick(OnItemClickListener onItemClickListener) {
                this.itemClickListener = onItemClickListener;
            }

            public void setOrientation(int orientation) {
                this.orientation = orientation;
                notifyDataSetChanged();
            }

            public void setLayout(int layout) {
                this.layout = layout;
                notifyDataSetChanged();
            }


            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (layout == GRID)
                    return new TopHolder(new LinearLayout(parent.getContext()));
                else if (orientation == HORIZONTAL)
                    return new TopHolder(new LinearLayout(parent.getContext()));
                else return new LeftHolder(new LinearLayout(parent.getContext()));
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                final Item item = mItems.get(position);

                TopHolder topHolder;
                LeftHolder leftHolder;

                if (layout == GRID) {

                    topHolder = (TopHolder) holder;

                    topHolder.item.setText(item.getTitle());
                    topHolder.item.setCompoundDrawablesWithIntrinsicBounds(null, topHolder.icon(item.getIcon()), null, null);
                    topHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemClickListener != null) itemClickListener.click(item);
                        }
                    });
                } else if (orientation == HORIZONTAL) {
                    topHolder = (TopHolder) holder;

                    topHolder.item.setText(item.getTitle());
                    topHolder.item.setCompoundDrawablesWithIntrinsicBounds(null, topHolder.icon(item.getIcon()), null, null);
                    topHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemClickListener != null) itemClickListener.click(item);
                        }
                    });
                } else {
                    int configuration = getContext().getResources().getConfiguration().orientation;
                    double showsize = configuration==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?6.0:5.0;
                    if(configuration!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                        container.setPadding(maddingwidith,titleView.getMeasuredHeight(),maddingwidith,0);
                    }
                    int heightlike = getContext().getResources().getDisplayMetrics().heightPixels;
                    int Titlehlike = titleView.getMeasuredHeight();

                    double cancelpercent = configuration==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?0.255:0.1;


                    int cacellike = (int)((heightlike*0.7)*(cancelpercent));
                    int listheight = (int)(heightlike*0.7) - Titlehlike - cacellike;
                    int eachheight = configuration==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?(int)((listheight)/(showsize)):140;
                    leftHolder = (LeftHolder) holder;

                    leftHolder.item.setText(item.getTitle());
                    leftHolder.item.setHeight(eachheight);
                    if(!item.is_checked){
                        leftHolder.item.setCompoundDrawablesWithIntrinsicBounds(leftHolder.icon(item.getIcon()), null, null, null);
                    }else{
                        leftHolder.item.setCompoundDrawablesWithIntrinsicBounds(leftHolder.icon(item.getIcon()), null, ContextCompat.getDrawable(getContext(), R.drawable.check), null);
                        CustomDialog.getGetcheckedItem = item;
                        try{
                            OnlyOnceView.setTextColor(R.color.sky_blue);
                        AlwaysView.setTextColor(R.color.sky_blue);
                        AlwaysView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogid = "dialogdismiss";
                                BottomDialog.dialogid = "dialogdismiss";
                                try{
                                    if(listener!=null) {
                                        listener.click(getGetcheckedItem);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "您还没有选择" ,Toast.LENGTH_SHORT).show();
                                }

//                    dismiss();
                            }
                        });
                        OnlyOnceView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogid = "dialogdismiss";
                                BottomDialog.dialogid = "dialogdismiss";
                                try{
                                    if(listener!=null) listener.click(getGetcheckedItem);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "您还没有选择" ,Toast.LENGTH_SHORT).show();
                                }

//                    dismiss();
                            }
                        });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    leftHolder.item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (itemClickListener != null && layout != 0) itemClickListener.click(item);
                            if(is_check != position){
                                mItems.get(is_check).is_checked = false;
                                notifyItemChanged(is_check);
                                is_check = position;
                                mItems.get(position).is_checked = true;
                                checkeditem = mItems.get(position);
                                notifyItemChanged(position);
                            }else{
                                mItems.get(position).is_checked = true;
                                checkeditem = mItems.get(position);
                                notifyItemChanged(position);
                            }
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                return mItems.size();
            }

            /**
             * horizontal item adapter
             */
            class TopHolder extends RecyclerView.ViewHolder {
                private TextView item;

                TopHolder(View view) {
                    super(view);

                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.width = Utils.getScreenWidth(getContext()) / 5;

                    item = new TextView(view.getContext());
                    item.setLayoutParams(params);
                    item.setMaxLines(1);
                    item.setEllipsize(TextUtils.TruncateAt.END);
                    item.setGravity(Gravity.CENTER);
                    item.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray_font_dark));
                    item.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.font_small));
                    item.setCompoundDrawablePadding(topPadding);
                    item.setPadding(0, padding, 0, padding);

                    TypedValue typedValue = new TypedValue();
                    view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                    item.setBackgroundResource(typedValue.resourceId);

                    ((LinearLayout) view).addView(item);
                }

                public Bitmap drawableToBitmap(Drawable drawable){
                    int width = drawable.getIntrinsicWidth();
                    int height = drawable.getIntrinsicHeight();
                    Bitmap bitmap = Bitmap.createBitmap(width, height,
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0,0,width,height);
                    drawable.draw(canvas);
                    return bitmap;
                }

                private Drawable icon(Drawable drawable) {
                    if (drawable != null) {
                        Bitmap bitmap = drawableToBitmap(drawable);
                        @SuppressWarnings("SuspiciousNameCombination") Drawable resizeIcon = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, topIcon, topIcon, true));
                        Drawable.ConstantState state = resizeIcon.getConstantState();
                        resizeIcon = DrawableCompat.wrap(state == null ? resizeIcon : state.newDrawable().mutate());
                        return resizeIcon;
                    }
                    return null;
                }
            }

            class LeftHolder extends RecyclerView.ViewHolder {
                private TextView item;

                LeftHolder(View view) {
                    super(view);

                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    view.setLayoutParams(params);
                    item = new TextView(view.getContext());
                    item.setLayoutParams(params);
                    item.setMaxLines(1);
                    item.setEllipsize(TextUtils.TruncateAt.END);
                    item.setGravity(Gravity.CENTER_VERTICAL);
                    item.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));
                    item.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.font_normal));
                    item.setCompoundDrawablePadding(leftPadding);
                    item.setPadding(padding, padding, padding, padding);

                    TypedValue typedValue = new TypedValue();
                    view.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                    item.setBackgroundResource(typedValue.resourceId);

                    ((LinearLayout) view).addView(item);
                }

                private Drawable icon(Drawable drawable) {
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        @SuppressWarnings("SuspiciousNameCombination") Drawable resizeIcon = new BitmapDrawable(getContext().getResources(), Bitmap.createScaledBitmap(bitmap, leftIcon, leftIcon, true));
                        Drawable.ConstantState state = resizeIcon.getConstantState();
                        resizeIcon = DrawableCompat.wrap(state == null ? resizeIcon : state.newDrawable().mutate());
                        return resizeIcon;
                    }
                    return null;
                }
            }
        }
    }
}

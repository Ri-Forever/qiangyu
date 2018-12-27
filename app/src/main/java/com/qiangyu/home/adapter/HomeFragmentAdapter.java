package com.qiangyu.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiangyu.R;
import com.qiangyu.app.GoodsInfoActivity;
import com.qiangyu.home.activity.DemandReleaseActivity;
import com.qiangyu.home.activity.ListActivity;
import com.qiangyu.home.activity.MoreRecommendActivity;
import com.qiangyu.home.activity.MoreSellingActivity;
import com.qiangyu.home.bean.Category;
import com.qiangyu.home.bean.GoodsBean;
import com.qiangyu.home.bean.HomeBanner;
import com.qiangyu.home.bean.Hot;
import com.qiangyu.home.bean.JsonBannerBeanData;
import com.qiangyu.home.bean.JsonCategoryBeanData;
import com.qiangyu.home.bean.JsonCommodityBeanData;
import com.qiangyu.home.bean.JsonHotBeanData;
import com.qiangyu.home.bean.JsonNavBeanData;
import com.qiangyu.home.bean.JsonTjBeanData;
import com.qiangyu.home.bean.ResultBeanData;
import com.qiangyu.home.bean.Tj;
import com.qiangyu.utils.Constants;
import com.qiangyu.utils.MD5Util;
import com.qiangyu.utils.SPUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.listener.OnLoadImageListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class HomeFragmentAdapter extends RecyclerView.Adapter {

    //广告条幅类型
    public static final int BANNER = 0;

    //频道
    public static final int CHANNEL = 1;

    //热卖
    public static final int HOT = 2;

    //商品
    public static final int RECOMMEND = 3;
    private static final String GOODS_BEAN = "goodsBean";

    //抢鱼数据
    private JsonBannerBeanData.Result bannerResult;
    private JsonNavBeanData.Result navResult;
    private JsonCategoryBeanData.Result categoryResult;
    private JsonHotBeanData.Result hotResult;
    private JsonTjBeanData.Result tjResult;
    private int currentType = BANNER;//当前类型

    //初始化布局
    private Context context;
    private ResultBeanData.ResultBean resultBean;
    private LayoutInflater mLayoutInflater;
    private JsonCommodityBeanData mJsonCommodityBeanData;
    private JsonCommodityBeanData.Result mResult;
    private String mChanpinId;

    /*public HomeFragmentAdapter(Context context, JsonBannerBeanData.Result bannerResult, JsonNavBeanData.Result navResult, JsonHotBeanData.Result hotResult, JsonTjBeanData.Result tjResult) {
        this.context = context;
        this.bannerResult = bannerResult;
        this.navResult = navResult;
        this.hotResult = hotResult;
        this.tjResult = tjResult;
        mLayoutInflater = LayoutInflater.from(context);
    }*/

    public HomeFragmentAdapter(Context context, JsonBannerBeanData.Result bannerResult, JsonCategoryBeanData.Result categoryResult, JsonHotBeanData.Result hotResult, JsonTjBeanData.Result tjResult) {
        this.context = context;
        this.bannerResult = bannerResult;
        this.categoryResult = categoryResult;
        this.hotResult = hotResult;
        this.tjResult = tjResult;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 相当于getView 创建ViewHolder方法
     * 创建ViewHolder
     *
     * @param viewGroup
     * @param i         当前的类型
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == BANNER) {
            return new BannerViewHolder(context, mLayoutInflater.inflate(R.layout.banner_viewpager, null));
        } else if (i == CHANNEL) {
            return new ChannelViewHolder(context, mLayoutInflater.inflate(R.layout.channel_item, null));
        } else if (i == HOT) {
            return new HotViewHolder(context, mLayoutInflater.inflate(R.layout.hot_item, null));
        } else if (i == RECOMMEND) {
            return new RecommendViewHolder(context, mLayoutInflater.inflate(R.layout.recommend_item, null));
        }
        return null;
    }

    /**
     * 相当于getView方法中的绑定数据模块
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == BANNER) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) viewHolder;
            bannerViewHolder.setData(bannerResult.getHomeBanner());
        } else if (getItemViewType(i) == CHANNEL) {
            ChannelViewHolder channelViewHolder = (ChannelViewHolder) viewHolder;
            channelViewHolder.setData(categoryResult.getCategory());
        } else if (getItemViewType(i) == HOT) {
            HotViewHolder hotViewHolder = (HotViewHolder) viewHolder;
            hotViewHolder.setData(hotResult.getHot());
        } else if (getItemViewType(i) == RECOMMEND) {
            RecommendViewHolder recommendViewHolder = (RecommendViewHolder) viewHolder;
            recommendViewHolder.setData(tjResult.getTj());
        }
    }

    /**
     * 启动商品信息列表页面
     *
     * @param goodsBean
     */
    private void startGoodsInfoActivity(GoodsBean goodsBean) {
        Intent intent = new Intent(context, GoodsInfoActivity.class);
        intent.putExtra(GOODS_BEAN, goodsBean);
        context.startActivity(intent);
    }

    // TODO: 2018/9/12 首页轮播

    /**
     * 首页轮播
     */
    class BannerViewHolder extends RecyclerView.ViewHolder {
        //https://github.com/youth5201314/banner
        private Context mContext;

        private Banner mBanner;

        public BannerViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            this.mContext = context;
            this.mBanner = itemView.findViewById(R.id.homeBanner);
        }

        public void setData(List<HomeBanner> homeBanner) {
            //得到图片集合地址
            List<String> imageUrls = new ArrayList<>();
            for (int i = 0; i < homeBanner.size(); i++) {
                String imageUrl = homeBanner.get(i).getPicture();
                imageUrls.add(imageUrl);
            }


            //轮播时间
            mBanner.setDelayTime(2000);
            //设置循环指示点
            mBanner.setBannerStyle(BannerConfig.NUM_INDICATOR);
            //设置图片轮播效果
            mBanner.setBannerAnimation(Transformer.DepthPage);//Flip Horizontal
            //OnLoadImageListener

            mBanner.setImages(imageUrls, new OnLoadImageListener() {
                @Override
                public void OnLoadImage(final ImageView view, Object url) {
                    //联网请求图片,Glide
                    Glide.with(mContext).load(Constants.QIANGYU_PIC_URL + url).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(view);
                }
            });
            mBanner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void OnBannerClick(int position) {
                }
            });
        }

    }


    /**
     * 商品分类
     */
    private class ChannelViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private GridView gv_channel;
        private ChannelAdapter mChannelAdapter;

        public ChannelViewHolder(final Context context, View itemView) {
            super(itemView);
            this.context = context;
            gv_channel = itemView.findViewById(R.id.gv_channel);
            TextView tv_more_type = itemView.findViewById(R.id.tv_more_type);
            tv_more_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //更多分类
                    Intent intent = new Intent(context, ListActivity.class);
                    context.startActivity(intent);
                }
            });

            //设置item的点击事件
            gv_channel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(context, "点击的是第<" + i + ">个", Toast.LENGTH_SHORT).show();
                    if (i == 0) {
                        Intent intent = new Intent(context, DemandReleaseActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void setData(List<Category> category) {
            mChannelAdapter = new ChannelAdapter(context, category);
            gv_channel.setAdapter(mChannelAdapter);
        }
    }

    /**
     * 热卖商品
     */
    public class HotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context mContext;
        private TextView tv_more_hot;
        private GridView gv_hot;
        private HotGridViewAdapter mHotGridViewAdapter;

        public HotViewHolder(Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            tv_more_hot = itemView.findViewById(R.id.tv_more_hot);
            gv_hot = itemView.findViewById(R.id.gv_hot);

            tv_more_hot.setOnClickListener(this);
        }

        public void setData(final List<Hot> hot) {
            //有数据,设置GridView的适配器
            mHotGridViewAdapter = new HotGridViewAdapter(mContext, hot);
            gv_hot.setAdapter(mHotGridViewAdapter);
            //设置监听
            gv_hot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //热卖商品
                    Hot hot1 = hot.get(i);
                    //Toast.makeText(context, "点击的商品是<" + hot1.getName() + ">", Toast.LENGTH_SHORT).show();
                    mChanpinId = hot1.getId() + "";
                    Intent intent = new Intent(context, GoodsInfoActivity.class);
                    intent.putExtra(GOODS_BEAN, mChanpinId);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MoreSellingActivity.class);
            context.startActivity(intent);
        }


    }

    /**
     * 推荐商品
     */
    private class RecommendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Context mContext;
        private TextView tv_more_recommend;
        private GridView gv_recommend;
        private RecommendGridViewAdapter mRecommendGridViewAdapter;

        public RecommendViewHolder(final Context mContext, View itemView) {
            super(itemView);
            this.mContext = mContext;
            tv_more_recommend = itemView.findViewById(R.id.tv_more_recommend);
            gv_recommend = itemView.findViewById(R.id.gv_recommend);

            tv_more_recommend.setOnClickListener(this);
        }

        public void setData(final List<Tj> tj) {
            //有数据,设置GridView的适配器
            mRecommendGridViewAdapter = new RecommendGridViewAdapter(mContext, tj);
            gv_recommend.setAdapter(mRecommendGridViewAdapter);
            //设置监听
            gv_recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Tj tj1 = tj.get(i);
                    //Toast.makeText(mContext, "点击的商品是<" + tj1.getName() + ">", Toast.LENGTH_SHORT).show();
                    //商品信息类
                    mChanpinId = tj1.getId() + "";
                    Intent intent = new Intent(context, GoodsInfoActivity.class);
                    intent.putExtra(GOODS_BEAN, mChanpinId);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MoreRecommendActivity.class);
            context.startActivity(intent);
        }


    }

    /**
     * 得到类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case BANNER:
                currentType = BANNER;
                break;
            case CHANNEL:
                currentType = CHANNEL;
                break;
            case HOT:
                currentType = HOT;
                break;
            case RECOMMEND:
                currentType = RECOMMEND;
                break;
        }
        return currentType;
    }

    /**
     * 总共有多少个item
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return 4;
    }

    //=================================================商品详情=================================================\\
    void getDataFromNet(String gid, final Hot hot) {
        String city = SPUtils.getInstance().getString("City");
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String randomStr = MD5Util.getRandomStr();
        String timeStamp = MD5Util.getTimeStamp();
        String characterEncoding = "UTF-8";
        parameters.put("shopId", city);
        parameters.put("gid", gid);
        parameters.put("nonceStr", randomStr);
        parameters.put("timeStamp", timeStamp);
        String mySign = MD5Util.createSign(characterEncoding, parameters);

        Log.d("qianming", "randomStr === " + randomStr + "---timeStamp === " + timeStamp + " ---- mySign ===" + mySign + " --- gid --- " + gid);
        OkHttpUtils
                .post()
                .url(Constants.QIANGYU_URL + "GetGoodsDetail")
                .addParams("shopId", city)
                .addParams("gid", gid)
                .addParams("nonceStr", randomStr)
                .addParams("timeStamp", timeStamp)
                .addParams("sign", mySign)
                .build()
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .connTimeOut(10000)
                /**
                 * 请求失败的时候回调
                 */
                .execute(new StringCallback() {

                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.d("getDataFromNet", "请求失败 === >" + e.getMessage());
                        getDataFromNet(String.valueOf(mChanpinId), hot);
                    }

                    /**
                     * 当联网成功时回调
                     * @param response
                     * @param id
                     */
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("getDataFromNet", " 未更改 === >" + response);
                        //Log.d("getDataFromNet", " --- >" + response);
                        //解析数据
                        //response = response.replace("", "");
                        response = response.replace("\\", "");
                        response = response.replace("\"[", "[");
                        response = response.replace("]\"", "]");
                        Log.d("getDataFromNet", " 更改过 === >" + response);
                        processData(response, hot);
                    }
                });

    }

    private void processData(String json, Hot hot) {
        Log.d("banner", "json === " + json);
        mJsonCommodityBeanData = JSON.parseObject(json, JsonCommodityBeanData.class);
        mResult = mJsonCommodityBeanData.getResult();
        GoodsBean goodsBean = new GoodsBean();
        goodsBean.setCover_price(String.valueOf(hot.getPrice()));
        goodsBean.setFigure(hot.getPicture());
        goodsBean.setName(hot.getName());
        goodsBean.setProduct_id(String.valueOf(hot.getId()));
        goodsBean.setRemark(hot.getRemark());
        goodsBean.setResult(mResult);
        Log.d("onItemClick", "onItemClick: " + mResult);
        startGoodsInfoActivity(goodsBean);
    }

}

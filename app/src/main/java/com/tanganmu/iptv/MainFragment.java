package com.tanganmu.iptv;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundFragment;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.RowPresenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;

public class MainFragment extends BrowseSupportFragment implements OnItemViewClickedListener  {

    private RequestQueue queue ;
    private static final int GRID_ITEM_WIDTH = 250;
    private static final int GRID_ITEM_HEIGHT = 250;
    public boolean vpnStart = false ;
    public String vpnstatus = "VPN ON" ;

    //vpn
    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUI();
        setupEventListeners();
    }


    public boolean stopVpn() {
        try {
            vpnThread.stop();
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private  void setUI(){
        //setTitle("IPTV");
        Drawable logo = ContextCompat.getDrawable(getActivity() , R.drawable.logo );
        setBadgeDrawable(logo);
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
//        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
//        backgroundManager.attach(getActivity().getWindow());
//        backgroundManager.setDrawable(getActivity().getDrawable(R.drawable.imagebg2));
//        backgroundManager.release();
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.Full_black));
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));
        loadRows();
        setOnItemViewClickedListener(this);

    }

    public class IconHeaderItemPresenter extends RowHeaderPresenter {

        @Override
        public void onBindViewHolder(Presenter.ViewHolder viewHolder,
                                     Object o) {
            // set text, icons etc
        }
        @Override
        public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
            // free resources
        }
    }


    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadRows();
               // Intent search = new Intent(getActivity() , search_support.class);
               // startActivity(search);
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    private void loadRows()  {
        HeaderItem LiveTv = new HeaderItem(0, "International Live");
        HeaderItem Kids = new HeaderItem(1 , "Kids live");
        HeaderItem Movies = new HeaderItem(2 , "English Movies");
        HeaderItem news = new HeaderItem(3 , "News live");
        HeaderItem Missellaneous = new HeaderItem(4 , "Miscellaneous");
        HeaderItem chinese = new HeaderItem(5 , "chinese channels");
        HeaderItem settings = new HeaderItem(6 , "Settings");

        final ArrayObjectAdapter adapterForRow1 = new ArrayObjectAdapter(new MyPresenter());
        final ArrayObjectAdapter adapterForRow2 = new ArrayObjectAdapter(new MyPresenter());
        ArrayObjectAdapter adapterForRow3 = new ArrayObjectAdapter(new MyPresenter());
        ArrayObjectAdapter adapterForRow4 = new ArrayObjectAdapter(new MyPresenter());
        final ArrayObjectAdapter adapterForRow5 = new ArrayObjectAdapter(new MyPresenter());
        final  ArrayObjectAdapter adapterForRow6 = new ArrayObjectAdapter(new MyPresenter());

        //live tv
        String name[] = {
          "SUN TV",
          "VIJAY TV",
          "K TV",
          "SUN MUSIC",
          "Chutti TV"
        };

        String ImageUrl[] = {
          "https://sund-images.sunnxt.com/9016/1920x1080_6cc7df10-0fc9-486b-b644-9eeb89b905a7.jpg",
          "https://vignette.wikia.nocookie.net/logopedia/images/c/cf/Star_Vijay_HD_2017-0.png/revision/latest?cb=20190715142233",
          "https://sund-images.sunnxt.com/32138/1920x1080_efe209b7-56d3-43c9-a458-6e8fc9eeb605.jpg",
          "https://image.airtel.tv/content/MWTV/LIVETVCHANNEL/MWTV_LIVETVCHANNEL_961/SunMusic-HD-Small-card-864X640.jpg",
          "https://image.airtel.tv/content/MWTV/LIVETVCHANNEL/MWTV_LIVETVCHANNEL_968/chutti-Small-card-864X640.jpg"
        };

        String VideoUrl[] = {
          "http://indtv.online/sunnxt/sunnxt/SunTVHD.m3u8",
          "https://suntvlive.s.llnwi.net/SunTVHD/SunTVHD.isml/SunTVHD-audio_1=64000-video=5000000.m3u8?p=51&se=1596796220&e=1596825920&h=ed54c938435dfe76f173fc4a0891696b",
          "http://indtv.online/sunnxt/sunnxt/KTVHD.m3u8",
          "https://suntvlive.s.llnwi.net/SunMusicHD/SunMusicHD.isml/SunMusicHD-audio_1=64000-video=1200000.m3u8?p=57&se=1596796317&e=1596826017&h=0a38eb548c8b7bd03ff8d51d7894f243",
          "https://suntvlive.s.llnwi.net/ChuttiTV/ChuttiTV.isml/ChuttiTV-audio_1=64000-video=1499968.m3u8?p=53&se=1596816597&e=1596846297&h=9b70ec81536c7efd5b2198faf88d3da0"
        };

        String Signal[] = {
           "Signal - 90%",
           "Signal - 60%",
           "Signal - 90%",
           "Signal - 80%",
           "Signal - 90%"
        };

        queue = Volley.newRequestQueue(getActivity());

        String url1 = "http://136.185.4.32:8081/data/get/livetv";

        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url1 , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject kids =  response.getJSONObject("response");
                            JSONObject obj = new JSONObject(kids.toString());
                            List<String> list = new ArrayList<String>();
                            JSONArray array = obj.getJSONArray("message");
                            // Toast.makeText(getActivity() , array.toString() , Toast.LENGTH_LONG).show();
                            System.out.print(array.toString());
                            for (int l = 0 ; l < array.length() ; l++){
                                    JSONObject Livetv = array.getJSONObject(l);
                                    adapterForRow1.add(new SingleRowView(Livetv.getString("name") , Livetv.getString("imageURL") , Livetv.getString("videoURL") , Livetv.getString("Signal") , "Livetv"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity() , "please wait...." , Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request1);

        //kids
        String KidsName[] = {
                "ChuChu TV",
                "Doremon TV",
                "CBBC HD",
                "CBEEBIES HD",
                "Akaram Kidz",
                "ibckids"
        };

        String ImageUrlKids[] = {
                "https://i.ytimg.com/vi/VXokId-v7oU/maxresdefault.jpg",
                "https://lumiere-a.akamaihd.net/v1/images/open-uri20160812-3094-1ujbj4u_653343d8.jpeg?region=0%2C0%2C480%2C255",
                "https://upload.wikimedia.org/wikipedia/en/thumb/3/37/CBBC_2016_logo.svg/1200px-CBBC_2016_logo.svg.png",
                "https://www.prolificlondon.co.uk/sites/prolificlondon.co.uk/files/styles/lightbox_large/public/images/news/cbeebies-bbc.png?itok=T9mUgx8J",
                "https://is4-ssl.mzstatic.com/image/thumb/Purple118/v4/1e/ee/5d/1eee5dbe-5b26-bbf4-332c-ea76ebb1ff8e/AppIcon-1x_U007emarketing-85-220-5.jpeg/1200x630wa.png",
                "https://i.ytimg.com/vi/jGp2fFfO1K4/maxresdefault.jpg"
        };

        String KidsVideoUrl[] = {
                "https://suntvlive.s.llnwi.net/SunTVHD/SunTVHD.isml/SunTVHD-audio_1=64000-video=5000000.m3u8?p=51&se=1596645434&e=1596675134&h=fe823a9f610e681a08ce713450b53f65",
                "https://suntvlive.s.llnwi.net/SunTVHD/SunTVHD.isml/SunTVHD-audio_1=64000-video=5000000.m3u8?p=51&se=1596645434&e=1596675134&h=fe823a9f610e681a08ce713450b53f65",
                "http://51.52.156.22:8888/http/003",
                "http://iptv1.privateiptv.club:80/random-robbie/random-robbie/824",
                "http://akaram.zecast.net/akaram-live/akaramkidz/index.m3u8",
                "https://ibckids-live.ibctamil.com/transcode/ibckids.m3u8"
        };

        String KidsSignal[] = {
                "Signal - 90%",
                "Signal - 60%",
                "Signal - 90%",
                "Signal - 80%",
                "Signal - 70%",
                "Signal - 90%"
        };

        queue = Volley.newRequestQueue(getActivity());

        String url = "http://136.185.4.32:8081/data/add/kids";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject kids =  response.getJSONObject("response");
                            JSONObject obj = new JSONObject(kids.toString());
                            List<String> list = new ArrayList<String>();
                            JSONArray array = obj.getJSONArray("message");
                           // Toast.makeText(getActivity() , array.toString() , Toast.LENGTH_LONG).show();
                            System.out.print(array.toString());
                            for (int l = 0 ; l < array.length() ; l++){
                                JSONObject kidsdetials = array.getJSONObject(l);
                                adapterForRow2.add(new SingleRowView(kidsdetials.getString("name") , kidsdetials.getString("imageURL") , kidsdetials.getString("videoURL") , kidsdetials.getString("Signal") , "Kids"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity() , "please wait...." , Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             error.printStackTrace();
            }
        });

        queue.add(request);

        //Movies
        String MoviesName[] = {
                "IronMan",
                "the avengers: endgame",
                "guardians of the galaxy 2",
                "Kannum Kannum Kollaiyadithaal",
                "shakuntala devi",
                "danny"
        };

        String MovieImageURL[] = {
          "https://upload.wikimedia.org/wikipedia/en/0/00/Iron_Man_poster.jpg",
          "https://img1.looper.com/img/gallery/the-ending-of-avengers-endgame-explained/intro-1556278985.jpg",
          "https://www.denofgeek.com/wp-content/uploads/2019/04/guardians_of_the_galaxy_2_large.jpg?fit=1688%2C949",
          "https://i.ytimg.com/vi/hPybzXeEWSI/maxresdefault.jpg",
          "https://images.indianexpress.com/2020/07/shakuntala-devi-trailer-759.jpg",
          "https://cdn.123telugu.com/content/wp-content/uploads/2020/08/Danny-m.jpg"
        };

        String MovieVideoUrl[] = {
                "https://suntvlive.s.llnwi.net/SunTVHD/SunTVHD.isml/SunTVHD-audio_1=64000-video=5000000.m3u8?p=51&se=1596645434&e=1596675134&h=fe823a9f610e681a08ce713450b53f65",
                "https://suntvlive.s.llnwi.net/SunTVHD/SunTVHD.isml/SunTVHD-audio_1=64000-video=5000000.m3u8?p=51&se=1596645434&e=1596675134&h=fe823a9f610e681a08ce713450b53f65",
                "https://suntvlive.s.llnwi.net/SunTVHD/SunTVHD.isml/SunTVHD-audio_1=64000-video=5000000.m3u8?p=51&se=1596645434&e=1596675134&h=fe823a9f610e681a08ce713450b53f65",
                "http://vpn.nahsolutions.in/2.mp4",
                "http://vpn.nahsolutions.in/3.mkv",
                "http://cdn61.vidorg.net/hls/h7tod4s4amlbu3tf6rudlo7v34275wa4ahvm5apbcwledlrq7vwzl3mpijca/index-v1-a1.m3u8"
        };

        String MoviesSignal[] = {
                "Signal - 90%",
                "Signal - 60%",
                "Signal - 90%",
                "Signal - 80%",
                "Signal - 90%",
                "Signal - 90%"
        };

        queue = Volley.newRequestQueue(getActivity());

        String url5 = "http://136.185.4.32:8081/data/get/englishmovies";

        JsonObjectRequest request5 = new JsonObjectRequest(Request.Method.GET, url5 , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject kids =  response.getJSONObject("response");
                            JSONObject obj = new JSONObject(kids.toString());
                            List<String> list = new ArrayList<String>();
                            JSONArray array = obj.getJSONArray("message");
                            // Toast.makeText(getActivity() , array.toString() , Toast.LENGTH_LONG).show();
                            System.out.print(array.toString());
                            for (int l = 0 ; l < array.length() ; l++){
                                JSONObject kidsdetials = array.getJSONObject(l);
                                adapterForRow3.add(new SingleRowView(kidsdetials.getString("name") , kidsdetials.getString("imageURL") , kidsdetials.getString("videoURL") , kidsdetials.getString("Signal") , "Movies"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity() , "please wait...." , Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request5);



        //news live

        String newsname[] = {
           "news 7 live"
        };

        String NewsImage[] = {
                "https://upload.wikimedia.org/wikipedia/en/0/00/Iron_Man_poster.jpg"
        };

        String NewsLiveVideo[] = {
           "http://cdn61.vidorg.net/hls/h7tod4s4amlbu3tf6rudlo7v34275wa4ahvm5apbcwledlrq7vwzl3mpijca/index-v1-a1.m3u8"
        };

        String newsSignal[]= {
           "Signal - 30%"
        };

        queue = Volley.newRequestQueue(getActivity());

        String url7 = "http://136.185.4.32:8081/data/get/news";

        JsonObjectRequest request7 = new JsonObjectRequest(Request.Method.GET, url7 , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject kids =  response.getJSONObject("response");
                            JSONObject obj = new JSONObject(kids.toString());
                            List<String> list = new ArrayList<String>();
                            JSONArray array = obj.getJSONArray("message");
                            // Toast.makeText(getActivity() , array.toString() , Toast.LENGTH_LONG).show();
                            System.out.print(array.toString());
                            for (int l = 0 ; l < array.length() ; l++){
                                JSONObject kidsdetials = array.getJSONObject(l);
                                adapterForRow4.add(new SingleRowView(kidsdetials.getString("name") , kidsdetials.getString("imageURL") , kidsdetials.getString("videoURL") , kidsdetials.getString("Signal") , "news"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity() , "please wait...." , Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request7);


        //Missellaneous
        queue = Volley.newRequestQueue(getActivity());

        String url2 = "http://136.185.4.32:8081/data/get/Missellaneous";

        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url2 , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject kids =  response.getJSONObject("response");
                            JSONObject obj = new JSONObject(kids.toString());
                            List<String> list = new ArrayList<String>();
                            JSONArray array = obj.getJSONArray("message");
                            // Toast.makeText(getActivity() , array.toString() , Toast.LENGTH_LONG).show();
                            System.out.print(array.toString());
                            for (int l = 0 ; l < array.length() ; l++){
                                JSONObject kidsdetials = array.getJSONObject(l);
                                adapterForRow5.add(new SingleRowView(kidsdetials.getString("name") , kidsdetials.getString("imageURL") , kidsdetials.getString("videoURL") , kidsdetials.getString("Signal") , "Missellaneous"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity() , "please wait...." , Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request2);

        //chinese

        queue = Volley.newRequestQueue(getActivity());

        String url3 = "http://136.185.4.32:8081/data/get/chinese";

        JsonObjectRequest request3 = new JsonObjectRequest(Request.Method.GET, url3 , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject kids =  response.getJSONObject("response");
                            JSONObject obj = new JSONObject(kids.toString());
                            List<String> list = new ArrayList<String>();
                            JSONArray array = obj.getJSONArray("message");
                            // Toast.makeText(getActivity() , array.toString() , Toast.LENGTH_LONG).show();
                            System.out.print(array.toString());
                            for (int l = 0 ; l < array.length() ; l++){
                                JSONObject kidsdetials = array.getJSONObject(l);
                                adapterForRow6.add(new SingleRowView(kidsdetials.getString("name") , kidsdetials.getString("imageURL") , kidsdetials.getString("videoURL") , kidsdetials.getString("Signal") , "chinese"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity() , "please wait...." , Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request3);



        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);



        ArrayObjectAdapter windowAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        windowAdapter.add(new ListRow(LiveTv , adapterForRow1));
        windowAdapter.add(new ListRow(Kids , adapterForRow2));
        windowAdapter.add(new ListRow(Movies , adapterForRow3));
        windowAdapter.add(new ListRow(news , adapterForRow4));
        windowAdapter.add(new ListRow(Missellaneous , adapterForRow5));
        windowAdapter.add(new ListRow(chinese , adapterForRow6));
        gridRowAdapter.add(getResources().getString(R.string.profile));
        gridRowAdapter.add(getString(R.string.CheckUpdate));
        gridRowAdapter.add(vpnstatus);
        gridRowAdapter.add(getResources().getString(R.string.Logout));
        windowAdapter.add((new ListRow(settings , gridRowAdapter)));
        setAdapter(windowAdapter);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        //Toast.makeText(getActivity() , item.toString() , Toast.LENGTH_LONG).show();
            try{
                if(item instanceof SingleRowView) {
                    SingleRowView movie = (SingleRowView) item;
                    String Movie = movie.toString();
                    JSONObject obj = new JSONObject(Movie);
                    if(obj.getString("category").equals("Movies")){
                        //movies
                        DetailsShareData myObj = new DetailsShareData();
                       // Toast.makeText(getActivity() , movie.getName() , Toast.LENGTH_LONG).show();
                        myObj.setName("we");
                        Intent intent = new Intent(getActivity(),DetailsActivity.class);
                        intent.putExtra("Movie", Movie);
                        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                "hero")
                                .toBundle();
                        getActivity().startActivity(intent, bundle);
                    }else if(obj.getString("category").equals("ViewMore")){
                       // Toast.makeText(getActivity() , "View more" , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity() , ViewmoreActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                        intent.putExtra("Movie", movie.toString());
                        startActivity(intent);
                    }

                }else if (item instanceof String) {
                    if (((String) item) == "VPN ON") {
                        if (!vpnStart) {
                            if (true) {
                                Intent intent = vpnService.prepare(getActivity());
                                if (intent != null) {
                                    startActivityForResult(intent, 1);
                                } else {
                                    startVpn();
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            vpnstatus = "VPN OFF";
                                            // Do something after 5s = 5000ms
                                            loadRows();
                                        }
                                    }, 2000);

                                }
                            }
                        }

                    }else if(((String) item) == "VPN OFF"){
                        new AlertDialog.Builder(getActivity() , R.style.Theme_AppCompat_Dialog_Alert)
                                .setTitle("Alert").setMessage("Would you like to cancel the current VPN Connection?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        stopVpn();
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Do something after 5s = 5000ms
                                                Toast.makeText(getActivity() , "VPN OFF" , Toast.LENGTH_LONG).show();
                                                vpnstatus = "VPN ON";
                                                loadRows();
                                            }
                                        }, 1500);

                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // User chose NO
                                    }
                                }).create().show();

                    }
                }
            }catch (Exception e){
                Intent Error = new Intent(getActivity(), ErrorLayout.class);
                startActivity(Error);
            }

    }

    private class MyPresenter extends Presenter {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
            ImageCardView icv = new ImageCardView(viewGroup.getContext());

            icv.setCardType(BaseCardView.CARD_TYPE_INFO_UNDER_WITH_EXTRA);
            icv.setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ACTIVATED);
            return new ViewHolder(icv);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object object) {
            SingleRowView srv = (SingleRowView) object ;
            ImageCardView icv = ((ImageCardView)viewHolder.view);
            icv.setMainImageDimensions(400 , 210);
            icv.setTitleText(srv.getName());
            icv.setBackgroundColor(Color.BLACK);
           // icv.setBackground(getActivity().getDrawable(R.drawable.logo));
            Glide.with(viewHolder.view.getContext()).load(srv.getImage()).centerCrop().error(R.drawable.logo).into(icv.getMainImageView());
            icv.setContentText(srv.getSignal());

        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }

    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setTextSize((float) 15.00);
            view.setBackgroundColor(
                    ContextCompat.getColor(getActivity(), R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }



    private  void startVpn(){
        try {
            // .ovpn file
            InputStream conf = getActivity().getAssets().open("india.ovpn");
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;

            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }

            br.readLine();
            OpenVpnApi.startVpn(getActivity(), config , "india", "nitish", "nitish123");

            // Update log
            vpnStart = true;

        } catch (IOException | RemoteException e) {

            e.printStackTrace();
        }
    }







}

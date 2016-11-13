package cc.yfree.yangf.everyday;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class BestFragment extends Fragment {
    View view;

    public BestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_best, container, false);
        // Inflate the layout for this fragment
        /*首页cardview图片相关*/
        ImageView today_img = (ImageView)view.findViewById(R.id.best_img1);
        Picasso.with(getActivity())
                .load(R.drawable.todo13)
                .fit()
                .into(today_img);
        ImageView today_img1 = (ImageView)view.findViewById(R.id.best_img2);
        Picasso.with(getActivity())
                .load(R.drawable.todo3)
                .fit()
                .into(today_img1);
        ImageView today_img2 = (ImageView)view.findViewById(R.id.best_img3);
        Picasso.with(getActivity())
                .load(R.drawable.todo1)
                .fit()
                .into(today_img2);

        /*下拉刷新*/
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 6000);
            }
        });
        return view;
    }

}

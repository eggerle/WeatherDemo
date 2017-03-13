package lover.zoe.com.weatherdemo.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import lover.zoe.com.weatherdemo.MainActivity;
import lover.zoe.com.weatherdemo.R;
import lover.zoe.com.weatherdemo.WeatherActivity;
import lover.zoe.com.weatherdemo.entity.City;
import lover.zoe.com.weatherdemo.entity.County;
import lover.zoe.com.weatherdemo.entity.Province;
import lover.zoe.com.weatherdemo.utils.HttpUtils;
import lover.zoe.com.weatherdemo.utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 */
public class ChooiceAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    @BindView(R.id.title_text)
    TextView title;
    @BindView(R.id.back_button)
    Button back_button;
    @BindView(R.id.listView)
    ListView areaListView;

    private ProgressDialog progressDialog;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选择省
     */
    private Province selectProvince;
    /**
     * 选择市
     */
    private City selectCity;

    /**
     * 选择的等级
     */
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chooice_area, container, false);
        ButterKnife.bind(this, view);
        areaListView = (ListView) view.findViewById(R.id.listView);
        back_button = (Button) view.findViewById(R.id.back_button);
        title = (TextView) view.findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        areaListView.setAdapter(adapter);

        return view;
    }

    @OnItemClick(R.id.listView)
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (currentLevel == LEVEL_PROVINCE) {
            selectProvince = provinceList.get(i);
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            selectCity = cityList.get(i);
            queryCounties();
        } else if (currentLevel == LEVEL_COUNTY) {
            if (getActivity() instanceof MainActivity) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                intent.putExtra("weather_id", countyList.get(i).getWeatherId());
                getActivity().startActivity(intent);
                getActivity().finish();
            } else if (getActivity() instanceof WeatherActivity) {
                WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                weatherActivity.drawerLayout.closeDrawers();
                weatherActivity.swipeRefreshLayout.setRefreshing(true);
                weatherActivity.requestWeather(countyList.get(i).getWeatherId());
            }

        }
    }

    @OnClick(R.id.back_button)
    public void onClick() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
    }

    private void queryProvinces() {
        title.setText("中国");
        back_button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            areaListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String url = "http://guolin.tech/api/china/";

            queryFromServer(url, "province");
        }

    }

    private void queryCities() {
        title.setText(selectProvince.getProvinceName());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("proviceId=?", String.valueOf(selectProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            areaListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String url = "http://guolin.tech/api/china/" + provinceCode;

            queryFromServer(url, "city");
        }

    }

    private void queryCounties() {

        title.setText(selectCity.getCityName());
        back_button.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId=?", String.valueOf(selectCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            areaListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String url = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;

            queryFromServer(url, "county");
        }
    }

    private void queryFromServer(String url, final String type) {
        showProgressDialog();
        HttpUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                closeProgressDialog();
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectProvince.getId());
                } else {
                    result = Utility.handleCountyResponse(responseText, selectCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}

package com.mobiquel.urbanclap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.mobiquel.urbanclap.adapter.FeatureAdapter;
import com.mobiquel.urbanclap.adapter.ProductAdapter;
import com.mobiquel.urbanclap.adapter.ServiceAdapter;
import com.mobiquel.urbanclap.adapter.SkillAdapter;
import com.mobiquel.urbanclap.model.FeatureModel;
import com.mobiquel.urbanclap.model.PartnerKYCVO;
import com.mobiquel.urbanclap.model.UserProductMappingVO;
import com.mobiquel.urbanclap.utils.AppConstants;
import com.mobiquel.urbanclap.utils.Preferences;
import com.mobiquel.urbanclap.utils.Utils;
import com.mobiquel.urbanclap.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesAndSkillsSecond extends AppCompatActivity implements OnClickListener {

	private Button submitProducts,submitService,editService,editProducts,submitSkills,skills,product,services;
	private View viewSkills,viewProduct,viewServices;
	private ListView materiaList,serviceSkillList,skillList;
	private SkillAdapter adapterSkill;
	private ServiceAdapter adapterService;
	private TextView text;
	private String skillIds,skillUncheckedIds,serviceids,serviceUncheckedIds;
	private int i=1;
	private LinearLayout servicesLayout,skillsLayout;
	private ProgressBarCircularIndeterminate progressBar;

	private RecyclerView serviceList;
	private FeatureAdapter mAdapter;
	private LinearLayout serviceListLayout;
	private List<FeatureModel> leads = new ArrayList<>();
	private RecyclerView productList;
	private ProductAdapter proAdapter;
	private JSONArray productJsonArray,getproductJsonArray;
	private String getSkillIds,getProductIds;
	private List<UserProductMappingVO> selectedProdcts;
	private TextView actionBarTitleTextView;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_new);
		getSupportActionBar().setTitle("Services");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c0392b")));
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		progressBar = (ProgressBarCircularIndeterminate)findViewById(R.id.progressBar);

		materiaList = (ListView) findViewById(R.id.materialList);
		serviceSkillList = (ListView) findViewById(R.id.serviceSkillList);
		skillList = (ListView) findViewById(R.id.skillList);

		productList = (RecyclerView) findViewById(R.id.productList);
		productList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

		submitProducts = (Button) findViewById(R.id.submitProducts);
		submitService = (Button) findViewById(R.id.submitServices);
		editProducts = (Button) findViewById(R.id.editProducts);
		editService = (Button) findViewById(R.id.editServices);
		submitSkills = (Button) findViewById(R.id.submitSkills);

		text = (TextView) findViewById(R.id.text);

		skills = (Button) findViewById(R.id.skills);
		product = (Button) findViewById(R.id.products);
		services = (Button) findViewById(R.id.services);

		viewSkills = (View) findViewById(R.id.viewSkills);
		viewProduct = (View) findViewById(R.id.viewProduct);
		viewServices = (View) findViewById(R.id.viewServices);

		servicesLayout = (LinearLayout) findViewById(R.id.servicesLayout);
		skillsLayout = (LinearLayout) findViewById(R.id.skillsLayout);
		serviceListLayout = (LinearLayout) findViewById(R.id.serviceListLayout);
		serviceList= (RecyclerView) findViewById(R.id.recycler_view);

		submitProducts.setOnClickListener(this);
		submitService.setOnClickListener(this);
		editService.setOnClickListener(this);
		editProducts.setOnClickListener(this);
		submitSkills.setOnClickListener(this);

		product.setOnClickListener(this);
		services.setOnClickListener(this);
		skills.setOnClickListener(this);


		skillIds="";
		skillUncheckedIds="";

		serviceids="";
		serviceUncheckedIds="";
		

		getUserServiceSkillProductFeatureMapping();

		Preferences.getInstance().loadPreferences(ServicesAndSkillsSecond.this);
		if(Preferences.getInstance().isProfileStatusApproved)
		{
			submitSkills.setVisibility(View.INVISIBLE);
		}
		else
		{
			submitSkills.setVisibility(View.VISIBLE);
		}


	}

	@Override
	public void onBackPressed() 
	{
		finish();
		
		super.onBackPressed();
	}
	
	
	
	private void getData() {
		progressBar.setVisibility(View.VISIBLE);
		RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
		String url="";
		url = AppConstants.SERVER_URL +"getServiceTypeProductList";
		StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONObject responseObject;
				try {
					toa();
					responseObject = new JSONObject(response);
					System.out.println(responseObject.toString());
					Log.e("RESPO_STUD", responseObject.toString());
					if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0")) 
					{

							productJsonArray=new JSONArray();
							productJsonArray=responseObject.getJSONArray("responseObject");
						Log.e("PRO_IDS"," "+getProductIds);

						proAdapter=new ProductAdapter(ServicesAndSkillsSecond.this,productJsonArray,getProductIds);
							productList.setAdapter(proAdapter);

					}
					else 
					{
						//Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) 
			{
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
				Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
			}
		}) 
		{
			@Override
			protected Map<String, String> getParams() {
				Preferences.getInstance().loadPreferences(ServicesAndSkillsSecond.this);
				Map<String, String> params = new HashMap<String, String>();

					params.put("serviceTypeIds", getIntent().getExtras().getString("SERVICE_IDS"));
				//}
				Log.e("PARAMS",params.toString());
				return params;
			}
		};
		requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		if (Utils.isNetworkAvailable(ServicesAndSkillsSecond.this)) {
			queue.add(requestObject);
		} else 
		{
			if (progressBar != null && progressBar.isShown()) {
				progressBar.setVisibility(View.GONE);
			}
			Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
		}
	}
	
	





	private void toa() {
		System.out.println("abc");
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.submitProducts:
			text.setText("Select Services!");

			if(proAdapter.getSelectedProduct()!=null)
			{
				String str[] = proAdapter.getSelectedProduct().split(",");

				Log.e("SELECTED_PRODUCT",proAdapter.getSelectedProduct());
				selectedProdcts=new ArrayList<UserProductMappingVO>();
				UserProductMappingVO[] o = new UserProductMappingVO[str.length];
				if(proAdapter.getSelectedProduct().equals("[]"))
				{
					Utils.showToast(ServicesAndSkillsSecond.this,"Please select any one product!");

				}
				else
				{
					for (int i=0;i<str.length;i++)
					{
						o[i] = new UserProductMappingVO();
						try
						{
							for(int k=0;k<productJsonArray.length();k++)
							{
								if(productJsonArray.getJSONObject(k).getString("productId").equals(String.valueOf(Integer.parseInt(str[i].replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")))))
								{
									o[i].setProductName(productJsonArray.getJSONObject(k).getString("name"));
									o[i].setProductId(productJsonArray.getJSONObject(k).getString("productId"));
									o[i].setIsComplaint(productJsonArray.getJSONObject(k).getString("isComplaint"));
									o[i].setIsInstall(productJsonArray.getJSONObject(k).getString("isInstall"));
									o[i].setIsService(productJsonArray.getJSONObject(k).getString("isService"));
								}
							}
							selectedProdcts.add(o[i]);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					mAdapter = new FeatureAdapter(selectedProdcts,ServicesAndSkillsSecond.this);
					RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
					serviceList.setLayoutManager(mLayoutManager);
					serviceList.setItemAnimator(new DefaultItemAnimator());
					serviceList.setAdapter(mAdapter);

					serviceListLayout.setVisibility(View.VISIBLE);
					productList.setVisibility(View.GONE);
					submitProducts.setVisibility(View.GONE);
					servicesLayout.setVisibility(View.VISIBLE);

					viewServices.setVisibility(View.VISIBLE);
					viewProduct.setVisibility(View.GONE);
					viewSkills.setVisibility(View.GONE);

				}

			}

			break;
			case R.id.services:
				text.setText("Select Services!");
				if(proAdapter.getSelectedProduct()!=null)
				{
					String str1[] = proAdapter.getSelectedProduct().split(",");

					Log.e("SELECTED_PRODUCT",proAdapter.getSelectedProduct());
					selectedProdcts=new ArrayList<UserProductMappingVO>();
					UserProductMappingVO[] o1 = new UserProductMappingVO[str1.length];
					if(proAdapter.getSelectedProduct().equals("[]"))
					{
						Utils.showToast(ServicesAndSkillsSecond.this,"Please select any one product!");

					}
					else
					{
						for (int i=0;i<str1.length;i++)
						{
							o1[i] = new UserProductMappingVO();
							try
							{
								for(int k=0;k<productJsonArray.length();k++)
								{
									if(productJsonArray.getJSONObject(k).getString("productId").equals(String.valueOf(Integer.parseInt(str1[i].replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")))))
									{
										o1[i].setProductName(productJsonArray.getJSONObject(k).getString("name"));
										o1[i].setProductId(productJsonArray.getJSONObject(k).getString("productId"));
										o1[i].setIsComplaint(productJsonArray.getJSONObject(k).getString("isComplaint"));
										o1[i].setIsInstall(productJsonArray.getJSONObject(k).getString("isInstall"));
										o1[i].setIsService(productJsonArray.getJSONObject(k).getString("isService"));
									}
								}
								selectedProdcts.add(o1[i]);
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						mAdapter = new FeatureAdapter(selectedProdcts,ServicesAndSkillsSecond.this);
						RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
						serviceList.setLayoutManager(mLayoutManager);
						serviceList.setItemAnimator(new DefaultItemAnimator());
						serviceList.setAdapter(mAdapter);

						serviceListLayout.setVisibility(View.VISIBLE);
						productList.setVisibility(View.GONE);
						submitProducts.setVisibility(View.GONE);
						servicesLayout.setVisibility(View.VISIBLE);
						skillsLayout.setVisibility(View.GONE);
						skillList.setVisibility(View.GONE);
						viewServices.setVisibility(View.VISIBLE);
						viewProduct.setVisibility(View.GONE);
						viewSkills.setVisibility(View.GONE);

					}

				}

				break;
			case R.id.editProducts:
				text.setText("Select Products!");

				//selectedProdcts.clear();
				viewServices.setVisibility(View.GONE);
				viewProduct.setVisibility(View.VISIBLE);
				viewSkills.setVisibility(View.GONE);

					/*product.setBackgroundResource(R.drawable.rectangle_background_blue);
					product.setTextColor(Color.parseColor("#FFFFFF"));

					skills.setBackgroundResource(R.drawable.box);
					skills.setTextColor(Color.parseColor("#000000"));
					services.setBackgroundResource(R.drawable.box);
					services.setTextColor(Color.parseColor("#000000"));*/

				submitProducts.setVisibility(View.VISIBLE);
				serviceListLayout.setVisibility(View.GONE);
				productList.setVisibility(View.VISIBLE);
				//serviceListLayout.setVisibility(View.GONE);
				servicesLayout.setVisibility(View.GONE);




				break;
			case R.id.products:
				text.setText("Select Products!");

				//selectedProdcts.clear();
				viewServices.setVisibility(View.GONE);
				viewProduct.setVisibility(View.VISIBLE);
				viewSkills.setVisibility(View.GONE);

					/*product.setBackgroundResource(R.drawable.rectangle_background_blue);
					product.setTextColor(Color.parseColor("#FFFFFF"));

					skills.setBackgroundResource(R.drawable.box);
					skills.setTextColor(Color.parseColor("#000000"));
					services.setBackgroundResource(R.drawable.box);
					services.setTextColor(Color.parseColor("#000000"));*/

				submitProducts.setVisibility(View.VISIBLE);
				serviceListLayout.setVisibility(View.GONE);
				productList.setVisibility(View.VISIBLE);
				//serviceListLayout.setVisibility(View.GONE);
				servicesLayout.setVisibility(View.GONE);
				skillsLayout.setVisibility(View.GONE);
				skillList.setVisibility(View.GONE);
				break;
			case R.id.submitServices:
				//	getData("SERVICE");
				text.setText("Select Skills!");


				viewServices.setVisibility(View.GONE);
				viewProduct.setVisibility(View.GONE);
				viewSkills.setVisibility(View.VISIBLE);


				serviceListLayout.setVisibility(View.GONE);
				servicesLayout.setVisibility(View.GONE);
				skillsLayout.setVisibility(View.VISIBLE);


				getSkills();
				break;
			case R.id.skills:
				//	getData("SERVICE");
				text.setText("Select Skills!");


				viewServices.setVisibility(View.GONE);
				viewProduct.setVisibility(View.GONE);
				viewSkills.setVisibility(View.VISIBLE);


				serviceListLayout.setVisibility(View.GONE);
				servicesLayout.setVisibility(View.GONE);
				skillsLayout.setVisibility(View.VISIBLE);
				productList.setVisibility(View.GONE);
				submitProducts.setVisibility(View.GONE);

				getSkills();
				break;

			case R.id.submitSkills:

				JSONObject jResult = new JSONObject();// main object
				JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

				for (int i = 0; i < selectedProdcts.size(); i++) {
					JSONObject jGroup = new JSONObject();// /sub Object

					try {


						if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
						{
							jGroup.put("userId", Preferences.getInstance().companyId);
						}
						else
						{
							jGroup.put("userId", Preferences.getInstance().userId);
						}
						jGroup.put("userType", Preferences.getInstance().userType);
						jGroup.put("productId", selectedProdcts.get(i).getProductId());
						jGroup.put("isInstall", selectedProdcts.get(i).getIsInstall());
						jGroup.put("isService", selectedProdcts.get(i).getIsService());
						jGroup.put("isComplaint", selectedProdcts.get(i).getIsComplaint());
						//jGroup.put("productName", selectedProdcts.get(i).getProductName());

						jArray.put(jGroup);


					} catch (JSONException e) {
						e.printStackTrace();
					}
					int a=selectedProdcts.size()-1;
					if(i==a)
					{
						generateUserServiceSkillProductFeatureMapping(jArray);
					}
				}



				break;
			case R.id.editServices:
				text.setText("Select Services!");

				viewServices.setVisibility(View.VISIBLE);
				viewProduct.setVisibility(View.GONE);
				viewSkills.setVisibility(View.GONE);


				skillsLayout.setVisibility(View.GONE);
				skillList.setVisibility(View.GONE);
				servicesLayout.setVisibility(View.VISIBLE);
				serviceListLayout.setVisibility(View.VISIBLE);

				break;

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) 
		{
			case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void generateUserServiceSkillProductFeatureMapping(final JSONArray jArray) {
		progressBar.setVisibility(View.VISIBLE);
		RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
		String url="";

		url = AppConstants.SERVER_URL +"generateUserServiceSkillProductFeatureMapping";



		StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONObject responseObject;
				try {
					toa();
					responseObject = new JSONObject(response);
					System.out.println(responseObject.toString());
					Log.e("RESPO_STUD", responseObject.toString());
					if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
					{

						Utils.showToast(ServicesAndSkillsSecond.this,responseObject.getString("errorMessage"));
						Preferences.getInstance().loadPreferences(ServicesAndSkillsSecond.this);
						if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
						{
							Intent i=new Intent(ServicesAndSkillsSecond.this,CompanyHome.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
							finish();
						}
						else
						{
							Intent i=new Intent(ServicesAndSkillsSecond.this,Home.class);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
							finish();
						}


					}
					else
					{
						//Utils.showToast(ServicesAndSkillsSecond.this,responseObject.getString("errorMessage"));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error)
			{
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
				Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
			}
		})
		{
			@Override
			protected Map<String, String> getParams() {
				Preferences.getInstance().loadPreferences(ServicesAndSkillsSecond.this);
				Map<String, String> params = new HashMap<String, String>();

				params.put("serviceTypeIds", getIntent().getExtras().getString("SERVICE_IDS"));
				if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
				{
					params.put("userId", Preferences.getInstance().companyId);

				}
				else
				{
					params.put("userId", Preferences.getInstance().userId);

				}
				params.put("userType", Preferences.getInstance().userType);
				params.put("serviceSkillIds", adapterSkill.getSelectedSkillIds());
				params.put("productJSONArray", jArray.toString());

				Log.e("PARAMS_FINAL","==> "+jArray.length()+" === "+params.toString());
				return params;
			}
		};
		requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		if (Utils.isNetworkAvailable(ServicesAndSkillsSecond.this)) {
			queue.add(requestObject);
		} else
		{
			if (progressBar != null && progressBar.isShown()) {
				progressBar.setVisibility(View.GONE);
			}
			Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
		}
	}


	private void getSkills() {
		progressBar.setVisibility(View.VISIBLE);
		RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
		String url="";

		url = AppConstants.SERVER_URL +"getServiceTypeSkillList";



		StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONObject responseObject;
				try {
					toa();
					responseObject = new JSONObject(response);
					System.out.println(responseObject.toString());
					Log.e("RESPO_STUD", responseObject.toString());
					if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
					{

						adapterSkill = new SkillAdapter(ServicesAndSkillsSecond.this,responseObject.getJSONArray("responseObject"),getSkillIds);

						skillList.setAdapter(adapterSkill);
						skillList.setVisibility(View.VISIBLE);
						adapterSkill.notifyDataSetChanged();


					}
					else
					{
						//Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error)
			{
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
				Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
			}
		})
		{
			@Override
			protected Map<String, String> getParams() {
				Preferences.getInstance().loadPreferences(ServicesAndSkillsSecond.this);
				Map<String, String> params = new HashMap<String, String>();

				params.put("serviceTypeIds", getIntent().getExtras().getString("SERVICE_IDS"));
				//}
				Log.e("PARAMS",params.toString());
				return params;
			}
		};
		requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		if (Utils.isNetworkAvailable(ServicesAndSkillsSecond.this)) {
			queue.add(requestObject);
		} else
		{
			if (progressBar != null && progressBar.isShown()) {
				progressBar.setVisibility(View.GONE);
			}
			Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
		}
	}

	private void getUserServiceSkillProductFeatureMapping() {
		progressBar.setVisibility(View.VISIBLE);
		RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
		String url="";

		url = AppConstants.SERVER_URL +"getUserServiceSkillProductFeatureMapping";



		StringRequest requestObject = new StringRequest(Method.POST, url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONObject responseObject;
				try {
					toa();
					responseObject = new JSONObject(response);
					System.out.println(responseObject.toString());
					Log.e("RESPO_FEATURE", responseObject.toString());
					if (responseObject.has("errorCode") && responseObject.getString("errorCode").equals("0"))
					{

						getproductJsonArray=new JSONArray();
						getproductJsonArray=responseObject.getJSONObject("responseObject").getJSONArray("productList");

						getProductIds="";
						if(getproductJsonArray!=null && getproductJsonArray.length() > 0) {
							for (int i = 0; i < getproductJsonArray.length() - 1; i++) {
								getProductIds += getproductJsonArray.getJSONObject(i).getString("productId") + ",";
							}
							getProductIds += getproductJsonArray.getJSONObject(getproductJsonArray.length() - 1).getString("productId");
						}
						getSkillIds=responseObject.getJSONObject("responseObject").getString("skillIds");
					}
					else
					{
						//Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
					}

					getData();

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error)
			{
				if (progressBar != null && progressBar.isShown()) {
					progressBar.setVisibility(View.GONE);
				}
				Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ERROR_FETCHING_DATA_MESSAGE);
			}
		})
		{
			@Override
			protected Map<String, String> getParams() {
				Preferences.getInstance().loadPreferences(ServicesAndSkillsSecond.this);
				Map<String, String> params = new HashMap<String, String>();

				if(Preferences.getInstance().userType.equalsIgnoreCase("COMPANY"))
				{
					params.put("userId", Preferences.getInstance().companyId);

				}
				else
				{
					params.put("userId", Preferences.getInstance().userId);

				}
				params.put("userType", Preferences.getInstance().userType);
				//}
				Log.e("PARAMS",params.toString());
				return params;
			}
		};
		requestObject.setRetryPolicy(new DefaultRetryPolicy(25000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		if (Utils.isNetworkAvailable(ServicesAndSkillsSecond.this)) {
			queue.add(requestObject);
		} else
		{
			if (progressBar != null && progressBar.isShown()) {
				progressBar.setVisibility(View.GONE);
			}
			Utils.showToast(ServicesAndSkillsSecond.this, AppConstants.MESSAGES.ENABLE_INTERNET_SETTING_MESSAGE);
		}
	}
}
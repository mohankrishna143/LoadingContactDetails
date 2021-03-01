package com.android.assignment;

import android.app.Activity;
import android.app.AsyncNotedAppOp;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Model.ProfileDetails;

public class UpdateData {

    MainActivity ctx;
    ProgressDialog progress;
    ArrayList<ProfileDetails> userList=new ArrayList<>();
    ArrayList<ProfileDetails> last_userDetails=new ArrayList<>();
    List<ProfileDetails> list=new ArrayList<>();
    int height,width;
    public Button btn_image;
    Dialog dialog;
    public UpdateData(MainActivity mcontext){
         this.ctx=mcontext;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }


    public void fetchData(int pageNo){
        RequestQueue queue = Volley.newRequestQueue(ctx);
//for POST requests, only the following line should be changed to

        StringRequest sr = new StringRequest(Request.Method.GET, "https://reqres.in/api/users?page=" +pageNo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("HttpClient", "success! response: " + response.toString());

                        new UpdateList(response).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HttpClient", "success! response: " + error.toString());
                    }
               })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Content-type", "application/json");
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    /*
     * pagination for api data
     */
    public class UpdateList extends AsyncTask<Void, Integer, Integer> {
        String response;

        public UpdateList(String respons) {
            this.response=respons;
        }

        @Override
        protected void onPreExecute() {
            if (userList != null && userList.size() > 0 && userList.get(userList.size() - 1) == null) {
                if (ctx.adapter != null) {
                    userList.remove(userList.size() - 1);
                    ctx.adapter.notifyItemRemoved(userList.size());
                }
            }
            if (ctx.adapter != null) {
                ctx.adapter.setLoaded();
            }
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                JSONObject json = new JSONObject(response);
                ctx.pageNo=json.getInt("page");
                ctx.total_Pages = json.getInt("total_pages");
                JSONArray array = json.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject list = array.getJSONObject(i);
                    ProfileDetails profile = new ProfileDetails();
                    profile.setId(list.getInt("id"));
                    profile.setFirst_Name(list.getString("first_name"));
                    profile.setLast_Name(list.getString("last_name"));
                    profile.setAvatar(list.getString("avatar"));
                    profile.setEmail(list.getString("email"));
                    profile.setPageNo(ctx.pageNo);
                    profile.setTotal_Pages(ctx.total_Pages);
                    userList.add(profile);
                    ctx.userList_Dao.insertUserList(profile);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(ctx.adapter!=null) {
                ctx.adapter.setData(userList);
            }
        }

    }

    /*
     * Fetching data from db or from api
     */
    public class fetchData extends AsyncTask<Void, Integer, Integer> {
        int pageNo;
        public fetchData(int position) {
            this.pageNo=position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            userList= (ArrayList<ProfileDetails>) ctx.userList_Dao.getAllUserList();
             if(userList!=null&&userList.size()>0){
                 last_userDetails= (ArrayList<ProfileDetails>) ctx.userList_Dao.getLastUserList();
                 for(ProfileDetails details:last_userDetails){
                     ctx.pageNo=details.getPageNo();
                     ctx.total_Pages=details.getTotal_Pages();
                     ctx.serialNo=details.getId();


                 }
             }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

           if(userList==null ||userList.size()==0) {
               fetchData(pageNo);
            }
            ctx.adapter.setData(userList);
        }
    }

    /*
     * Dialog window for user details
     */
    public void showDialog(){
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_form);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        final EditText et_fname = (EditText) dialog.findViewById(R.id.et_fname);
        final EditText et_lname = (EditText) dialog.findViewById(R.id.et_lname);
        final EditText et_email = (EditText) dialog.findViewById(R.id.et_email);
        btn_image = (Button) dialog.findViewById(R.id.btn_image);

        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ctx.startActivityForResult(i, 101);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName=et_fname.getText().toString();
                String lName=et_lname.getText().toString();
                String email=et_email.getText().toString();

                if(fName.isEmpty()){
                    Toast.makeText(ctx, "Please enter FirstName",
                            Toast.LENGTH_LONG).show();
                }else if(lName.isEmpty()){
                    Toast.makeText(ctx, "Please enter LastName",
                            Toast.LENGTH_LONG).show();
                }else if(email.isEmpty()){
                    Toast.makeText(ctx, "Please enter Email-Id",
                            Toast.LENGTH_LONG).show();
                }else if(ctx.picturePath==null||ctx.picturePath.isEmpty()){
                    Toast.makeText(ctx, "Select image from Gallery",
                            Toast.LENGTH_LONG).show();
                }else{
                     dialog.dismiss();
                     new updateInsertedData(fName,lName,email).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            }
        });

        dialog.show();

    }

    /*
     * Inserting user created data into database
     */

    public class updateInsertedData extends AsyncTask<Void, Integer, Integer> {
        String frst_Name,last_Name, email_Id;

        public updateInsertedData(String fName,String LName,String email) {
            this.frst_Name=fName;
            last_Name=LName;
            email_Id=email;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progress=new ProgressDialog(ctx);
            progress.setMessage("Please Wait");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                    String filePath=saveToInternalStorage(ctx.selectedImage);
                    ProfileDetails profile = new ProfileDetails();
                    profile.setId(ctx.serialNo+1);
                    profile.setFirst_Name(frst_Name);
                    profile.setLast_Name(last_Name);
                    profile.setAvatar(filePath);
                    profile.setEmail(email_Id);
                    profile.setPageNo(ctx.pageNo);
                    profile.setTotal_Pages(ctx.total_Pages);
                    userList.add(profile);
                    ctx.userList_Dao.insertUserList(profile);

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(ctx.adapter!=null) {
                ctx.adapter.setData(userList);
            }
            progress.dismiss();

        }

    }

    /*
     * Saving selected image in directory path
     */
    private String saveToInternalStorage(Uri imageUri){
        FileOutputStream fos = null;
        File mypath = null;
        try{

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), imageUri);
        String id= getCurrentTimeStamp();
        String filePath="/data/data/"+BuildConfig.APPLICATION_ID+"/Images";
        File root = new File(filePath);
        if (!root.exists()) {
            root.mkdirs();
        }
        String fileName=id+".png";
        mypath = new File(root+"/"+fileName);

        fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    /*
     * Image unique id
     */
    public  String getCurrentTimeStamp(){
        try {
            return new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

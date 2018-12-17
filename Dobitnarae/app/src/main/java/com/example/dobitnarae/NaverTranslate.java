package com.example.dobitnarae;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NaverTranslate extends AsyncTask<String, Void, String>  {
    //Naver
    String clientId = "zgvN61GVH8aqvH8kEtH0";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "NbjOJYrZci";//애플리케이션 클라이언트 시크릿값";
    //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.
    String sourceLang = "ko";
    String targetLang = "en";
    private EditText ed = null;

    public NaverTranslate() {

    }

    public NaverTranslate(EditText editText) {
        this.ed = editText;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //AsyncTask 메인처리
    @Override
    protected String doInBackground(String... strings) {
        //네이버제공 예제 복사해 넣자.
        //Log.d("AsyncTask:", "1.Background");

        String sourceText = strings[0];

        try {
            //String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
            String text = URLEncoder.encode(sourceText, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/language/translate";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            //System.out.println(response.toString());
            return response.toString();

        } catch (Exception e) {
            //System.out.println(e);
            Log.d("error", e.getMessage());
            return null;
        }
    }

    public String translatedResult(String temp) {
        String result="";
        NaverTranslate JT = new NaverTranslate();
        try{
            //최종 결과 처리부
            //Log.d("background result", JT.execute(temp).get().toString()); //네이버에 보내주는 응답결과가 JSON 데이터이다.

            //JSON데이터를 자바객체로 변환해야 한다.
            //Gson을 사용할 것이다.

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(JT.execute(temp).get().toString())
                    //원하는 데이터 까지 찾아 들어간다.
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            //안드로이드 객체에 담기
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
            Log.d("result", items.getTranslatedText());
            //번역결과를 대입
            //if(ed!=null)
            //ed.setText(items.getTranslatedText());
            result = items.getTranslatedText();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /*
    //번역된 결과를 받아서 처리
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //최종 결과 처리부
        Log.d("background result", s.toString()); //네이버에 보내주는 응답결과가 JSON 데이터이다.

        //JSON데이터를 자바객체로 변환해야 한다.
        //Gson을 사용할 것이다.

        Gson gson = new GsonBuilder().create();
        JsonParser parser = new JsonParser();
        JsonElement rootObj = parser.parse(s.toString())
        //원하는 데이터 까지 찾아 들어간다.
                .getAsJsonObject().get("message")
                .getAsJsonObject().get("result");
        //안드로이드 객체에 담기
        TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
        Log.d("result", items.getTranslatedText());
        //번역결과를 대입
        //if(ed!=null)
            //ed.setText(items.getTranslatedText());
        //else
        resultText.setData(items.getTranslatedText());
    }
    */
    //자바용 그릇
    public class TranslatedItem {
        String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }
    }
}

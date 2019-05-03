package com.acrcloud.rec.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Description extends Activity {

    TextView SongName, SongArtist, SongDuration, SongReleaseDate, SongGenre, SongLabel, Youtube, SpotifyAlbum, SpotifyArtist, SpotifyTrack, title, artist,back;
    String songName ="";
    String songArtist = "";
    String songDuration = "";
    String songReleaseDate = "";
    String songGenre = "";
    String songLabel = "";
    String youtube = "";
    String spotifyAlbum = "";
    String spotifyAlbumLink = "";
    String spotifyTrack = "";
    String spotifyTrackLink = "";
    String spotifyArtist = "";
    String spotifyArtistLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        String Result = getIntent().getExtras().getString("Result");

        title = findViewById(R.id.tv_songName1);
        artist = findViewById(R.id.tv_songArtist1);
        SongName = findViewById(R.id.tv_songName);
        SongArtist = findViewById(R.id.tv_songArtist);
        SongDuration = findViewById(R.id.tv_songDuration);
        SongReleaseDate = findViewById(R.id.tv_songReleaseDate);
        SongGenre = findViewById(R.id.tv_songGenre);
        SongLabel = findViewById(R.id.tv_songLabel);
        Youtube = findViewById(R.id.tv_youtube);
        SpotifyAlbum = findViewById(R.id.tv_spotifyAlbum);
        SpotifyArtist = findViewById(R.id.tv_spotifyArtist);
        SpotifyTrack = findViewById(R.id.tv_spotifyTrack);
        back = findViewById(R.id.tv_back);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Description.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        try{
            JSONObject j = new JSONObject(Result);
            JSONObject metadata = j.getJSONObject("metadata");


            if (metadata.has("music")) {
                JSONArray musics = metadata.getJSONArray("music");
                JSONObject tt = (JSONObject) musics.get(0);


                try{
                    songName = tt.getString("title");
                }
                catch (JSONException e){
                    songName = "-";
                }
                try{
                    songArtist = ((JSONObject) tt.getJSONArray("artists").get(0)).getString("name");
                }
                catch (JSONException e){
                    songArtist ="-";
                }
                try{
                    Integer sec = Integer.parseInt(tt.getString("duration_ms"))%60000/1000;
                    Integer Mnt = Integer.parseInt(tt.getString("duration_ms"))/60000;
                    if(sec < 10 && Mnt> 10){
                        songDuration = String.valueOf(Integer.parseInt(tt.getString("duration_ms"))/60000 + ":0"+ Integer.parseInt(tt.getString("duration_ms"))%60000/1000);
                    }
                    else
                    if(sec > 10 && Mnt< 10)
                    {
                        songDuration = String.valueOf("0"+Integer.parseInt(tt.getString("duration_ms"))/60000 + ":"+ Integer.parseInt(tt.getString("duration_ms"))%60000/1000);
                    }
                    else
                        if(sec > 10 && Mnt > 10){
                            songDuration = String.valueOf(+Integer.parseInt(tt.getString("duration_ms"))/60000 + ":"+ Integer.parseInt(tt.getString("duration_ms"))%60000/1000);
                        }
                        else
                        {
                            songDuration = String.valueOf("0"+Integer.parseInt(tt.getString("duration_ms"))/60000 + ":0"+ Integer.parseInt(tt.getString("duration_ms"))%60000/1000);
                        }

                }
                catch (JSONException e){
                    songDuration = "-";
                }
                try{
                    songReleaseDate = tt.getString("release_date");
                }
                catch (JSONException e){
                    songReleaseDate = "-";
                }
                try{
                    songGenre = ((JSONObject)tt.getJSONArray("genres").get(0)).getString("name");
                }
                catch (JSONException e){
                    songGenre = "-";
                }
                try{
                    songLabel = tt.getString("label");
                }
                catch (JSONException e){
                    songLabel = "-";
                }

                try{
                    youtube = "https://www.youtube.com/watch?v=" + ((JSONObject) tt.getJSONObject("external_metadata").getJSONObject("youtube")).getString("vid");
                }
                catch (JSONException e){
                    youtube= "Link Not Found";
                }

                try{
                    spotifyAlbum = tt.getJSONObject("external_metadata").getJSONObject("spotify").getJSONObject("album").getString("name");
                    spotifyAlbumLink =  "https://open.spotify.com/album/"+ tt.getJSONObject("external_metadata").getJSONObject("spotify").getJSONObject("album").getString("id");
                }
                catch (JSONException e){
                    spotifyAlbum = "-";
                    spotifyAlbumLink = "-";
                }

                try{
                    spotifyArtist= ((JSONObject)tt.getJSONObject("external_metadata").getJSONObject("spotify").getJSONArray("artists").get(0)).getString("name");
                    spotifyArtistLink =  "https://open.spotify.com/artist/"+ ((JSONObject)tt.getJSONObject("external_metadata").getJSONObject("spotify").getJSONArray("artists").get(0)).getString("id");
                }
                catch (JSONException e){
                    spotifyArtist = "-";
                    spotifyArtistLink = "-";
                }

                try{
                    spotifyTrack= tt.getJSONObject("external_metadata").getJSONObject("spotify").getJSONObject("track").getString("name");
                    spotifyTrackLink =  "https://open.spotify.com/track/"+ tt.getJSONObject("external_metadata").getJSONObject("spotify").getJSONObject("track").getString("id");
                }
                catch (JSONException e){
                    spotifyTrack = "-";
                    spotifyTrackLink = "-";
                }

                title.setText(songName);
                artist.setText(songArtist);
                SongName.setText(songName);
                SongArtist.setText(songArtist);
                SongDuration.setText(songDuration);
                SongReleaseDate.setText(songReleaseDate);
                SongGenre.setText(songGenre);
                SongLabel.setText(songLabel);
                Youtube.setText(youtube);
                SpotifyAlbum.setText('"'+spotifyAlbum+'"');
                SpotifyArtist.setText('"'+spotifyArtist+'"');
                SpotifyTrack.setText('"'+spotifyTrack+'"');
            }
        }catch (JSONException e){

        }
        //
        Youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!youtube.equals("Link Not Found")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(youtube));
                    startActivity(i);
                }
            }
        });

        SpotifyAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!spotifyAlbumLink.equals("-")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(spotifyAlbumLink));
                    startActivity(i);
                }
            }
        });

        SpotifyArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!spotifyArtistLink.equals("-")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(spotifyArtistLink));
                    startActivity(i);
                }
            }
        });

        SpotifyTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!spotifyTrackLink.equals("-")){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(spotifyArtistLink));
                    startActivity(i);
                }
            }
        });
    }
}

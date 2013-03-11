package com.dvcs.gilbertcleanup;

import java.util.ArrayList;

import com.dvcs.gilbertcleanup.web.HeroesOfGilbert;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

public class GetIssuesTask extends AsyncTask<Void, Void, Issue[]> {
		
	protected Issue[] doInBackground(Void... _) {
		return HeroesOfGilbert.getIssues();
	}
	
	protected void onPostExecute(Issue[] issues) {
		ArrayList<String> listItems_Title = new ArrayList<String>();
		for(int i = 1; i < issues.length; i++) {
			listItems_Title.add(issues[i].toString());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(null, android.R.layout.activity_list_item, listItems_Title);
		adapter.setNotifyOnChange(true);
	}
	
}

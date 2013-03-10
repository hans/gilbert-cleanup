package com.dvcs.gilbertcleanup;

import com.dvcs.gilbertcleanup.web.HeroesOfGilbert;

import android.os.AsyncTask;

public class GetIssuesTask extends AsyncTask<Void, Void, Issue[]> {
		
	protected Issue[] doInBackground(Void... _) {
		return HeroesOfGilbert.getIssues();
	}
	
}

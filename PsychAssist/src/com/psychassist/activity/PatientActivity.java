package com.psychassist.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class PatientActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		
		Intent intent = getIntent();

		if (intent.getStringExtra("Mode").equals("View")) {
			String docId = intent.getStringExtra("Document");
			String name = intent.getStringExtra("name");
			String surname = intent.getStringExtra("surname");
			int pos = intent.getIntExtra("Pos",0);
			return PatientFragment.newInstance(docId, name, surname);
		} else {
			return new PatientAddFragment();
		}
	}
}
